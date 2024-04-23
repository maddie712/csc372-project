import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Print {
	private Pattern print = Pattern.compile("^(display\\().*\\)$");
	private Pattern println = Pattern.compile("^(displayLine\\().*\\)$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
	private Pattern string = Pattern.compile("^\".*\"$");
	private Pattern stringManip = Pattern.compile("^(.*)\\s*([+*])\\s*(.*)$");

    private Condition condition = null;
    private MultDiv multDiv = null;
    private HashMap<String,String> varTypes;
    public boolean match;
    public String result = "";
    public String translated = "System.out.print";

    public Print(HashMap<String,String> varTypes) {
        this.varTypes = varTypes;
        condition = new Condition(varTypes);
        multDiv = new MultDiv(varTypes);
    }

	public boolean parseCmd(String cmd) {
        result = "";
        translated = "System.out.print";
		match = print(cmd);
		if (!match) {
			result = "";
			translated = "System.out.print";
			match = println(cmd);
		}
		translated += "\n";
		return match;
	}

	private boolean print(String cmd) {
		Matcher m = print.matcher(cmd);
		boolean match = m.find();
		if (match) {
			result += "<print>: " + cmd + "\n";
			String token = cmd.substring(cmd.indexOf("(") + 1, cmd.length() - 1);

			token = token.trim();
			translated += "(";

			if (intValue(token.trim())) {
				result += "<int>: " + token.trim() + "\n";
				translated += token + ");";
			} else if (variable(token)) {
                if (!varCheck(token)) 
                    return false;
				result += "<var>: " + token.trim() + "\n";
				translated += token + ");";
			} else if (string(token)) {
				result += "<string>: " + token.trim() + "\n";
				translated += token + ");";
			} else if (multDiv.parseCmd(token)) {
				result += multDiv.result;
				translated += multDiv.translated + ");";
			} else if (condition.parseCmd(token)) {
				result += condition.result;
				translated += condition.translated + ");";
			} else if (stringManip(cmd)) {
				result += "<string_manip: " + token.trim() + "\n";
				translated += token + ");";
			} else {
				result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized printable value.\n";
				translated = "";
				return false;
			}
		} else {
			result = "Failed to parse: { " + cmd + " } is not a valid print expression.\n";
			translated = "";
			return false;
		}
		return match;
	}

	private boolean println(String cmd) {
		Matcher m = println.matcher(cmd);
		boolean match = m.find();
		if (match) {
			result += "<print>: " + cmd + "\n";
			String token = cmd.substring(cmd.indexOf("(") + 1, cmd.length() - 1);

			token = token.trim();
			translated += "ln(";

			if (intValue(token.trim())) {
				result += "<int>: " + token.trim() + "\n";
				translated += token + ");";
			} else if (variable(token)) {
                if(!varCheck(token)) 
                    return false;
				result += "<var>: " + token.trim() + "\n";
				translated += token + ");";
			} else if (string(token)) {
				result += "<string>: " + token.trim() + "\n";
				translated += token + ");";
			} else if (multDiv.parseCmd(token)) {
				result += multDiv.result;
				translated += multDiv.translated + ");";
			} else if (condition.parseCmd(token)) {
				result += condition.result;
				translated += condition.translated + ");";
			} else if (stringManip(cmd)) {
				result += "<string_manip: " + token.trim() + "\n";
				translated += token + ");";
			} else {
				result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized printable value.\n";
				translated = "";
				return false;
			}
		} else {
			result = "Failed to parse: { " + cmd + " } is not a valid print line expression.\n";
			translated = "";
			return false;
		}
		return match;
	}

	private boolean intValue(String cmd) {
		Matcher m = intVal.matcher(cmd);
		return m.find();
	}

	private boolean variable(String cmd) {
		Matcher m = var.matcher(cmd);
		return m.find();
	}

	private boolean string(String cmd) {
		Matcher m = string.matcher(cmd);
		return m.find();
	}

    private boolean stringManip(String cmd) {
        Matcher m = stringManip.matcher(cmd);
        return m.find();
    }

    /*
     * Checks that any variables used are defined.
     */
    private boolean varCheck(String cmd) {
        String type = varTypes.get(cmd);
        if (type==null || type.equals("undef")) {
            result = "Failed to parse: {" + cmd + "} has no value assigned.\n";
            translated = "";
            return false;
        }
        else {
            return true;
        }
    }
}
