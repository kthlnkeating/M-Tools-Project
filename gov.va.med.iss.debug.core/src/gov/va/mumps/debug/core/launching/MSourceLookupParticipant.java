package gov.va.mumps.debug.core.launching;

import gov.va.mumps.debug.core.model.MStackFrame;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

public class MSourceLookupParticipant extends AbstractSourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
	   if (object instanceof MStackFrame) {
		   String routineName = ((MStackFrame)object).getRoutineName();
		   if (routineName == null)
			   return null;
		   else
			   return routineName + ".m";
	   }
	   return null;
	}

}
