import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AndOr {
	private static Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    
    private static Pattern andOr = Pattern.compile("^([\\s\\S]+)\\s*(and|or)\\s*([\\s\\S]+)$");
    private static Pattern not = Pattern.compile("(not)\\s*([\\S]+)$");
    private static Pattern notLiteral = Pattern.compile("^not$");
    private static Pattern bool = Pattern.compile("^true$|^false$");
    private static Pattern andOrLiteral = Pattern.compile("^and$|^or$");


    // interactive terminal version 
    public static void main(String[] args) {
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

    public static boolean parseCmd(String cmd) {
        boolean match = andOrExpr(cmd);
        if (match) {
        
        }
        else {

        }
        return match;
    }

    // parens currently not working for statements like not (true or false)
    private static boolean andOrExpr(String cmd) {
        Matcher m = andOr.matcher(cmd);
        boolean match = m.find();
        Matcher n = not.matcher(cmd);
        boolean match2 = n.find();
        
        if (match) {
            System.out.println("<and_or>: " + cmd);
            String[] tokens = cmd.split("(?=and|or)|(?<=and|or)");
            for(int i = 0; i < tokens.length; i++) {
            	System.out.println(tokens[i]);
            }
            if (tokens.length < 3) {
                System.out.println("Failed to parse: { " + cmd.trim() + " } " + "is missing an and or or.");
                System.exit(0);
            }
            int leftParen = 0;
            int rightParen = 0;
            for (String token : tokens) {
                token = token.trim();
                if (token.length() > 0 && token.substring(0, 1).equals("(")) {
                    leftParen++;
                    token = token.replace("(", "");
                    System.out.println("<paren>: (");
                }
                boolean right = false;
                if (token.length() > 0 && token.substring(token.length()-1).equals(")")) {
                    right = true;
                    rightParen++;
                    token = token.replace(")", "");
                }

               if (bool(token)) {
                	 printMsg(match, "<bool>", token.trim(), "boolean");
                	//System.out.println("<bool>: " + token.trim());
                }
                else if (andOrLiteral(token)) {
                	printMsg(match, "<andOrLiteral>", token.trim(), "and/or literal");
                	//System.out.println("<andOrLiteral>: " + token.trim());
                }
                else if (notExpr(token)) {
                	String[] tokens2 = token.split("(?<=not)");
                	for (String token2 : tokens2) {
                		if (bool(token2)) {
                			printMsg(match, "<bool>", token2.trim(), "boolean");
                		}
                		else if (notLiteral(token2)) {
                			printMsg(match, "<notLiteral>", token2.trim(), "not literal");
                		}
                		else {
                			printMsg(match, "<var>", token2.trim(), "variable");
                		}
                	}
                	printMsg(match, "<not_expr>", token.trim(), "not expression");
                	//System.out.println("<not>: " + token.trim());
                }
                else if (variable(token)) {
                	printMsg(match, "<var>", token.trim(), "variable");
                    //System.out.println("<var>: " + token.trim());
                }
      
                else {
                    System.out.println("Failed to parse: { " + token.trim() + " } " + "is not a recognized integer, variable, or arithmetic operator.");
                    System.exit(0);
                }

                if (right) { System.out.println("<paren>: )"); }
            }
            if (leftParen != rightParen) {
                System.out.println("Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"");
                System.exit(0);
            }
        }
        
        else if (match2) {
        	System.out.println("<not_expr>: " + cmd);
            String[] tokens = cmd.split("(?<=not)");
            for(int i = 0; i < tokens.length; i++) {
            	System.out.println(tokens[i]);
            }
            if (tokens.length < 2) {
                System.out.println("Failed to parse: { " + cmd.trim() + " } " + "is missing an and or or.");
                System.exit(0);
            }
            int leftParen = 0;
            int rightParen = 0;
            for (String token : tokens) {
                token = token.trim();
                if (token.length() > 0 && token.substring(0, 1).equals("(")) {
                    leftParen++;
                    token = token.replace("(", "");
                    System.out.println("<paren>: (");
                }
                boolean right = false;
                if (token.length() > 0 && token.substring(token.length()-1).equals(")")) {
                    right = true;
                    rightParen++;
                    token = token.replace(")", "");
                }
                if (bool(token)) {
                	 printMsg(match2, "<bool>", token.trim(), "boolean");
                	//System.out.println("<bool>: " + token.trim());
                }
                else if (andOrLiteral(token)) {
                	printMsg(match2, "<andOrLiteral>", token.trim(), "and/or literal");
                	//System.out.println("<andOrLiteral>: " + token.trim());
                }
                else if (notLiteral(token)) {
                	
                	printMsg(match2, "<notLiteral>", token.trim(), "not literal");
                	//System.out.println("<not>: " + token.trim());
                }
                else if (variable(token)) {
                	printMsg(match2, "<var>", token.trim(), "variable");
                    //System.out.println("<var>: " + token.trim());
                }
                else {
                    System.out.println("Failed to parse: { " + token.trim() + " } " + "is not a recognized integer, variable, or arithmetic operator.");
                    System.exit(0);
                }

                if (right) { System.out.println("<paren>: )"); }
            }
            if (leftParen != rightParen) {
                System.out.println("Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"");
                System.exit(0);
            }
        } else {
            System.out.println("Failed to parse: {" + cmd + "} is not a valid and/or expression or subdivision.");
            System.exit(0);
        }
        
        return match;
    }
    
    private static boolean notExpr(String cmd) {
		boolean match = false;
		Matcher m = not.matcher(cmd);
		if (m.find()) {
			match = true;
			match = match && notLiteral(m.group(1));
			match = (match && bool(m.group(2))) || (match && variable(m.group(2)));
		} 
		//printMsg(match, "<not_expr>", cmd, "not expression");
		return match;
	}

    private static boolean variable(String cmd) {
        Matcher m = var.matcher(cmd);
        boolean match = m.find();
       
        return match;
    }
    
    private static boolean bool(String cmd) {
    	Matcher m = bool.matcher(cmd);
    	boolean match = m.find();
    	//printMsg(match, "<bool>", cmd, "boolean");
    	return match;
    }
    
    private static boolean andOrLiteral(String cmd) {
    	Matcher m = andOrLiteral.matcher(cmd);
    	boolean match = m.find();
    	//printMsg(match, "<andOrLiteral>", cmd, "and/or literal");
    	return match;
    }
    
    private static boolean notLiteral(String cmd) {
    	Matcher m = notLiteral.matcher(cmd);
    	boolean match = m.find();
    	//printMsg(match, "<notLiteral>", cmd, "not literal");
    	return match;
    }
    
    private static void printMsg(boolean match, String ntName, String cmd, String item) {
		if (match)
			System.out.println(ntName + ": " + cmd);
		else
			System.out.println("Failed to parse: {" + cmd + "} is not a valid " + item + ".");
	}
}
