import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Arithmetic {
    private static Pattern expr = Pattern.compile("^(\\S+)\\s*([+\\-*/%])\\s*(\\S+)$");
	private static Pattern intVal = Pattern.compile("^\\d+$");
	private static Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private static Pattern op = Pattern.compile("^[+\\-*/%]$");

    // public Arithmetic(String attempt) {
    //     parseCmd(attempt);
    // }

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

    private static void parseCmd(String cmd) {
        boolean match = arithmeticExpr(cmd);
        if (match) {
            System.out.println("<stmt>: " + cmd);
        }
    }

    private static boolean arithmeticExpr(String cmd) {
        Matcher m = expr.matcher(cmd);
        boolean match = m.find();
        if (match) {
            System.out.println("<arithmetic_expr>: " + cmd);
            String[] tokens = cmd.split("(?=[+\\-*/])|(?<=[+\\-*/])");
            int leftParen = 0;
            int rightParen = 0;
            for (String token : tokens) {
                token = token.trim();
                if (token.substring(0, 1).equals("(")) {
                    leftParen++;
                    token = token.replace("(", "");
                    System.out.println("<paren>: (");
                }
                boolean right = token.substring(token.length()-1).equals(")");
                if (right) {
                    rightParen++;
                    token = token.replace(")", "");
                }
                boolean integer = intValue(token);
                boolean variable = variable(token);
                boolean operator = operator(token);
                if (integer) {
                    System.out.println("<int>: " + token);
                }
                else if (variable) {
                    System.out.println("<var>: " + token);
                }
                else if (operator) {
                    System.out.println("<operator>: " + token);
                }
                else {
                    System.out.println("Failed to parse: { " + token + " } " + "is not a recognized integer, variable, or operator.");
                    System.exit(0);
                }

                if (right) { System.out.println("<paren>: )"); }
            }
            if (leftParen != rightParen) {
                System.out.println("Failed to parse: { " + cmd + " } " + "is missing a \"(\" or \")\"");
                System.exit(0);
            }
        } else {
            System.out.println("Failed to parse: {" + cmd + "} is not a valid arithmetic expression.");
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
