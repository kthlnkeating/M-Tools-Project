package com.pwc.us.rgi.m.token;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.m.token.MDelimitedList;
import com.pwc.us.rgi.m.token.MDoArgument;
import com.pwc.us.rgi.m.token.MObjectSupply;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MToken;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFTest {
	private static MTFSupply supplyStd95;
	private static MTFSupply supplyCache;
	private static ObjectSupply<MToken> objectSupply;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		supplyStd95 = MTFSupply.getInstance(MVersion.ANSI_STD_95);
		supplyCache = MTFSupply.getInstance(MVersion.CACHE);
		objectSupply = new MObjectSupply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		supplyStd95 = null;
		supplyCache = null;
		objectSupply = null;
	}

	public void testTFActual(MTFSupply m) {
		TokenFactory<MToken> f = m.actual;
		TFCommonTest.validCheck(f, objectSupply, ".57");
		TFCommonTest.validCheck(f, objectSupply, ".57  ", ".57");
		TFCommonTest.validCheck(f, objectSupply, ".INPUT");
		TFCommonTest.validCheck(f, objectSupply, ".INPUT  ", ".INPUT");
		TFCommonTest.validCheck(f, objectSupply, "5+A-B");
		TFCommonTest.validCheck(f, objectSupply, "5+A-B   ", "5+A-B");
		TFCommonTest.validCheck(f, objectSupply, "@(\"PSBTAB\"_(FLD-1))+1");
		TFCommonTest.validCheck(f, objectSupply, "((@(\"PSBTAB\"_(FLD))-(@(\"PSBTAB\"_(FLD-1))+1)))");
		TFCommonTest.validCheck(f, objectSupply, ".@VAR");
	}
	
	@Test
	public void testTFActual() {
		testTFActual(supplyCache);
		testTFActual(supplyStd95);		
	}

	private void testActualList(MTFSupply m) {
		TokenFactory<MToken> f = m.actuallist;
		TFCommonTest.validCheck(f, objectSupply, "()");		
		TFCommonTest.validCheck(f, objectSupply, "(C'>3)");		
		TFCommonTest.validCheck(f, objectSupply, "(C'>3,B>1)");		
		TFCommonTest.validCheck(f, objectSupply, "(C'>3,A=3,B]]1)");		
		TFCommonTest.validCheck(f, objectSupply, "(LST,\",\",FLD)");		
		TFCommonTest.validCheck(f, objectSupply, "(.LST,.5,FLD)");		
		TFCommonTest.validCheck(f, objectSupply, "(.5,RCSUBJ,XMBODY,.XMTO,,.XMZ)");
		TFCommonTest.validCheck(f, objectSupply, "(@(\"PSBTAB\"_(FLD-1))+1,((@(\"PSBTAB\"_(FLD))-(@(\"PSBTAB\"_(FLD-1))+1))),PSBVAL)");
	}

	@Test
	public void testActualList() {
		testActualList(supplyCache);
		testActualList(supplyStd95);
	}

	public void testTFComment(MTFSupply m) {
		TokenFactory<MToken> f = m.comment;
		TFCommonTest.validCheck(f, objectSupply, ";", false);
		TFCommonTest.validCheck(f, objectSupply, "; this is a comment", false);
		TFCommonTest.nullCheck(f, objectSupply, "this is a comment");
		TFCommonTest.validCheck(f, objectSupply, "; comment\n", "; comment");
		TFCommonTest.validCheck(f, objectSupply, "; comment\n  ", "; comment");
		TFCommonTest.validCheck(f, objectSupply, "; comment\r\n", "; comment");
		TFCommonTest.validCheck(f, objectSupply, "; comment\r\n  ", "; comment");
	}

	@Test
	public void testTFComment() {
		testTFComment(supplyCache);
		testTFComment(supplyStd95);		
	}
		
	public void testTFEnvironment(MTFSupply m) {
		TokenFactory<MToken> f = m.environment;
		TFCommonTest.validCheck(f, objectSupply, "|A|");
		TFCommonTest.validCheck(f, objectSupply, "|A+B|");
		TFCommonTest.validCheck(f, objectSupply, "[A]");
		TFCommonTest.validCheck(f, objectSupply, "[A,B]");
		TFCommonTest.validCheck(f, objectSupply, "[A,\"B\"]");
		TFCommonTest.errorCheck(f, objectSupply, "||", MError.ERR_GENERAL_SYNTAX, 1);
		TFCommonTest.errorCheck(f, objectSupply, "[A,B", MError.ERR_GENERAL_SYNTAX, 4);
		TFCommonTest.errorCheck(f, objectSupply, "[]", MError.ERR_GENERAL_SYNTAX, 1);
		TFCommonTest.errorCheck(f, objectSupply, "[A+B]", MError.ERR_GENERAL_SYNTAX, 2);
	}

	@Test
	public void testTFEnvironment() {
		testTFEnvironment(supplyCache);
		testTFEnvironment(supplyStd95);		
	}
		
	public void TFDeviceParams(MTFSupply m) {
		TokenFactory<MToken> f = m.deviceparams;
		TFCommonTest.validCheck(f, objectSupply, "(:XOBPORT:\"AT\")");
	}
	
	@Test
	public void testTFDeviceParams() {
		TFDeviceParams(supplyCache);
		TFDeviceParams(supplyStd95);		
	}

	public void testTFExtDoArgument(MTFSupply m) {
		TokenFactory<MToken> f = m.extdoargument;
		TFCommonTest.validCheck(f, objectSupply, "&ROUTINE");
		TFCommonTest.validCheck(f, objectSupply, "&ROUTINE(P0,\"RGI\",13)");
		TFCommonTest.validCheck(f, objectSupply, "&%^R5");
		TFCommonTest.validCheck(f, objectSupply, "&T1^ROUTINE(P0,,.P2)");
		TFCommonTest.validCheck(f, objectSupply, "&P0.ROUTINE");
		TFCommonTest.validCheck(f, objectSupply, "&P1.ROUTINE(P0,\"RGI\",13)");
		TFCommonTest.validCheck(f, objectSupply, "&P2.%^R5");
		TFCommonTest.validCheck(f, objectSupply, "&P3.T1^ROUTINE(P0,,.P2)");
		TFCommonTest.nullCheck(f, objectSupply, "^ROUTINE");
		TFCommonTest.errorCheck(f, objectSupply, "&&", MError.ERR_GENERAL_SYNTAX, 1);
		TFCommonTest.errorCheck(f, objectSupply, "&RO(P0,", MError.ERR_GENERAL_SYNTAX, 7);
		TFCommonTest.errorCheck(f, objectSupply, "&RO..A,", MError.ERR_GENERAL_SYNTAX, 4);
		TFCommonTest.errorCheck(f, objectSupply, "&RO.(A),", MError.ERR_GENERAL_SYNTAX, 4);
	}

	@Test
	public void testTFExtDoArgument() {
		testTFExtDoArgument(supplyCache);
		testTFExtDoArgument(supplyStd95);		
	}
	
	public void testTFDoArgument(MTFSupply m) {
		TokenFactory<MToken> f = m.doargument;
		TFCommonTest.validCheck(f, objectSupply, "T1:COND1", MDoArgument.class);
		TFCommonTest.validCheck(f, objectSupply, "T2", MDoArgument.class);
		TFCommonTest.validCheck(f, objectSupply, "T0", MDoArgument.class);
		if (m == supplyCache) {
			TFCommonTest.validCheck(f, objectSupply, "DecomposeStatus^%SYS.DATABASE(RC,.MSGLIST,0,\"\")", MDoArgument.class);		
		}
	}

	@Test
	public void testTFDoArgument() {
		testTFDoArgument(supplyCache);
		testTFDoArgument(supplyStd95);		
	}
	
	public void testTFDoArguments(MTFSupply m) {
		TokenFactory<MToken> f = m.doarguments;
		Token t = TFCommonTest.validCheck(f, objectSupply, "T0,T1:COND1,T2", MDelimitedList.class);
		Assert.assertNotNull(t);
		MDelimitedList dl = (MDelimitedList) t;
		for (Token lt : dl) {
			Assert.assertNotNull(lt);
			Assert.assertTrue(lt instanceof MDoArgument);
		}
	}

	@Test
	public void testTFDoArguments() {
		testTFDoArguments(supplyCache);
		testTFDoArguments(supplyStd95);		
	}
	
	public void testTFExternal(MTFSupply m) {
		TokenFactory<MToken> f = m.external;
		TFCommonTest.validCheck(f, objectSupply, "$&ZLIB.%GETDVI(%XX,\"DEVCLASS\")");
	}

	@Test
	public void testTFExternal() {
		testTFExternal(supplyCache);
		testTFExternal(supplyStd95);		
	}

	private void testTFExpr(MTFSupply m) {
		TokenFactory<MToken> f = m.expr;
		TFCommonTest.validCheck(f, objectSupply, "^A");
		TFCommonTest.validCheck(f, objectSupply, "@^%ZOSF(\"TRAP\")");
		TFCommonTest.validCheck(f, objectSupply, "^A(1)");
		TFCommonTest.validCheck(f, objectSupply, "C'>3");
		TFCommonTest.validCheck(f, objectSupply, "^YTT(601,YSTEST,\"G\",L,1,1,0)");
		TFCommonTest.validCheck(f, objectSupply, "IOST?1\"C-\".E");
		TFCommonTest.validCheck(f, objectSupply, "IOST?1\"C-\".E ", "IOST?1\"C-\".E");
		TFCommonTest.validCheck(f, objectSupply, "LST");
		TFCommonTest.validCheck(f, objectSupply, "\",\"");
		TFCommonTest.validCheck(f, objectSupply, "FLD");
		TFCommonTest.validCheck(f, objectSupply, "$L($T(NTRTMSG^HDISVAP))");
		TFCommonTest.validCheck(f, objectSupply, "@CLIN@(0)=0");
		TFCommonTest.validCheck(f, objectSupply, "$P(LA7XFORM,\"^\")?1.N");
		TFCommonTest.validCheck(f, objectSupply, "LA7VAL?1(1N.E,1\".\".E)");
		TFCommonTest.validCheck(f, objectSupply, "$D(@G)#10");
		TFCommonTest.validCheck(f, objectSupply, "$O(^$ROUTINE(ROU))");
		TFCommonTest.validCheck(f, objectSupply, "@SCLIST@(0)>0");
	}

	@Test
	public void testTFExpr() {
		testTFExpr(supplyCache);
		testTFExpr(supplyStd95);
	}

	public void testTFExprItem(MTFSupply m) {
		TokenFactory<MToken> f = m.expritem;
		TFCommonTest.validCheck(f, objectSupply, "$$TEST(A)");
		TFCommonTest.validCheck(f, objectSupply, "$$TEST^DOHA");
		TFCommonTest.validCheck(f, objectSupply, "$$TEST");
		TFCommonTest.validCheck(f, objectSupply, "$$TEST^DOHA(A,B)");
		TFCommonTest.validCheck(f, objectSupply, "$$MG^XMBGRP(\"RCCPC STATEMENTS\",0,.5,1,\"\",.DES,1)");
		TFCommonTest.validCheck(f, objectSupply, "$P(LST,\",\",FLD)");		
		TFCommonTest.validCheck(f, objectSupply, "+$P(LST,\",\",FLD)");
		TFCommonTest.validCheck(f, objectSupply, "$$AB^VC()");
		TFCommonTest.validCheck(f, objectSupply, "$$AB^VC");
		TFCommonTest.validCheck(f, objectSupply, "$$@AB^VC");
		TFCommonTest.validCheck(f, objectSupply, "$$@AB^@VC");
		TFCommonTest.validCheck(f, objectSupply, "$$AB^@VC");
		TFCommonTest.validCheck(f, objectSupply, "$T(NTRTMSG^HDISVAP)");
		TFCommonTest.validCheck(f, objectSupply, "$T(+3)");
		TFCommonTest.validCheck(f, objectSupply, "0");
		TFCommonTest.validCheck(f, objectSupply, "$$STOREVAR^HLEME(EVENT,.@VAR,VAR)");
	}

	@Test
	public void testTFExprItem() {
		testTFExprItem(supplyCache);
		testTFExprItem(supplyStd95);		
	}

	public void testTFGvn(MTFSupply m) {
		TokenFactory<MToken> f = m.gvn;
		TFCommonTest.validCheck(f, objectSupply, "^PRCA(430,+$G(PRCABN),0)");
	}

	@Test
	public void testTFGvn() {
		testTFGvn(supplyCache);
		testTFGvn(supplyStd95);		
	}

	public void testTFGvnAll(MTFSupply m) {
		TokenFactory<MToken> f = m.gvnall;
		TFCommonTest.validCheck(f, objectSupply, "^(A)");
		TFCommonTest.validCheck(f, objectSupply, "^A");
		TFCommonTest.validCheck(f, objectSupply, "^PRCA(430,+$G(PRCABN),0)");
		TFCommonTest.validCheck(f, objectSupply, "^(430,+$G(PRCABN),0)");
		TFCommonTest.validCheck(f, objectSupply, "^$ROUTINE(ROU)");
		TFCommonTest.validCheck(f, objectSupply, "^[ZTM,ZTN]%ZTSCH");
		TFCommonTest.validCheck(f, objectSupply, "^$W(\"ZISGTRM\")");
	}

	@Test
	public void testTFGvnAll() {
		testTFGvnAll(supplyCache);
		testTFGvnAll(supplyStd95);		
	}

	private void testTFIndirection(MTFSupply m) {
		TokenFactory<MToken> f = m.indirection;		
		TFCommonTest.validCheck(f, objectSupply, "@A");
		TFCommonTest.validCheck(f, objectSupply, "@(+$P(LST,\",\",FLD))");
		TFCommonTest.validCheck(f, objectSupply, "@H@(0)");
		TFCommonTest.validCheck(f, objectSupply, "@XARRAY@(FROMX1,TO1)");
		TFCommonTest.validCheck(f, objectSupply, "@RCVAR@(Z,\"\")");
		TFCommonTest.validCheck(f, objectSupply, "@RCVAR@(Z,\"*\")");
		TFCommonTest.validCheck(f, objectSupply, "@CLIN@(0)");
		TFCommonTest.validCheck(f, objectSupply, "@(\"PSBTAB\"_(FLD-1))");
		TFCommonTest.validCheck(f, objectSupply, "@SCLIST@(0)");
	}
	
	@Test
	public void testTFIndirection() {
		testTFIndirection(supplyCache);
		testTFIndirection(supplyStd95);
	}

	public void testTFLvn(MTFSupply m) {
		TokenFactory<MToken> f = m.lvn;
		TFCommonTest.validCheck(f, objectSupply, "A");
	}

	@Test
	public void testTFLvn() {
		testTFLvn(supplyCache);
		testTFLvn(supplyStd95);		
	}

	public void testTFName(MTFSupply m) {
		TokenFactory<MToken> f = m.name;
		TFCommonTest.validCheck(f, objectSupply, "RGI3");
		TFCommonTest.validCheck(f, objectSupply, "%RGI");
		TFCommonTest.validCheck(f, objectSupply, "rgi");
		TFCommonTest.validCheck(f, objectSupply, "%rgi");
		TFCommonTest.validCheck(f, objectSupply, "rGi5");
		TFCommonTest.validCheck(f, objectSupply, "%rGi5");
		TFCommonTest.nullCheck(f, objectSupply, "2RGI");
		TFCommonTest.nullCheck(f, objectSupply, ":RGI");
		TFCommonTest.validCheck(f, objectSupply, "%%", "%");
		TFCommonTest.validCheck(f, objectSupply, "%RGI%", "%RGI");
	}

	@Test
	public void testTFName() {
		testTFName(supplyCache);
		testTFName(supplyStd95);		
	}

	public void testTFNumLit(MTFSupply m) {
		TokenFactory<MToken> f = m.numlit;
		TFCommonTest.validCheck(f, objectSupply, ".11");
		TFCommonTest.validCheck(f, objectSupply, "1.11");
		TFCommonTest.validCheck(f, objectSupply, "3.11");
		TFCommonTest.validCheck(f, objectSupply, ".11E12");
		TFCommonTest.errorCheck(f, objectSupply, "1.E12", MError.ERR_GENERAL_SYNTAX, 2);
		TFCommonTest.errorCheck(f, objectSupply, "1.E-12", MError.ERR_GENERAL_SYNTAX, 2);
		TFCommonTest.errorCheck(f, objectSupply, "1.E+12", MError.ERR_GENERAL_SYNTAX, 2);
	}

	@Test
	public void testTFNumLit() {
		testTFNumLit(supplyCache);
		testTFNumLit(supplyStd95);		
	}

	public void testTFStringLiteral(MTFSupply m) {
		TokenFactory<MToken> f = m.strlit;
		TFCommonTest.validCheck(f, objectSupply, "\"This is a comment\"");
		TFCommonTest.validCheck(f, objectSupply, "\"Comment with quotes \"\" one\"");
		TFCommonTest.validCheck(f, objectSupply, "\"Comment with quotes \"\" one \"\" two\"");
		TFCommonTest.validCheck(f, objectSupply, "\"Comment with quotes \"\" one \"\" two and end \"\"\"");
		TFCommonTest.validCheck(f, objectSupply, "\"\"\"\"\"\"");
		TFCommonTest.errorCheck(f, objectSupply, "\" unmatched", MError.ERR_GENERAL_SYNTAX, 11);
		TFCommonTest.errorCheck(f, objectSupply, "\" unmatched \"\" one", MError.ERR_GENERAL_SYNTAX, 18);
		TFCommonTest.errorCheck(f, objectSupply, "\" unmatched \"\" one \"\" two", MError.ERR_GENERAL_SYNTAX, 25);
	}
	
	@Test
	public void testTFStringLiteral() {
		testTFStringLiteral(supplyCache);
		testTFStringLiteral(supplyStd95);		
	}

	private void testPattern(MTFSupply m) {
		TokenFactory<MToken> f = m.pattern;
		TFCommonTest.validCheck(f, objectSupply, "1\"C-\".E");
		TFCommonTest.validCheck(f, objectSupply, "1\"C-\".E ","1\"C-\".E");
		TFCommonTest.validCheck(f, objectSupply, ".P1N.NP");
		TFCommonTest.validCheck(f, objectSupply, ".P1N.NP ", ".P1N.NP");		
		TFCommonTest.validCheck(f, objectSupply, "1.N");		
		TFCommonTest.validCheck(f, objectSupply, "1(1N)");
		TFCommonTest.validCheck(f, objectSupply, "1N.E");		
		TFCommonTest.validCheck(f, objectSupply, "1(1N,1E)");		
		TFCommonTest.validCheck(f, objectSupply, "1\".\".E");		
		TFCommonTest.validCheck(f, objectSupply, "1(1\".\")");		
		TFCommonTest.validCheck(f, objectSupply, "1(1N,1\".\")");		
		TFCommonTest.validCheck(f, objectSupply, "1(1N.E,1A)");		
		TFCommonTest.validCheck(f, objectSupply, "1(1N.E,1\".\")");		
		TFCommonTest.validCheck(f, objectSupply, "1(1N.E,1\".\".E)");		
	}

	@Test
	public void testPattern() {
		testPattern(supplyCache);
		testPattern(supplyStd95);
	}
}
