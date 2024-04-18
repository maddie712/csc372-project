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

    private HashMap<String,String> varTypes= null;
    private HashMap<String,FuncInfo> funcs= null;

    private Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private Pattern func_dec = Pattern.compile("^func (.+)\\s*\\((.*)\\)\\s*{$");
	private Pattern return_ln = Pattern.compile("^return( .+)*$");
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
    public FuncInfo parseFuncDec(String cmd) {
        Matcher m = func_dec.matcher(cmd);
        boolean match = false;
        FuncInfo fn = null;
        if(m.find()) {
            fn = new FuncInfo();
            fn.name = m.group(1).strip();
            match = true;
            match = match && parseName(fn.name, true);
            match = match && parseParams(fn, m.group(2).strip());
        }

        if(!match) { // for invalid func name or parameters
            return null;
        }
        
        
        return fn;
    }



    /*
     * Parses a return line in our language.
     */
    public boolean parseReturn(String cmd) {
        Matcher m = return_ln.matcher(cmd);
        boolean match = false;
        if(m.find()) {
            match = parseReturn(m.group(1));
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
     * Parses the parameters of a function declaration.
     */
    private boolean parseParams(FuncInfo fn, String paramsStr) {
        fn.paramTypes = new HashMap<>();
        if(paramsStr.equals("")) {
            return true;
        }

        String[] params = paramsStr.split(",");
        for(String param: params) {
            Matcher m = var.matcher(paramsStr);
            if(m.find()) {
                fn.paramTypes.put(param, "undef"); 
            }
            else {
                System.err.println("Parameter '" + param + "' is not a variable.");
                return false;
            }
        }

        return true;
    }



    /*
     * Prints a function declaration header according to java format
     */
    private void printDec(FuncInfo fn) {
        System.out.print(fn.type + " ");
        System.out.print(fn.name + " (");
        for (String param: fn.paramTypes.keySet()) {
            System.out.print(fn.paramTypes.get(param) + " " + param + ", ");
        }
        System.out.println(") {");
    }

}