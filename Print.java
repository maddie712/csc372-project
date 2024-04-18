import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Print {
    private Pattern print = Pattern.compile("^(print\\().*\\)$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private Pattern string = Pattern.compile("^\"\\w*\"$");

    private Condition condition = new Condition();
    private MultDiv multDiv = new MultDiv();
    public boolean match;
    public String result = "";

    public boolean parseCmd(String cmd) {
        match = print(cmd);
        return match;
    }

    private boolean print(String cmd) {
        Matcher m = print.matcher(cmd);
        boolean match = m.find();
        if (match) {
            result += "<print>: " + cmd + "\n";
            String token = cmd.substring(cmd.indexOf("(")+1, cmd.length()-1);

            token = token.trim();
            
            if (intValue(token.trim())) {
                result += "<int>: " + token.trim() + "\n";
            }
            else if (variable(token)) {
                result += "<var>: " + token.trim() + "\n";
            }
            else if (string(token)) {
                result += "<string>: " + token.trim() + "\n";
            }
            else if (multDiv.parseCmd(token)) {
                result += multDiv.result;
            }
            else if (condition.parseCmd(token)) {
                result += condition.result;
            }
            else {
                result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized printable value.\n";
                return false;
            }
        } else {
            result = "Failed to parse: {" + cmd + "} is not a valid print expression.\n";
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

    private boolean string(String cmd) {
        Matcher m = string.matcher(cmd);
        boolean match = m.find();
        return match;
    }
}
