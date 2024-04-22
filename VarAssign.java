import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarAssign {
	public boolean match;
	public String result;
	public String translated;

	private String varName = null;
	private String type = null;
	private String val = null;
	private HashMap<String, String> varTypes = null;
	private HashMap<String, FuncInfo> funcs = null;

	private Pattern var_assign = Pattern.compile("^(.+)\\s*=\\s*(.+)$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern bool = Pattern.compile("^true$|^false$");
	private Pattern string = Pattern.compile("^\"[^\"]*\"$");

	public VarAssign(HashMap<String, String> varTypes, HashMap<String, FuncInfo> funcs) {
		this.varTypes = varTypes;
		this.funcs = funcs;
	}

	/*
	 * Parses a variable assignment line according to the grammar for <var_assign>.
	 */
	public boolean parseCmd(String cmd) {
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

		return match;
	}

	/*
	 * Translates the var assign line to java syntax. Assumes parseCmd() was
	 * successful.
	 */
	public String translate() {
		return translated;
	}

	/*
	 * Parses the variable name of a variable assignment. If a variable of that name
	 * already exists, performs type-checking to ensure the new value is the same
	 * type.
	 */
	private boolean parseVar(String cmd) {
		Matcher m = var.matcher(cmd);
		match = false;
		if (m.find()) {
			match = true;
			if (!varTypes.containsKey(cmd)) {
				varName = cmd;
				varTypes.put(varName, type);
				result += "<var>: " + varName;
				translated = type + " " + varName + translated;
			} else if (varTypes.get(cmd).equals("undef")) {
				varName = cmd;
				varTypes.put(varName, type);
				result += "<var>: " + varName;
				translated = varName + translated;
			} else if (varTypes.get(cmd).equals(type)) {
				varName = cmd;
				result += "<var>: " + varName;
				translated = varName + translated;
			}
			// if var already exists but with different type assignment
			else {
				result = "Failed to parse '" + cmd + "'. Mismatch type assign.\n";
				match = false;
			}
		} else {
			result = "Failed to parse '" + cmd + "'. Invalid variable name.\n";
		}

		return match;
	}

	/*
	 * Parses the value being assigned to a variable. Valid assignments fall under
	 * <mult_div>, <condition>, <string>, and <var>.
	 */
	private boolean parseVal(String cmd) {
		FuncCall fnCall = new FuncCall(varTypes, funcs);
		MultDiv md = new MultDiv();
		Condition cond = new Condition();
		Input in = new Input();

		// checks for func call assignment
		if (fnCall.parseCmd(cmd)) {
			type = fnCall.func.type;
			val = fnCall.translated;
			result += "<func_call>: " + cmd + "\n";
		}
		// checks for int assignment
		else if (md.parseCmd(cmd)) {
			type = "int";
			val = md.translated;
			result += "<mult_div>: " + cmd + "\n";
		} else if (intVal.matcher(cmd).find()) {
			type = "int";
			val = cmd;
			result += "<int>: " + cmd + "\n";
		}
		// checks for boolean assignment
		else if (cond.parseCmd(cmd)) {
			type = "boolean";
			val = cond.translated;
			result += "<condition>: " + cmd + "\n";
		} else if (bool.matcher(cmd).find()) {
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
			result += "<input>: " + cmd + "\n";
		}
		// checks for variable assignment and checks var is initialised
		else if (var.matcher(cmd).find() && varTypes.get(cmd) != null) {
			if (varTypes.get(cmd).equals("undef")) {
				result = "Failed to parse '" + cmd + "'. Need to initialise parameter before using.";
				return false;
			}
			type = varTypes.get(cmd);
			val = cmd;
			result += "<var>: " + cmd + "\n";
		} else {
			result = "";
			result += fnCall.result;
			result += md.result;
			result += cond.result;
			result += in.result;
			result += "Failed to parse '" + cmd + "'. Invalid value to assign.\n";
			return false;
		}

		translated += " = " + val + ";\n";

		return true;
	}
}