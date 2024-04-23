import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompExpr {
	private Pattern comp = Pattern.compile("^([\\s\\S]+)\\s*((>=|<=|==|!=|<|>))\\s*([\\s\\S]+)$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^(?!^true|false$)[a-zA-Z][a-zA-z_0-9]*$");
	private Pattern op = Pattern.compile("^((>=|<=|==|!=|<|>))$");

	private MultDiv multDiv = null;
    private HashMap<String,String> varTypes = null;

	public boolean match;
	public String result = "";
	public String translated = "";

    public CompExpr(HashMap<String,String> varTypes) {
        this.varTypes = varTypes;
        multDiv = new MultDiv(varTypes);
    }

	public boolean parseCmd(String cmd) {
		result = "";
		translated = "";
		match = comparisonExpr(cmd);
		return match;
	}

	private boolean comparisonExpr(String cmd) {
		Matcher m = comp.matcher(cmd);
		boolean match = m.find();
		if (match) {
			String[] tokens;
			result += "<comparison_expr>: " + cmd + "\n";
			if (cmd.contains(">=") || cmd.contains("<=") || cmd.contains("!=")) {
				tokens = cmd.split("(?<=(=))|(?=(<|>|!))");
			} else {
				tokens = cmd.split("(?<=(==|<|>))|(?=(==|<|>))");
			}
			if (tokens.length < 3) {
				result = "'"+cmd.trim() + "' is missing an operand.";
				return false;
			}
			int leftParen = 0;
			int rightParen = 0;
			for (String token : tokens) {
				token = token.trim();
				if (token.length() > 0 && token.substring(0, 1).equals("(")) {
					leftParen++;
					token = token.replace("(", "");
					result += "<paren>: (\n";
					translated += "(";
				}
				boolean right = false;
				if (token.length() > 0 && token.substring(token.length() - 1).equals(")")) {
					right = true;
					rightParen++;
					token = token.replace(")", "");
				}

				if (intValue(token.trim())) {
					result += "<int>: " + token.trim() + "\n";
					translated += token;
				} else if (variable(token)) {
                    if (!typeCheck(token))
                        return false;
					result += "<var>: " + token.trim() + "\n";
					translated += token;
				} else if (operator(token)) {
					result += "<operator>: " + token.trim() + "\n";
					translated += token;
				} else if (multDiv.parseCmd(token)) {
					result += multDiv.result;
					translated += multDiv.translated;
				} else {
					result = "'"+token.trim() + "' is not a recognized integer, variable, or comparison operator.";
					translated = "";
					return false;
				}

				if (right) {
					result += "<paren>: )\n";
					translated += ")";
				}
			}
			if (leftParen != rightParen) {
				result = "'"+cmd + "' is missing a \"(\" or \")\"";
				translated = "";
				return false;
			}
		} else {
			result = "'"+cmd + "' is not a valid comparison expression.";
			translated = "";
			return false;
		}
		return match;
	}

	private boolean intValue(String cmd) {
		Matcher m = intVal.matcher(cmd);
		boolean match = m.find();
		return match;
	}

	private boolean variable(String cmd) {
		Matcher m = var.matcher(cmd);
		boolean match = m.find();
		return match;
	}

	private boolean operator(String cmd) {
		Matcher m = op.matcher(cmd);
		boolean match = m.find();
		return match;
	}

    /*
     * Checks that any variables used are initialised to int values.
     * 
     * If booleans/and_or is added will need to be updated.
     */
    private boolean typeCheck(String cmd) {
        String type = varTypes.get(cmd);
        if (type==null) {
            result = "'" + cmd + "' is not an initialized integer variable.\n";
            return false;
        }
        else if (type.equals("int")) {
			return true;
		}
		else if (type.equals("undef")) {
			varTypes.put(cmd,"int");
			return true;
		}
		else {
            result = "'" + cmd + "' is not an initialized integer variable.\n";
            return false;
        }
    }
}
