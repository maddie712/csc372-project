import java.util.Scanner;

public class Condition {

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
        boolean match = condition(cmd);
        if (match) {
        
        }
        else {

        }
        return match;
    }

    private static boolean condition(String cmd) {
        System.out.println("<condition>: " + cmd);

        boolean match = false;
        String token = cmd.trim();
        
        boolean comp = CompExpr.parseCmd(token);
        boolean andOr = AndOr.parseCmd(token);
        if (comp) { match = true; }
        else if (andOr) { match = true; }
        else {
            System.out.println("Failed to parse: {" + cmd + "} is not a valid condition.");
            System.exit(0);
        }
        return match;
    }

}
