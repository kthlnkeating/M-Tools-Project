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

public class OpenCloseUseCmdNodes {
	public static class AtomicOpenCmd extends AdditionalNodeHolder {
		private static final long serialVersionUID = 1L;

		public AtomicOpenCmd(Node node) {
			super(node);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAtomicOpenCmd(this);
		}	
	}
	
	public static class OpenCmd extends MultiCommand {
		private static final long serialVersionUID = 1L;

		public OpenCmd(Node postCondition, Node argument) {
			super(postCondition, argument);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitOpenCmd(this);
		}		
	}

	public static class AtomicCloseCmd extends AdditionalNodeHolder {
		private static final long serialVersionUID = 1L;

		public AtomicCloseCmd(Node node) {
			super(node);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAtomicCloseCmd(this);
		}	
	}
	
	public static class CloseCmd extends MultiCommand {
		private static final long serialVersionUID = 1L;

		public CloseCmd(Node postCondition, Node argument) {
			super(postCondition, argument);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitCloseCmd(this);
		}		
	}

	public static class AtomicUseCmd extends AdditionalNodeHolder {
		private static final long serialVersionUID = 1L;

		public AtomicUseCmd(Node node) {
			super(node);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAtomicUseCmd(this);
		}	
	}
	
	public static class UseCmd extends MultiCommand {
		private static final long serialVersionUID = 1L;

		public UseCmd(Node postCondition, Node argument) {
			super(postCondition, argument);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitUseCmd(this);
		}		
	}
	
	public static class DeviceParameters extends AdditionalNodeHolder {
		private static final long serialVersionUID = 1L;

		public DeviceParameters(Node node) {
			super(node);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitDeviceParameters(this);
		}		
	}
}
