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

package com.pwc.us.rgi.parsergen.ruledef;

public interface RuleDefinitionVisitor {
	void visitCharSymbol(CharSymbol charSymbol, String name, RuleSupplyFlag flag);
	void visitConstSymbol(ConstSymbol constSymbol, String name, RuleSupplyFlag flag);
	void visitSymbol(Symbol symbol, String name, RuleSupplyFlag flag);
	void visitCharSymbolList(CharSymbol charSymbol, String name, RuleSupplyFlag flag);
	void visitSymbolList(RuleSupply ruleSupply, String name, RuleSupplyFlag flag);
	void visitDelimitedSymbolList(RuleSupply element, RuleSupply delimiter, String name, RuleSupplyFlag flag);
	void visitEnclosedDelimitedSymbolList(SymbolList symbolList, String name, RuleSupplyFlag flag);
	void visitChoiceOfSymbols(RuleSupplies choiceOfSymbols, String name, RuleSupplyFlag flag);
	void visitSymbolSequence(RuleSupplies sequence, String name, RuleSupplyFlag flag);
}
