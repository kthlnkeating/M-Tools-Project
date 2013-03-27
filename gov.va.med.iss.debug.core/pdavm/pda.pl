#!perl.exe

use strict;
use warnings;
use IO::Socket;

#####################################################################
# Copyright (c) 2004 IBM Corporation and others.
# All rights reserved. This program and the accompanying materials 
# are made available under the terms of the Common Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/cpl-v10.html
# 
# Contributors:
#     Bjorn Freeman-Benson - initial API and implementation
#####################################################################

#####################################################################
#																	#
#	I N I T I A L I Z A T I O N   A N D   V A R I A B L E S			#
#																	#
#####################################################################
#
# The push down automata stack (the data stack)
#
my @stack;
#
# Load all the code into memory
# The code is stored as an array of strings, each line of
# the source file being one entry in the array.
#
my $filename = shift;
open INFILE, $filename or die $!;
my @code = <INFILE>;
close INFILE;
#
# A mapping of labels to indicies in the code array
#
my %labels;
my $idx = 0;
while( $idx <= $#code ) {
	$code[$idx] =~ /^\s*(.+?)\s*$/;
	$code[$idx] = $1;
	$labels{$1} = $idx if( $code[$idx] =~ /^:(\S+)/ );
	$idx ++;
}
#
# The stack of stack frames (the control stack)
# Each stack frame is a mapping of variable names to values.
# There are a number of special variable names:
#  _pc_  is the current program counter in the frame
#        the pc points to the next instruction to be executed
#  _func_ is the name of the function in this frame
#
my @frames;
my $currentframe;
$currentframe = {
	_pc_ => 0,
	_func_ => 'main'
	};

#
# The command line argument to start a debug session.
#
my $debugflag = shift;
#
# The port to listen for debug commands on
# and the port to send debug events to
#
my $debugport;
my $debugport2;
#
# The socket to listen for debug commands on
# and the socket to send debug events on
#
my $debugsock;
my $debugsock2;
#
# An input buffer
#
my $debugbuf;
#
# Breakpoint array
# breakpoints are stored as a boolean for each line of code
# if the boolean is true, there is a breakpoint on that line
#
my @breakpoints;
#
# Mapping of debugger commands to functions that evaluate them
#
my %debug_commands = (
	clear => \&debug_clear_breakpoint,
	data => \&debug_data,
	exit => \&debug_exit,
	resume => \&debug_resume,
	set => \&debug_set_breakpoint,
	stack => \&debug_stack,
	step => \&debug_step,
	suspend => \&debug_suspend,
	var => \&debug_var
);

#
# The run flag is true if the VM is running.
# If the run flag is false, the VM exits the
# next time the main instruction loop runs.
#
my $run = 1;
#
# The suspend flag is true if the VM should suspend
# running the program and just listen for debug commands.
#
my $suspend = 0;
my $started = 1;
$suspend = 1 if( $debugflag );
#
# The step flag is used to control single-stepping.
# See the implementation of the "step" debug command.
# The brokepoint flag is used to control breakpoints.
#
my $step = 0;
my $brokepoint = 0;

#
# Mapping of the names of the instructions to the functions that evaluate them
#
my %instructions = (
	add => \&add,
	branch_not_zero => \&branch_not_zero,
	call => \&call,
	dec => \&dec,
	dup => \&dup,
	halt => \&halt,
	output => \&output,
	pop => \&ipop,
	push => \&ipush,
	return => \&return,
	var => \&var
);

#####################################################################
#																	#
#				M A I N  I N T E R P R E T E R						#
#																	#
#####################################################################
#
# Open a debug session if the command line argument is given.
#
start_debugger();
send_debug_event( "started", 0 );
debug_ui() if( $suspend );
#
# The main run loop
#
while( $run ) {
	check_for_breakpoint();
	debug_ui() if( $suspend );
	yield_to_debug();
	do_one_instruction();
	$run = 0 if( $$currentframe{_pc_} > $#code );
}
send_debug_event( "terminated", 0 );
#
# Executing an instruction consists of splitting the statement
# into words (recall that the statements are stored as strings)
# and then interpreting the first word as the instruction.
#
sub do_one_instruction {
	my $pc = $$currentframe{_pc_};
	my $theinstruction = $code[$pc];
	#print "$pc $theinstruction\n";
	$pc++;
	$$currentframe{_pc_} = $pc;
	if( $theinstruction =~ /^:/ ) {
		# label
		$suspend = 1 if( $step );
	} elsif( $theinstruction =~ /^#/ ) {
		# comment
	} else {
		$theinstruction =~ /^(\S+)\s*(.*)/;
		my $op = $1;
		my $instr = $instructions{$1};
		if( $instr ) {
			&$instr( $theinstruction, $2 );
		} else {
			send_debug_event( "unimplemented instruction $op", 1 );
		}
		$suspend = 1 if( $step );
	}
}

#####################################################################
#																	#
#					I N S T R U C T I O N S							#
#																	#
#####################################################################
sub add {
	my $val1 = pop @stack;
	my $val2 = pop @stack;
	my $val = $val1 + $val2;
	push @stack, $val;
}

sub branch_not_zero {
	my $val = pop @stack;
	if( $val ) {
		shift;
		my $label = shift;
		my $dest = $labels{$label};
		if( !defined $dest ) {
			send_debug_event( "no such label $label", 1 );
		} else {
			$$currentframe{_pc_} = $dest;
		}
	}
}

sub call {
	shift;
	my $label = shift;
	my $dest = $labels{$label};
	if( !defined $dest ) {
		send_debug_event( "no such label $label", 1 );
	} else {
		push @frames, $currentframe;
		$currentframe = {
			_pc_ => $dest,
			_func_ => $label
     	};
	}
}

sub dec {
	my $val = pop @stack;
	$val--;
	push @stack, $val;
}
	
sub dup {
	my $val = pop @stack;
	push @stack, $val;
	push @stack, $val;
}

sub halt {
	$run = 0;
}

sub output {
	my $val = pop @stack;
	print "$val\n";
}

sub ipop {
	shift;
	my $arg = shift;
	if( $arg =~ /^\$(.*)/ ) {
		$$currentframe{$1} = pop @stack;
	} else {
		pop @stack;
	}
}

sub ipush {
	shift;
	my $arg = shift;
	if( $arg =~ /^\$(.*)/ ) {
		my $val = $$currentframe{$1};
		push @stack, $val;
	} else {
		push @stack, $arg;
	}
}

sub return {
	$currentframe = pop @frames;
}

sub var {
	shift;
	my $name = shift;
	$$currentframe{$name} = 0;
}

#####################################################################
#																	#
#			D E B U G G E R  I N T E R F A C E						#
#																	#
#####################################################################

sub check_for_breakpoint {
	if( $debugflag ) {
		my $pc = $$currentframe{_pc_};
		if( $breakpoints[$pc] ) {
			$suspend = 1;
			$brokepoint = 1;
		}
	}
}
#
# For each instruction, we check the debug co-routine for
# control input. If there is input, we process it.
#
sub yield_to_debug {
	if( $debugflag ) {
		my $bytes_to_read = 1024;
		my $bytes_read = sysread($debugsock, $debugbuf, $bytes_to_read);
		if( defined($bytes_read) ) {
			#print "read $bytes_to_read\n";
			my $rin = '';
			my $win = '';
			my $ein = '';
    		vec($rin,fileno($debugsock),1) = 1;
	   		$ein = $rin | $win;
			my $debugline = $debugbuf;
			while( !($debugline =~ /\n/) ) {
	  				select($rin, undef, undef, undef);
				my $bytes_to_read = 1024;
				my $bytes_read = sysread($debugsock, $debugbuf, $bytes_to_read);
				$debugline .= $debugbuf;
			}
			#print "read: $debugline";
			process_debug_command($debugline);
			$debugline = '';
		} else {
			# no bytes read
		}
	}
}

#
# If the execution is suspended, then we go into the debug
# ui loop, reading and processing instructions.
#
sub debug_ui {
	return unless( $suspend );
	my $pc = $$currentframe{_pc_};
	if (!$started) {
	   	send_debug_event( "suspended step", 0 )       if(  $step );
	   	send_debug_event( "suspended breakpoint $pc", 0 ) if( !$step &&  $brokepoint );
	   	send_debug_event( "suspended client", 0 )     if( !$step && !$brokepoint );
	} else {
		$started = 0;
	}
   	$step = 0;
   	$brokepoint = 0;
	my $rin = '';
	my $win = '';
	my $ein = '';
    vec($rin,fileno($debugsock),1) = 1;
    $ein = $rin | $win;
    my $debugline = '';
    while( $suspend ) {
	    select($rin, undef, undef, undef);
		my $bytes_to_read = 1024;
		my $bytes_read = sysread($debugsock, $debugbuf, $bytes_to_read);
		$debugline .= $debugbuf;
		if( $debugline =~ /\n/ ) {
			#print "read: $debugline";
			process_debug_command($debugline);
			$debugline = '';
		}
    }
	send_debug_event( "resumed step", 0 ) if( $step );
	send_debug_event( "resumed client", 0 ) unless( $step );
}

sub process_debug_command {
	my $line = shift;
	my @words = split /\s/, $line;
	my $command = lc($words[0]);
	my $dfunc = $debug_commands{$words[0]};
	if( $dfunc ) {
		&$dfunc( @words );
	}
}

sub debug_clear_breakpoint {
	shift;
	my $line = shift;
	$breakpoints[$line] = 0;
	print $debugsock "ok\n";
}
sub debug_data {
	my $result = '';
	foreach my $d ( @stack ) {
		$result .= $d . '|';
	}
	print $debugsock "$result\n";
}
sub debug_exit {
	send_debug_event( "terminated", 0 );
	exit 0;
}
sub debug_resume {
	$suspend = 0;
	print $debugsock "ok\n";
}
sub debug_set_breakpoint {
	shift;
	my $line = shift;
	$breakpoints[$line] = 1;
	print $debugsock "ok\n";
}
sub debug_stack {
	my $result = '';
	foreach my $frame ( @frames ) {
		$result .= print_frame($frame);
		$result .= '#';
	}
	$result .= print_frame($currentframe);
	print $debugsock "$result\n";
}
sub debug_step {
	# set suspend to 0 to allow the debug loop to exit back to
	# the instruction loop and thus run an instruction. However,
	# we want to come back to the debug loop right away, so the
	# step flag is set to true which will cause the suspend flag
	# to get set to true when we get to the next instruction.
	$step = 1;
	$suspend = 0;
	print $debugsock "ok\n";
}
sub debug_suspend {
	$suspend = 1;
	print $debugsock "ok\n";
}
sub debug_var {
	shift;
	my $sfnumber = shift;
	my $var = shift;
	if( $sfnumber > $#frames ) {
		print $debugsock "$$currentframe{$var}\n";
	} else {
		my $theframe = $frames[$sfnumber];
		print $debugsock "$$theframe{$var}\n";
	}
}
#
# Some event has happened so notify the debugger.
# If there is no debugger, we may still want to report the
# event (such as if it is an error).
#
sub send_debug_event {
	my $event = shift;
	if( $debugflag ) {
		print $debugsock2 "$event\n";
	} else {
		my $use_stderr = shift;
		print "Error: $event\n" if $use_stderr;
	}
}
#
# The stack frame output is:
#    frame # frame # frame ...
# where each frame is:
#    filename | line number | function name | var | var | var | var ...
#
sub print_frame {
	my $frame = shift;
	my $result = $filename;
	$result .= '|' . $$frame{_pc_};
	$result .= '|' . $$frame{_func_};
	for my $var ( keys %$frame ) {
		$result .= '|' . $var unless( substr($var,0,1) eq '_');
	}
	return $result;
}

sub start_debugger {
  if( defined($debugflag) ) {
	if( $debugflag eq "-debug" ) {
		{ # make STDOUT unbuffered
			my $ofh = select STDOUT;
	  		$| = 1;
	  		select $ofh;
		}
		$debugflag = 1;
		$debugport = shift @ARGV;
		$debugport2 = shift @ARGV;
		print "-debug $debugport $debugport2\n";

		my $mainsock = new IO::Socket::INET (LocalHost => '127.0.0.1',
                                   LocalPort => $debugport,
                                   Listen    => 1,
                                   Proto     => 'tcp',
                                   Reuse     => 1,
                                  );
		$debugsock = $mainsock->accept();
		my $set_it = "1"; 
		my $ioctl_val = 0x80000000 | (4 << 16) | (ord('f') << 8) | 126; 
		ioctl($debugsock, $ioctl_val, $set_it) or die "couldn't set nonblocking: $^E";

		my $mainsock2 = new IO::Socket::INET (LocalHost => '127.0.0.1',
                                   LocalPort => $debugport2,
                                   Listen    => 1,
                                   Proto     => 'tcp',
                                   Reuse     => 1,
                                  );
		$debugsock2 = $mainsock2->accept();

		print "debug connection accepted\n";
	} else {
		$debugflag = 0;
	}
  }
}
