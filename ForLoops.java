import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForLoops {
    private Pattern forLoopPattern;
    private Pattern whileLoopPattern;
    private Scanner scanner;

    public ForLoops() {
        forLoopPattern = Pattern.compile("^\\s*for\\s*\\((.+);\\s*(.+);\\s*(.+)\\+\\+\\)\\s*\\{(.*)\\}\\s*$");
        whileLoopPattern = Pattern.compile("^\\s*while\\s*\\((.+)\\)\\s*\\{(.*)\\}\\s*$");
        scanner = new Scanner(System.in);
    }

    public void startInteractiveSession() {
        System.out.print(">> ");
        String input = scanner.nextLine().trim();
        while (!input.equals("exit")) {
            translateLoop(input);
            System.out.print(">> ");
            input = scanner.nextLine().trim();
        }
        scanner.close();
    }

    public boolean translateLoop(String input) {
        Matcher forLoopMatcher = forLoopPattern.matcher(input);
        Matcher whileLoopMatcher = whileLoopPattern.matcher(input);

        if (forLoopMatcher.matches()) {
            String initialization = forLoopMatcher.group(1).trim();
            String condition = forLoopMatcher.group(2).trim();
            String update = forLoopMatcher.group(3).trim();
            String block = forLoopMatcher.group(4).trim();

            // Print translation
            System.out.println("for (" + initialization + "; " + condition + "; " + update + ") {");
            System.out.println("\t" + block);
            System.out.println("}");
            return true;
        } else if (whileLoopMatcher.matches()) {
            String condition = whileLoopMatcher.group(1).trim();
            String block = whileLoopMatcher.group(2).trim();

            // Print translation
            System.out.println("while (" + condition + ") {");
            System.out.println("\t" + block);
            System.out.println("}");
            return true;
        } else {
            System.out.println("Failed to parse: {" + input + "} is not a valid loop expression.");
            return false;
        }
    }

}
