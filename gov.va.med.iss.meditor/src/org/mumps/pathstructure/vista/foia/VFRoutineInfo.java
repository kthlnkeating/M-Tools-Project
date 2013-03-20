package org.mumps.pathstructure.vista.foia;

public class VFRoutineInfo implements Comparable<VFRoutineInfo> {

	private String directoryName;
	private String prefix;
	// note: for lower memory footprint, only implement the other fields in Packages.csv until they are used.
	
	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public int compareTo(VFRoutineInfo o) { //sort by prefix(namespace) in collections, for quicker namespace lookup time
		return getPrefix().compareTo(o.getPrefix());
	}
}
