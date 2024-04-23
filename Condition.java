import java.util.HashMap;

public class Condition {

    private CompExpr comp = null;
    private AndOr andOr = null;
    public boolean match;
    public String result = "";
    public String translated = "";

    public Condition(HashMap<String,String> varTypes) {
        comp = new CompExpr(varTypes);
        andOr = new AndOr(varTypes);
    }

	public boolean parseCmd(String cmd) {
        result = "";
        translated = "";
		match = condition(cmd);
		return match;
	}

	private boolean condition(String cmd) {
		result += "<condition>: " + cmd + "\n";

		String token = cmd.trim();
		boolean comparison = comp.parseCmd(token);
		boolean andOrBool = andOr.parseCmd(token);
		if (andOrBool) {
			result += andOr.result;
			translated += andOr.translated;
		} else if (comparison) {
			result += comp.result;
			translated += comp.translated;
		} else {
			result = "Failed to parse: { " + token + " } " + andOr.result + " AND " + comp.result + "\n";
			return false;
		}
		return true;
	}

}
