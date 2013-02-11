package gov.va.med.iss.mdebugger.views;

import org.eclipse.jface.viewers.*;


public class ViewerUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String getSelectedString(String stringVal, TableViewer viewer) {
		String current = stringVal;
		String data = "";
		for (int i=viewer.getTable().getSelectionCount()-1; i>-1; i--) {
			int value = viewer.getTable().getSelectionIndices()[i];
			data = data + ViewerUtils.getOne(current,value);
		}
		return data;
	}
	
	public static String removeSelectedString(String stringVal, TableViewer viewer) {
		for (int i=viewer.getTable().getSelectionCount()-1; i>-1; i--) {
			int value = viewer.getTable().getSelectionIndices()[i];
			stringVal = ViewerUtils.removeOne(stringVal,value);
		}
		return stringVal;
	}
	
	public static String getAll(String stringVal) {
		String data = "";
		String input = stringVal;
		while (input.indexOf(";") > -1) {
			int loc = input.indexOf(";");
			data = data + input.substring(0,loc+1);
			input = input.substring(loc+1);
			loc = input.indexOf("\n");
			if (loc > -1) {
				input = input.substring(loc+1);
			}
		}
		return data;
	}
	
	public static String getOne(String input, int index) {
		String data = "";
		int count = 0;
		while (input.indexOf(";") > -1) {
			int loc = input.indexOf(";");
			if (count == index) {
				data = data + input.substring(0,loc)+";";
				break;
			}
			input = input.substring(loc+1);
			loc = input.indexOf("\n");
			if (loc > -1) {
				input = input.substring(loc+1);
			}
			count++;
		}
		return data;
	}
	
	public static String removeOne(String input, int index) {
		String data = "";
		int count = 0;
		while (input.indexOf("\n") > -1) {
			int loc = input.indexOf("\n");
			String current = input.substring(0,loc+1);
			input = input.substring(loc+1);
			if (count != index) {
				data = data + current;
			}
			count++;
		}
		return data;
	}

}
