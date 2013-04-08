package gov.va.med.iss.mdebugger.vo;

public class VariableVO implements Comparable<VariableVO> {

	private String name;
	private String value;
	
	public VariableVO(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableVO other = (VariableVO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	
	@Override
	public int compareTo(VariableVO obj) {
		if (obj == null)
			return -1;
		
		if (this == obj || this.equals(obj))
			return 0;
		
		if (this.getName() == null) //note: equals already compares the case where both are not null.
			return 1;
		if (obj.getName() == null)
			return -1;
		
		return this.getName().compareTo(obj.getName());
	}

}
