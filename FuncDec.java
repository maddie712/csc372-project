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

    private FuncInfo fn= null;
    private String retVal= null;
    private HashMap<String,String> varTypes= null;
    private HashMap<String,FuncInfo> funcs= null;

    private Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private Pattern func_dec = Pattern.compile("^func (.+)\\s*\\((.*)\\)\\s*\\{$");
	private Pattern return_ln = Pattern.compile("^return( .+)*$");
    private Pattern string = Pattern.compile("\".*\"$");
    private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");


    // Constructor
    public FuncDec(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
        this.varTypes = varTypes;
        this.funcs = funcs;
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
            fn.name = m.group(1).strip();
            match = true;
            match = match && parseName(fn.name);
            match = match && parseParams(fn, m.group(2).strip());
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
            String retType = getType(m.group(1).strip());
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
        return fn.type + " " + fn.name + "(" + fn.javaParams() + ") {\n";
    }

    /*
     * Translates a return line into java syntax.
     * Assumes parseReturn was successful.
     */
    public String translateReturn() {
        return "return " + retVal + ";\n";
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
                result += "Failed to parse: '" + cmd + "'. Function already exists.\n";
            }
        }
        else {
            result += "Failed to parse: '" + cmd + "'. Invalid function name.\n";
        }
        return false;
    }

    /*
     * Parses the parameters of a function declaration.
     */
    private boolean parseParams(FuncInfo fn, String paramsStr) {
        fn.paramTypes = new HashMap<>();
        if(paramsStr.equals("")) {
            result += "<params>:\n";
            return true;
        }

        String[] params = paramsStr.split(",");
        for(String param: params) {
            Matcher m = var.matcher(param.strip());
            if(m.find()) {
                fn.paramTypes.put(param.strip(), "undef"); 
            }
            else {
                result += "Failed to parse: '" + param + "'. Invalid variable name.\n";
                return false;
            }
        }

        String paramsFmt = String.join(", ",fn.paramTypes.keySet());
        result += "<params>: (" + paramsFmt + ")\n";
        return true;
    }

    /*
     * Gets the type of a (return) value.
     */
    private String getType(String cmd) {
        String type = null;
        FuncCall fnCall = new FuncCall(varTypes,funcs);
        if (fnCall.parseCmd(cmd)){
            retVal = cmd;
			type = funcs.get(cmd).type;
		}
        else if(MultDiv.parseCmd(cmd)){
            retVal = cmd;
			type = "int";
        }
        else if(Condition.parseCmd(cmd)){
            retVal = cmd;
			type = "boolean";
        }
        else {
            Matcher m = string.matcher(cmd);
            if(m.find()) {
                retVal = cmd;
			    type = "String";
            }
            else {
                m = var.matcher(cmd);
                if(m.find()) {
                    if(varTypes.containsKey(cmd)) {
                        retVal = cmd;
			            type = varTypes.get(cmd);
                    }
                }
            }
        }

        return type;
    }
}