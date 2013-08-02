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

package com.pwc.us.rgi.m.parsetree;

public class Line extends ParentNode {
	private static final long serialVersionUID = 1L;

	private String tag;
	private int index;
	private int lineIndex;
	private int level;
		
	public Line(String tag, int index, int level, int lineIndex) {
		this.tag = tag;
		this.index = index;
		this.level = level;
		this.lineIndex = lineIndex;
	}

	public String getTag() {
		return this.tag;
	}
	
	public int getIndex() {
		return this.index;
	}

	public int getLineIndex() {
		return this.lineIndex;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitLine(this);
	}
	
	@Override
	public boolean isCloseble() {
		return true;
	}
	
	public boolean isClosed() {
		return this.getLastNode() instanceof DeadCmds;
	}
	
	public void tranformToClosed() {
		DeadCmds cmds = new DeadCmds(this.level);
		cmds.setNodes(this.nodes);
		NodeList<Node> newNodes = new NodeList<Node>(1);
		newNodes.add(cmds);
		this.setNodes(newNodes);
	}
}
