package com.pwc.us.rgi.m.token;

import com.pwc.us.rgi.m.parsetree.ErrorNode;
import com.pwc.us.rgi.m.parsetree.Line;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.NodeList;
import com.pwc.us.rgi.m.parsetree.ParentNode;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.Tokens;

public class MLine extends MSequence {
	private String tagName = "";
	private int index = 0;
	private int lineIndex = 0;

	public MLine(int length) {
		super(length);
	}

	public MLine(SequenceOfTokens<MToken> tokens) {
		super(tokens);
	}

	public String getTag() {
		Token tag = this.getToken(0);
		if (tag == null) {
			return null;
		} else {
			return tag.toValue().toString();
		}
	}
	
	public String[] getParameters() {
		Tokens<MToken> paramTokens = this.getTokens(1, 1);
		if (paramTokens != null) {
			int length = paramTokens.size();
			if (length > 0) {
				String[] result = new String[paramTokens.size()];
				int i=0;
				for (Token t : paramTokens.toLogicalIterable()) {
					result[i] = t.toValue().toString();
					++i;
				}
				return result;
			}
		}
		return null;
	}
	
	public int getLevel() {
		int level = 0;
		Token levelToken = this.getToken(3);
		if (levelToken != null) {
			TextPiece levelTokenValue = levelToken.toValue();
			return levelTokenValue.count('.');
		}		
		return level;
	}

	public void setIdentifier(String tagName, int index, int lineIndex) {
		this.tagName = tagName;
		this.index = index;
		this.lineIndex = lineIndex;
	}

	public String getTagName() {
		return this.tagName;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getLineIndex() {
		return this.lineIndex;
	}
	
	public Line getErrorNode(ErrorNode errorNode) {
		Line result = new Line(this.tagName, this.index, this.getLevel(), this.lineIndex);
		NodeList<Node> nodes = new NodeList<Node>(1);
		nodes.add(errorNode);
		result.setNodes(null);
		return result;
	}
	
	public Line getAsErrorNode(ErrorNode errorNode) {
		Line result = new Line(this.tagName, this.index, this.getLevel(), this.lineIndex);
		NodeList<Node> nodes = new NodeList<Node>(1);
		nodes.add(errorNode);
		result.setNodes(nodes);
		return result;
	}
	
	@Override
	public Line getNode() {
		int level = this.getLevel();
		Line result = new Line(this.tagName, this.index, level, this.lineIndex);
		ParentNode currentParent = result;
		Tokens<MToken> cmds = this.getTokens(4);
		if (cmds != null) {
			NodeList<Node> nodes = null;
			for (MToken t : cmds.toLogicalIterable()) {
				Node node = t.getNode();
				if (node != null) {
					if (nodes == null) nodes = new NodeList<Node>(cmds.size());
					currentParent = node.addSelf(currentParent, nodes, level);
				}
			}
			if ((nodes != null) && (nodes.size() > 0)) {
				currentParent.setNodes(nodes.copy());
			}
		}
		return result;
	}
}
