import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncCall {
    // Class Variables
    private FuncInfo fn= null;
    private HashMap<String,String> varTypes= null;
	private HashMap<String,FuncInfo> funcs= null;
    
    private Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private Pattern func_call = Pattern.compile("^(.+)\\s*\\(((.*))\\)\\s*$"); 
    private Pattern string = Pattern.compile("\".*\"$");
    private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");


    // Constructor
    public FuncCall() {

    }

        /*
     * Parses a function call in our language.
     */
    public boolean parseCmd(String cmd) {
        Matcher m = func_call.matcher(cmd);
        boolean match = false;
        if(m.find()) {
            String name = m.group(1).strip();
            match = parseName(name, false);
            if (match && funcs.containsKey(name)) {
                fn = funcs.get(name);
                match = match && parseArgs(fn, m.group(2).strip());
            }
            
        }

        return match;
    }

        
    // Private Methods

    /*
     * Parses a function name by checking if it is a valid name.
     */
    private boolean parseName(String nm, boolean isDec) {
        Matcher m = func_name.matcher(nm);
        if(m.find()) {
            if(isDec && !funcs.containsKey(nm))  // no double-declaring methods
                return true;
            else if (!isDec && funcs.containsKey(nm))  // method must exist to call
                return true;
        }
        return false;
    }

    /*
     * Parses and validates the arguments of a function call.
     */
    private boolean parseArgs(FuncInfo fn, String argsStr) {
        boolean match = false;
        String[] argsArr;
        if(argsStr.equals(""))  // handles if no arguments given
            argsArr = new String[0];
        else
            argsArr = argsStr.split(",");  // splits arguments by commas
        
        if (argsArr.length==fn.paramTypes.size()) {
            match = true;
            for(String arg: argsArr) {
                if(fn.paramTypes.containsKey(arg) && fn.paramTypes.get(arg).equals(getType(arg))) {

                }
                else {
                    return false;
                }
            }
        }

        return match;
    }

    /*
     * Gets the type of an argument.
     */
    private String getType(String arg) {
        if(MultDiv.parseCmd(arg)){
            return "int";
        }
        else if(Condition.parseCmd(arg)){
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

        return "";
    }
}
