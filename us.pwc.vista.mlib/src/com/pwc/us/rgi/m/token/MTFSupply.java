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

import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.parser.TFDelimitedList;
import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.TFString;
import com.pwc.us.rgi.parser.TFSyntaxError;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ParseException;
import com.pwc.us.rgi.parsergen.SequenceTokenType;
import com.pwc.us.rgi.parsergen.StringTokenType;
import com.pwc.us.rgi.parsergen.TokenType;
import com.pwc.us.rgi.parsergen.rulebased.Rule;
import com.pwc.us.rgi.parsergen.rulebased.RuleBasedParserGenerator;

public class MTFSupply {
	@Rule("'('")
	public TokenFactory<MToken> lpar;
	@Rule("')'")
	public TokenFactory<MToken> rpar;
	@Rule("' '")
	public TokenFactory<MToken> space;
	
	@Rule("{'a'...'x' + 'A'...'X'}")
	public TokenFactory<MToken> paton;
	@Rule("'y' + 'Y', {'a'...'z' + 'A'...'Z' - 'y' - 'Y'}, 'y' + 'Y'")
	public TokenFactory<MToken> patony;
	@Rule("'z' + 'Z', {'a'...'y' + 'A'...'Y'}, 'z' + 'Z'")
	public TokenFactory<MToken> patonz;
	@Rule("paton | patony | patonz")
	public TokenFactory<MToken> patons;
	@Rule("['\\''], patons")
	public TokenFactory<MToken> patcode;	
	@Rule("[intlit], ['.'], [intlit]")
	public TokenFactory<MToken> repcount;
	@Rule("alternation | patcode | strlit")
	public TokenFactory<MToken> patatomre;
	@Rule("repcount, patatomre")
	public TokenFactory<MToken> patatom;	
	@Rule("{patatoms:',':'(':')'}")
	public TokenFactory<MToken> alternation;	
	@Rule("{patatom}")	
	public TokenFactory<MToken> patatoms;
	@Rule("indirection | patatoms")
	public TokenFactory<MToken> pattern;
	
	@SequenceTokenType(StringTokens.MName.class)
	@Rule("'%' + 'a'...'z' + 'A'...'Z', [{'a'...'z' + 'A'...'Z' + '0'...'9'}]")
	public TokenFactory<MToken> name;	
	
	@StringTokenType(StringTokens.MIdent.class)
	@Rule("{'a'...'z' + 'A'...'Z'}")
	public TFString<MToken> ident;
	
	@StringTokenType(MIntLit.class)
	@Rule("{'0'...'9'}")
	public TokenFactory<MToken> intlit;
	
	@Rule("name | intlit")
	public TokenFactory<MToken> label;
	
	@Rule("'^', [environment], name")
	public TokenFactory<MToken> envroutine;
	
	@SequenceTokenType(MNumLit.class)
	@Rule("'.', intlit, ['E', ['+' | '-'], intlit]")
	public TokenFactory<MToken> numlita;
	@SequenceTokenType(MNumLit.class)
	@Rule("intlit, ['.', intlit], ['E', ['+' | '-'], intlit]")
	public TokenFactory<MToken> numlitb;
	
	@Rule("numlita | numlitb")
	public TokenFactory<MToken> numlit;
	
	public TFOperator operator = new TFOperator("operator");
	public TokenFactory<MToken> error = new TFSyntaxError<MToken>("error", MError.ERR_GENERAL_SYNTAX);
	
	@Rule("'+' + '-' + '\\''")
	public TokenFactory<MToken> unaryop;
	
	@SequenceTokenType(MStringLiteral.class)
	@Rule("('\"', [{-'\\r' - '\\n' - '\"'}], '\"'), [strlit]")	
	public TokenFactory<MToken> strlit;
	
	@Rule("('|', expr, '|') | {expratom:',':'[':']'}")
	public TokenFactory<MToken> environment;
	
	@Rule("'?', pattern")
	public TokenFactory<MToken> exprtaila;
	@Rule("\"'?\", pattern")
	public TokenFactory<MToken> exprtailb;
	@Rule("operator, expratom")
	public TokenFactory<MToken> exprtailc;
	@Rule("exprtaila | exprtailb | exprtailc")
	public TokenFactory<MToken> exprtails;
	@Rule("{exprtails}")
	public TokenFactory<MToken> exprtail;

	@Rule("{expr:','}")
	public TFDelimitedList<MToken> exprlist;
	
	@Rule("{expr:',':'(':')'}")
	public TokenFactory<MToken> exprlistinparan;

	@Rule("'(', expr, ')'")
	public TokenFactory<MToken> exprinpar;
	
	@SequenceTokenType(MIndirection.class)
	@Rule("'@', expratom, [\"@(\", exprlist, ')']")
	public TokenFactory<MToken> indirection;
	@SequenceTokenType(MIndirection.class)
	@Rule("'@', expratom")
	public TokenFactory<MToken> rindirection;
		
	@Rule("lvn | gvnall | indirection")
	public TokenFactory<MToken> glvn;
	
	@SequenceTokenType(MGlobal.class)
	@Rule("'^', ([environment], name, [exprlistinparan])")
	public TokenFactory<MToken> gvn;
	
	@SequenceTokenType(MSsvn.class)
	@Rule("\"^$\", ident, exprlistinparan")
	public TokenFactory<MToken> gvnssvn;

	@Rule("\"$$\", extrinsicarg")
	public TokenFactory<MToken> extrinsic;
	
	@Rule("unaryop, expratom")
	public TokenFactory<MToken> unaryexpritem;
	
	@SequenceTokenType(MNakedGlobal.class)
	@Rule("'^', exprlistinparan")
	public TokenFactory<MToken> gvnnaked;
	
	@Rule("expr, ':', expr")
	public TokenFactory<MToken> dselectarge;
	@Rule("{dselectarge:','}")
	public TFDelimitedList<MToken> dselectarg;
	
	@Rule("gvnssvn | gvnnaked | gvn")
	public TokenFactory<MToken> gvnall;

	@Rule("extrinsic | external | intrinsic")
	public TokenFactory<MToken> expritemd;
	
	@Rule("strlit | expritemd | unaryexpritem | numlit | exprinpar")
	public TokenFactory<MToken> expritem;
	
	@SequenceTokenType(MLocalByRef.class)
	@Rule("'.', name")
	public TokenFactory<MToken> actualda;
	@Rule("'.', indirection")
	public TokenFactory<MToken> actualdb;
	@Rule("numlita | actualda | actualdb | expr")
	public TokenFactory<MToken> actual;
	
	@Rule("glvn | expritem")
	public TokenFactory<MToken> expratom;

	@SequenceTokenType(MLocal.class)
	@Rule("name, [exprlistinparan]")
	public TokenFactory<MToken> lvn;
	
	@SequenceTokenType(MExpression.class)
	@Rule("expratom, [exprtail]")
	public TokenFactory<MToken> expr;
	
	@SequenceTokenType(MActualList.class)
	@Rule("{actual:',':'(':')':1:1}")
	public TokenFactory<MToken> actuallist;

	@Rule("'=', expr")
	public TokenFactory<MToken> deviceparama;
	@Rule("expr, [deviceparama]")
	public TokenFactory<MToken> deviceparam;
	@Rule("{deviceparam:':':'(':')':1}")
	public TokenFactory<MToken> deviceparamsi;
	@Rule("deviceparamsi | deviceparam")
	public TokenFactory<MToken> deviceparams;
	
	@Rule("exprlistinparan | expr")
	public TokenFactory<MToken> exprorinlist;
	@SequenceTokenType(OpenCloseUseCmdTokens.MDeviceParameters.class)	
	@Rule("':', [deviceparams], [':'], [expr], [':'], [exprorinlist]")
	public TokenFactory<MToken> cmdoargtail;
	@SequenceTokenType(OpenCloseUseCmdTokens.MAtomicOpenCmd.class)	
	@Rule("expr, [cmdoargtail]")
	public TokenFactory<MToken> cmdoargbasic;
	@Rule("indirection | cmdoargbasic")
	public TokenFactory<MToken> cmdoarg;
	@Rule("{cmdoarg:','}")
	public TokenFactory<MToken> cmdoargs;
		
	@Rule("indirection | label")
	public TokenFactory<MToken> linetagname;
	@Rule("'+', expr")
	public TokenFactory<MToken> lineoffset;
	@Rule("[linetagname], [lineoffset]")
	public TokenFactory<MToken> tagspec;
	@Rule("[environment], name")
	public TokenFactory<MToken> envname;
	@Rule("rindirection | envname")
	public TokenFactory<MToken> routinespeccc;
	@Rule("'^', routinespeccc")
	public TokenFactory<MToken> routinespec;
	@Rule("[tagspec], [routinespec]")
	public TokenFactory<MToken> cmdgargmain;
	
	@Rule("'#', expr")
	public TokenFactory<MToken> readcount;

	@Rule("'?', expr")
	public TokenFactory<MToken> tabformat;
	@Rule("{'!' + '#'}, [tabformat]")
	public TokenFactory<MToken> xtabformat;
	@Rule("tabformat | xtabformat")
	public TokenFactory<MToken> format;
	
	@Rule("glvn, [readcount], [timeout]")
	public TokenFactory<MToken> cmdrargdef;	
	@Rule("'*', [glvn], [timeout]")
	public TokenFactory<MToken> cmdrargast;	
	@Rule("indirection, [timeout]")
	public TokenFactory<MToken> cmdrargat;	
	@Rule("format | strlit | cmdrargast | cmdrargat | cmdrargdef")
	public TokenFactory<MToken> cmdrarg;
	@Rule("{cmdrarg:','}")
	public TokenFactory<MToken> cmdrargs;
	
	@SequenceTokenType(BasicTokens.MPostCondition.class)
	@Rule("':', expr")
	public TokenFactory<MToken> postcondition;
	@Rule("'*', expr")
	public TokenFactory<MToken> asterixexpr;
	@Rule("':', expr")
	public TokenFactory<MToken> timeout;
	
	@Rule("{expr:':':'(':')':1}")
	public TokenFactory<MToken> usedeviceparamlist;
	@TokenType(OpenCloseUseCmdTokens.MUseDeviceParameters.class)	
	@Rule("usedeviceparamlist | expr")
	public TokenFactory<MToken> usedeviceparam;
	@Rule("':', [usedeviceparam]")
	public TokenFactory<MToken> colonusedeviceparam;
	@SequenceTokenType(OpenCloseUseCmdTokens.MAtomicUseCmd.class)	
	@Rule("expr, [colonusedeviceparam], [colonusedeviceparam]")
	public TokenFactory<MToken> cmduarg;
	@Rule("{cmduarg:','}")
	public TokenFactory<MToken> cmduargs;
	
	@Rule("label, [lineoffset]")
	public TokenFactory<MToken> labelwoffset;
	@Rule("rindirection | labelwoffset")
	public TokenFactory<MToken> entryspeca;
	@Rule("[entryspeca], [routinespec], [actuallist], [colonusedeviceparam], [timeout]")
	public TokenFactory<MToken> cmdjarg;
	@Rule("'^', [usedeviceparam]")
	public TokenFactory<MToken> jobparams;
	@Rule("{cmdjarg:','}")
	public TokenFactory<MToken> cmdjargs;
	
	
	
	@SequenceTokenType(MExtrinsic.class)	
	@Rule("indfanoutlabel, ['^', ((envfanoutroutine | fanoutroutine) , [actuallist]) | indfanoutroutine]")
	public TokenFactory<MToken> indexargument;

	@SequenceTokenType(MExtrinsic.class)	
	@Rule("fanoutlabel, actuallist")
	public TokenFactory<MToken> labelcallexargument;

	@SequenceTokenType(MExtrinsic.class)	
	@Rule("fanoutlabel, ['^', ((envfanoutroutine | fanoutroutine) , [actuallist]) | indfanoutroutine]")
	public TokenFactory<MToken> exargument;
	
	@SequenceTokenType(MExtrinsic.class)	
	@Rule("'^', noindroutinepostcaret, [actuallist]")
	public TokenFactory<MToken> onlyrsimpleexargument;
	
	@SequenceTokenType(MExtrinsic.class)	
	@Rule("'^', indfanoutroutineb")
	public TokenFactory<MToken> onlyrexargument;
	
	@Rule("indexargument | labelcallexargument | exargument | onlyrsimpleexargument | onlyrexargument")
	public TokenFactory<MToken> extrinsicarg;
	
	
	@SequenceTokenType(MExtDoArgument.class)
	@Rule("'&', name, ['.', name], ['^', name], [actuallist], [postcondition]")
	public TokenFactory<MToken> extdoargument;

	@TokenType(BasicTokens.MTFanoutLabelA.class)
	@Rule("name")
	public TokenFactory<MToken> fanoutlabela;
	@TokenType(BasicTokens.MTFanoutLabelB.class)
	@Rule("intlit")
	public TokenFactory<MToken> fanoutlabelb;
	@Rule("fanoutlabela | fanoutlabelb")
	public TokenFactory<MToken> fanoutlabel;
	@TokenType(BasicTokens.MTIndirectFanoutLabel.class)
	@Rule("rindirection")
	public TokenFactory<MToken> indfanoutlabel;
	@SequenceTokenType(BasicTokens.MTEnvironmentFanoutRoutine.class)
	@Rule("environment, name")
	public TokenFactory<MToken> envfanoutroutine;	
	@TokenType(BasicTokens.MTFanoutRoutine.class)
	@Rule("name")
	public TokenFactory<MToken> fanoutroutine;
	@TokenType(BasicTokens.MTIndirectFanoutRoutine.class)
	@Rule("rindirection")
	public TokenFactory<MToken> indfanoutroutine;
	@TokenType(BasicTokens.MTIndirectFanoutRoutine.class)
	@Rule("indirection")
	public TokenFactory<MToken> indfanoutroutineb;
	@Rule("envfanoutroutine | fanoutroutine")
	public TokenFactory<MToken> noindroutinepostcaret;
	
	@Rule("envfanoutroutine | fanoutroutine | indfanoutroutine")
	public TokenFactory<MToken> goroutinepostcaret;
	
	@Rule("'^', goroutinepostcaret")
	public TokenFactory<MToken> goroutineref;
	
	@SequenceTokenType(MGotoArgument.class)	
	@Rule("indfanoutlabel, [dolineoffset], [goroutineref], [postcondition]")
	public TokenFactory<MToken> indgoargument;

	@SequenceTokenType(MGotoArgument.class)	
	@Rule("fanoutlabel, dolineoffset, [goroutineref], [postcondition]")
	public TokenFactory<MToken> offsetgoargument;

	@SequenceTokenType(MGotoArgument.class)	
	@Rule("fanoutlabel, ['^', envfanoutroutine | fanoutroutine | indfanoutroutine], [postcondition]")
	public TokenFactory<MToken> goargument;
	
	@SequenceTokenType(MGotoArgument.class)	
	@Rule("'^', indfanoutroutineb, [postcondition]")
	public TokenFactory<MToken> onlyrgoargument;
	
	@SequenceTokenType(MGotoArgument.class)	
	@Rule("'^', noindroutinepostcaret, [postcondition]")
	public TokenFactory<MToken> onlyrsimplegoargument;
	
	@Rule("indgoargument | offsetgoargument | goargument | onlyrsimplegoargument | onlyrgoargument")
	public TokenFactory<MToken> goargumentall;
	
	@Rule("{goargumentall:','}")
	public TokenFactory<MToken> gotoarguments;	
	
	@SequenceTokenType(MDoArgument.class)	
	@Rule("indfanoutlabel, [dolineoffset], [doroutineref], [postcondition]")
	public TokenFactory<MToken> inddoargument;

	@SequenceTokenType(MDoArgument.class)	
	@Rule("fanoutlabel, dolineoffset, [doroutineref], [postcondition]")
	public TokenFactory<MToken> offsetdoargument;

	@SequenceTokenType(MDoArgument.class)	
	@Rule("fanoutlabel, actuallist, [postcondition]")
	public TokenFactory<MToken> labelcalldoargument;

	@SequenceTokenType(MDoArgument.class)	
	@Rule("fanoutlabel, ['^', ((envfanoutroutine | fanoutroutine) , [actuallist]) | indfanoutroutine], [postcondition]")
	public TokenFactory<MToken> doargument;
	
	@SequenceTokenType(MDoArgument.class)	
	@Rule("'^', noindroutinepostcaret, [actuallist], [postcondition]")
	public TokenFactory<MToken> onlyrsimpledoargument;
	
	@SequenceTokenType(MDoArgument.class)	
	@Rule("'^', indfanoutroutineb, [postcondition]")
	public TokenFactory<MToken> onlyrdoargument;
	
	@Rule("extdoargument | inddoargument | offsetdoargument | labelcalldoargument | doargument | onlyrsimpledoargument | onlyrdoargument")
	public TokenFactory<MToken> doargumentall;
	
	@Rule("{doargumentall:','}")
	public TokenFactory<MToken> doarguments;	
	
	@Rule("envfanoutroutine | fanoutroutine | indfanoutroutine")
	public TokenFactory<MToken> doroutinepostcaret;
	
	@Rule("'^', doroutinepostcaret")
	public TokenFactory<MToken> doroutineref;
	
	@SequenceTokenType(MLineOffset.class)
	@Rule("'+', expr")
	public TokenFactory<MToken> dolineoffset;

	@Rule("intrinsic | glvn")
	public TokenFactory<MToken> setlhsbasic;
	@Rule("{setlhsbasic:',':'(':')'}")
	public TokenFactory<MToken> setlhsbasics;	
	@Rule("expr")
	public TokenFactory<MToken> setrhs;
	@SequenceTokenType(SetCmdTokens.MSingleAtomicSetCmd.class)
	@Rule("setlhsbasic, '=', setrhs")
	public TokenFactory<MToken> setargsingle;
	@SequenceTokenType(SetCmdTokens.MMultiAtomicSetCmd.class)
	@Rule("setlhsbasics, '=', setrhs")
	public TokenFactory<MToken> setargmulti;
	@SequenceTokenType(SetCmdTokens.MSingleAtomicSetCmd.class)
	@Rule("indirection, ['='], [setrhs]")
	public TokenFactory<MToken> setargindirect;
	@Rule("setargindirect | setargmulti | setargsingle")
	public TokenFactory<MToken> setarg;
	@Rule("{setarg:','}")
	public TokenFactory<MToken> setargs;
	
	@SequenceTokenType(OpenCloseUseCmdTokens.MDeviceParameters.class)	
	@Rule("':', deviceparams")
	public TokenFactory<MToken> closeargdp;
	@SequenceTokenType(OpenCloseUseCmdTokens.MAtomicCloseCmd.class)		
	@Rule("expr, [closeargdp]")
	public TokenFactory<MToken> closeargdirect;
	@Rule("indirection | closeargdirect")
	public TokenFactory<MToken> closearg;
	@Rule("{closearg:','}")
	public TokenFactory<MToken> closeargs;
	
	@SequenceTokenType(MForCmdEQRHS.class)
	@Rule("expr, [':', expr], [':', expr]")
	public TokenFactory<MToken> forrhs;
	@Rule("{forrhs:','}")
	public TokenFactory<MToken> forrhss;
	@Rule("lvn, '=', forrhss")
	public TokenFactory<MToken> forarg;
	
	@Rule("gvn | indirection | lvn")
	public TokenFactory<MToken> lockeesingle;
	@Rule("lockeesingle | {lockee:',':'(':')'}")
	public TokenFactory<MToken> lockee;
	@Rule("['+' + '-'], lockee, [timeout]")
	public TokenFactory<MToken> lockarg;
	@Rule("{lockarg:','}")
	public TokenFactory<MToken> lockargs;
	
	@TokenType(NewCmdTokens.MNewedLocal.class)
	@Rule("name")
	public TokenFactory<MToken> newedlocal;
	@Rule("rindirection | newedlocal")
	public TokenFactory<MToken> exclusivenewsingle;
	@SequenceTokenType(NewCmdTokens.MExclusiveAtomicNewCmd.class)	
	@Rule("{exclusivenewsingle:',':'(':')'}")
	public TokenFactory<MToken> exclusivenew;
	@TokenType(NewCmdTokens.MAtomicNewCmd.class)	
	@Rule("rindirection | intrinsicname | newedlocal")
	public TokenFactory<MToken> normalnew;	
	@Rule("exclusivenew | normalnew")
	public TokenFactory<MToken> newarg;
	@Rule("{newarg:','}")
	public TokenFactory<MToken> newargs;
		
	@TokenType(KillCmdTokens.MKilledLocal.class)
	@Rule("name")
	public TokenFactory<MToken> killedlocal;
	@Rule("rindirection | killedlocal")
	public TokenFactory<MToken> exclusivekillsingle;
	@SequenceTokenType(KillCmdTokens.MExclusiveAtomicKillCmd.class)	
	@Rule("{exclusivekillsingle:',':'(':')'}")
	public TokenFactory<MToken> exclusivekill;
	@TokenType(KillCmdTokens.MAtomicKillCmd.class)	
	@Rule("glvn")
	public TokenFactory<MToken> normalkill;		
	@Rule("exclusivekill | normalkill")
	public TokenFactory<MToken> killarg;
	@Rule("{killarg:','}")
	public TokenFactory<MToken> killargs;

	@SequenceTokenType(MergeCmdTokens.MAtomicMergeCmd.class)
	@Rule("glvn, '=', glvn")
	public TokenFactory<MToken> mergeargdirect;
	@SequenceTokenType(MergeCmdTokens.MIndirectAtomicMergeCmd.class)
	@Rule("indirection, ['=', glvn]")
	public TokenFactory<MToken> mergeargindirect;
	@Rule("mergeargindirect | mergeargdirect")
	public TokenFactory<MToken> mergearg;
	@Rule("{mergearg:','}")
	public TokenFactory<MToken> mergeargs;
	
	@Rule("'.', name")
	public TokenFactory<MToken> dname;
	@Rule("'^', name")
	public TokenFactory<MToken> cname;
	@Rule("name, [dname], [cname]")
	public TokenFactory<MToken> ampersandtail;
	@Rule("\"$&\", [ampersandtail], [actuallist]")
	public TokenFactory<MToken> external;

	@Rule("',', expr")
	public TokenFactory<MToken> dorderarga;
	@Rule("glvn, [dorderarga]")
	public TokenFactory<MToken> dorderarg;
	
	@Rule("indirection | expr")
	public TokenFactory<MToken> xecuteargmain;
	@Rule("xecuteargmain, [postcondition]")
	public TokenFactory<MToken> xecutearg;
	@Rule("{xecutearg:','}")
	public TokenFactory<MToken> xecuteargs;

	@Rule("'/', name, actuallist")
	public TokenFactory<MToken> writeargslash;
	@Rule("format | writeargslash | asterixexpr | indirection | expr")
	public TokenFactory<MToken> writearg;
	@Rule("{writearg:','}")
	public TokenFactory<MToken> writeargs;
	
	@Rule("{name:',':'(':')':0:1}")
	public TokenFactory<MToken> lineformal;
		
	@Rule("'$', ident")
	public TFSequence<MToken> intrinsicname;

	@Rule("{' '} | comment | end")
	public TokenFactory<MToken> commandend;
	
	public TFCommand command = new TFCommand("command", this);
	
	@SequenceTokenType(MComment.class)
	@Rule("';', [{- '\\r' - '\\n'}]")
	public TokenFactory<MToken> comment;
	
	@Rule("{command | comment | error}")
	public TokenFactory<MToken> commandorcommentlist;
	
	@Rule("{' ' + '\\t'}")
	public TokenFactory<MToken> ls;
	
	@Rule("{' ' + '.'}")
	public TokenFactory<MToken> level;
	
	@SequenceTokenType(MLine.class)
	@Rule("[label], [lineformal], [ls], [level], [commandorcommentlist]")
	public TokenFactory<MToken> line;
	
	public TFIntrinsic intrinsic = new TFIntrinsic("intrinsic", this);
	
	protected void initialize() {
		this.intrinsic.addVariable("D", "DEVICE"); 	
		this.intrinsic.addVariable("EC", "ECODE"); 	
		this.intrinsic.addVariable("ES", "ESTACK"); 	
		this.intrinsic.addVariable("ET", "ETRAP"); 	
		this.intrinsic.addVariable("H", "HOROLOG"); 	
		this.intrinsic.addVariable("I", "IO"); 	
		this.intrinsic.addVariable("J", "JOB"); 	
		this.intrinsic.addVariable("K", "KEY"); 	
		this.intrinsic.addVariable("PD", "PDISPLAY"); 	
		this.intrinsic.addVariable("P", "PRINCIPAL"); 	
		this.intrinsic.addVariable("Q", "QUIT"); 	
		this.intrinsic.addVariable("S", "STORAGE"); 	
		this.intrinsic.addVariable("ST", "STACK"); 	
		this.intrinsic.addVariable("SY", "SYSTEM"); 	
		this.intrinsic.addVariable("T", "TEST"); 	
		this.intrinsic.addVariable("X", "X"); 	
		this.intrinsic.addVariable("Y", "Y"); 	

		this.intrinsic.addFunction(this.exprlist, "A", "ASCII", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "C", "CHAR", 1, 999); 	
		this.intrinsic.addFunction(this.exprlist, "D", "DATA", 1, 1); 	
		this.intrinsic.addFunction(this.exprlist, "E", "EXTRACT", 1, 3); 	
		this.intrinsic.addFunction(this.exprlist, "F", "FIND", 2, 3); 	
		this.intrinsic.addFunction(this.exprlist, "G", "GET", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "I", "INCREMENT", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "J", "JUSTIFY", 2, 3); 	
		this.intrinsic.addFunction(this.exprlist, "L", "LENGTH", 1, 2); 		
		this.intrinsic.addFunction(this.dorderarg, "O", "ORDER", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "P", "PIECE", 2, 4); 	
		this.intrinsic.addFunction(this.exprlist, "Q", "QUERY", 1, 1); 	
		this.intrinsic.addFunction(this.exprlist, "R", "RANDOM", 1, 1); 	
		this.intrinsic.addFunction(this.exprlist, "RE", "REVERSE", 1, 1);		
		this.intrinsic.addFunction(this.dselectarg, "S", "SELECT", 1, 999);
		this.intrinsic.addFunction(this.cmdgargmain, "T", "TEXT", 1, 1); 
		this.intrinsic.addFunction(this.exprlist, "V", "VIEW", 1, 999); 	
		this.intrinsic.addFunction(this.exprlist, "FN", "FNUMBER", 2, 3); 	
		this.intrinsic.addFunction(this.exprlist, "N", "NEXT", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "NA", "NAME", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "Q", "QUERY", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "QL", "QLENGTH", 1, 2); 	
		this.intrinsic.addFunction(this.exprlist, "QS", "QSUBSCRIPT", 1, 3);
		this.intrinsic.addFunction(this.exprlist, "ST", "STACK", 1, 2);
		this.intrinsic.addFunction(this.exprlist, "TR", "TRANSLATE", 1, 3);
		this.intrinsic.addFunction(this.exprlist, "WFONT", 4, 4);
		this.intrinsic.addFunction(this.exprlist, "WTFIT", 6, 6);
		this.intrinsic.addFunction(this.exprlist, "WTWIDTH", 5, 5);

		this.intrinsic.addVariable("ZA");
		this.intrinsic.addVariable("ZB");
		this.intrinsic.addVariable("ZC");
		this.intrinsic.addVariable("ZE");
		this.intrinsic.addVariable("ZH");
		this.intrinsic.addVariable("ZJ");
		this.intrinsic.addVariable("ZJOB");	
		this.intrinsic.addVariable("ZR");
		this.intrinsic.addVariable("ZT");
		this.intrinsic.addVariable("ZV");
		this.intrinsic.addVariable("ZIO");	
		this.intrinsic.addVariable("ZIOS");	
		this.intrinsic.addVariable("ZVER");
		this.intrinsic.addVariable("ZEOF");
		this.intrinsic.addVariable("ZNSPACE");
		this.intrinsic.addVariable("ZINTERRUPT");
		this.intrinsic.addVariable("ZRO");
		this.intrinsic.addVariable("R");
		this.intrinsic.addVariable("EREF");
		this.intrinsic.addVariable("ZDIR");
		this.intrinsic.addVariable("ZS");
		this.intrinsic.addVariable("ZROUTINES");
		this.intrinsic.addVariable("ZGBLDIR");
		this.intrinsic.addVariable("ZN");
		this.intrinsic.addVariable("ZSTATUS");
		this.intrinsic.addVariable("REFERENCE");
		this.intrinsic.addVariable("ETRAP");
		this.intrinsic.addVariable("ZTIMESTAMP");
		this.intrinsic.addVariable("ZERROR");
		this.intrinsic.addVariable("ZCMDLINE");
		this.intrinsic.addVariable("ZPOSITION");
		this.intrinsic.addFunction(this.exprlist, "ZBITGET");
		this.intrinsic.addFunction(this.exprlist, "ZBN");
		this.intrinsic.addFunction(this.exprlist, "ZC");
		this.intrinsic.addFunction(this.exprlist, "ZF");
		this.intrinsic.addFunction(this.exprlist, "ZJ");
		this.intrinsic.addFunction(this.exprlist, "ZU");
		this.intrinsic.addFunction(this.exprlist, "ZUTIL");
		this.intrinsic.addFunction(this.exprlist, "ZTRNLNM");	
		this.intrinsic.addFunction(this.exprlist, "ZBOOLEAN");	
		this.intrinsic.addFunction(this.exprlist, "ZDEV");	
		this.intrinsic.addFunction(this.exprlist, "ZGETDV");
		this.intrinsic.addFunction(this.exprlist, "ZSORT");
		this.intrinsic.addFunction(this.exprlist, "ZESCAPE");
		this.intrinsic.addFunction(this.exprlist, "ZSEARCH");
		this.intrinsic.addFunction(this.exprlist, "ZPARSE");
		this.intrinsic.addFunction(this.exprlist, "ZCONVERT");
		this.intrinsic.addFunction(this.exprlist, "ZDVI");
		this.intrinsic.addFunction(this.exprlist, "ZGETDVI");
		this.intrinsic.addFunction(this.exprlist, "ZOS");
		this.intrinsic.addFunction(this.exprlist, "ZINTERRUPT");
		this.intrinsic.addFunction(this.exprlist, "ZJOB");
		this.intrinsic.addFunction(this.exprlist, "ZBITSTR");
		this.intrinsic.addFunction(this.exprlist, "ZBITXOR");
		this.intrinsic.addFunction(this.exprlist, "LISTGET");
		this.intrinsic.addFunction(this.exprlist, "ZDEVSPEED");
		this.intrinsic.addFunction(this.exprlist, "ZGETJPI");
		this.intrinsic.addFunction(this.exprlist, "ZGETSYI");
		this.intrinsic.addFunction(this.exprlist, "ZUTIL");	
		this.intrinsic.addFunction(this.exprlist, "ZK");	
		this.intrinsic.addFunction(this.exprlist, "ZWA");
		this.intrinsic.addFunction(this.exprlist, "ZVERSION");

		this.command.addCommands(this);

		this.command.addCommand("ZB", "ZBREAK", this);
		this.command.addCommand("ZS", "ZSAVE", this);     // Cache only
		this.command.addCommand("ZC", "ZCONTINUE", this); //GTM only
		this.command.addCommand("ZR", this);
		this.command.addCommand("ZI", this);
		this.command.addCommand("ZQ", this);
		this.command.addCommand("ZT", this);
		this.command.addCommand("ZU", this);
		this.command.addCommand("ZSH", "ZSHOW", this);    // GTM only
		this.command.addCommand("ZN", "ZNSPACE", this);   // CACHE only
		this.command.addCommand("ZETRAP", this);
		this.command.addCommand("ESTART", this);
		this.command.addCommand("ESTOP", this);
		this.command.addCommand("ABORT", this);
		this.command.addCommand("ZRELPAGE", this);
		this.command.addCommand("ZSYSTEM", this);
		this.command.addCommand("ZL", "ZLINK", this);    // GTM Only		
		this.command.addCommand("ZESCAPE", this);
		this.command.addCommand("ZITRAP", this);
		this.command.addCommand("ZGETPAGE", this);
		
		String[] ops = {
				"-", "+", "_", "*", "/", "#", "\\", "**", 
				"&", "!", "=", "<", ">", "[", "]", "?", "]]",
				"'&", "'!", "'=", "'<", "'>", "'[", "']", "'?", "']]"};
		for (String op : ops) {
			this.operator.addOperator(op);
		}
	}
	
	public static class CacheSupply extends MTFSupply {
		@Rule("glvn | expritem | classmethod")
		public TokenFactory<MToken> expratom;
		
		@SequenceTokenType(MObjectExpr.class)
		@Rule("name, '.', {name:'.'}, [actuallist]")
		public TokenFactory<MToken> objectexpr;
		
		@Rule("objectexpr | lvn | gvnall | indirection")
		public TokenFactory<MToken> glvn;
			
		@SequenceTokenType(MExpression.class)
		@Rule("expratom | classmethod, [exprtail]")
		public TokenFactory<MToken> expr;
		
		@Rule("\"##class\"")
		public TokenFactory<MToken> ppclass;
		@Rule("'.', name")
		public TokenFactory<MToken> classreftail;
		@Rule("{classreftail}")
		public TokenFactory<MToken> classreftaillst;
		@Rule("name, [classreftaillst]")
		public TokenFactory<MToken> classref;
		@SequenceTokenType(MCacheClassMethod.class)
		@Rule("ppclass, '(', classref, ')', '.', name, actuallist")
		public TokenFactory<MToken> classmethod;
		
		@Rule("\"$SYSTEM\":1")
		public TokenFactory<MToken> system;
		@Rule("'.', name")
		public TokenFactory<MToken> method;
		@Rule("{method}")
		public TokenFactory<MToken> methods;
		@SequenceTokenType(MCacheSystemCall.class)
		@Rule("system, [methods], actuallist")
		public TokenFactory<MToken> systemcall;
		
		@Rule("fanoutlabel, method, [dolineoffset], [doroutineref], [actuallist], [postcondition]")
		public TokenFactory<MToken> objdoargument;
		@Rule("classmethod, [dolineoffset], [doroutineref], [actuallist], [postcondition]")
		public TokenFactory<MToken> clsdoargument;
		@Rule("systemcall, [dolineoffset], [doroutineref], [actuallist], [postcondition]")
		public TokenFactory<MToken> sysdoargument;
		@SequenceTokenType(MDoArgument.class)	
		@Rule("fanoutlabel, ['^', ((envfanoutroutine | objdoroutine | fanoutroutine) , [actuallist]) | indfanoutroutine], [postcondition]")
		public TokenFactory<MToken> doargument;

		@Rule("objdoargument | extdoargument | inddoargument | offsetdoargument | labelcalldoargument | doargument | onlyrsimpledoargument | onlyrdoargument | clsdoargument | sysdoargument")
		public TokenFactory<MToken> doargumentall;
		
		@SequenceTokenType(MCacheObjectDoRoutine.class)
		@Rule("name, method")
		public TokenFactory<MToken> objdoroutine;
		@Rule("envfanoutroutine | objdoroutine | fanoutroutine | indfanoutroutine")
		public TokenFactory<MToken> doroutinepostcaret;
		
		@SequenceTokenType(MExtrinsic.class)	
		@Rule("fanoutlabel, ['^', ((envfanoutroutine | objdoroutine | fanoutroutine) , [actuallist]) | indfanoutroutine]")
		public TokenFactory<MToken> exargument;
		
		@Rule("classmethod | expr")
		public TokenFactory<MToken> setrhs;
		
		@Rule("expr, ',', {([expr], [':', expr]):','}")
		public TokenFactory<MToken> dcasearg;
	
		@Rule("{actual:','}")
		public TFDelimitedList<MToken> dsystemarg;
		
		@Rule("'$', ident, [methods]")
		public TokenFactory<MToken> intrinsicname;

		@Override
		protected void initialize() {		
			super.initialize();
			this.intrinsic.addFunction(this.dcasearg, "CASE", 1, Integer.MAX_VALUE);			
			this.intrinsic.addFunction(this.dsystemarg, "SYS", "SYSTEM", 0, Integer.MAX_VALUE);
			
			this.operator.addOperator(">=");
			this.operator.addOperator("<=");
			this.operator.addOperator("&&");
			this.operator.addOperator("||");
		}
	}
	
	private static MTFSupply CACHE_SUPPLY;
	private static MTFSupply STD_95_SUPPLY;
	
	private static MTFSupply generateSupply(Class<? extends MTFSupply> cls) throws ParseException {
		RuleBasedParserGenerator<MToken> parserGen = new RuleBasedParserGenerator<MToken>();
		MTFSupply result = 	parserGen.generate(cls, MToken.class);
		result.initialize();
		return result;
	}
	
	public static MTFSupply getInstance(MVersion version) throws ParseException {
		switch (version) {
			case CACHE: {
				if (CACHE_SUPPLY == null) {
					CACHE_SUPPLY = generateSupply(CacheSupply.class);
				}
				return CACHE_SUPPLY;
			}
			case ANSI_STD_95: {
				if (STD_95_SUPPLY == null) {
					STD_95_SUPPLY = generateSupply(MTFSupply.class);
				}
				return STD_95_SUPPLY;
			}
			default:
				throw new IllegalArgumentException("Unknown M version");
		}
	}	
}
