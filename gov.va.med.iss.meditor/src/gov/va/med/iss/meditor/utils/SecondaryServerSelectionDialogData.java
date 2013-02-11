package gov.va.med.iss.meditor.utils;

import java.util.ArrayList;

//import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;

public class SecondaryServerSelectionDialogData {
	
	static private ArrayList arrayList = null;
	static private String[] serverList;
	static private String[] checkedList;
//	static private boolean buttonResponse = false;
	
	static public void setArrayList(ArrayList array) {
		arrayList = array;
		serverList = new String[array.size()-1];
		for (int i=1; i<array.size(); i++) {
			String str = (String)array.get(i);
			if (!(str.compareTo("") == 0)) {
				serverList[i-1] = "0;" + str;
				if (! (checkedList == null)) {
					for (int i1=0; i1<checkedList.length; i1++) {
						String str1 = checkedList[i1];
						if (! (str1 == null) ) {
							if (str1.compareTo(str) == 0) {
								serverList[i-1] = "1;"+str;
							}
						}
					}
				}
			}
			else {
				serverList[i] = "";
			}
		}
	}
	
	static public ArrayList getArrayList() {
		return arrayList;
	}
	
	static public String[] getServerList() {
		return serverList;
	}
	static public void setButtonResponse(boolean response) {
//		buttonResponse = response;
	}
	
	static public void setServerList(String[] serverArray) {
		serverList = serverArray;
	}
	
	public String[] getCheckedList() {
		return checkedList;
	}
	
	static public void setCheckedList(String[] inputList) {
		checkedList = inputList;
	}

}
