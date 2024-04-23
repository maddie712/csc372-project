import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarAssign {
	// Public Variables
	public boolean match;
	public String result;
	public String translated;

	// Private Variables
    private String varName= null;
    private String type= null;
    private String val= null;
	private HashMap<String,String> varTypes= null;
	private HashMap<String,FuncInfo> funcs= null;

	// Patterns
    private Pattern var_assign = Pattern.compile("^(.+)\\s*=\\s*(.+)$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern bool = Pattern.compile("^true$|^false$");
	private Pattern string = Pattern.compile("^\"[^\"]*\"$");


	// Constructor
	public VarAssign(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
		this.varTypes = varTypes;
		this.funcs = funcs;
	}


	// Public Methods

	/* 
	 * Parses a variable assignment line according to the grammar for 
	 * <var_assign>. 
	 */
    public boolean parseCmd(String cmd) {
		// resets current line info
		varName = "";
		type = "";
		val = "";
		result = "";
		translated = "";
		
        Matcher m = var_assign.matcher(cmd);
		boolean match = false;
		if (m.find()) {
			result += "<var_assign>: " + cmd + "\n";
			match = true;
            match = match && parseVal(m.group(2).trim());
            match = match && parseVar(m.group(1).trim());
        }
		else {
			result += "Failed to parse '" + cmd + "'. Invalid var_assign expression.\n";
		}

        return match;
    }


	// Private Methods

	/* 
	 * Parses the variable name of a variable assignment. If a variable of that
	 * name already exists, performs type-checking to ensure the new value 
	 * is the same type.
	 */
    private boolean parseVar(String cmd) {
		if(var.matcher(cmd).find()) {  // checks valid var name
			match = true;
			varName = cmd;

			// handles new variables
			if(!varTypes.containsKey(cmd)) {
				varTypes.put(varName,type);
				result += "<var>: " + varName + "\n";
				translated = type + " " + varName + translated;
			}
			// handles parameters in functions (no type def in java)
			else if (varTypes.get(cmd).equals("undef")) {
				varTypes.put(varName,type);
				result += "<var>: " + varName + "\n";
				translated = varName + translated;
			}
			// makes sure prev initialised variables match type
			else if (varTypes.get(cmd).equals(type)) {
				result += "<var>: " + varName + "\n";
				translated = varName + translated;
        	}
			// if var already exists but with different type assignment
			else {
				result = "Failed to parse '" + cmd + "'. Mismatch type assign.\n";
				return false;
			}
    	}
		else {
			result += "Failed to parse '" + cmd + "'. Invalid variable name.\n";
			return false;
		}

        return true;
    }

	/* 
	 * Parses the value being assigned to a variable. Valid assignments fall 
	 * under <mult_div>, <condition>, <string>, and <var>.
	 * 
	 * Currently doesn't work for non-<mult_div> assignments because of how
	 * MultDiv.java is written and the same would be true for Condition.java.
	 */
    private boolean parseVal(String cmd) {
		FuncCall fnCall = new FuncCall(varTypes,funcs);
		MultDiv md = new MultDiv(varTypes);
		Condition cond = new Condition(varTypes);
		Input in = new Input();
		result += "<val>: " + cmd + "\n";  // val isn't a nt but I think makes parsing is clearer

		// checks for func call assignment
        if (fnCall.parseCmd(cmd)){
			type = fnCall.func.type;
			val = fnCall.translated;
			result += "<func_call>: " + cmd + "\n" + fnCall.result;
		}
		// checks for int assignment
		else if (md.parseCmd(cmd)) { 
			type = "int";
			val = md.translated;
			result += "<mult_div>: " + cmd + "\n" + md.result;
		}
		else if (intVal.matcher(cmd).find()) { 
			type = "int";
			val = cmd;
			result += "<int>: " + cmd + "\n";
		}
		// checks for boolean assignment
		else if (cond.parseCmd(cmd)) {
			type = "boolean";
			val = cond.translated;
			result += "<condition>: " + cmd + "\n" + cond.result;
		}
		else if (bool.matcher(cmd).find()) {
			type = "boolean";
			val = cmd;
			result += "<bool>: " + cmd + "\n";
		}
		// checks for string assignment
		else if (string.matcher(cmd).find()) {
			type = "String";
			val = cmd;
			result += "<string>: " + cmd + "\n";
		}
		// checks for user input assignment
		else if (in.parseCmd(cmd)) {
			type = in.result.contains("Str") ? "String" : "int";
			val = in.translated;
			result += "<input>: " + cmd + "\n" + in.result;
		}
		// checks for variable assignment and checks var is initialised
		else if (var.matcher(cmd).find() && varTypes.get(cmd)!=null) {
			if(varTypes.get(cmd).equals("undef")) {
				result = "Failed to parse '" + cmd + "'. Need to initialise parameter before using.\n";
				return false;
			}
			type = varTypes.get(cmd);
			val = cmd;
			result += "<var>: " + cmd + "\n";
		}
		else {
			result = "Failed to parse '" + cmd + "'. Invalid value to assign.\n";
			return false;
		}

		translated += " = " + val + ";\n";

        return true;
    }
}