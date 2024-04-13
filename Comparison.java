import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comparison {
    private static Pattern comp = Pattern.compile("^([\\s\\S]+)\\s*((>=|<=|==|<|>))\\s*([\\s\\S]+)$");
	private static Pattern intVal = Pattern.compile("^\\d+$");
	private static Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private static Pattern op = Pattern.compile("^((>=|<=|==|<|>))$");


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
        boolean match = comparisonExpr(cmd);
        if (match) {
        
        }
        else {

        }
        return match;
    }

    private static boolean comparisonExpr(String cmd) {
        Matcher m = comp.matcher(cmd);
        boolean match = m.find();
        if (match) {
            String[] tokens;
            System.out.println("<comparison_expr>: " + cmd);
            if (cmd.contains(">=") || cmd.contains("<=")) {
                tokens = cmd.split("(?<=(=))|(?=(<|>))");
            }
            else {
                tokens = cmd.split("(?<=(==|<|>))|(?=(==|<|>))");
            }
            if (tokens.length < 3) {
                System.out.println("Failed to parse: { " + cmd.trim() + " } " + "is missing an operand.");
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

                if (intValue(token.trim())) {
                    System.out.println("<int>: " + token.trim());
                }
                else if (variable(token)) {
                    System.out.println("<var>: " + token.trim());
                }
                else if (operator(token)) {
                    System.out.println("<operator>: " + token.trim());
                }
                else if (Arithmetic.parseCmd(token)) { }
                else {
                    System.out.println("Failed to parse: { " + token.trim() + " } " + "is not a recognized integer, variable, or comparison operator.");
                    System.exit(0);
                }

                if (right) { System.out.println("<paren>: )"); }
            }
            if (leftParen != rightParen) {
                System.out.println("Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"");
                System.exit(0);
            }
        } else {
            System.out.println("Failed to parse: {" + cmd + "} is not a valid comparison expression.");
            System.exit(0);
        }
        return match;
    }

    private static boolean intValue(String cmd) {
        Matcher m = intVal.matcher(cmd);
        boolean match = m.find();
        return match;
    }

    private static boolean variable(String cmd) {
        Matcher m = var.matcher(cmd);
        boolean match = m.find();
        return match;
    }

    private static boolean operator(String cmd) {
        Matcher m = op.matcher(cmd);
        boolean match = m.find();
        return match;
    }
}
