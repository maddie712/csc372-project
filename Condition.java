public class Condition {

    private CompExpr comp = new CompExpr();
    private AndOr andOr = new AndOr();
    public boolean match;
    public String result = "";
    public String translated = "";

    public boolean parseCmd(String cmd) {
        match = condition(cmd);
        return match;
    }

    private boolean condition(String cmd) {
        result += "<condition>: " + cmd + "\n";

        boolean match = false;
        String token = cmd.trim();
        
        boolean comparison = comp.parseCmd(token);
        boolean andOrBool = andOr.parseCmd(token);
        if (comparison) {
            result += comp.result;
            translated += comp.translated;
            match = true;
        }
        else if (andOrBool) {
            result += andOr.string;
            translated += andOr.translated;
            match = true;
        }
        else {
            result = "Failed to parse: {" + cmd + "} is not a valid condition.\n";
            return false;
        }
        return match;
    }

}
