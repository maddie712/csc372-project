import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarAssign {
	public boolean match;
	public String result;

    private String varName= null;
    private String type= null;
    private String val= null;
	private HashMap<String,String> varTypes= null;
	private HashMap<String,FuncInfo> funcs= null;

    private Pattern var_assign = Pattern.compile("^(.+)=(.+)$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
	private Pattern string = Pattern.compile("\".*\"$");

	public VarAssign(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
		this.varTypes = varTypes;
		this.funcs = funcs;
	}

	/* 
	 * Parses a variable assignment line according to the grammar for 
	 * <var_assign>. 
	 */
    public boolean parseCmd(String cmd) {
		// resets current line info
		varName = "";
		type = "";
		val = "";

        Matcher m = var_assign.matcher(cmd);
		boolean match = false;
		if (m.find()) {
			match = true;
            match = match && parseVal(m.group(2).strip());
            match = match && parseVar(m.group(1).strip());
			
			result = javaTranslate();
        }

        return match;
    }

	/* 
	 * Parses the variable name of a variable assignment. If a variable of that
	 * name already exists, performs type-checking to ensure the new value 
	 * is the same type.
	 */
    private boolean parseVar(String cmd) {
		Matcher m = var.matcher(cmd);
		boolean match = false;
		if(m.find()) {
			match = true;
        	if(varTypes.containsKey(cmd) && varTypes.get(cmd).equals(type)) {
				varName = cmd;
        	}
			else if(varTypes.containsKey(cmd)) {
				match = false;
			}
			else {
				varName = cmd;
				varTypes.put(varName,type);
			}
    	}

        return match;
    }

	/* 
	 * Parses the value being assigned to a variable. Valid assignments fall 
	 * under <mult_div>, <condition>, <string>, and <var>.
	 * 
	 * Currently doesn't work for non-<mult_div> assignments because of how
	 * MultDiv.java is written and the same would be true for Condition.java.
	 */
    private boolean parseVal(String cmd) {
		boolean match = false;
        if (Func.parseFuncCall(cmd)){
			type = funcs.get(cmd).type;  // this needs to be error checked
			val = cmd;
			match = true;
		}
		
		else if (MultDiv.parseCmd(cmd)) { 
			type = "int";
			val = cmd;
			match = true;
		}
		else if (Condition.parseCmd(cmd)) {
			type = "boolean";
			val = cmd;
			match = true;
		}
		else {
			Matcher m = string.matcher(cmd);
			if(m.find()) {
				type = "String";
				val = cmd;
				match = true;
			}
			else {
				// Checks if the value is a variable
				m = var.matcher(cmd);
				if(m.find()) {
					type = varTypes.get(cmd);
					if (type!=null) { // if value var already declared
						val = cmd;
						match = true;
					}
				}
			}
		}
        // still need to check Input

        return match;
    }

	private String javaTranslate() {
		return type + " " + varName + " = " + val + ";\n";
	}

	private void printTranslation() {
		System.out.print(type + " ");
		System.out.print(varName + " = ");
		System.out.print(val);
		System.out.println(";");
	}

    
}