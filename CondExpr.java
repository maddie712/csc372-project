import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CondExpr {
    private Pattern ifElsePattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{(.*)\\}\\s*else\\s*\\{(.*)\\}\\s*$", Pattern.DOTALL);
    private Pattern ifPattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{(.*)\\}\\s*$", Pattern.DOTALL);

    private Condition cond = null;
    private Line line1 = null;
    private Line line2 = null;

    public boolean match;
    public String result = "";
    public String translated = "";


    public CondExpr(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
        cond = new Condition(varTypes);
        line1 = new Line(varTypes, funcs);
        line2 = new Line(varTypes, funcs);
    }

    public boolean parseCmd(String cmd) {
        match = translateCondExpr(cmd);
        return match;
    }

    public boolean translateCondExpr(String input) {
        Matcher ifElseMatcher = ifElsePattern.matcher(input);
        Matcher ifMatcher = ifPattern.matcher(input);

        if (ifElseMatcher.matches()) {
            String condition = ifElseMatcher.group(1).trim();
            String ifBlock = ifElseMatcher.group(2).trim();
            String elseBlock = ifElseMatcher.group(3).trim();

            if (cond.parseCmd(condition)) {
                result += "<if_dec>: if (" + condition + ") {";
                translated += "if (" + cond.translated + ") {\n";
            }
            else {
                result = "Failed to parse: {" + input + "} is not a valid conditional expression.\n";
                translated = "";
                return false;
            }

            String[] lines1 = ifBlock.split("\n");
            for (String l : lines1) {
                line1.parseCmd(l);
                if (!line1.match) {
                    result = line1.result;
                    return false;
                }
            }

            result += "<block>: \n";
            result += line1.result;
            translated += line1.translated + "}\nelse {\n";

            result += "<else>: } else {\n";

            String[] lines2 = elseBlock.split("\n");
            for (String l : lines2) {
                line2.parseCmd(l);
                if (!line2.match) {
                    result = line2.result;
                    return false;
                }
            }

            result += "<block>: \n";
            result += line2.result;
            translated += line2.translated + "}\n";
            return true;
        } else if (ifMatcher.matches()) {
            String condition = ifMatcher.group(1).trim();
            String ifBlock = ifMatcher.group(2).trim();

            if (cond.parseCmd(condition)) {
                result += "<if_dec>: if (" + condition + ") {";
                translated += "if (" + cond.translated + ") {\n";
            }
            else {
                result = "Failed to parse: {" + input + "} is not a valid conditional expression.\n";
                translated = "";
                return false;
            }

            String[] lines1 = ifBlock.split("\n");
            for (String l : lines1) {
                line1.parseCmd(l);
                if (!line1.match) {
                    result = line1.result;
                    return false;
                }
            }

            result += "<block>: \n";
            result += line1.result;
            translated += line1.translated + "}\n";
            return true;
        } else {
            System.out.println("Failed to parse: {" + input + "} is not a valid conditional expression.");
            return false;
        }
    }

}

