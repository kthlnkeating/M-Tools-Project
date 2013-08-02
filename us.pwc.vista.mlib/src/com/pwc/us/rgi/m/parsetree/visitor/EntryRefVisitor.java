package com.pwc.us.rgi.m.parsetree.visitor;

import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.AtomicDo;
import com.pwc.us.rgi.m.parsetree.ErrorNode;
import com.pwc.us.rgi.m.parsetree.FanoutLabel;
import com.pwc.us.rgi.m.parsetree.FanoutRoutine;
import com.pwc.us.rgi.m.parsetree.Visitor;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.token.MLine;
import com.pwc.us.rgi.m.token.MObjectSupply;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MToken;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.parsergen.ParseException;

public class EntryRefVisitor extends Visitor {
	
	@Override
	protected void visitErrorNode(ErrorNode error) {
		super.visitErrorNode(error);
	}



	private List<EntryId> entryTags = new ArrayList<EntryId>();
	private String label;
	private String routine;
	
	@Override
	protected void visitFanoutLabel(FanoutLabel label) {
		super.visitFanoutLabel(label);
		this.label = label.getValue();
		//System.out.println("LBL: " + label.getValue());
	}

	@Override
	protected void visitFanoutRoutine(FanoutRoutine routine) { //this is not visited if the call is to a local tag (ie: D TAG)
		super.visitFanoutRoutine(routine);
		this.routine = routine.getName();
		//System.out.println("RTN: " + routine.getName());
	}
	
	@Override
	protected void visitAtomicDo(AtomicDo atomicDo) {
		reset();
		super.visitAtomicDo(atomicDo);
		updateEntryTags();
	}
	
	private void reset() {
		label = null;
		routine = null;
	}

	private void updateEntryTags() {
		entryTags.add(new EntryId(routine, label));
	}
	
	public List<EntryId> getEntryTags() {
		return entryTags;
	}
	
	
	
//	@Override
//	protected void visitAtomicGoto(AtomicGoto atomicGoto) {
//		this.lastInfo.reset();
//		super.visitAtomicGoto(atomicGoto);
//		this.updateFanout();
//	}
//	
//	@Override
//	protected void visitExtrinsic(Extrinsic extrinsic) {
//		LastInfo current = this.lastInfo;
//		this.lastInfo = new LastInfo();
//		super.visitExtrinsic(extrinsic);
//		this.updateFanout();
//		this.lastInfo = current;
//	}
//	
//	@Override
//	protected void visitObjectMethodCall(ObjectMethodCall omc) {
//		LastInfo current = this.lastInfo;
//		this.lastInfo = new LastInfo();
//		super.visitObjectMethodCall(omc);
//		this.lastInfo = current;
//	}
	
	private static ObjectSupply<MToken> mAdapterSupply = new MObjectSupply();
	private static TokenFactory<MToken> tfLine;
	
	static {
		try {
			tfLine = MTFSupply.getInstance(MVersion.CACHE).line; //TODO: should be set from cli args
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}
	
	
	public static void main(String args[]) {
		System.out.println("Started");
		//String line ="#@%n234n43\"\\09nr02dn";
		//String line = " D SHOW^VALM";
		//String line =" D SELSS,HDR^GMRCNOTF,^GMRCNOTF";
		//String line = " Q:$S('$D(^ORD(100.99)):1,'$D(^PS(59.7,1,20)):1,1:^(20)<2.8)  D TAG^RTN(33)"; //node 20
		String line = " S VAFCSORT=\"\"SS\"\" D SACT^VAFCEHLM"; //bug with no comparing right
		Text text = new Text(line);
		MLine mline = null;
		try {
			mline = (MLine) tfLine.tokenize(text, mAdapterSupply);
		} catch (SyntaxErrorException e) {
			e.printStackTrace();
		}

		EntryRefVisitor erv = new EntryRefVisitor();
		mline.getNode().accept(erv);
		
	}
}


