import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * A function written in our language, translated line-by-line to Java.
 */
public class Func {
    // Class Variables
    private static HashMap<String,FuncInfo> funcs= null;
    private static HashMap<String,String> varTypes= null;

    private static Pattern func_dec = Pattern.compile("^func (.+)\\s*\\((.*)\\)\\s*{$");
	private static Pattern return_ln = Pattern.compile("^return( .+)*$");
	private static Pattern func_call = Pattern.compile("^(.+)\\s*\\(((.*))\\)\\s*$"); 
    private static Pattern func_name = Pattern.compile("^([a-zA-Z])+\\w*$");
    private static Pattern string = Pattern.compile("\".*\"$");
    private static Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");


    // Public Methods

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine().strip();
        while(!input.equals("exit")) {
            FuncInfo fn = parseFuncDec(input);
            if(fn!=null) {
                System.out.println("<func>: " + fn.name);
                System.out.println("<params>: " + fn.javaParams());
            }
            input = scan.nextLine().strip();
        }
        scan.close();
    }

    /*
     * Parses the header line of a function declaration in our language.
     */
    public static FuncInfo parseFuncDec(String cmd) {
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
     * Parses a function call in our language.
     */
    public static boolean parseFuncCall(HashMap<String,FuncInfo> funcs, String cmd) {
        Matcher m = func_call.matcher(cmd);
        boolean match = false;
        if(m.find()) {
            String name = m.group(1).strip();
            match = parseName(name, false);
            if (match && funcs.containsKey(name)) {
                FuncInfo fn = funcs.get(name);
                match = match && parseArgs(fn, m.group(2).strip());
            }
            
        }

        return match;
    }

    /*
     * Parses a return line in our language.
     */
    public static boolean parseReturn(String cmd) {
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
    private static boolean parseName(String nm, boolean isDec) {
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
    private static boolean parseParams(FuncInfo fn, String paramsStr) {
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
     * Parses and validates the arguments of a function call.
     */
    private static boolean parseArgs(FuncInfo fn, String argsStr) {
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
    private static String getType(String arg) {
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

    /*
     * Prints a function declaration header according to java format
     */
    private static void printDec(FuncInfo fn) {
        System.out.print(fn.type + " ");
        System.out.print(fn.name + " (");
        for (String param: fn.paramTypes.keySet()) {
            System.out.print(fn.paramTypes.get(param) + " " + param + ", ");
        }
        System.out.println(") {");
    }

}