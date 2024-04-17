import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarAssign {
    private static String varName= null;
    private static String type= null;
    private static String val= null;
    private static HashMap<String,String> varToType= null;

    private static Pattern var_assign = Pattern.compile("^(.+)=(.+)$");
	private static Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
	private static Pattern string = Pattern.compile("\".*\"$");

    public static void main(String[] args) {
		HashMap<String,String> varTypes = new HashMap<>();
		String[] cmds = {"x = 10+2","y=true","z=\"hello world\"", "mult_div= 20/x", "cond=(1<mult_div)"};
		//Scanner scan = new Scanner(System.in);

		for(String cmd: cmds) {
			if(!parseLine(varTypes,cmd)){
				System.out.println("invalid <var_assign>: " + cmd);
			}
		}
	}

	/* 
	 * Parses a variable assignment line according to the grammar for 
	 * <var_assign>. 
	 */
    public static boolean parseLine(HashMap<String,String> varTypes, String cmd) {
		varToType = varTypes;
        Matcher m = var_assign.matcher(cmd);
		boolean match = false;
		if (m.find()) {
			match = true;
            match = match && parseVal(m.group(2).strip());
            match = match && parseVar(m.group(1).strip());
			printTranslation();
        }

        return match;
    }

	/* 
	 * Parses the variable name of a variable assignment. If a variable of that
	 * name already exists, performs type-checking to ensure the new value 
	 * is the same type.
	 */
    private static boolean parseVar(String cmd) {
		Matcher m = var.matcher(cmd);
		if(m.find()) {
        	if(varToType.containsKey(cmd) && varToType.get(cmd).equals(type)) {
				varName = cmd;
				return true;
        	}
			else {
				varName = cmd;
				varToType.put(varName,type);
				return true;
			}
    	}

        return false;
    }

	/* 
	 * Parses the value being assigned to a variable. Valid assignments fall 
	 * under <mult_div>, <condition>, <string>, and <var>.
	 * 
	 * Currently doesn't work for non-<mult_div> assignments because of how
	 * MultDiv.java is written and the same would be true for Condition.java.
	 */
    private static boolean parseVal(String cmd) {
        if (MultDiv.parseCmd(cmd)) { 
			type = "int";
			val = cmd;
			return true;
		}
		else if (Condition.parseCmd(cmd)) {
			type = "boolean";
			val = cmd;
			return true;
		}
		else {
			Matcher m = string.matcher(cmd);
			if(m.find()) {
				type = "String";
				val = cmd;
				return true;
			}
			else {
				// Checks if the value is a variable
				m = var.matcher(cmd);
				if(m.find()) {
					type = varToType.get(cmd);
					if (type!=null) { // if value var already declared
						val = cmd;
						return true;
					}
				}
			}
		}
        // still need to check Input

		System.err.println("Could not parse value of <var_assign>: " + cmd);
        return false;
    }

	private static void printTranslation() {
		System.out.print(type + " ");
		System.out.print(varName + " = ");
		System.out.print(val);
		System.out.println(";");
	}

    
}