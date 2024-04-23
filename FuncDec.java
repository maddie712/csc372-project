import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * A function written in our language, translated line-by-line to Java.
 */
public class FuncDec {
	public boolean match;
	public String result;
	public String retResult;
	public String translated;
	public String retTranslated;
	public String name;

	private FuncInfo fn = null;
	private String retVal = null;
	private HashMap<String, String> varTypes = null;
	private HashMap<String, FuncInfo> funcs = null;

    // Patterns
    private Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private Pattern func_dec = Pattern.compile("^\\s*func (.+)\\s*\\((.*)\\)\\s*\\{\\s*$");
	private Pattern return_ln = Pattern.compile("^\\s*return( .+)*\\s*$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern bool = Pattern.compile("^true$|^false$");
    private Pattern string = Pattern.compile("^\"[^\"]*\"$");
    private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private ArrayList<String> illegalNames = new ArrayList<String>(List.of("loop","if","input","inputInt","display","displayLine","not", "main"));

	public FuncDec(HashMap<String, String> varTypes, HashMap<String, FuncInfo> funcs) {
		this.varTypes = varTypes;
		this.funcs = funcs;
	}

	/*
	 * Parses the header line of a function declaration in our language.
	 */
	public boolean parseCmd(String cmd) {
		match = false;
		result = "";  // must be empty for Translator check (cmd not func dec)
		translated = "";
		fn = null;
		name = null;
		Matcher m = func_dec.matcher(cmd);
		if (m.find()) {
			result += "<func_dec>: " + cmd + "\n";
			fn = new FuncInfo();
			name = m.group(1).trim();
			fn.name = name;
			match = true;
			match = match && parseName(fn.name);
			match = match && parseParams(fn, m.group(2).trim());
			return true;
		}
		else {
			return false;
		}
	}

	/*
	 * Parses a return line in our language.
	 */
	public boolean parseReturn(String cmd) {
		Matcher m = return_ln.matcher(cmd);
		boolean match = false;
		retResult = "";
		retTranslated = "";
		if (m.find()) {
			retResult += "<return>: " + cmd + "\n";
			retVal = m.group(1);
			if (retVal==null) {
				retVal = "";
			}
			retVal = retVal.trim();
			String retType = getType(retVal);
			match = (retType != null);
			if (fn.type != null) {
				match = match && fn.type.equals(retType);
			} else {
				fn.type = retType;
			}
			if (match && retTranslated.equals("")) {
				retTranslated = "return " + retVal + ";\n";
			}
		}

		return match;
	}

	/*
	 * Performs any tasks needed for when the end of the function is detected.
	 * 
	 * Returns false if the function isn't complete.
	 */
	public boolean endFunc() {
		boolean match = true;
		String headerStr = "";

		// if never returns, makes a void function
		if (fn.type == null) {
			result = "Functions must end with a return statement.\n";
			return false;
		}
		headerStr += "static " + fn.type + " " + name + " (";
		String paramStr = "";

		for (String param : fn.params) {
			// checks all params were assigned values
			if (fn.paramTypes.get(param).equals("undef")) {
				if (varTypes.get(param).equals("undef")) {
					result = "Failed to parse { " + param + " } Paremeter never initialized.\n";
					match = false;
				} else {
					fn.paramTypes.put(param, varTypes.get(param));
				}
			}

			// adds param translation
			if (!paramStr.equals("")) {
				paramStr += ", ";
			}
			paramStr += fn.paramTypes.get(param) + " " + param;
		}

		// removes all variables set within function (all b/c funcs at top of file)
		ArrayList<String> vars = new ArrayList<>(varTypes.keySet()); 
		for (String var:vars) {
			varTypes.remove(var);
		}

		headerStr += paramStr + ") {\n";
		translated = headerStr + translated + "}\n";
		return match;
	}

	public void addToFuncs() {
		funcs.put(name, fn);
	}


    /*
     * Parses a function name by checking if it is a valid name.
     */
    private boolean parseName(String cmd) {
        Matcher m = func_name.matcher(cmd);
        if(m.find()) {
            if(illegalNames.contains(cmd)) {
                result = "Failed to parse: '" + cmd + "'. Is illegal function name.\n";
            }
            else if(!funcs.containsKey(cmd)) { 
                result += "<func>: " + cmd + "\n";
                return true;
            }
            else {
                result = "Failed to parse: '" + cmd + "'. Function already exists.\n";
            }
        }
        else {
            result = "Failed to parse: '" + cmd + "'. Invalid function name.\n";
        }
        return false;
    }

	/*
	 * Parses the parameters of a function declaration.
	 */
	private boolean parseParams(FuncInfo fn, String paramsStr) {
		fn.params = new ArrayList<String>();
		fn.paramTypes = new HashMap<>();
		if (paramsStr.equals("")) {
			result += "<params>:\n";
			return true;
		}

		String[] params = paramsStr.split(",");
		for (String param : params) {
			param = param.trim();
			Matcher m = var.matcher(param);
			if (m.find()) {
				if (fn.params.contains(param)) {
					result = "Failed to parse { " + param + " } Same parameter name used twice.\n";
					return false;
				}
				fn.params.add(param);
				fn.paramTypes.put(param, "undef");
				varTypes.put(param, "undef");
			} else {
				result = "Failed to parse: { " + param + " } Invalid variable name.\n";
				return false;
			}
		}

		String paramsFmt = String.join(", ", fn.params);
		result += "<params>: " + paramsFmt + "\n";
		return true;
	}

    /*
     * Gets the type of a (return) value.
     */
    private String getType(String cmd) {
        FuncCall fnCall = new FuncCall(varTypes,funcs);
		MultDiv md = new MultDiv(varTypes);
		Condition cond = new Condition(varTypes);

		// checks for no (void) return value
		if (cmd.equals("")) {
			retTranslated = "return;\n";
			return "void";
		}
		// checks for a func call value (null if func dne or is this func)
		else if (fnCall.parseCmd(cmd)) {
			retResult += "<func_call>: " + cmd + "\n";
			retTranslated = "return " + fnCall.translated + ";\n";
			return funcs.get(cmd).type;
		}
		// checks for int return value
		else if (md.parseCmd(cmd)) {
			retResult += "<mult_div>: " + cmd + "\n";
			retTranslated = "return " + md.translated + ";\n";
			return "int";
		} else if (intVal.matcher(cmd).find()) {
			retResult += "<int>: " + cmd + "\n";
			return "int";
		}
		// checks for boolean return value
		else if (cond.parseCmd(cmd)) {
			retResult += "<condition>: " + cmd + "\n";
			retTranslated = "return " + cond.translated + ";\n";
			return "boolean";
		} else if (bool.matcher(cmd).find()) {
			retResult += "<bool>: " + cmd + "\n";
			return "boolean";
		}
		// checks for string return value
		else if (string.matcher(cmd).find()) {
			retResult += "<string>: " + cmd + "\n";
			return "String";
		}
		// checks for a variable value (null if variable not declared)
		else if (var.matcher(cmd).find()) {
			retResult += "<var>: " + cmd + "\n";
			return varTypes.get(cmd);
		} else {
			retResult = "Failed to parse '" + cmd + "'. Invalid value return value.\n";
		}

		return null;
	}
}