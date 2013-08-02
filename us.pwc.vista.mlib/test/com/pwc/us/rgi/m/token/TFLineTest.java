package com.pwc.us.rgi.m.token;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.struct.KeywordRefactorUseFlag;
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.m.token.MLine;
import com.pwc.us.rgi.m.token.MObjectSupply;
import com.pwc.us.rgi.m.token.MSyntaxError;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MToken;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.token.TFRoutine;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parser.Tokens;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.struct.StringCase;

public class TFLineTest {
	private static TokenFactory<MToken> fStd95;
	private static TokenFactory<MToken> fCache;
	private static ObjectSupply<MToken> objectSupply;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MTFSupply tfsStd95 = MTFSupply.getInstance(MVersion.ANSI_STD_95);
		fStd95 = tfsStd95.line;
		
		MTFSupply tfsCache = MTFSupply.getInstance(MVersion.CACHE);
		fCache = tfsCache.line;
		
		objectSupply = new MObjectSupply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		fStd95 = null;
		fCache = null;
		objectSupply = null;
	}
		
	private MToken lineTest(TokenFactory<MToken> f, String line, boolean errorAsWell) {
		try {
			Text text = new Text(line);
			MToken t = f.tokenize(text, objectSupply);
			String r = t.toValue().toString();
			Assert.assertEquals(line, r);	
			@SuppressWarnings("unchecked")
			Tokens<MToken> commands = (Tokens<MToken>) ((Tokens<MToken>) t).getToken(4);
			boolean found = false;
			for (Token errorCandidate : commands.toIterable()) {
				if (errorCandidate instanceof MSyntaxError) {
					found = true;
					break;
				}
			}
			Assert.assertTrue(errorAsWell == !found);
			return t;
		} catch(SyntaxErrorException e) {
			Token t = TFRoutine.recoverFromError(line, e);
			String r = t.toValue().toString();
			Assert.assertEquals(line, r);
			Assert.assertFalse(errorAsWell);
			return null;
		}			
	}

	private void lineTest(TokenFactory<MToken> f, String line, int errorCommand, int errorLocation) {
		try {
			Text text = new Text(line);
			MToken t = f.tokenize(text, objectSupply);
			String r = t.toValue().toString();
			Assert.assertEquals(line, r);	
			@SuppressWarnings("unchecked")
			Tokens<MToken> commands = (Tokens<MToken>) ((Tokens<MToken>) t).getToken(4);
			MToken error = commands.getToken(errorCommand);
			Assert.assertTrue(error instanceof MSyntaxError);
			Assert.assertEquals(errorLocation, ((MSyntaxError) error).getErrorIndex());
		} catch (SyntaxErrorException e) {
			Assert.fail("Unexpected exception.");
		}
	}

	private MToken lineTest(TokenFactory<MToken> f, String line) {
		return lineTest(f, line, true);
	}
	
	private void lineParameterTest(TokenFactory<MToken> f, String line, String[] expectedParams) {
		try {
			Text text = new Text(line);
			MLine t = (MLine) f.tokenize(text, objectSupply);
			String[] params = t.getParameters();
			Assert.assertEquals(params.length, expectedParams.length);
			Arrays.sort(params);
			Arrays.sort(expectedParams);
			for (int i=0; i<params.length; ++i) {
				Assert.assertEquals(params[i], expectedParams[i]);
			}
		} catch (SyntaxErrorException e) {
			Assert.fail("Unexpected exception.");			
		}
	}

	@Test
	public void testParams() {
		testBasic(fCache, true);
		testBasic(fStd95, false);
		lineParameterTest(fCache, "TAG(A,B,C) ;", new String[]{"A", "B", "C"});
		lineParameterTest(fStd95, "TAG(A,B,C) ;", new String[]{"A", "B", "C"});
	}
	
	public void testBasic(TokenFactory<MToken> f, boolean cache) {
		lineTest(f, " S A=A+1  F  S B=$O(^F(B)) Q:B=\"\"   S ^F(B,A)=5");
		lineTest(f, " S $E(A)=J+1 Q:B=\"\"\"YYY\"\"\"  Q:B=\"\"\"XXX\"\"\"");
		lineTest(f, " I '$D(USERPRT),(STATUS'=\"c\") Q ;not individual & not complete");
		lineTest(f, "PRCA219P ;ALB/RRG - REPORT LIKELY BILLS TO PRINT;;");
		lineTest(f, " I $$DEVICE() D ENTER");
		lineTest(f, "DEVICE() ;");
		lineTest(f, " SET @A=\"S\"  SET @H@(0)=3");
		lineTest(f, " I Y>0 S DEBT=$P($G(^PRCA(430,Y,0)),\"^\",9) I DEBT S PRCADB=$P($G(^RCD(340,DEBT,0)),\"^\"),^DISV(DUZ,\"^PRCA(430,\")=Y,$P(DEBT,\"^\",2)=$$NAM^RCFN01(DEBT) D COMP,EN1^PRCAATR(Y) G:$D(DTOUT) Q G ASK");
		lineTest(f, " S ^DISV(DUZ,\"^RCD(340,\")=+Y,PRCADB=$P(Y,\"^\",2),DEBT=+Y_\"^\"_$P(@(\"^\"_$P(PRCADB,\";\",2)_+PRCADB_\",0)\"),\"^\")");
		lineTest(f, " D DIVIS G:IBQUIT EXIT");
		lineTest(f, " D DQ");
		lineTest(f, " .F FLD=1:1:$L(LST,\",\") Q:$P(LST,\",\",FLD)']\"\"  D @(+$P(LST,\",\",FLD)) Q:$G(PSODIR(\"DFLG\"))!($G(PSODIR(\"QFLG\")))");
		lineTest(f, " ESTART");
		lineTest(f, " D ^%ZISC");
		lineTest(f, " U IO D @PRCARN D ^%ZISC K %ZIS Q");
		lineTest(f, " S K=7 F L=2:1:4 S K=K-1 D:IOST?1\"C-\".E WAIT^YSUTL:$Y+4>IOSL Q:YSLFT  W !!,^YTT(601,YSTEST,\"G\",L,1,1,0) D CK");	
		lineTest(f, " D ##class(%XML.TextReader).ParseStream(RESTOBJ.HttpResponse.Data,.AREADER)", cache);
		lineTest(f, " S CT=CT+1,^TMP(\"RCXM_344.5\",$J,CT)=\"This message is sent to alert you to conditions regarding this \"_RCTYP_\".\",CT=CT+1,^TMP(\"RCXM_344.5\",$J,CT)=\" \"");
		lineTest(f, " S CT=CT+1,^TMP(\"RCXM_344.5\",$J,CT)=\"The following electronic \"_RCTYP_\" was received at your site.\",CT=CT+1,^TMP(\"RCXM_344.5\",$J,CT)=\"It was received on: \"_$$FMTE^XLFDT($$NOW^XLFDT(),2)_\" in mail msg # \"_RCXMG_\".\"");
		lineTest(f, " S Z=0 F  S Z=$O(@RCVAR@(Z)) Q:'Z  I $D(@RCVAR@(Z,\"*\")) S CT=CT+1,^TMP(\"RCXM_344.5\",$J,CT)=@RCVAR@(Z,\"\")");
		lineTest(f, " L -@TASKNODE@(\"T\",0)");
		lineTest(f, " . X \"S RC=\"_@RULENODE@(1)");
		lineTest(f, " Q $D(@HANDLE@(\"Pr\",\"Handle\",CHILD))");
		lineTest(f, " .S BREAK=0 F  Q:BREAK||READER.EOF||'READER.Read()  D", cache);
		lineTest(f, " F STAT=42,16 F  S BILLN=$O(^PRCA(430,\"AC\",STAT,BILLN)) Q:'BILLN  I $$ACCK(BILLN) D");
		lineTest(f, " S PRCANODE=.11 S:$P(Y,\";\",2)=\"DIC(4,\" PRCANODE=1 S PRCANODE=\"^\"_$P(Y,\";\",2)_+$P(Y,\"^\",2)_\",\"_PRCANODE_\")\",PRCANODE=$G(@PRCANODE)");
		lineTest(f, " . D SENDMSG^XMXAPI(.5,RCSUBJ,XMBODY,.XMTO,,.XMZ)");
		lineTest(f, "CONT F XT1=1:1 S XT2=$T(ROU+XT1) Q:XT2=\"\"  S X=$P(XT2,\" \",1),XT3=$P(XT2,\";\",3) X XT4 I $T W !,X X ^%ZOSF(\"TEST\") S:'$T XT3=0 X:XT3 ^%ZOSF(\"RSUM\") W ?10,$S('XT3:\"Routine not in UCI\",XT3'=Y:\"Calculated \"_$C(7)_Y_\", off by \"_(Y-XT3),1:\"ok\")");
		lineTest(f, " S XT4=\"I 1\",X=$T(+3) W !!,\"Checksum routine created on \",$P(X,\";\",4),\" by KERNEL V\",$P(X,\";\",3),!");
		lineTest(f, " S IOP=IOP_\";255\",%ZIS=\"\" D ^%ZIS G:POP H^XUS U IO X ^%ZOSF(\"TYPE-AHEAD\"),^%ZOSF(\"LABOFF\") S X=0 X ^%ZOSF(\"RM\")");
		lineTest(f, " I $D(@G)#10 D WRITE(IO,G)");
		lineTest(f, " .I $Y>(IOSL-9) D UP^DVBCRPR1,NEXT,HDR^DVBCRPR1 W:$O(^DVB(396.4,OLDA,\"RES\",LINE))]\"\"&('+$G(DVBGUI)) !!,\"Exam Results Continued\",!!");
		lineTest(f, " S Y=$$FPS^RCAMFN01($S($G(LDT)>0:$E(LDT,1,5),1:$E(DT,1,5))_$TR($J($$PST^RCAMFN01(DEB),2),\" \",0),$S(+$E($G(LDT),6,7)>$$STD^RCCPCFN:2,1:1)) D DD^%D");
		lineTest(f, " S A=1 H  ");
		lineTest(f, " I ZTOS'[\"VAX DSM\" J RESTART^%ZTM0[ZTUCI] D DONE Q", 1, 34);
		lineTest(f, " S A=4  K ,CC,EE", false);
		lineTest(f, " G @(\"TAG\"_B):C'>3");
		lineTest(f, " G @A^@B");
		lineTest(f, " G TAG3:A=3,@(\"TAG\"_B):C'>3,@A^@B");
		lineTest(f, " D COMP,EN1^PRCAATR(Y) G:$D(DTOUT) Q G ASK");
		lineTest(f, " D @(+$P(LST,\",\",FLD))");
	}
	
	@Test
	public void testBasic() {
		testBasic(fCache, true);
		testBasic(fStd95, false);
	}
	
	public void testRefactor(TokenFactory<MToken> f, String current, String expected, MRefactorSettings settings) {
		MToken l = lineTest(f, current);
		l.refactor(settings);
		String actual = l.toValue().toString();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testRefactor() {
		MRefactorSettings settings = new MRefactorSettings();
		testRefactor(fCache, " S @A=\"S\"  S @H@(0)=3", " SET @A=\"S\"  SET @H@(0)=3", settings);
		testRefactor(fStd95, " S @A=\"S\"  S @H@(0)=3", " SET @A=\"S\"  SET @H@(0)=3", settings);
		
		settings.commandNameSettings.setStringCaseFlag(StringCase.TITLE_CASE);
		testRefactor(fCache, " S @A=\"S\"  S @H@(0)=3", " Set @A=\"S\"  Set @H@(0)=3", settings);
		testRefactor(fStd95, " S @A=\"S\"  S @H@(0)=3", " Set @A=\"S\"  Set @H@(0)=3", settings);

		settings.commandNameSettings.setStringCaseFlag(StringCase.LOWER_CASE);
		settings.commandNameSettings.setUseFlag(KeywordRefactorUseFlag.USE_MNEMONIC);		
		testRefactor(fCache, " Set @A=\"S\"  Set @H@(0)=3", " s @A=\"S\"  s @H@(0)=3", settings);
		testRefactor(fStd95, " Set @A=\"S\"  Set @H@(0)=3", " s @A=\"S\"  s @H@(0)=3", settings);

		settings = new MRefactorSettings();
		testRefactor(fCache, " S A=$P(B,\"^\",2)  S @H@(0)=3", " SET A=$PIECE(B,\"^\",2)  SET @H@(0)=3", settings);
		settings.commandNameSettings.setUseFlag(KeywordRefactorUseFlag.DO_NOT_CHANGE);
		settings.commandNameSettings.setStringCaseFlag(StringCase.SAME_CASE);
		testRefactor(fCache, " Set A=$P(B,\"^\",2)  S @H@(0)=3", " Set A=$PIECE(B,\"^\",2)  S @H@(0)=3", settings);
	}
}
