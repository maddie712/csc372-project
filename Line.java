
public class Line {
    private VarAssign varAssign = new VarAssign(null, null);
    //private FuncCall funcCall = new FuncCall();
    private Print print = new Print();

    public boolean match;
    public String result = "";
    public String translated = "";

    public boolean parseCmd(String cmd) {
        match = line(cmd);
        return match;
    }

    public boolean line(String cmd) {
        if (print.parseCmd(cmd)) {
            result = print.result;
            translated = print.translated;
            return true;
        }
        // else if (funcCall.parseCmd(cmd)) {
            
        // }
        else if (varAssign.parseCmd(cmd)) {
            result = varAssign.result;
            translated = varAssign.translate();
            return true;
        }
        else {
            result += print.result;
            // result += funcCall.result;
            result += varAssign.result;
            return false;
        }
    }

}
