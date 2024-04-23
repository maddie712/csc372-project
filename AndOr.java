import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndOr {
	public boolean match;
	public String result = "";
	public String translated = "";

	private CompExpr comp;
    private HashMap<String,String> varTypes = null;

	Pattern var = Pattern.compile("^(?!^not|and|or$)[a-zA-Z][a-zA-z_0-9]*$");
	Pattern andOr = Pattern.compile("^([\\s\\S]+)\\s*(and|or)\\s*([\\s\\S]+)$");
	Pattern not = Pattern.compile("not\\s*(\\(.*\\)|[a-zA-Z][a-zA-Z_0-9]*)");
	Pattern notLiteral = Pattern.compile("^not$");
	Pattern bool = Pattern.compile("^true$|^false$");
	Pattern andOrLiteral = Pattern.compile("^and$|^or$");

    public AndOr(HashMap<String,String> varTypes) {
        this.varTypes = varTypes;
		comp = new CompExpr(varTypes);
    }

	public boolean parseCmd(String cmd) {
        result = "";
        translated = "";
		match = notExpr(cmd);
		return match;
	}

	private boolean andOrExpr(String cmd) {
		Matcher m = andOr.matcher(cmd);
		boolean match = m.find();

		if (match) {
			result += "<and_or>: " + cmd + "\n";
			String[] tokens = cmd.split("(?=and|or)|(?<=and|or)");
			if (tokens.length < 3) {
				result = "'" + cmd.trim() + "' is missing an operand.";
				return false;
			}
			int leftParen = 0;
			int rightParen = 0;
			for (String token : tokens) {
				token = token.trim();
				if (token.length() > 0 && token.contains("(")) {
					leftParen++;
					token = token.replace("(", "");
					result += "<paren>: (\n";
					translated += "(";
				}
				boolean right = false;
				if (token.length() > 0 && token.contains(")")) {
					right = true;
					rightParen++;
					token = token.replace(")", "");
				}

				if (andOrLiteral(token)) {
					printMsg(match, "<andOrLiteral>", token.trim(), "and/or literal");
				} else if (token.contains("not")) {
					notExpr(token);
				} else if (bool(token)) {
					printMsg(match, "<bool>", token.trim(), "boolean");
				} else if (variable(token)) {
                    if (!typeCheck(token)) 
                        return false;
					printMsg(match, "<var>", token.trim(), "variable");
				} else if (comp.parseCmd(token)) {
					result += comp.result;
					translated += comp.translated;
				} else {
					result = "'"+token.trim() + "' is not a recognized boolean, variable, or boolean operator.";
					return false;
				}

				if (right) {
					result += "<paren>: )\n";
					translated += ")";
				}
			}
			if (leftParen != rightParen) {
				result = "'"+cmd + "' is missing a \"(\" or \")\"";
				return false;
			}
		} else {
			result = "'"+cmd + "' is not a valid and/or expression or subdivision.";
			return false;
		}

		return match;
	}

	private boolean notExpr(String cmd) {
		Matcher m = not.matcher(cmd);
		boolean match = m.find();

		if (match) {
			result += "<not_expr>: " + cmd + "\n";
			result += "<notLiteral>: not\n";

			String token = cmd.replaceFirst("not", "").trim();
			System.out.println("token: " + token);
			int leftParen = 0;
			int rightParen = 0;
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

			if (bool(token)) {
				printMsg(match, "<bool>", token.trim(), "boolean");
			} else if (variable(token)) {
                if (typeCheck(token)) 
    				printMsg(match, "<var>", token.trim(), "variable");
                else 
                    return false;
			} else if (token.contains("and") || token.contains("or")) {
				andOrExpr(token);
			} else if (comp.parseCmd(token)) {
				result += comp.result;
				translated += comp.translated;
			} else {
				result = "'"+token.trim() + "' is not a recognized boolean, variable, or boolean expression.";
				return false;
			}

			if (right) {
				result += "<paren>: )\n";
				translated += ")";
			}

			if (leftParen != rightParen) {
				result = "'"+cmd + "' is missing a \"(\" or \")\"";
				return false;
			}
		} else if (cmd.contains("and") || cmd.contains("or")) {
			match = andOrExpr(cmd);
		}
		else {
			result = "'"+cmd + "' is not a valid and/or/not expression or subdivision.";
			return false;
		}
		return match;
	}

	private boolean variable(String cmd) {
		Matcher m = var.matcher(cmd);
		boolean match = m.find();
		return match;
	}

	private boolean bool(String cmd) {
		Matcher m = bool.matcher(cmd);
		boolean match = m.find();
		return match;
	}

	private boolean andOrLiteral(String cmd) {
		Matcher m = andOrLiteral.matcher(cmd);
		boolean match = m.find();
		return match;
	}

	private void printMsg(boolean match, String ntName, String cmd, String item) {
		if (match) {
			result += ntName + ": " + cmd + "\n";
			cmd = cmd.replace("and", "&&").replace("or", "||");
			translated += cmd;
		}
		else
			result = "'"+cmd + "' is not a valid " + item + ".";
	}

    /*
     * Checks that any variables used are initialised to boolean values.
     */
    private boolean typeCheck(String cmd) {
        String type = varTypes.get(cmd);
        if (type==null) {
            result = "'" + cmd + "' is not an initialized boolean variable.\n";
            return false;
        }
        else if (type.equals("boolean")) {
			return true;
		}
		else if (type.equals("undef")) {
			varTypes.put(cmd,"boolean");
			return true;
		}
        else {
            result = "'" + cmd + "' is not an initialized boolean variable.\n";
            return false;
        }
    }

}
