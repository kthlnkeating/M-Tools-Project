package gov.va.mumps.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class MValue extends MDebugElement implements IValue {

	private MVariable variable;
	private String value;
	
	public MValue(MVariable variable, String value) {
		super((MDebugTarget) variable.getDebugTarget());
		
		this.variable = variable;
		this.value = value;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return ""; //Since mumps has no strict datatypes / is context based
	}

	@Override
	public String getValueString() throws DebugException {
		return value;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return new IVariable[0]; //TODO: perhaps indirection could be evaluated here? or perhaps it can walk down a mumps tree.
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return false;
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

}
