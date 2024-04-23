import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CondExpr {
	private Pattern ifElsePattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{\\s*(.*)\\s*\\}\\s*else\\s*\\{\\s*(.*)\\s*\\}\\s*$", Pattern.DOTALL);
	private Pattern ifPattern = Pattern.compile("^\\s*if\\s*\\((.+)\\)\\s*\\{\\s*(.*)\\s*\\}\\s*$", Pattern.DOTALL);

	private Condition cond = null;
	private Line line1 = null;
	private Line line2 = null;
	private HashMap<String,String> varTypes;
	private HashMap<String,FuncInfo> funcs;

	public boolean match;
	public String result = "";
	public String translated = "";


	public CondExpr(HashMap<String, String> varTypes, HashMap<String, FuncInfo> funcs) {
		this.varTypes = varTypes;
		this.funcs = funcs;
        cond = new Condition(varTypes);
		line1 = new Line(varTypes, funcs);
		line2 = new Line(varTypes, funcs);
	}

	public boolean parseCmd(String cmd) {
        result = "";
        translated = "";
		match = translateCondExpr(cmd);
		return match;
	}

	public boolean translateCondExpr(String input) {
		Matcher ifElseMatcher = ifElsePattern.matcher(input);
		Matcher ifMatcher = ifPattern.matcher(input);

		if (ifElseMatcher.matches() || ifMatcher.matches()) {
			String condition = input.substring(input.indexOf("(")+1, input.indexOf(")")).trim();
			if (input.contains("else")) {
				input = input.split("else")[0];
			}
			String ifBlock = "";
			try { ifBlock = input.substring(input.indexOf("{")+1, input.lastIndexOf("}")).trim(); }
			catch (Exception e) { System.out.println("Failed to parse: Program does not contain enough lines, or some important lines are missing"); System.exit(0);}

			if (cond.parseCmd(condition)) {
				result += "<if_dec>: if (" + condition + ") {\n";
				translated += "if (" + cond.translated + ") {\n";
			} else {
				result = "Failed to parse: {" + condition + "} is not a valid condition.\n";
				translated = "";
				return false;
			}

			result += "<block>: \n";
			String[] lines = ifBlock.split("\n");
			int i = 0;
			while (i < lines.length) {
				if (lines[i].contains("loop(")) {
					int loopBlocklen = findBlock(i + 1, lines);
					String loopBlock = buildBlock(i, loopBlocklen, lines);
					ForLoops loop = new ForLoops(varTypes, funcs);
					if (loop.parseCmd(loopBlock)) {
						result += loop.result;
						translated += loop.translated;
					}
					i = loopBlocklen + 1;
				} else if (lines[i].contains("if ")) {
					int ifElseBlocklen = findBlock(i + 1, lines);
					String ifElseBlock = buildBlock(i, ifElseBlocklen, lines);
					CondExpr condExpr = new CondExpr(varTypes, funcs);
					if (condExpr.parseCmd(ifElseBlock)) {
						result += condExpr.result;
						translated += condExpr.translated;
					}
					i = ifElseBlocklen + 1;
				} else {
					line1.parseCmd(lines[i]);
					if (!line1.match) {
						result = line1.result;
						return false;
					}
					result += line1.result;
					translated += line1.translated;
					i += 1;
				}
			}

			translated += "}\n";

			if (ifElseMatcher.matches()) {
				String elseBlock = ifElseMatcher.group(3).trim();

				translated += "else {\n";
				result += "<else>: } else {\n<block>: \n";

				lines = elseBlock.split("\n");
				i = 0;
				while (i < lines.length) {
					if (lines[i].contains("loop(")) {
						int loopBlocklen = findBlock(i + 1, lines);
						String loopBlock = buildBlock(i, loopBlocklen, lines);
						ForLoops loop = new ForLoops(varTypes, funcs);
						if (loop.parseCmd(loopBlock)) {
							result += loop.result;
							translated += loop.translated;
						}
						i = loopBlocklen + 1;
					} else if (lines[i].contains("if ")) {
						int ifElseBlocklen = findBlock(i + 1, lines);
						String ifElseBlock = buildBlock(i, ifElseBlocklen, lines);
						CondExpr condExpr = new CondExpr(varTypes, funcs);
						if (condExpr.parseCmd(ifElseBlock)) {
							result += condExpr.result;
							translated += condExpr.translated;
						}
						i = ifElseBlocklen + 1;
					} else {
						line2.parseCmd(lines[i]);
						if (!line2.match) {
							result = line2.result;
							return false;
						}
						result += line2.result;
						translated += line2.translated;
						i += 1;
					}
				}

				translated += "}\n";
			}
			return true;
		} else {
			result = "Failed to parse: {" + input + "} is not a valid conditional expression.\n";
			return false;
		}
	}

	public int findBlock(int index, String[] in) {
		Stack<String> stack = new Stack<>();
		stack.push("{");

		while (!stack.empty()) {
			String cur = in[index];
			if (cur.contains("{")) {
				stack.push("{");
			}
			if (cur.contains("}")) {
				stack.pop();
			}
			index += 1;

			if (cur.contains("}") && index + 1 < in.length && in[index + 1].contains("else")) {
				index += findBlock(index, in);
			}
		}
		return index - 1;
	}

	public String buildBlock(int start, int end, String[] list) {
		result = "";
		for (int i = start; i <= end; i++) {
			result += list[i];
		}

		return result;
	}
}
