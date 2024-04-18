import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Print {
    private Pattern print = Pattern.compile("^(display\\().*\\)$");
    private Pattern println = Pattern.compile("^(displayLine\\().*\\)$");
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private Pattern string = Pattern.compile("^\"\\w*\"$");

    private Condition condition = new Condition();
    private MultDiv multDiv = new MultDiv();
    public boolean match;
    public String result = "";
    public String translated = "System.out.print";

    public boolean parseCmd(String cmd) {
        match = print(cmd);
        if (!match) {
            result = "";
            translated = "";
            match = println(cmd);
        }
        return match;
    }

    private boolean print(String cmd) {
        Matcher m = print.matcher(cmd);
        boolean match = m.find();
        if (match) {
            result += "<print>: " + cmd + "\n";
            String token = cmd.substring(cmd.indexOf("(")+1, cmd.length()-1);

            token = token.trim();
            translated += "(";
            
            if (intValue(token.trim())) {
                result += "<int>: " + token.trim() + "\n";
                translated += token + ");";
            }
            else if (variable(token)) {
                result += "<var>: " + token.trim() + "\n";
                translated += token + ");";
            }
            else if (string(token)) {
                result += "<string>: " + token.trim() + "\n";
                translated += token + ");";
            }
            else if (multDiv.parseCmd(token)) {
                result += multDiv.result;
                translated += token + ");";
            }
            else if (condition.parseCmd(token)) {
                result += condition.result;
                translated += token + ");";
            }
            else {
                result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized printable value.\n";
                translated = "";
                return false;
            }
        } else {
            result = "Failed to parse: {" + cmd + "} is not a valid print expression.\n";
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
            String token = cmd.substring(cmd.indexOf("(")+1, cmd.length()-1);

            token = token.trim();
            translated += "ln(";
            
            if (intValue(token.trim())) {
                result += "<int>: " + token.trim() + "\n";
                translated += token + ");";
            }
            else if (variable(token)) {
                result += "<var>: " + token.trim() + "\n";
                translated += token + ");";
            }
            else if (string(token)) {
                result += "<string>: " + token.trim() + "\n";
                translated += token + ");";
            }
            else if (multDiv.parseCmd(token)) {
                result += multDiv.result;
                translated += token + ");";
            }
            else if (condition.parseCmd(token)) {
                result += condition.result;
                translated += token + ");";
            }
            else {
                result = "Failed to parse: { " + token.trim() + " } " + "is not a recognized printable value.\n";
                translated = "";
                return false;
            }
        } else {
            result = "Failed to parse: {" + cmd + "} is not a valid print expression.\n";
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

    private boolean string(String cmd) {
        Matcher m = string.matcher(cmd);
        boolean match = m.find();
        return match;
    }
}
