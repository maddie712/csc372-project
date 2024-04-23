import java.util.HashMap;

public class Condition {

    private CompExpr comp = null;
    private AndOr andOr = null;
    public boolean match;
    public String result = "";
    public String translated = "";

    public Condition(HashMap<String,String> varTypes) {
        comp = new CompExpr(varTypes);
        andOr = new AndOr(varTypes);
    }

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
            result += andOr.result;
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
