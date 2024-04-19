import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {
    private Pattern intInput = Pattern.compile("^\\s*inputInt\\(\\)\\s*$");
	private Pattern stringInput = Pattern.compile("^\\s*inputStr\\(\\)\\s*$");

    public boolean match;
    public String result = "";
    public String translated = "new Scanner(System.in).next";

    public boolean parseCmd(String cmd) {
        match = input(cmd);
        return match;
    }

    private boolean input(String cmd) {
        Matcher integer = intInput.matcher(cmd);
        Matcher str = stringInput.matcher(cmd);

        if (integer.find()) {
            result += "<inputInt>: inputInt()";
            translated += "Int();";
        }
        else if (str.find()) {
            result += "<inputStr>: inputStr()";
            translated += "Line();";
        }
        else {
            result = "Failed to parse: { " + cmd.trim() + " } " + "is not a recognized input getter.\n";
            translated = "";
            return false;
        }
        return true;
    }
}
