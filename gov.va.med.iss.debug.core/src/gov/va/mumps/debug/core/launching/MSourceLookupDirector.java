package gov.va.mumps.debug.core.launching;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class MSourceLookupDirector extends AbstractSourceLookupDirector {

	@Override
	public void initializeParticipants() {
		 addParticipants(new ISourceLookupParticipant[] { new MSourceLookupParticipant() });
	}

}
