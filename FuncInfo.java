import java.util.HashMap;

/*
 * Stores the name, type, and parameters for a function.
 */
public class FuncInfo {
    public String name= null;
    public String type= null;
    public HashMap<String,String> paramTypes= null;

    // Constructor
    public FuncInfo() {
        
    }

    /*
     * Returns a string of comma separated parameters. Java format used so each
     * parameter has the type and variable name for the parameter.
     */
    public String javaParams() {
        String ret = "";

        for(String param:paramTypes.keySet()) {
            if(!ret.equals(""))
                ret = ret + ", ";

            ret = ret + paramTypes.get(param) + " " + param;
        }

        return ret;
    }

    
}
