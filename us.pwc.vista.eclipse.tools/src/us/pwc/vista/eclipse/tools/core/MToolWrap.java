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

package us.pwc.vista.eclipse.tools.core;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeFiles;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.OSTerminal;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.output.TerminalFormatter;

import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;
import us.pwc.vista.eclipse.tools.toolconsole.MToolsPatternMatchListener;

public abstract class MToolWrap {
	protected OutputFlags getOutputFlags() {
		OutputFlags fs = new OutputFlags();
		fs.setSkipEmpty(true);
		return fs;
	}
	
	protected void updateFormat(TerminalFormatter formatter) {
		formatter.setTitleWidth(12);
	}
		
	public void writeResult(IProject project, IWorkbenchWindow window, ToolResult result, SourceCodeFiles scf) throws IOException {
		OutputFlags flags = this.getOutputFlags();
		String consoleName = "M Tools Console (" +  project.getName() + ")";
		MessageConsole console = MessageConsoleHelper.getMessageConsole(consoleName);
		IDocument document = console.getDocument();
		document.set("");
		IPatternMatchListener listener = new MToolsPatternMatchListener(project, window, scf);
		console.addPatternMatchListener(listener);
		MessageConsoleStream os = console.newMessageStream();
		Terminal t = new OSTerminal(os);
		this.updateFormat(t.getTerminalFormatter());
		result.write(t, flags);
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}
	
	public abstract ToolResult getRoutinesResult(IProject project, ParseTreeSupply pts, List<String> routineNames);

	public abstract ToolResult getTagsResult(IProject project, ParseTreeSupply pts, String routineName, List<String> tags);
}
