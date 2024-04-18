import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Translator {
	ArrayList<String> intVars = new ArrayList<>();
	ArrayList<String> stringVars = new ArrayList<>();
	ArrayList<String> boolVars = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the name of the file you want to translate: ");
		String filename = in.nextLine();
		in.close();

		File inFile = new File(filename);
		FileWriter outFile = initializeFile(filename);
		Scanner reader;
		try {
			reader = new Scanner(inFile);
			while (reader.hasNextLine()) {
				String line = reader.nextLine().trim();
				if (line.contains("loop")) {
					String loopBlock = buildBlock(line, reader);
					ForLoops loop = new ForLoops();
					if (loop.parseCmd(loopBlock)) {
						System.out.println(loop.result);
						outFile.write(loop.translated + "\n");
					}
					else {
						System.out.println(loop.result);
						System.exit(0);
					}
				}
				else if (line.contains("if")) {
					String ifElseBlock = buildBlock(line, reader);
					CondExpr condExpr = new CondExpr();
					if (condExpr.parseCmd(ifElseBlock)) {
						System.out.println(condExpr.result);
						outFile.write(condExpr.translated + "\n");
					}
					else {
						System.out.println(condExpr.result);
						System.exit(0);
					}
				}
				else if (line.contains("func")) {
					String funcBlock = buildBlock(line, reader);
					// TODO
					FuncDec func = new FuncDec(null, null);
					if (func.parseCmd(funcBlock)) {
						System.out.println(func.result);
						//outFile.write(func.translated + "\n");
					}
					else {
						System.out.println(func.result);
						System.exit(0);
					}
				}
				else {
					Line lineParser = new Line();
					boolean match = lineParser.parseCmd(line);
					System.out.println(lineParser.result);
					if (match) {
						outFile.write(lineParser.translated + "\n");
					}
					else {
						System.exit(0);
					}
				}
			}
			outFile.write("}");
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find the specified file, please try again.");
		}
	}

	public static FileWriter initializeFile(String oldFilename) {
		String newFilename = oldFilename.split("\\.")[0];
		//String[] newFilename = oldFilename.split("\\.");
		//System.out.print(oldFilename);
		//System.out.print(newFilename.length);
		FileWriter out;
		try {
			out = new FileWriter(newFilename + ".java");
			out.write("public class " + newFilename + "{");
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String buildBlock(String firstLine, Scanner in) {
		String result = firstLine + "\n";
		Stack<String> stack = new Stack<>();
		stack.push("{");

		while (!stack.empty()) {
			String cur = in.nextLine();
			if (cur.contains("{")) {
				stack.push("{");
			}
			if (cur.contains("}")) {
				stack.pop();
			}
			result += cur + "\n";
		}
		return result;
	}

}
