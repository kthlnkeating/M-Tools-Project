package gov.va.mumps.debug.core.model;

public class EntryTag {
	private String routine;
	private String tag;
	private int offset;
	
	public EntryTag(String routine, String tag, int offset) {
		super();
		this.routine = routine;
		this.tag = tag;
		this.offset = offset;
	}

	public String getRoutine() {
		return routine;
	}

	public void setRoutine(String routine) {
		this.routine = routine;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public static EntryTag getInstance(String description) {
		int plusLocation = -1;
		int caretLocation = -1;
		int n = description.length();
		
		for (int i=0; i<n; ++i) {
			char ch = description.charAt(i);
			if (ch == '+') {
				plusLocation = i;
				continue;
			}
			if (ch == '^') {
				caretLocation = i;
				break;
			}
		}
		String routine = description.substring(caretLocation+1);
		if (plusLocation > -1) {
	 		String tag = description.substring(0, plusLocation);
	 		String offset = description.substring(plusLocation, caretLocation);
	 		return new EntryTag(routine, tag, Integer.parseInt(offset));
		} else {
	 		String tag = description.substring(0, caretLocation);
	 		return new EntryTag(routine, tag, 0);			
		}
	}
		
}
