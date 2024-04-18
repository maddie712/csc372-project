import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultDiv {
    private Pattern arith = Pattern.compile("^([\\s\\S]+)\\s*([+\\-*/%])\\s*([\\s\\S]+)$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private Pattern op = Pattern.compile("^[+\\-*/%]$");

    public boolean match;
    public String result = "";

    public boolean parseCmd(String cmd) {
        match = arithmeticExpr(cmd);
        return match;
    }

    private boolean arithmeticExpr(String cmd) {
        Matcher m = arith.matcher(cmd);
        boolean match = m.find();
        if (match) {
            result += "<arithmetic_expr>: " + cmd;
            String[] tokens = cmd.split("(?=[+\\-*/])|(?<=[+\\-*/])");
            if (tokens.length < 3) {
                result += "Failed to parse: { " + cmd.trim() + " } " + "is missing an operand.";
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
                }
                boolean right = false;
                if (token.length() > 0 && token.substring(token.length()-1).equals(")")) {
                    right = true;
                    rightParen++;
                    token = token.replace(")", "");
                }

                if (intValue(token.trim())) {
                    result += "<int>: " + token.trim() + "\n";
                }
                else if (variable(token)) {
                    result += "<var>: " + token.trim() + "\n";
                }
                else if (operator(token)) {
                    result += "<operator>: " + token.trim() + "\n";
                }
                else {
                    result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized integer, variable, or arithmetic operator.\n";
                    return false;
                }

                if (right) { result += "<paren>: )\n"; }
            }
            if (leftParen != rightParen) {
                result = "Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"\n";
                return false;
            }
        } else {
            result = "Failed to parse: {" + cmd + "} is not a valid arithmetic expression.\n";
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
}
