import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Print {
    private static Pattern print = Pattern.compile("^(print\\().*\\)$");
	private static Pattern intVal = Pattern.compile("^\\d+$");
	private static Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
    private static Pattern string = Pattern.compile("^\"\\w*\"$");


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
        boolean match = print(cmd);
        if (match) {
        
        }
        else {

        }
        return match;
    }

    private static boolean print(String cmd) {
        Matcher m = print.matcher(cmd);
        boolean match = m.find();
        if (match) {
            System.out.println("<print>: " + cmd);
            String token = cmd.substring(cmd.indexOf("(")+1, cmd.length()-1);

            token = token.trim();
            
            if (intValue(token.trim())) {
                System.out.println("<int>: " + token.trim());
            }
            else if (variable(token)) {
                System.out.println("<var>: " + token.trim());
            }
            else if (string(token)) {
                System.out.println("<string>: " + token.trim());
            }
            else if (MultDiv.parseCmd(token)) {}
            else if (Condition.parseCmd(token)) {}
            else {
                System.out.println("Failed to parse: { " + token.trim() + " } " + "is not a recognized printable value.");
                System.exit(0);
            }
        } else {
            System.out.println("Failed to parse: {" + cmd + "} is not a valid print expression.");
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

    private static boolean string(String cmd) {
        Matcher m = string.matcher(cmd);
        boolean match = m.find();
        return match;
    }
}
