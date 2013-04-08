package gov.va.mumps.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class MVariable extends MDebugElement implements IVariable {
	
	private String name;
	private MStackFrame stackFrame;
	private MValue value;

	public MVariable(MStackFrame stackFrame, String name) {
		super((MDebugTarget) stackFrame.getDebugTarget());
		
		this.name = name;
		this.stackFrame = stackFrame;
	}

	@Override
	public void setValue(String arg0) throws DebugException {
	}

	@Override
	public void setValue(IValue arg0) throws DebugException {
		//I think this is called from the UI, letting the user change this to alter the debug behavior
		//not supported in our API currently
	}

	void setValue(MValue value) {
		this.value = value;
	}
	
	@Override
	public boolean supportsValueModification() {
		return false; //not supported atm
	}

	@Override
	public boolean verifyValue(String arg0) throws DebugException {
		return false; //what is this for?
	}

	@Override
	public boolean verifyValue(IValue arg0) throws DebugException {
		return false;
	}

	@Override
	public String getName() throws DebugException {
		return name;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return ""; //TODO: how to determine this, with regards to the MUMPS language?
	}

	@Override
	public IValue getValue() throws DebugException {
		return value;
	}

	@Override
	public boolean hasValueChanged() throws DebugException {
		return false; //I believe that since the value cannot be set, it cannot change
	}

}
