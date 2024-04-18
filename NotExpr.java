import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NotExpr {

	public boolean match;
	public String result = "";
	public String translated = "";
	
	private AndOr andOrObject = new AndOr();
	private Condition cond = new Condition();
	
	Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    Pattern not = Pattern.compile("(not)\\s*([\\S]+)$");
    Pattern notLiteral = Pattern.compile("^not$");
    Pattern bool = Pattern.compile("^true$|^false$");
    
 // interactive terminal version 
    public void run(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print(">> ");
		String cmd = in.nextLine();
		while (!cmd.equals("exit")) {
			parseCmd(cmd);
			System.out.print(">> ");
			cmd = in.nextLine();
		}
		in.close();
    }

    public boolean parseCmd(String cmd) {
    	match = notExpr(cmd);
    	System.out.print(result);
        return match;
    }

    // parens currently not working for statements like not (true or false)
    // shows bool expr
    private boolean notExpr(String cmd) {
    	Matcher n = not.matcher(cmd);
        boolean match2 = n.find();
        
        if (match2) {
        	result+= "<not_expr>: " + cmd + "\n";
            String[] tokens = cmd.split("(?<=not)");
            //for(int i = 0; i < tokens.length; i++) {
            	//System.out.println(tokens[i]);
           //}
            if (tokens.length < 2) {
                result= "Failed to parse: { " + cmd.trim() + " } " + "is missing an and or or.\n";
                //System.exit(0);
                return false;
            }
            int leftParen = 0;
            int rightParen = 0;
            for (String token : tokens) {
                token = token.trim();
                if (token.length() > 0 && token.substring(0, 1).equals("(")) {
                    leftParen++;
                    token = token.replace("(", "");
                    result+= "<paren>: (\n";
                    if (cond.parseCmd(token)) {
                    	result += cond.result;
                    	translated += cond.translated;
                    	printMsg(match2, "<bool_expr>", token.trim(), "boolean expression");
                    }
                    
                }
                boolean right = false;
                if (token.length() > 0 && token.substring(token.length()-1).equals(")")) {
                    right = true;
                    rightParen++;
                    token = token.replace(")", "");
                }
                else if (notLiteral(token)) {
                	printMsg(match2, "<notLiteral>", token.trim(), "not literal");
                }
                else if (boolExpr(token)) {
                	if (bool(token)) {
                		printMsg(match2, "<bool>", token.trim(), "boolean");
                	}
                	else {
                		printMsg(match2, "<var>", token.trim(), "variable");
                	}
                	printMsg(match2, "<bool_expr>", token.trim(), "boolean expression");
                }
                else {
                    result= "Failed to parse: { " + token.trim() + " } " + "is not a recognized integer, variable, or arithmetic operator.\n";
                    //System.exit(0);
                    return false;
                }

                if (right) {result+= "<paren>: )\n"; }
            }
            if (leftParen != rightParen) {
                result= "Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"\n";
                //System.exit(0);
                return false;
            }
        }
        else {
            result= "Failed to parse: {" + cmd + "} is not a valid and/or expression or subdivision.\n";
            //System.exit(0);
            return false;
        }
        
        return match;
    }
    
    
    private boolean variable(String cmd) {
        Matcher m = var.matcher(cmd);
        boolean match = m.find();
       
        return match;
    }
    
    private boolean bool(String cmd) {
    	Matcher m = bool.matcher(cmd);
    	boolean match = m.find();
    	//printMsg(match, "<bool>", cmd, "boolean");
    	return match;
    }
    
    private boolean notLiteral(String cmd) {
    	Matcher m = notLiteral.matcher(cmd);
    	boolean match = m.find();
    	//printMsg(match, "<notLiteral>", cmd, "not literal");
    	return match;
    }
    
    private boolean boolExpr(String cmd) {
    	Matcher m = bool.matcher(cmd);
    	boolean match = m.find();
    	Matcher n = var.matcher(cmd);
    	boolean match2 = n.find();
    	Condition cond = new Condition();
    	boolean condBool = cond.parseCmd(cmd); 
    	return match || match2 || condBool;
    }
    
    private void printMsg(boolean match, String ntName, String cmd, String item) {
		if (match)
			result+= ntName + ": " + cmd + "\n";
		else
			result= "Failed to parse: {" + cmd + "} is not a valid " + item + ".\n";
	}
    
}
