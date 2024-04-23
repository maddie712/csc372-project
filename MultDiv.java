import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultDiv {
    private Pattern arith = Pattern.compile("^([\\s\\S]+)\\s*([+\\-*/%])\\s*([\\s\\S]+)$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private Pattern op = Pattern.compile("^[+\\-*/%]$");
    private HashMap<String,String> varTypes = null;

    public boolean match;
    public String result = "";
    public String translated = "";

    public MultDiv(HashMap<String,String> varTypes) {
        this.varTypes = varTypes;
    }

    public boolean parseCmd(String cmd) {
        match = arithmeticExpr(cmd);
        return match;
    }

    private boolean arithmeticExpr(String cmd) {
        Matcher m = arith.matcher(cmd);
        boolean match = m.find();
        if (match) {
            result += "<arithmetic_expr>: " + cmd + "\n";
            String[] tokens = cmd.split("(?=[+\\-*/%])|(?<=[+\\-*/%])");
            if (tokens.length < 3) {
                result += "Failed to parse: { " + cmd.trim() + " } " + "is missing an operand.";
                translated = "";
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
                if (token.length() > 0 && token.substring(token.length()-1).equals(")")) {
                    right = true;
                    rightParen++;
                    token = token.replace(")", "");
                }

                if (intValue(token.trim())) {
                    result += "<int>: " + token.trim() + "\n";
                    translated += token;
                }
                else if (variable(token)) {
                    if (!typeCheck(token)) 
                        return false;
                    result += "<var>: " + token.trim() + "\n";
                    translated += token;
                }
                else if (operator(token)) {
                    result += "<operator>: " + token.trim() + "\n";
                    translated += token;
                }
                else {
                    result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized integer, variable, or arithmetic operator.\n";
                    translated = "";
                    return false;
                }

                if (right) { 
                    result += "<paren>: )\n";
                    translated += ")";
                }
            }
            if (leftParen != rightParen) {
                result = "Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"\n";
                translated = "";
                return false;
            }
        } else {
            result = "Failed to parse: {" + cmd + "} is not a valid arithmetic expression.\n";
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
     * Checks that any variables used are initialised to integer values.
     */
    private boolean typeCheck(String cmd) {
        String type = varTypes.get(cmd);
        if (type!=null && type.equals("int")) {
            return true;
        }
        else {
            result = "Failed to parse: {" + cmd + "} is not assigned an integer.\n";
            translated = "";
            return false;
        }
    }
}
