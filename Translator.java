import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Translator {
	static HashMap<String,String> varTypes = new HashMap<>();
	static HashMap<String,FuncInfo> funcs = new HashMap<>();

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the name of the file you want to translate: ");
		String filename = in.nextLine();
		in.close();

		File inFile = new File(filename);
		String newFilename = filename.split("\\.")[0].replace("\\", "").replace("/", "");
		FileWriter outFile = new FileWriter(newFilename + ".java");
		outFile.write("import java.util.Scanner;\n");
		outFile.write("public class " + newFilename + " {\n");
		int check = 0;
		Scanner reader;
		try {
			reader = new Scanner(inFile);
			while (reader.hasNextLine()) {
				String line = reader.nextLine().trim();
				if (line.equals("")) { continue; }
				if (line.contains("loop(")) {
					check++;
					if (check == 1) {
						outFile.write("public static void main(String[] args) {\n");
					}
					String loopBlock = buildBlock(line, reader);
					ForLoops loop = new ForLoops(varTypes, funcs);
					if (loop.parseCmd(loopBlock)) {
						System.out.println(loop.result);
						outFile.write(loop.translated);
					}
					else {
						System.out.println(loop.result);
						outFile.close();
						System.exit(0);
					}
				}
				else if (line.contains("if ")) {
					check++;
					if (check == 1) {
						outFile.write("public static void main(String[] args) {\n");
					}
					String ifElseBlock = buildBlock(line, reader);
					CondExpr condExpr = new CondExpr(varTypes,funcs);
					if (condExpr.parseCmd(ifElseBlock)) {
						System.out.println(condExpr.result);
						outFile.write(condExpr.translated);
					}
					else {
						System.out.println(condExpr.result);
						outFile.close();
						System.exit(0);
					}
				}
				else if (line.startsWith("func ")) {
					FuncDec fn = new FuncDec(varTypes, funcs);
					if (funcHelper(line, reader, fn)) {
						outFile.write(fn.translated);
					}
					else {
						System.out.println("Failed to parse function line");
						outFile.close();
						System.exit(0);
					}
				}
				else {
					check++;
					if (check == 1) {
						outFile.write("public static void main(String[] args) {\n");
					}
					Line lineParser = new Line(varTypes,funcs);
					boolean match = lineParser.parseCmd(line);
					if (match) {
						System.out.println(lineParser.result);
						outFile.write(lineParser.translated);
					}
					else {
						System.out.println(lineParser.result);
						outFile.close();
						System.exit(0);
					}
				}
			}
			if (check >= 1) {
				outFile.write("}\n");
			}
			outFile.write("}\n");
			reader.close();
			outFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find the specified file, please try again.");
		}
	}


	public static String buildBlock(String firstLine, Scanner in) {
		try {
			String result = firstLine + "\n";
			Stack<String> stack = new Stack<>();
			stack.push("{");

			while (!stack.empty()) {
				String cur = in.nextLine().trim();
				if (cur.contains("{")) {
					stack.push("{");
				}
				if (cur.contains("}")) {
					stack.pop();
				}

				result += cur + "\n";

				if (cur.contains("}") && in.hasNext("\\s*else\\s*.*")) {
					result += buildBlock(in.nextLine(), in);
				}
			}
			return result;
		} catch (Exception e) {
			System.out.println("Failed to parse: Program does not contain enough lines, or some important lines are missing");
			System.exit(0);
		}
		return "";
	}

	public static boolean funcHelper(String line, Scanner reader, FuncDec fn) {
		// parse header to validate
		if(!fn.parseCmd(line)) {
			System.out.println(fn.result);
			System.exit(0);
		}
		boolean inFunc = true;

		// while in func (stack not empty)
		while(reader.hasNext() && inFunc) {
			line = reader.nextLine().trim();

			// handles final func return
			if(fn.parseReturn(line)) {
				if(reader.nextLine().trim().equals("}")) {
					inFunc = false;  // exits func if 
					fn.result += fn.retResult;
					fn.translated += fn.translateReturn();
				}
				else {
					System.out.println("Failed to parse '" + fn.name + "'. Must have '}' on line after final function return.");
					System.exit(0);
				}
			}
			else if (line.startsWith("loop")) {
				String[] helpRet = loopHelper(line, reader, fn);
				fn.result += helpRet[0];
				fn.translated += helpRet[1];
			}
			else if (line.startsWith("if")) {
				String[] helpRet = condExprHelper(line, reader, fn);
				fn.result += helpRet[0];
				fn.translated += helpRet[1];
			}
			else {
				Line lineParser = new Line(varTypes,funcs);
				boolean match = lineParser.parseCmd(line);
				if (match) {
					fn.result += lineParser.result;
					fn.translated += lineParser.translated;
				}
				else {
					System.out.println(lineParser.result);
					System.exit(0);
				}
			}
		}

		// handles if file ends before func closed
		if(inFunc) {
			System.out.println("Failed to parse '" + fn.name + "'. Function must be closed with '}'.");
			System.exit(0);
		}

		if(fn.endFunc()) {
			fn.addToFuncs();
			return true;
		}
		else {
			return false;
		}		
	}

	public static String[] loopHelper(String line, Scanner reader, FuncDec fn) {
		//boolean inFunc = fn!=null;
		ForLoops loopBlock = new ForLoops();
		String result = "";
		String translated = "";
		boolean inLoop = true;

		if(loopBlock.parseCmd(line)) {
			result += loopBlock.result;
			translated += loopBlock.translated;
			while(reader.hasNextLine() && inLoop) {
				line = reader.nextLine().trim();

				if(fn.parseReturn(line)) {
					result += fn.retResult;
					translated += fn.translateReturn();
				}
				else if (line.startsWith("loop(")) {
					String[] helpRet = loopHelper(line, reader, fn);
					result += helpRet[0];
					translated += helpRet[1];
				}
				else if (line.startsWith("if(")) {
					String[] helpRet = condExprHelper(line, reader, fn);
					result += helpRet[0];
					translated += helpRet[1];
				}
				else {
					Line lineParser = new Line(varTypes,funcs);
					if (lineParser.parseCmd(line)) {
						result += lineParser.result;
						translated += lineParser.translated;
					}
					else {
						System.out.println(lineParser.result);
						System.exit(0);
					}
				}
			}

			if(inLoop) {
				System.out.println("Failed to parse loop '" + "*fn name*" + "'. Loop must be closed with '}'.");
			}

		}
		else {
			System.out.println(loopBlock.result);
			System.exit(0);
		}

		String[] ret = {result, translated+"\n"};
		
		return ret;
	} 

	public static String[] condExprHelper(String line, Scanner reader, FuncDec fn) {
		//boolean inFunc = fn!=null;
		CondExpr condBlock = new CondExpr();
		String result = "";
		String translated = "";
		boolean inExpr = true;

		if(condBlock.parseCmd(line)) {
			result += condBlock.result;
			translated += condBlock.translated;
			while(reader.hasNextLine() && inExpr) {
				line = reader.nextLine().trim();

				if(fn.parseReturn(line)) {
					result += fn.retResult;
					translated += fn.translateReturn();
				}
				else if (line.startsWith("loop(")) {
					String[] helpRet = loopHelper(line, reader, fn);
					result += helpRet[0];
					translated += helpRet[1];
				}
				else if (line.startsWith("if(")) {
					String[] helpRet = condExprHelper(line, reader, fn);
					result += helpRet[0];
					translated += helpRet[1];
				}
				else {
					Line lineParser = new Line(varTypes,funcs);
					if (lineParser.parseCmd(line)) {
						result += lineParser.result;
						translated += lineParser.translated;
					}
					else {
						System.out.println(lineParser.result);
						System.exit(0);
					}
				}
			}

			if(inExpr) {
				System.out.println("Failed to parse loop '" + "*fn name*" + "'. Loop must be closed with '}'.");
			}

		}
		else {
			System.out.println(condBlock.result);
			System.exit(0);
		}

		String[] ret = {result, translated+"\n"};
		
		return ret;
	}

}
