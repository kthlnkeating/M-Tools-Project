package gov.va.med.iss.mdebugger.util;

public class MPiece {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String getPiece(String input, String separator) {
		return getPiece(input, separator, 1);
	}
	
	public static String getPiece(String input, String separator, int pieceNumber) {
		return getPiece(input, separator, pieceNumber, pieceNumber);
	}
	
	public static String getPiece(String input, String separator, int startingPiece, int endingPiece) {
		String inputString;
		String value = "";
		inputString = input;
		int currPiece = 1;
		while (currPiece < startingPiece) {
			int loc = inputString.indexOf(separator);
			if (loc > -1) {
				if (inputString.length() < (loc+separator.length()))
					inputString = "";
				else
					inputString = inputString.substring(loc+separator.length());
			}
			currPiece++;
		}
		int pieces = 0;
		while ((! (currPiece > endingPiece)) && (! (inputString.compareTo("") == 0))) {
			int loc = inputString.indexOf(separator);
			if (loc > -1) {
				if (pieces > 0)
					value = value + separator;
				if (loc > 0)
					value = value + inputString.substring(0,loc);
				if (inputString.length() < (loc + separator.length()))
					inputString = "";
				else
					inputString = inputString.substring(loc+separator.length());
			}
			else {
				if (pieces > 0)
					value = value + separator;
				value = value + inputString;
				inputString = "";
			}
			currPiece++;
			pieces++;
		}
		return value;
	}
	
	public static int numberOfPieces(String inputString, String separator) {
		int loc = inputString.indexOf(separator);
		if (loc == -1)
			return 1;
		int pieces = 0;
		while (! (inputString.compareTo("") == 0)) {
			pieces++;
			if (inputString.length() < (loc + separator.length()))
				inputString = "";
			else
				inputString = inputString.substring(loc+separator.length());
		}
		return pieces;
	}

}
