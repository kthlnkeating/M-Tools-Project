package gov.va.med.iss.connection.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;
import gov.va.med.iss.connection.utilities.MPiece;

public class SecondaryServerSelectionDialogData {
	
	static private ArrayList arrayList = null;
	static private String[] serverList;
	static private String[] checkedList;
	static private boolean buttonResponse = false;
	static private String[] savedCheckedList;
	static private boolean dontAskSecondaries = false;
	
	static public void setArrayList(ArrayList array) {
		arrayList = array;
		serverList = new String[array.size()-1];
		boolean skip = false;
		// skip primary server, which is index 0
		for (int i=1; i<array.size(); i++) {
			skip = false;
			String str = (String)array.get(i);
			if (!(str.compareTo("") == 0)) {
				for (int j=0; j<i; j++) {
					if (MPiece.getPiece((String)arrayList.get(i),";",2,3).toUpperCase().compareTo(MPiece.getPiece((String)arrayList.get(j),";",2,3).toUpperCase()) == 0) {
						arrayList.remove(i);
						i--;
						skip = true;
					}
				}
				if (! skip) {
					serverList[i-1] = "0;" + str;
					if (! (savedCheckedList == null)) {
						for (int i1=0; i1<savedCheckedList.length; i1++) {
							String str1 = savedCheckedList[i1];
							if (! (str1.charAt(str1.length()-1) == ';')) {
								str1 = str1 + ";";
							}
							if (! (str1 == null) ) {
								if (str1.compareTo(str) == 0) {
									serverList[i-1] = "1;"+str;
								}
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
	
	static public void setTotalList(ArrayList array) {
		arrayList = array;
		serverList = new String[array.size()];
		for (int i=0; i<array.size(); i++) {
			String str = (String)array.get(i);
			if (!(str.compareTo("") == 0)) {
				serverList[i] = "0;" + str;
			}
			else {
				serverList[i] = "";
			}
		}
	}

	static public ArrayList getArrayList() {
		return arrayList;
	}
	
	static public String[] getAllServers() {
		String[] resultStrings = new String[arrayList.size()];
		for (int i=0; i<arrayList.size(); i++) {
			resultStrings[i] = (String)arrayList.get(i);
			if (resultStrings[i].length() > 0) {
				resultStrings[i] = "0;" + resultStrings[i];
			}
		}
		return resultStrings;
	}
	
	static public String[] getServerList() {
		return serverList;
	}
	static public void setButtonResponse(boolean response) {
		buttonResponse = response;
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
	
	static public void setSavedCheckedList(String[] inputList) {
		savedCheckedList = inputList;
	}
	
	static public String[] getSavedCheckedList() {
		return savedCheckedList;
	}

	static public void setDontAsk(boolean value) {
		dontAskSecondaries = value;
	}
	
	static public boolean getDontAsk() {
		return dontAskSecondaries;
	}
	
	static public void addCheckedServer(String serverData) {
		// int number = savedCheckedList.length; // 091027
		int number = 0;  // 091027
		if (savedCheckedList != null) { // 091027
			number = savedCheckedList.length; //091027
		} // 091027
		String[] newList = new String[number+1];
		for (int i=0; i<number; i++) {
			newList[i] = savedCheckedList[i];
		}
		// newList[number-1] = serverData; // 091027
		newList[number] = serverData;  // 091027
		savedCheckedList = newList;
	}

}
