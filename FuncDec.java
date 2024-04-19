import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * A function written in our language, translated line-by-line to Java.
 */
public class FuncDec {
    // Class Variables
    public boolean match;
    public String result;
    public boolean inFunc;

    private FuncInfo fn= null;
    private String retVal= null;
    private HashMap<String,String> varTypes= null;
    private HashMap<String,FuncInfo> funcs= null;

    private Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private Pattern func_dec = Pattern.compile("^func (.+)\\s*\\((.*)\\)\\s*\\{$");
	private Pattern return_ln = Pattern.compile("^return( .+)*$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern bool = Pattern.compile("^true$|^false$");
    private Pattern string = Pattern.compile("\".*\"$");
    private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");


    // Constructor
    public FuncDec(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
        this.varTypes = varTypes;
        this.funcs = funcs;
        inFunc = false;
    }


    // Public Methods

    /*
     * Parses the header line of a function declaration in our language.
     */
    public boolean parseCmd(String cmd) {
        match = false;
        result = "";
        fn = null;
        Matcher m = func_dec.matcher(cmd);
        if(m.find()) {
            result += "<func_dec>: " + cmd + "\n";
            fn = new FuncInfo();
            fn.name = m.group(1).trim();
            match = true;
            match = match && parseName(fn.name);
            match = match && parseParams(fn, m.group(2).trim());
        }
        
        return match;
    }

    /*
     * Parses a return line in our language.
     */
    public boolean parseReturn(String cmd) {
        Matcher m = return_ln.matcher(cmd);
        boolean match = false;
        if(m.find()) {
            result += "<return>: " + cmd + "\n";
            retVal = m.group(1).trim();
            String retType = getType(retVal);
            match = (retType!=null);
            if(fn.type!=null) {
                match = match && fn.type.equals(retType);
            }
            else {
                fn.type = retType;
                result += "<type>: " + retType + "\n";
            }
        }
        return match;
    }

    /*
     * Translates the header of a function into java syntax.
     * Assumes parseCmd() was successful and function has been fully parsed.
     */
    public String translateHeader() {
        String paramStr = "";
        for(String param:fn.params) {
            if(!paramStr.equals("")) {
                paramStr += ", ";
            }
            paramStr += fn.paramTypes.get(param) + " " + param;
        }

        return fn.type + " " + fn.name + "(" + String.join(", ",fn.params) + ") {\n";
    }

    /*
     * Translates a return line into java syntax.
     * Assumes parseReturn was successful.
     */
    public String translateReturn() {
        return "return " + retVal + ";\n";
    }

    /*
     * Performs any tasks needed for when the end of the function is detected.
     * 
     * Returns false if the function isn't complete.
     */
    public boolean endFunc() {
        boolean match = true;
        inFunc = false;

        // if never returns, makes a void function; can be changed to be invalid to match grammar
        if(fn.type==null) {
            fn.type = "void";
        }

        for(String param:fn.params) {
            // checks all parameters were assigned values
            if(fn.paramTypes.get(param).equals("undef")) {
                if(varTypes.containsKey(param)) {
                    fn.paramTypes.put(param, varTypes.get(param)); // should prob error check this
                }
                else {
                    result = "Failed to parse '" + param + "'. Paremeter never initialized.\n";
                    match = false;
                }
            }

            // removes params from vars and restores old vals if needed
            if(fn.oldVars.containsKey(param)) {
                varTypes.replace(param, fn.oldVars.get(param));
            }
            else {
                varTypes.remove(param);
            }
        }

        return match;
    }


    // Private Methods

    /*
     * Parses a function name by checking if it is a valid name.
     */
    private boolean parseName(String cmd) {
        Matcher m = func_name.matcher(cmd);
        if(m.find()) {
            if(!funcs.containsKey(cmd)) { 
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
        fn.oldVars = new HashMap<>();
        // fn.paramTypes = new HashMap<>();
        if(paramsStr.equals("")) {
            result += "<params>:\n";
            return true;
        }

        String[] params = paramsStr.split(",");
        for(String param: params) {
            param = param.trim();
            Matcher m = var.matcher(param);
            if(m.find()) {
                if(varTypes.containsKey(param)) {
                    fn.oldVars.put(param, varTypes.get(param));
                    varTypes.remove(param);
                }
                fn.params.add(param); 
                fn.paramTypes.put(param,"undef");
            }
            else {
                result = "Failed to parse: '" + param + "'. Invalid variable name.\n";
                return false;
            }
        }

        String paramsFmt = String.join(", ",fn.params);
        result += "<params>: " + paramsFmt + "\n";
        return true;
    }

    /*
     * Gets the type of a (return) value.
     */
    private String getType(String cmd) {
        FuncCall fnCall = new FuncCall(varTypes,funcs);
		MultDiv md = new MultDiv();
		Condition cond = new Condition();

        // checks for no (void) return value
        if(cmd.equals("")) {
            return "void";
        }
        // checks for a func call value (null if func dne or is this func)
        else if (fnCall.parseCmd(cmd)){
            result += "<func_call>: " + cmd + "\n";
			return funcs.get(cmd).type;
		}
        // checks for int return value
        else if(md.parseCmd(cmd) || intVal.matcher(cmd).find()){
            result += "<mult_div>: " + cmd + "\n";
			return "int";
        }
        // checks for boolean return value
        else if(cond.parseCmd(cmd) || bool.matcher(cmd).find()){
            result += "<condition>: " + cmd + "\n";
			return "boolean";
        }
        // checks for string return value
        else if(string.matcher(cmd).find()) {
            result += "<string>: " + cmd + "\n";
			return "String";
        }
        // checks for a variable value (null if variable not declared)
        else if(var.matcher(cmd).find()) {
            result += "<var>: " + cmd + "\n";
			return varTypes.get(cmd);
        }
        else {
            result = "Failed to parse '" + cmd + "'. Invalid value to return.";
        }

        return null;
    }
}