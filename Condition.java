public class Condition {

	private CompExpr comp = new CompExpr();
	private AndOr andOr = new AndOr();
	public boolean match;
	public String result = "";
	public String translated = "";

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
			result = "Failed to parse: {" + cmd + "} is not a valid condition.\n";
			return false;
		}
		return true;
	}

}
