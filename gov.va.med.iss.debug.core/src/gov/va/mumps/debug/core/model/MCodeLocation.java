package gov.va.mumps.debug.core.model;

public class MCodeLocation {
	private String routine;
	private String tag;
	private int offset;
	
	public MCodeLocation(String routine, String tag, int offset) {
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

	public String getAsDollarTextInput() {
		String result = '^' + this.routine;
		if (this.offset > 0) {
			result = '+' + String.valueOf(this.offset) + result;
		}
		if ((this.tag != null) && (! this.tag.isEmpty())) {
			result = this.tag + result;
		}
		return result;
	}
	
	public static MCodeLocation getInstance(String description) {
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
	 		return new MCodeLocation(routine, tag, Integer.parseInt(offset));
		} else {
	 		String tag = description.substring(0, caretLocation);
	 		return new MCodeLocation(routine, tag, 0);			
		}
	}
		
}
