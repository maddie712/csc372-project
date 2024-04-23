import java.util.ArrayList;
import java.util.HashMap;

/*
 * Stores the name, type, and parameters for a function. 
 * 
 * If parameters reuse names of other variables, stores the original types for 
 * those variables so can be restored after ending the function.
 */
public class FuncInfo {
	public String name = null;
	public String type = null;
	public ArrayList<String> params = null;
	public HashMap<String, String> paramTypes = null;
	public HashMap<String, String> oldVars = null;

}
