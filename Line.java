public class Line {
    //private VarAssign varAssign = new VarAssign();
    //private FuncCall funcCall = new FuncCall();
    //private FuncDec funcDec = new FuncDec();
    private Print print = new Print();
    private ForLoops loop = new ForLoops();
    private CondExpr condExpr = new CondExpr();

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
        // else if (loop.translateLoop(cmd)) {
        //     // result = loop.result;
        //     // translated = loop.translated;
        //     return true;
        // }
        else if (condExpr.translateCondExpr(cmd)) {
            // result = condExpr.result;
            // translated = condExpr.translated;
            return true;
        }
        // else if (funcCall.parseCmd(cmd)) {
            
        // }
        // else if (funcDec.parseCmd(cmd)) {
            
        // }
        // else if (varAssign.parseCmd(cmd)) {
            
        // }
        else {
            result += print.result;
            // result += loop.result;
            // result += condExpr.result;
            // result += funcCall.result;
            // result += funcDec.result;
            // result += varAssign.result;
            return false;
        }
    }

}
