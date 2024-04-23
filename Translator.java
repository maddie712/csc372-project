import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Translator {
	private static HashMap<String,String> varTypes = null;
	private static HashMap<String,FuncInfo> funcs = null;
	private static FuncDec func = null;
	private static ForLoops loop = null;
	private static CondExpr condExpr = null;
	private static Line lineParser = null;

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
		Scanner reader;
		try {
			reader = new Scanner(inFile);

			varTypes = new HashMap<>();
			funcs = new HashMap<>();
			func = new FuncDec(varTypes,funcs);
			loop = new ForLoops(varTypes,funcs);
			condExpr = new CondExpr(varTypes,funcs);
			lineParser = new Line(varTypes,funcs);
			boolean readFuncs = true;
			


			while (reader.hasNextLine()) {
				String line;
				if (readFuncs) {  // parses all funcs from top of file first
					line = parseFuncs(reader, outFile);
					readFuncs = false;
					outFile.write("public static void main(String[] args) {\n");
				}
				else {
					line = reader.nextLine().trim();
				}

				if (line.isBlank()) { 
					outFile.write("\n"); 
					continue;
				}
				if (line.contains("loop(")) {
					String loopBlock = buildBlock(line, reader);
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
					String ifElseBlock = buildBlock(line, reader);
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
				else if (func.parseCmd(line)) {
					System.out.println("Failed to parse '" + line + "'. Functions must be initialized before rest of code.");
					System.exit(0);
				}
				else {
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
			outFile.write("}\n}\n");
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

	/*
	 * Handles parsing through all funcs (if any) declared at top of file input.
	 * 
	 * Returns the first line outside of functions in the file
	 */
	public static String parseFuncs(Scanner reader, FileWriter outFile) throws IOException {
		while (reader.hasNextLine()) {
			String line = reader.nextLine().trim();
			if(line.isBlank()) { 
				outFile.write("\n"); 
			}
			else if(func.parseCmd(line)) {
				// reads through the func that starts on the current line
				if (funcHelper(line, reader)) {
					System.out.println(func.translated);
					outFile.write(func.translated);
				}
				else {
					System.out.println(func.result);
					System.exit(0);
				}
			}
			// returns when stops receiving funcs (no more at top of file)
			else if (func.result.isEmpty()) {
				//System.out.println("Failed to parse '" + line + "'. Invalid func_dec expression.");
				return line;
			}
			// exits if there is an error in the current func
			else {
				System.out.println(func.result);
				System.exit(0);
			}
		}

		return "";  // if no lines left in file
	}

	/*
	 * Handles parsing through a function.
	 */
	public static boolean funcHelper(String line, Scanner reader) {
		// parse header to validate
		if(!func.parseCmd(line)) {
			System.out.println(func.result);
			System.exit(0);
		}
		boolean inFunc = true;

		// while in func (stack not empty)
		while(reader.hasNext() && inFunc) {
			line = reader.nextLine().trim();

			if(line.isBlank()) { 
				func.translated += "\n"; 
			}
			// handles final func return
			else if(func.parseReturn(line)) {
				if(reader.nextLine().strip().equals("}")) {
					inFunc = false;
					func.result += func.retResult;
					func.translated += func.translateReturn();
				}
				else {
					System.out.println("Failed to parse '" + func.name + "'. Must have '}' on line after final function return.");
					System.exit(0);
				}
			}
			// parses & translates any loops in the func
			else if (loop.parseCmd(line)) {
				String[] helpRet = loopHelper(line,reader);
				func.result += helpRet[0];
				func.translated += helpRet[1];
			}
			// parses & translates any if stmts in the func
			else if (condExpr.parseCmd(line)) {
				String[] helpRet = condExprHelper(line,reader);
				func.result += helpRet[0];
				func.translated += helpRet[1];
			}
			// parses & translates any line's in the func
			else {
				boolean match = lineParser.parseCmd(line);
				func.result += lineParser.result;
				if (match) {
					func.result += lineParser.result;
					func.translated += lineParser.translated;
				}
				else {
					System.out.println(lineParser.result);
					System.exit(0);
				}
			}
		}

		// handles if file ends before func closed
		if(inFunc) {
			System.out.println("Failed to parse '" + func.name + "'. Function must be closed with '}'.");
			System.exit(0);
		}

		// makes sure func was complete before saving
		if(func.endFunc()) {
			func.addToFuncs();
			return true;
		}
		else {
			return false;
		}		
	}

	/*
	 * Handles parsing through a loop in a function body.
	 */
	public static String[] loopHelper(String line, Scanner reader) {
		String result = "";
		String translated = "";
		boolean inLoop = true;
		String firstLine = line;

		result += loop.result;
		translated += loop.translated;
		while(reader.hasNextLine() && inLoop) {
			line = reader.nextLine().trim();

			if(func.parseReturn(line)) {
				result += func.retResult;
				translated += func.translateReturn();
			}
			else if (loop.parseCmd(line)) {
				String[] helpRet = loopHelper(line, reader);
				result += helpRet[0];
				translated += helpRet[1];
			}
			else if (condExpr.parseCmd(line)) {
				String[] helpRet = condExprHelper(line, reader);
				result += helpRet[0];
				translated += helpRet[1];
			}
			else {
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
			System.out.println("Failed to parse '" + firstLine + "'. Loop must be closed with '}'.");
		}
		
		String[] ret = {result, translated+"\n"};
		return ret;
	} 

	/*
	 * Handles parsing through a cond expr in a function body.
	 */
	public static String[] condExprHelper(String line, Scanner reader) {
		String result = "";
		String translated = "";
		boolean inExpr = true;
		String firstLine = line;

		result += condExpr.result;
		translated += condExpr.translated;
		while(reader.hasNextLine() && inExpr) {
			line = reader.nextLine().trim();

			if(func.parseReturn(line)) {
				result += func.retResult;
				translated += func.translateReturn();
			}
			else if (loop.parseCmd(line)) {
				String[] helpRet = loopHelper(line,reader);
				result += helpRet[0];
				translated += helpRet[1];
			}
			else if (condExpr.parseCmd(line)) {
				String[] helpRet = condExprHelper(line,reader);
				result += helpRet[0];
				translated += helpRet[1];
			}
			else if (lineParser.parseCmd(line)) {
				result += lineParser.result;
				translated += lineParser.translated;
			}
			else {
						System.out.println(lineParser.result);
				System.exit(0);
			}
		}

			if(inExpr) {
				System.out.println("Failed to parse loop '" + firstLine + "'. Loop must be closed with '}'.");
			}

		String[] ret = {result, translated+"\n"};
		
		return ret;
	}


}
