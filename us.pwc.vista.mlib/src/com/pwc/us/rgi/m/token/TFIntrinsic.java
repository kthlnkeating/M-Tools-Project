//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.m.token;

import java.util.HashMap;
import java.util.Map;

import com.pwc.us.rgi.m.parsetree.IntrinsicFunction;
import com.pwc.us.rgi.m.parsetree.IntrinsicVariable;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.struct.KeywordRefactorFlags;
import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.m.struct.MNameWithMnemonic;
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.parser.DelimitedListOfTokens;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.TFDelimitedList;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFIntrinsic extends TokenFactory<MToken> {		
	private static class TIVariable extends MSequence {
		public TIVariable(int length) {
			super(length);
		}
		
		public TIVariable(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			TextPiece name = this.getToken(1).toValue();
			return new IntrinsicVariable(name.toString());
		}
	}
	
	private static class TIFunction extends MSequence {
		private TIFunction(int length) {
			super(length);
		}
		
		private TIFunction(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			TextPiece name = this.getToken(1).toValue();
			MToken arguments = this.getToken(4);	
			return new IntrinsicFunction(name.toString(), arguments == null ? null : arguments.getNode());
		}
	}
	
	private static abstract class FunctionInfo extends MNameWithMnemonic {
		public FunctionInfo(String mnemonic, String name) {
			super(mnemonic, name);
		}

		public abstract MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) throws SyntaxErrorException;
	}
	
	private static class MultiArgumentFunctionInfo extends FunctionInfo {
		private TFDelimitedList<MToken> argumentFactory;
		private int minNumArguments;
		private int maxNumArguments;
		
		public MultiArgumentFunctionInfo(String mnemonic, String name, TFDelimitedList<MToken> argumentFactory, int minNumArguments, int maxNumArguments) {
			super(mnemonic, name);
			this.argumentFactory = argumentFactory;
			this.minNumArguments = minNumArguments;
			this.maxNumArguments = maxNumArguments;
		}		

		public MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) throws SyntaxErrorException {
			DelimitedListOfTokens<MToken> args = this.argumentFactory.tokenizeCommon(text, objectSupply);
			if ((args == null) && (this.minNumArguments > 0)) {
				throw new SyntaxErrorException(MError.ERR_GENERAL_SYNTAX);			
			}
			if (args != null) {
				int length = args.size();
				if ((length < this.minNumArguments) || (length > this.maxNumArguments)) {
					throw new SyntaxErrorException(MError.ERR_WRONGARG_INTRINSIC_FUNCTION);								
				}
			}
			return this.argumentFactory.convert(objectSupply, args);			
		}		
	}

	private static class SingleArgumentFunctionInfo extends FunctionInfo {
		private TokenFactory<MToken> argumentFactory;
		private boolean emptyAllowed;

		public SingleArgumentFunctionInfo(String mnemonic, String name, TokenFactory<MToken> argumentFactory, boolean emptyAllowed) {
			super(mnemonic, name);
			this.argumentFactory = argumentFactory;
			this.emptyAllowed = emptyAllowed;
		}
		
		public MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) throws SyntaxErrorException {
			MToken args = this.argumentFactory.tokenize(text, objectSupply);
			if ((args == null) && (! this.emptyAllowed)) {
				throw new SyntaxErrorException(MError.ERR_GENERAL_SYNTAX);			
			}
			return args;
		}
	}
	
	private Map<String, MNameWithMnemonic> variables = new HashMap<String, MNameWithMnemonic>();
	private Map<String, FunctionInfo> functions;
	
	private MTFSupply supply;
	
	private class TIVariableName extends MKeyWord {
		private static final long serialVersionUID = 1L;

		public TIVariableName(TextPiece p) {
			super(p);
		}
		
		@Override
		public KeywordRefactorFlags getKeywordFlags(MRefactorSettings settings) {
			return settings.instrinsicNameSettings;
		}

		@Override
		public MNameWithMnemonic getNameWithMnemonic(String name) {
			return TFIntrinsic.this.variables.get(name);			
		}
	}
 	
	private class TIFunctionName extends MKeyWord {
		private static final long serialVersionUID = 1L;

		public TIFunctionName(TextPiece p) {
			super(p);
		}
		
		@Override
		public KeywordRefactorFlags getKeywordFlags(MRefactorSettings settings) {
			return settings.instrinsicNameSettings;
		}

		@Override
		public MNameWithMnemonic getNameWithMnemonic(String name) {
			return TFIntrinsic.this.functions.get(name);			
		}
	}
 	
	public TFIntrinsic(String name, MTFSupply supply) {
		super(name);
		this.supply = supply;
	}
	
	public void addVariable(String name) {
		String nameUpperCase = name.toUpperCase();
		MNameWithMnemonic.update(this.variables, nameUpperCase, nameUpperCase); 	
	}
	
	public void addVariable(String mnemonic, String name) {
		MNameWithMnemonic.update(this.variables, mnemonic.toUpperCase(), name.toUpperCase()); 	
	}
	
	public FunctionInfo addFunction(FunctionInfo functionInfo, String mnemonic, String name) {
		if (this.functions == null) {
			this.functions = new HashMap<String, FunctionInfo>();	
		}
		this.functions.put(mnemonic, functionInfo);
		if (! mnemonic.equals(name)) {
			this.functions.put(name, functionInfo);
		}
		return functionInfo;
	}
		
	public FunctionInfo addFunction(TokenFactory<MToken> argumentFactory, String mnemonic, String name, int minNumArguments, int maxNumArguments) {
		FunctionInfo fi = new SingleArgumentFunctionInfo(mnemonic, name, argumentFactory, false);
		this.addFunction(fi, mnemonic, name);
		return fi;
	}
		
	public FunctionInfo addFunction(TFDelimitedList<MToken> argumentFactory, String mnemonic, String name, int minNumArguments, int maxNumArguments) {
		FunctionInfo fi = new MultiArgumentFunctionInfo(mnemonic, name, argumentFactory, minNumArguments, maxNumArguments);
		this.addFunction(fi, mnemonic, name);
		return fi;
	}
		
	public FunctionInfo addFunction(TokenFactory<MToken> argumentFactory, String name, int minNumArguments, int maxNumArguments) {
		return this.addFunction(argumentFactory, name, name, minNumArguments, maxNumArguments);
	}
		
	public FunctionInfo addFunction(TokenFactory<MToken> argumentFactory, String name) {
		return this.addFunction(argumentFactory, name, 1, Integer.MAX_VALUE);
	}

	public FunctionInfo addFunction(TFDelimitedList<MToken> argumentFactory, String name, int minNumArguments, int maxNumArguments) {
		return this.addFunction(argumentFactory, name, name, minNumArguments, maxNumArguments);
	}
		
	public FunctionInfo addFunction(TFDelimitedList<MToken> argumentFactory, String name) {
		return this.addFunction(argumentFactory, name, 1, Integer.MAX_VALUE);
	}

	public void addFunctionTailTokens(String name, Text text, ObjectSupply<MToken> objectSupply, TIFunction result) throws SyntaxErrorException {
		FunctionInfo info = TFIntrinsic.this.functions.get(name);
		if (info == null) {
			throw new SyntaxErrorException(MError.ERR_UNKNOWN_INTRINSIC_FUNCTION);
		}
		MToken args = info.tokenize(text, objectSupply);
		MToken rpar = this.supply.rpar.tokenize(text, objectSupply);
		if (rpar == null) {
			throw new SyntaxErrorException(MError.ERR_GENERAL_SYNTAX);						
		}
		result.addToken(args);
		result.addToken(rpar);
	}

	private TIVariable getVariableToken(String name, SequenceOfTokens<MToken> nameTokens) throws SyntaxErrorException {
		if (this.variables.containsKey(name)) {
			return new TIVariable(nameTokens);
		} else {
			throw new SyntaxErrorException(MError.ERR_UNKNOWN_INTRINSIC_VARIABLE);
		}						
	}
	
	@Override
	public MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) throws SyntaxErrorException {
		SequenceOfTokens<MToken> nameTokens = this.supply.intrinsicname.tokenizeCommon(text, objectSupply);
		if (nameTokens != null) {
			MToken lParToken = this.supply.lpar.tokenize(text, objectSupply);
			TextPiece nameValue = nameTokens.getToken(1).toValue();
			String name = nameValue.toString().toUpperCase();
			if (lParToken == null) {
				TIVariableName t = this.new TIVariableName(nameValue);
				nameTokens.setToken(1, t);
				return this.getVariableToken(name, nameTokens);
			} else {
				TIFunction result = new TIFunction(6);
				result.addToken(nameTokens.getToken(0));
				result.addToken(this.new TIFunctionName(nameValue));
				result.addToken(nameTokens.getToken(2));
				result.addToken(lParToken);
				this.addFunctionTailTokens(name, text, objectSupply, result);				
				return result;
			}		
		} else {
			return null;
		}
	}	
}
