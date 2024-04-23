import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForLoops {
	private String loopRegex = "\\s*loop\\(([^,{}]+)(?:,([^{}]+))?\\)\\s*\\{(.+)\\}\\s*";
	private Pattern loopPattern = Pattern.compile(loopRegex, Pattern.DOTALL);
	private Pattern intVal = Pattern.compile("^\\d+$");
	private Pattern var = Pattern.compile("^[a-zA-Z][a-zA-z_0-9]*$");
	private MultDiv multDiv1 = null;
	private MultDiv multDiv2 = null;
	private Condition condition = null;
	private Line line = null;
    private HashMap<String,String> varTypes = null;
	private HashMap<String, FuncInfo> funcs = null;
	private String[] vars = {"_a","_b","_c","_d","_e","_f","_g","_h","_i","_j","_k","_l","_m"};
	private static int curVar = 0;

	public boolean match;
	public String result = "";
	public String translated = "";


	public ForLoops(HashMap<String, String> varTypes, HashMap<String, FuncInfo> funcs) {
		this.varTypes = varTypes;
		this.funcs = funcs;
		line = new Line(varTypes, funcs);
        multDiv1 = new MultDiv(varTypes);
        multDiv2 = new MultDiv(varTypes);
        condition = new Condition(varTypes);
	}

	public boolean parseCmd(String cmd) {
        result = "";
        translated = "";
		match = translateLoop(cmd);
		return match;
	}

	public boolean translateLoop(String input) {
		Matcher matcher = loopPattern.matcher(input);
		if (matcher.matches()) {
			String firstExpression = matcher.group(1).trim();
			String secondExpression = matcher.group(2) != null ? matcher.group(2).trim() : null;
			String block = matcher.group(3).trim();
			String c = vars[curVar];
			curVar++;

			if (secondExpression != null) {
				Matcher v1 = var.matcher(firstExpression);
				Matcher v2 = var.matcher(secondExpression);
				Matcher i1 = intVal.matcher(firstExpression);
				Matcher i2 = intVal.matcher(secondExpression);
				boolean first = v1.find() || i1.find();
				boolean second = v2.find() || i2.find();
				if (multDiv1.parseCmd(firstExpression) && multDiv2.parseCmd(secondExpression)) {
					result += "<loop>: loop(" + firstExpression + ", " + secondExpression + ") {\n";
					result += multDiv1.result + multDiv2.result;
					translated += "for (int " + c + "=" + multDiv1.translated + "; " + c + "<" + multDiv2.translated + "; " + c + "++) {\n";
				} else if (first && second) {
					result += "<loop>: loop(" + firstExpression + ", " + secondExpression + ") {\n";
					result += "<loop_val>: " + firstExpression + "\n<loop_val>: " + secondExpression + "\n";
					translated += "for (int " + c + "=" + firstExpression + "; " + c + "<" + secondExpression + "; " + c + "++) {\n";
				} else {
					result = "Failed to parse: { " + input.trim() + " } " + "is not a recognized loop definition.\n";
					translated = "";
					return false;
				}
			} else {
				Matcher v = var.matcher(firstExpression);
				Matcher i = intVal.matcher(firstExpression);
				if (condition.parseCmd(firstExpression)) {
					result += "<loop>: loop(" + firstExpression + ") {\n";
					result += condition.result;
					translated += "while (" + condition.translated + ") {\n";
				} else if (multDiv1.parseCmd(firstExpression)) {
					result += "<loop>: loop(" + firstExpression + ") {\n";
					result += condition.result;
					translated += "for (int " + c + "=0; " + c + "<" + multDiv1.translated + "; " + c + "++) {\n";
				} else if (v.find()) {
					result += "<loop>: loop(" + firstExpression + ") {\n";
					result += "<var>: " + firstExpression + "\n";
					translated += "while (" + firstExpression + "!= 0) {\n";
				} else if (i.find()) {
					result += "<loop>: loop(" + firstExpression + ") {\n";
					result += "<int>: " + firstExpression + "\n";
					translated += "for (int " + c + "=0; " + c + "<" + firstExpression + "; " + c + "++) {\n";
				} else {
					result = "Failed to parse: { " + input.trim() + " } " + "is not a recognized loop definition.\n";
					translated = "";
					return false;
				}
			}

			result += "<block>: \n";
			String[] lines = block.split("\n");
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
					line.parseCmd(lines[i]);
					if (!line.match) {
						result = line.result;
						return false;
					}
					result += line.result;
					translated += line.translated;
					i += 1;
				}
			}

			result += "}\n";
			translated += "}\n";
			return true;
		} else {
			result = "Failed to parse: {" + input + "} is not a valid loop expression.";
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
