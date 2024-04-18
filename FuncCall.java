import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncCall {
    // Class Variables
    public boolean match;
    public String result;

    private FuncInfo fn= null;
    private String args= null;
    private HashMap<String,String> varTypes= null;
	private HashMap<String,FuncInfo> funcs= null;
    
    private Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private Pattern func_call = Pattern.compile("^(.+)\\s*\\((.*)\\)\\s*$"); 
    private Pattern string = Pattern.compile("\".*\"$");
    private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");


    // Constructor
    public FuncCall(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
        this.varTypes = varTypes;
        this.funcs = funcs;
    }

    /*
     * Parses a function call in our language.
     */
    public boolean parseCmd(String cmd) {
        Matcher m = func_call.matcher(cmd);
        match = false;
        result = "";
        args = "";
        if(m.find()) {
            result += "<func_call>: " + cmd + "\n";
            String name = m.group(1).strip();
            match = parseName(name);
            if(match && funcs.containsKey(name)) {
                fn = funcs.get(name);
                match = match && parseArgs(fn, m.group(2).strip());
            }
            else if(match) {
                result += "Failed to parse: '" + name + "'. Function does not exist.\n";
                match = false;
            }

            
        }

        return match;
    }

    /*
     * Translates the function call into java syntax. 
     * Does not add newline or ; to end because can be inline
     */
    public String translate() {
        return fn.name + "(" + args + ")";
    }
        
    // Private Methods

    /*
     * Parses a function name by checking if it is a valid name.
     */
    private boolean parseName(String cmd) {
        Matcher m = func_name.matcher(cmd);
        if(m.find()) {
            if (funcs.containsKey(cmd)) {  // method must exist to call
                result += "<func>: " + cmd + "\n";
                return true;
            }
            else {
                result += "Failed to parse: '" + cmd + "'. Function does not exist.\n";
            }
        }
        else {
            result += "Failed to parse: '" + cmd + "'. Invalid function name.\n";
        }
        return false;
    }

    /*
     * Parses and validates the arguments of a function call.
     */
    private boolean parseArgs(FuncInfo fn, String cmd) {
        boolean match = false;
        String[] argsArr;
        if(cmd.equals(""))  // handles if no arguments given
            argsArr = new String[0];
        else
            argsArr = cmd.split(",");  // splits arguments by commas
        
        if (argsArr.length==fn.params.size()) {
            match = true;
            for(int i = 0; i < argsArr.length; i++) {
                String param = fn.params.get(i);
                String argType = getType(argsArr[i]);
                if(varTypes.containsKey(param) && varTypes.get(param).equals(argType)) {
                    
                }
                else {
                    result += "Failed to parse: '" + argsArr[i] + "'. Invalid arg value.\n";
                    return false;
                }
            }
            args = String.join(", ", argsArr);
        }

        return match;
    }

    /*
     * Gets the type of an argument.
     */
    private String getType(String arg) {
		MultDiv md = new MultDiv();
		Condition cond = new Condition();

        if(md.parseCmd(arg)){
            return "int";
        }
        else if(cond.parseCmd(arg)){
            return "boolean";
        }
        else {
            Matcher m = string.matcher(arg);
            if(m.find()) {
                return "String";
            }
            else {
                m = var.matcher(arg);
                if(m.find()) {
                    if(varTypes.containsKey(arg)) {
                        return varTypes.get(arg);
                    }
                }
            }
        }

        result += "Failed to parse: '" + arg + "'. Invalid arg value.\n";
        return "";
    }
}
