import java.util.Scanner; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CondExpr {
    private static Pattern condPattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{(.*)\\}\\s*else\\s*\\{(.*)\\}\\s*$");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">> ");
        String input = scanner.nextLine().trim();
        while (!input.equals("exit")) {
            translateCondExpr(input);
            System.out.print(">> ");
            input = scanner.nextLine().trim();
        }
        scanner.close();
    }

    public static boolean translateCondExpr(String input) {
        Matcher matcher = condPattern.matcher(input);
        if (matcher.matches()) {
            String condition = matcher.group(1).trim();
            String ifBlock = matcher.group(2).trim();
            String elseBlock = matcher.group(3).trim();

            // Print translation
            System.out.println("if (" + condition + ") {");
            System.out.println("\t" + ifBlock);
            System.out.println("} else {");
            System.out.println("\t" + elseBlock);
            System.out.println("}");

            return true;
        } else {
            System.out.println("Failed to parse: {" + input + "} is not a valid conditional expression.");
            return false;
        }
    }
}
