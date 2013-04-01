package gov.va.med.iss.mdebugger;

import gov.va.med.iss.mdebugger.vo.StackVO;
import gov.va.med.iss.mdebugger.vo.StepResultsVO;
import gov.va.med.iss.mdebugger.vo.VariableVO;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepResultsParser {
	
	private static final Pattern captureStepMode = Pattern.compile("^STEP MODE\\: ([\\w\\+\\^]+)\\s*.*$");
	private static final Pattern stackCaller = Pattern.compile("^\\s*([%\\w\\d\\+\\^]*).*$");
	
	public StepResultsVO parse(String data) {
		//results
		String routineName = null;
		boolean complete = false;
		LinkedList<StackVO> stack = new LinkedList<StackVO>();
		int lineLocation = 0;
		String locationAsTag = null;
		String nextCommand = null;
		LinkedHashSet<VariableVO> variables = new LinkedHashSet<VariableVO>(70);
		
		//scanning logic
		SectionType section = null;
		Scanner scanner = new Scanner(data);
		scanner = scanner.useDelimiter("\r\n");
		String line;

		while (scanner.hasNext()) {
			line = scanner.next();
			try {
//				System.out.println("line:");
//				System.out.println(line);
//				System.out.flush();
				String str = line.substring(8);
//				System.out.println("str:");
//				System.out.println(str);
				section = SectionType.valueOf(str);
				
				if (section == SectionType.VALUES) {
					scanner.next();
					assert scanner.next().equals("VALUES");
					continue;
				}
			} catch (IllegalArgumentException e1) {
			} catch (NullPointerException e2) {
			} catch (IndexOutOfBoundsException e3) {
			}
			
			switch (section) {
			
			case REASON:
				if (line.equals("DONE -- PROCESSING FINISHED"))
					complete = true;
				else if (line.startsWith("STEP MODE: ")) {
					//System.out.println(line);
					Matcher m = captureStepMode.matcher(line);
					m.find();
					locationAsTag = m.group(1);
				} else if (line.startsWith("   NEXT COMMAND: ")) {
					nextCommand = line.substring(17);
				}
				break;
			case LOCATION:
				if (line.startsWith("ROUTINE: "))
					routineName = line.substring(9);
				else if (line.startsWith("LINE: ")) {
					if (line.substring(6).equals(""))
						lineLocation = -1;
					else
						lineLocation = Integer.parseInt(line.substring(6));
				}
				break;
			case STACK:				
				if (line.indexOf('>') != -1) {
					int gtLoc = line.indexOf('>');
					Matcher m = stackCaller.matcher(line);
					m.find();
					String caller = m.group(1);
					stack.add(new StackVO(
							line.substring(gtLoc+2, line.length()),
							caller.equals("") ? null : caller));
				}
				
				break;
			case VALUES:
				variables.add(new VariableVO(line, scanner.next()));
				break;
			case WHATCH:
				
				break;
			case READ:
				
				break;
			case WRITE:
				
			}

		}
		
		return new StepResultsVO(complete, variables, routineName, lineLocation, 
				locationAsTag, nextCommand, stack);
	}

	private enum SectionType {
		REASON, WRITE, READ, VALUES, STACK, WHATCH, LOCATION;
		
	}
}
