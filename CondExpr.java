import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CondExpr {
    private Pattern ifElsePattern; // Pattern to match if-else conditionals
    private Pattern ifPattern; // Pattern to match if-only conditionals
    private Scanner scanner;

    public CondExpr() {
        ifElsePattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{(.*)\\}\\s*else\\s*\\{(.*)\\}\\s*$", Pattern.DOTALL);
        ifPattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{(.*)\\}\\s*$", Pattern.DOTALL);
        scanner = new Scanner(System.in);
    }

    public void startInteractiveSession() {
        System.out.print(">> ");
        String input = scanner.nextLine().trim();
        while (!input.equals("exit")) {
            translateCondExpr(input);
            System.out.print(">> ");
            input = scanner.nextLine().trim();
        }
        scanner.close();
    }

    public boolean translateCondExpr(String input) {
        Matcher ifElseMatcher = ifElsePattern.matcher(input);
        Matcher ifMatcher = ifPattern.matcher(input);

        if (ifElseMatcher.matches()) {
            String condition = ifElseMatcher.group(1).trim();
            String ifBlock = ifElseMatcher.group(2).trim();
            String elseBlock = ifElseMatcher.group(3).trim();

            System.out.println("if (" + condition + ") {");
            System.out.println("\t" + ifBlock);
            System.out.println("} else {");
            System.out.println("\t" + elseBlock);
            System.out.println("}");
            return true;
        } else if (ifMatcher.matches()) {
            String condition = ifMatcher.group(1).trim();
            String ifBlock = ifMatcher.group(2).trim();

            System.out.println("if (" + condition + ") {");
            System.out.println("\t" + ifBlock);
            System.out.println("}");
            return true;
        } else {
            System.out.println("Failed to parse: {" + input + "} is not a valid conditional expression.");
            return false;
        }
    }

}

