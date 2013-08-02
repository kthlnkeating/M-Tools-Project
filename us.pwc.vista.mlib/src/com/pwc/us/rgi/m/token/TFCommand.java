package com.pwc.us.rgi.m.token;

import java.util.HashMap;
import java.util.Map;

import com.pwc.us.rgi.m.parsetree.Do;
import com.pwc.us.rgi.m.parsetree.DoBlock;
import com.pwc.us.rgi.m.parsetree.ElseCmd;
import com.pwc.us.rgi.m.parsetree.ForLoop;
import com.pwc.us.rgi.m.parsetree.Goto;
import com.pwc.us.rgi.m.parsetree.IfCmd;
import com.pwc.us.rgi.m.parsetree.KillCmdNodes;
import com.pwc.us.rgi.m.parsetree.MergeCmdNodes;
import com.pwc.us.rgi.m.parsetree.NewCmdNodes;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.Nodes;
import com.pwc.us.rgi.m.parsetree.OpenCloseUseCmdNodes;
import com.pwc.us.rgi.m.parsetree.QuitCmd;
import com.pwc.us.rgi.m.parsetree.ReadCmd;
import com.pwc.us.rgi.m.parsetree.SetCmdNodes;
import com.pwc.us.rgi.m.parsetree.WriteCmd;
import com.pwc.us.rgi.m.parsetree.XecuteCmd;
import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.m.struct.MNameWithMnemonic;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.TFEmptyVerified;
import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parser.Tokens;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFCommand extends TokenFactory<MToken> {
	private Map<String, TCSFactory> commandSpecs = new HashMap<String, TCSFactory>();
	private MTFSupply supply;
	
	public TFCommand(String name, MTFSupply supply) {
		super(name);
		this.supply = supply;
	}
	
	private static class TFGenericArgument extends TokenFactory<MToken> {
		public TFGenericArgument(String name) {
			super(name);
		}
		
		@Override
		public MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) {
			int index = 0;
			boolean inQuotes = false;
			while (text.onChar(index)) {
				char ch = text.getChar(index);
								
				if (ch == '"') {
					inQuotes = ! inQuotes;
				} else if (ch == ' ') {
					if (! inQuotes) break;
				} else if ((ch == '\r') || (ch == '\n')) {
					break;
				}
				++index;
			}
			if (index > 0) {
				TextPiece p = text.extractPiece(index);
				return objectSupply.newString(p);
			} else {
				return new MEmpty();
			}
		}
	}

	private static final TFEmptyVerified<MToken> TF_EMPTY = new TFEmptyVerified<MToken>("commandempty", ' ');
	
	private abstract class TCommandSpec extends MCommand {
		private TokenFactory<MToken> argumentFactory;
		
		public TCommandSpec(TextPiece name, TokenFactory<MToken> argumentFactory) {
			super(name);
			this.argumentFactory = argumentFactory;
		}

		public TokenFactory<MToken> getArgumentFactory() {
			return this.argumentFactory;
		}

		public MToken tokenizeWhatFollows(Text text, ObjectSupply<MToken> objectSupply) throws SyntaxErrorException {
			TokenFactory<MToken> argumentFactory = this.getArgumentFactory();
			TFSequence<MToken> tf = new TFCommandRest(TFCommand.this.getName());
			tf.add(TFCommand.this.supply.postcondition, false);
			tf.add(TFCommand.this.supply.space, false);
			tf.add(argumentFactory, false);
			tf.add(TFCommand.this.supply.commandend, false);
			SequenceOfTokens<MToken> whatFollowsTokens = tf.tokenizeCommon(text, objectSupply);
			if (whatFollowsTokens == null) {
				this.setWhatFollows(null);
			} else {
				MSequence whatFollows = new MSequence(whatFollowsTokens);
				this.setWhatFollows(whatFollows);
			}
			return this;
		}
	}
		
	private class TBCommandSpec extends TCommandSpec {
		private TBCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.expr);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("B", "BREAK");
		}			
	}
	
	private class TCCommandSpec extends TCommandSpec {
		private TCCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.closearg);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("C", "CLOSE");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new OpenCloseUseCmdNodes.CloseCmd(postConditionNode, argumentNode);				
		}
	}
	
	private class TDCommandSpec extends TCommandSpec {
		private TDCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.doarguments);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("D", "DO");
		}			

		@Override
		public Node getNode() {
			Node postConditionNode = this.getPostConditionNode();
			Node argumentNode = this.getArgumentNode();
			if (argumentNode == null) {
				return new DoBlock(postConditionNode);
			} else {
				Do result = new Do(postConditionNode, argumentNode);
				return result;
			}
		}
	}
	
	private class TECommandSpec extends TCommandSpec {
		private TECommandSpec(TextPiece value, MTFSupply supply) {
			super(value, TF_EMPTY);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("E", "ELSE");
		}			
		
		@Override
		public Node getNode() {
			return new ElseCmd();
		}			
	}

	private class TFCommandSpec extends TCommandSpec {
		private TFCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.forarg);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("F", "FOR");
		}			

		@Override
		public Node getNode() {
			MToken argument = this.getArgument();
			if (argument == null) {
				return new ForLoop();
			} else {
				Node n0 = this.getArgumentNode(0);
				Node n2 = this.getArgumentNode(2);
				return new ForLoop(n0, n2);
			}
		}	
	}

	private class TGCommandSpec extends TCommandSpec {
		private TGCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.gotoarguments);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("G", "GOTO");
		}	
		
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new Goto(postConditionNode, argumentNode);	
		}
	}

	private class THCommandSpec extends TCommandSpec {
		private THCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.expr);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {
			MToken argument = this.getArgument();
			if (argument == null) {
				return new MNameWithMnemonic("H", "HALT");
			} else {
				return new MNameWithMnemonic("H", "HANG");
			}
		}			
	}

	private class TICommandSpec extends TCommandSpec {
		private TICommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.exprlist);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("I", "IF");
		}			

		@Override
		public Node getNode() {
			Tokens<MToken> argument = this.getArgumentTokens(2);
			if (argument != null) {
				Nodes<Node> node = NodeUtilities.getNodes(argument.toLogicalIterable(), argument.size());
				return new IfCmd(node);
			} else {
				return new IfCmd();
			}
		}			
	}

	private class TJCommandSpec extends TCommandSpec {
		private TJCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.cmdjargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("J", "JOB");
		}			
	}

	private class TKCommandSpec extends TCommandSpec {
		private TKCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.killargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("K", "KILL");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			if (argumentNode == null) {
				return new KillCmdNodes.AllKillCmd(postConditionNode);
			} else {
				return new KillCmdNodes.KillCmd(postConditionNode, argumentNode);				
			}
		}
	}

	private class TLCommandSpec extends TCommandSpec {
		private TLCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.lockargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("L", "LOCK");
		}			
	}

	private class TMCommandSpec extends TCommandSpec {
		private TMCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.mergeargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("M", "MERGE");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new MergeCmdNodes.MergeCmd(postConditionNode, argumentNode);	
		}
	}

	private class TNCommandSpec extends TCommandSpec {
		private TNCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.newargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("N", "NEW");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			if (argumentNode == null) {
				return new NewCmdNodes.AllNewCmd(postConditionNode);
			} else {
				return new NewCmdNodes.NewCmd(postConditionNode, argumentNode);				
			}
		}
	}

	private class TOCommandSpec extends TCommandSpec {
		private TOCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.cmdoargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("O", "OPEN");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new OpenCloseUseCmdNodes.OpenCmd(postConditionNode, argumentNode);				
		}
	}

	private class TQCommandSpec extends TCommandSpec {
		private TQCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.expr);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("Q", "QUIT");
		}			

		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new QuitCmd(postConditionNode, argumentNode);	
		}
	}

	private class TRCommandSpec extends TCommandSpec {
		private TRCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.cmdrargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("R", "READ");
		}			

		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new ReadCmd(postConditionNode, argumentNode);	
		}
	}

	private class TSCommandSpec extends TCommandSpec {
		private TSCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.setargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("S", "SET");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new SetCmdNodes.SetCmd(postConditionNode, argumentNode);	
		}
	}

	private class TTCCommandSpec extends TCommandSpec {
		private TTCCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, TF_EMPTY);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("TC", "TCOMMIT");
		}			
	}

	private class TTRCommandSpec extends TCommandSpec {
		private TTRCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, TF_EMPTY);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("TR", "TRESTART");
		}			
	}

	private class TTROCommandSpec extends TCommandSpec {
		private TTROCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, TF_EMPTY);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("TRO", "TROLLBACK");
		}			
	}

	private class TTSCommandSpec extends TCommandSpec {
		private TTSCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, TF_EMPTY);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("TS", "TSTART");
		}			
	}

	private class TUCommandSpec extends TCommandSpec {
		private TUCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.cmduargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("U", "USE");
		}
		
		@Override
		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new OpenCloseUseCmdNodes.UseCmd(postConditionNode, argumentNode);				
		}
	}

	private class TWCommandSpec extends TCommandSpec {
		private TWCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.writeargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("W", "WRITE");
		}			

		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new WriteCmd(postConditionNode, argumentNode);	
		}
	}

	private class TVCommandSpec extends TCommandSpec {
		private TVCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, new TFGenericArgument("vargument"));
		}
		
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("V", "VIEW");
		}			
	}

	private class TXCommandSpec extends TCommandSpec {
		private TXCommandSpec(TextPiece value, MTFSupply supply) {
			super(value, supply.xecuteargs);
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return new MNameWithMnemonic("X", "XECUTE");
		}			

		protected Node getNode(Node postConditionNode, Node argumentNode) {
			return new XecuteCmd(postConditionNode, argumentNode);	
		}
	}

	private class TGenericCommandSpec extends TCommandSpec {
		private MNameWithMnemonic mnwm;
		
		private TGenericCommandSpec(TextPiece value, MNameWithMnemonic mnwm, MTFSupply supply) {
			super(value, new TFGenericArgument("genericargument"));
			this.mnwm = mnwm;
		}
	
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {		
			return this.mnwm;
		}					
	}
	
	private static abstract class TCSFactory {
		public abstract TCommandSpec get(TextPiece name);
	}
	
	public void addCommands(final MTFSupply supply) {
		TCSFactory b = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TBCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("B", b);
		this.commandSpecs.put("BREAK", b); 	
		
		TCSFactory c = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TCCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("C", c);
		this.commandSpecs.put("CLOSE", c); 	
		
		TCSFactory d = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TDCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("D", d);
		this.commandSpecs.put("DO", d); 	
		
		TCSFactory e = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TECommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("E", e);
		this.commandSpecs.put("ELSE", e); 	

		TCSFactory f = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TFCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("F", f);
		this.commandSpecs.put("FOR", f); 	
		
		TCSFactory g = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TGCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("G", g);
		this.commandSpecs.put("GOTO", g); 	
		
		TCSFactory h = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new THCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("H", h);
		this.commandSpecs.put("HALT", h); 	
		this.commandSpecs.put("HANG", h); 
		
		TCSFactory i = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TICommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("I", i);
		this.commandSpecs.put("IF", i); 	
		
		TCSFactory j = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TJCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("J", j);
		this.commandSpecs.put("JOB", j); 	
		
		TCSFactory k = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TKCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("K", k);
		this.commandSpecs.put("KILL", k); 	
		
		TCSFactory l = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TLCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("L", l);
		this.commandSpecs.put("LOCK", l); 	
		
		TCSFactory m = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TMCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("M", m);
		this.commandSpecs.put("MERGE", m); 
		
		TCSFactory n = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TNCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("N", n);
		this.commandSpecs.put("NEW", n);		
		
		TCSFactory o = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TOCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("O", o);
		this.commandSpecs.put("OPEN", o); 	
		
		TCSFactory q = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TQCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("Q", q);
		this.commandSpecs.put("QUIT", q); 	
		
		TCSFactory r = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TRCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("R", r);
		this.commandSpecs.put("READ", r); 	
		
		TCSFactory s = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TSCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("S", s);
		this.commandSpecs.put("SET", s); 	
		
		TCSFactory tc = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TTCCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("TC", tc);
		this.commandSpecs.put("TCOMMIT", tc); 	
		
		TCSFactory tr = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TTRCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("TR", tr);
		this.commandSpecs.put("TRESTART", tr); 	
		
		TCSFactory tro = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TTROCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("TRO", tro);
		this.commandSpecs.put("TROLLBACK", tro); 	
		
		TCSFactory ts = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TTSCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("TS", ts);
		this.commandSpecs.put("TSTART", ts); 	
		
		TCSFactory u = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TUCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("U", u);
		this.commandSpecs.put("USE", u);
		
		TCSFactory v = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TVCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("V", v);
		this.commandSpecs.put("VIEW", v); 	
		
		TCSFactory w = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TWCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("W", w);
		this.commandSpecs.put("WRITE", w);	
		
		TCSFactory x = new TCSFactory() {			
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TXCommandSpec(name, supply);
			}
		};
		this.commandSpecs.put("X", x);
		this.commandSpecs.put("XECUTE", x);		
	}
	
	public void addCommand(final String cmdName, final MTFSupply supply) {
		TCSFactory generic = new TCSFactory() {		
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TGenericCommandSpec(name, new MNameWithMnemonic(cmdName, cmdName), supply);
			}
		};
		this.commandSpecs.put(cmdName, generic);
	}
	
	public void addCommand(final String mnemonic, final String cmdName, final MTFSupply supply) {
		TCSFactory generic = new TCSFactory() {		
			@Override
			public TCommandSpec get(TextPiece name) {
				return new TGenericCommandSpec(name, new MNameWithMnemonic(mnemonic, cmdName), supply);
			}
		};
		this.commandSpecs.put(mnemonic, generic);
		this.commandSpecs.put(cmdName, generic);
	}
	
	private class TFCommandRest extends TFSequence<MToken> {
		public TFCommandRest(String name) {
			super(name, 4);
		}
		
		@Override
		protected ValidateResult validateNull(int seqIndex, SequenceOfTokens<MToken> foundTokens, boolean noException) throws SyntaxErrorException {
			if (seqIndex == 3) {
				if (noException) return ValidateResult.NULL_RESULT;
				throw new SyntaxErrorException(MError.ERR_GENERAL_SYNTAX);				
			} else {
				return ValidateResult.CONTINUE;
			}
		}
	}

	private class TFCommandName extends TokenFactory<MToken> {
		public TFCommandName(String name) {
			super(name);
		}
		
		@Override
		public TCommandSpec tokenize(Text text, ObjectSupply<MToken> objectSupply) throws SyntaxErrorException {
			TextPiece cmdName = TFCommand.this.supply.ident.tokenizeCommon(text, objectSupply);
			if (cmdName != null) {
				TCSFactory tcs = TFCommand.this.commandSpecs.get(cmdName.toString().toUpperCase());
				if (tcs == null) {
					throw new SyntaxErrorException(MError.ERR_UNDEFINED_COMMAND);					
				} else {
					TCommandSpec spec = tcs.get(cmdName);
					return spec;
				}
			}
			return null;
		}
	}
	
	@Override
	public MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) {
		Text textCopy = text.getCopy();
		try {
			TFCommandName cmdSpecFactory = this.new TFCommandName("command.name");
			TCommandSpec cmdSpec = cmdSpecFactory.tokenize(text, objectSupply);
			return (cmdSpec == null) ? null : cmdSpec.tokenizeWhatFollows(text, objectSupply);
		} catch (SyntaxErrorException e) {
			int errorIndex = text.getIndex();
			int lengthToEOL = textCopy.findEOL();
			TextPiece t = textCopy.extractPiece(lengthToEOL);
			text.copyFrom(textCopy);
			return new MSyntaxError(e.getCode(), t, errorIndex);
		}
	}
}
