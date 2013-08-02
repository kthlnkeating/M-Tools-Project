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

package com.pwc.us.rgi.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Terminal {
	private TerminalFormatter tf = new TerminalFormatter();
	
	public TerminalFormatter getTerminalFormatter() {
		return this.tf;
	}
	
	public abstract void stop() throws IOException;
	
	public abstract void write(String data) throws IOException;
	
	public abstract void writeEOL() throws IOException;

	public void writeEOL(String data) throws IOException {
		this.write(data);
		this.writeEOL();
	}
	
	public void writeIndented(String data) throws IOException {
		this.write(this.tf.getIndent());
		this.writeEOL(data);
	}
	
	public void writeFormatted(String title, String message) throws IOException {
		String line = this.tf.titled(title, message);
		this.writeEOL(line);
	}
	
	public void writeFormatted(String title, int count) throws IOException {
		this.write(this.tf.startList(title));
		this.write(String.valueOf(count));
		this.writeEOL();		
	}

	public void writeFormatted(String title, String[] dataArray) throws IOException {
		this.write(this.tf.startList(title));
		if ((dataArray == null) || (dataArray.length == 0)) {
			this.write("--");
		} else {
			for (String data : dataArray) {
				this.write(this.tf.addToList(data));					
			}
		}
		this.writeEOL();		
	}

	public void writeFormatted(String title, Collection<String> dataList) throws IOException {
		this.write(this.tf.startList(title));
		if (dataList.size() > 0) {
			for (String data : dataList) {
				this.write(this.tf.addToList(data));
			}
		} else {
			this.write("--");
		}
		this.writeEOL();		
	}

	public void writeSortedFormatted(String title, Collection<String> dataList) throws IOException {
		List<String> sorted = new ArrayList<String>(dataList);
		Collections.sort(sorted);		
		this.writeFormatted(title, sorted);
	}
}
