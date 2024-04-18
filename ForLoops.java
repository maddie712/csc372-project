import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForLoops {
    private String loopRegex = "\\s*loop\\(([^,{}]+)(?:,([^{}]+))?\\)\\s*\\{(.+)\\}\\s*";
    private Pattern loopPattern = Pattern.compile(loopRegex, Pattern.DOTALL);
    private MultDiv multDiv1 = new MultDiv();
    private MultDiv multDiv2 = new MultDiv();
    private Condition condition = new Condition();
    private Line line = new Line();
    
    public boolean match;
    public String result = "";
    public String translated = "";

    public boolean parseCmd(String cmd) {
        match = translateLoop(cmd);
        return match;
    }

    public boolean translateLoop(String input) {
        Matcher matcher = loopPattern.matcher(input);
        if (matcher.matches()) {
            String firstExpression = matcher.group(1).trim();
            String secondExpression = matcher.group(2) != null ? matcher.group(2).trim() : null;
            String block = matcher.group(3).trim();

            if (secondExpression != null) {
                if (multDiv1.parseCmd(firstExpression) && multDiv2.parseCmd(secondExpression)) {
                    result += "<loop>: loop(" + firstExpression + ", " + secondExpression + ") {";
                    result += multDiv1.result + multDiv2.result;
                    translated += "for (int i=" + multDiv1.translated + "; i<" + multDiv2.translated + "; i++) {\n";
                }
                else {
                    result = "Failed to parse: { " + input.trim() + " } " + "is not a recognized loop definition.\n";
                    translated = "";
                    return false;
                }
            } else {
                if (condition.parseCmd(firstExpression)) {
                    result += "<loop>: loop(" + firstExpression + ") {";
                    result += condition.result;
                    translated += "while (" + condition.translated + ") {\n";
                }
                else if (multDiv1.parseCmd(firstExpression)) {
                    result += "<loop>: loop(" + firstExpression + ") {";
                    result += condition.result;
                    translated += "for (int i=0; i<" + multDiv1.translated + "; i++) {\n";
                }
                else {
                    result = "Failed to parse: { " + input.trim() + " } " + "is not a recognized loop definition.\n";
                    translated = "";
                    return false;
                }
            }

            String[] lines = block.split("\n");
            for (String l : lines) {
                line.parseCmd(l);
                if (!line.match) {
                    result = line.result;
                    return false;
                }
            }

            result += "<block>: \n";
            result += line.result;
            translated += line.translated + "}\n";
            return true;
        } else {
            System.out.println("Failed to parse: {" + input + "} is not a valid loop expression.");
            return false;
        }
    }

}
