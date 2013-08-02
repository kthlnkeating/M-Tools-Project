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

package com.pwc.us.rgi.m.tool.entry;

import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.tool.NamespaceFilter;
import com.pwc.us.rgi.struct.Filter;

public class RecursionSpecification {
	private static class InLabelFanoutFilter implements Filter<Fanout> {
		@Override
		public boolean isValid(Fanout input) {
			return false;
		}		
	}
		
	private static class InEntryFanoutFilter implements Filter<Fanout> {
		@Override
		public boolean isValid(Fanout input) {			
			return input.getType() == FanoutType.ASSUMED_GOTO;
		}		
	}
		
	private static class InRoutineFanoutFilter implements Filter<Fanout> {
		@Override
		public boolean isValid(Fanout input) {
			if (input != null) {
				String routineName = input.getEntryId().getRoutineName();
				if (routineName == null) return true;
			}
			return false;
		}
	}
	
	private static class NamespaceBasedFanoutFilter implements Filter<Fanout> {
		private NamespaceFilter namespaceFilter;
		
		public NamespaceBasedFanoutFilter(NamespaceFilter filter) {
			this.namespaceFilter = filter;
		}
		
		@Override
		public boolean isValid(Fanout input) {
			String routineName = input.getEntryId().getRoutineName();
			if ((routineName == null) || (routineName.isEmpty())) return true;
			return this.namespaceFilter.contains(routineName);
		}
	}

	private RecursionDepth depth = RecursionDepth.LABEL;
	private NamespaceFilter namespaceFilter = new NamespaceFilter();
	
	public void setDepth(RecursionDepth depth) {
		this.depth = depth;
	}

	public void setNamespaceFilter(NamespaceFilter filter) {
		this.namespaceFilter = filter;
	}

	public Filter<Fanout> getFanoutFilter() {
		switch (this.depth) {
		case ALL:
			return new NamespaceBasedFanoutFilter(this.namespaceFilter);
		case ROUTINE:
			return new InRoutineFanoutFilter();
		case ENTRY:
			return new InEntryFanoutFilter();
		default: // LABEL
			return new InLabelFanoutFilter();
		}
	}
}
