import java.util.HashMap;

public class Line {
    private VarAssign varAssign = null;
    private FuncCall funcCall = null;
    private Print print = null;

    public boolean match;
    public String result = "";
    public String translated = "";

    public Line(HashMap<String,String> varTypes, HashMap<String,FuncInfo> funcs) {
        varAssign = new VarAssign(varTypes, funcs);
        funcCall = new FuncCall(varTypes, funcs);
        print = new Print(varTypes);
    }

    public boolean parseCmd(String cmd) {
        result = "";
        translated = "";
        match = line(cmd);
        return match;
    }

    public boolean line(String cmd) {
        if (print.parseCmd(cmd)) {
            result = print.result;
            translated = print.translated;
            return true;
        }
        else if (funcCall.parseCmd(cmd)) {
            result = funcCall.result;
            translated = funcCall.translated + ";\n";
            return true;
        }
        else if (varAssign.parseCmd(cmd)) {
            result = varAssign.result;
            translated = varAssign.translated;
            return true;
        }
        else {
            result += print.result;
            result += funcCall.result;
            result += varAssign.result;
            return false;
        }
    }

}
