ZZUTXTL4	;JLI/FO-OAK - UNIT TESTS FOR XTMLOG ;2/19/08  13:40
	;;7.3;TOOLKIT;**???**;Apr 25,1995
	D EN^XTMUNIT("ZZUTXTL4")
	Q
	;
FORMAT	;
	N TSTNAME,ROOT,INFO,XX,ROOTVAL
	S TSTNAME="TEST"
	S ROOT=$NA(ROOTVAL(TSTNAME,"APPENDER","MYAPPENDER"))
	S @ROOT@("LAYOUT.CONVERSIONPATTERN")="%5p [%t] - %m%n"
	S INFO("PRIORITY")="DEBUG"
	S INFO("$H")="59443,57959",INFO("COUNT")=5,INFO("LOCATION")="ENTRY+5^ROUNAME"
	S XX=$$FORMAT^XTMLOG1(ROOT,.INFO,"Text of message")
	D CHKEQ^XTMUNIT("DEBUG ["_$J_"] - Text of message",XX,"Did not format correctly")
	;
	S INFO("PRIORITY")="INFO"
	S XX=$$FORMAT^XTMLOG1(ROOT,.INFO,"Text of message")
	D CHKEQ^XTMUNIT(" INFO ["_$J_"] - Text of message",XX,"Did not right justify correctly")
	;
	S @ROOT@("LAYOUT.CONVERSIONPATTERN")="%-5p [%t] - %m%n"
	S XX=$$FORMAT^XTMLOG1(ROOT,.INFO,"Text of message")
	D CHKEQ^XTMUNIT("INFO  ["_$J_"] - Text of message",XX,"Did not left justify correctly")
	;
	S @ROOT@("LAYOUT.CONVERSIONPATTERN")="%-5p [%t] {%M} [%L] [%F] - %m%n"
	S XX=$$FORMAT^XTMLOG1(ROOT,.INFO,"Text of message")
	D CHKEQ^XTMUNIT("INFO  ["_$J_"] {ENTRY} [ENTRY+5] [ROUNAME] - Text of message",XX,"Did not handle locations correctly")
	;
	S @ROOT@("LAYOUT.CONVERSIONPATTERN")="%d{dd MMM yyyy HH:mm:ss,SSS} ^ %-5p [%t] - %m%n"
	S XX=$$FORMAT^XTMLOG1(ROOT,.INFO,"Text of message")
	D CHKEQ^XTMUNIT("01 OCT 2003 16:05:59, ^ INFO  ["_$J_"] - Text of message",XX,"Did not handle date format correctly")
	Q
	;
LOGGING	;
	N X
	S X=$$INITNONE^XTMLOG("JLITEST")
	D EN^XTMUNIT("ZZUTXTL2")
	D ENDLOG^XTMLOG("JLITEST")
	Q
	;
XTROU	;
	;;ZZUTXTL1;TESTS FOR XTMLOG
XTENT	;
	;;FORMAT;TEST FORMAT HANDLING
