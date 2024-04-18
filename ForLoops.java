import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForLoops {
    private Scanner scanner;
    private Pattern loopPattern;

    public ForLoops() {
        String loopRegex = "\\s*loop\\(([^,{}]+)(?:,([^{}]+))?\\)\\s*\\{(.+)\\}\\s*";
        loopPattern = Pattern.compile(loopRegex, Pattern.DOTALL);
        scanner = new Scanner(System.in);
    }

    public void startInteractiveSession() {
        System.out.println("Enter loop expression or type 'exit' to quit:");
        String input = scanner.nextLine().trim();
        while (!input.equalsIgnoreCase("exit")) {
            translateLoop(input);
            input = scanner.nextLine().trim();
        }
        scanner.close();
    }

    public boolean translateLoop(String input) {
        Matcher matcher = loopPattern.matcher(input);
        if (matcher.matches()) {
            String firstExpression = matcher.group(1).trim();
            String secondExpression = matcher.group(2) != null ? matcher.group(2).trim() : null;
            String block = matcher.group(3).trim();

            if (secondExpression != null) {
                System.out.println("Detected a dual-expression loop:");
                System.out.println("loop(" + firstExpression + ", " + secondExpression + ") {");
            } else {
                System.out.println("Detected a single-expression loop:");
                System.out.println("loop(" + firstExpression + ") {");
            }
            System.out.println("\t" + block);
            System.out.println("}");
            return true;
        } else {
            System.out.println("Failed to parse: {" + input + "} is not a valid loop expression.");
            return false;
        }
    }

    
}

