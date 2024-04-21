import java.util.ArrayList;
import java.util.HashMap;

public class TestVarAssign {
	private static VarAssign va = null;
	private static HashMap<String, String> varTypes = null;
	private static HashMap<String, FuncInfo> funcs = null;
	private static Line line = null;

	public static void main(String[] args) {
		varTypes = new HashMap<>();
		funcs = new HashMap<>();
		va = new VarAssign(varTypes, funcs);

		if (!testInts()) {
			System.out.println("Failed int assignment.\n");
		}

		if (!testBools()) {
			System.out.println("Failed bool assignment.\n");
		}

		if (!testStr()) {
			System.out.println("Failed string assignment.\n");
		}

		if (!testVar()) {
			System.out.println("Failed variable assignment.\n");
		}

		if (!testFuncCall()) {
			System.out.println("Failed function call assignment.\n");
		}

		if (!testInvalid()) {
			System.out.println("Failed invalid assignments.\n");
		}

		if (!testLine()) {
			System.out.println("Failed Line assignments.\n");
		}
	}

	private static boolean testInts() {
		System.out.println("Testing int assignments...");

		boolean match = true;

		match = match && va.parseCmd("x = 515");
		// System.out.println(va.result);
		match = match && va.translated.equals("int x = 515;\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("y =10/x");
		// System.out.println(va.result);
		match = match && va.translated.equals("int y = 10/x;\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("z= x*y");
		// System.out.println(va.result);
		match = match && va.translated.equals("int z = x*y;\n");
		// System.out.println(va.translated);

		System.out.println();
		return match;
	}

	private static boolean testBools() {
		System.out.println("Testing bool assignments...");

		boolean match = true;

		match = match && va.parseCmd("a = true");
		// System.out.println(va.result);
		match = match && va.translated.equals("boolean a = true;\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("b= a and false");
		// System.out.println(va.result);
		match = match && va.translated.equals("boolean b = a&&false;\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("c = a or b");
		// System.out.println(va.result);
		match = match && va.translated.equals("boolean c = a||b;\n");
		// System.out.println(va.translated);

		System.out.println();
		return match;
	}

	private static boolean testStr() {
		System.out.println("Testing string assignments...");

		boolean match = true;

		match = match && va.parseCmd("str1 = \"hello world\"");
		// System.out.println(va.result);
		match = match && va.translated.equals("String str1 = \"hello world\";\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("str2 = \"123\"");
		// System.out.println(va.result);
		match = match && va.translated.equals("String str2 = \"123\";\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("str2 = \"true\"");
		// System.out.println(va.result);
		match = match && va.translated.equals("str2 = \"true\";\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("str3 = \"\"");
		// System.out.println(va.result);
		match = match && va.translated.equals("String str3 = \"\";\n");
		// System.out.println(va.translated);

		match = match && !va.parseCmd("str4 = \"\"\"");
		// System.out.println(va.result);
		// match = match && va.translated.equals("String str1 = \"\";\n");
		// System.out.println(va.translated);

		System.out.println();
		return match;
	}

	private static boolean testVar() {
		System.out.println("Testing var assignments...");

		boolean match = true;

		match = match && va.parseCmd("z = x");
		// System.out.println(va.result);
		match = match && va.translated.equals("z = x;\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("d = a");
		// System.out.println(va.result);
		match = match && va.translated.equals("boolean d = a;\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("str5 = str1");
		// System.out.println(va.result);
		match = match && va.translated.equals("String str5 = str1;\n");
		// System.out.println(va.translated);

		System.out.println();
		return match;
	}

	private static boolean testFuncCall() {
		System.out.println("Testing function call assignments...");

		boolean match = true;
		FuncInfo foo = new FuncInfo();
		foo.name = "foo";
		foo.type = "int";
		foo.params = new ArrayList<>();
		FuncInfo bar = new FuncInfo();
		bar.name = "bar";
		bar.type = "boolean";
		bar.params = new ArrayList<>();
		FuncInfo baz = new FuncInfo();
		baz.name = "baz";
		baz.type = "String";
		baz.params = new ArrayList<>();
		funcs.put("foo", foo);
		funcs.put("bar", bar);
		funcs.put("baz", baz);

		match = match && va.parseCmd("foo = foo()");
		// System.out.println(va.result);
		match = match && va.translated.equals("int foo = foo();\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("bar = bar()");
		// System.out.println(va.result);
		match = match && va.translated.equals("boolean bar = bar();\n");
		// System.out.println(va.translated);

		match = match && va.parseCmd("baz = baz()");
		// System.out.println(va.result);
		match = match && va.translated.equals("String baz = baz();\n");
		// System.out.println(va.translated);

		System.out.println();
		return match;
	}

	private static boolean testInvalid() {
		System.out.println("Testing invalid assignments...");

		boolean match = true;

		// test not a var assign
		match = match && !va.parseCmd("3+7");
		match = match && va.result.equals("");

		// tests mismatch types
		match = match && !va.parseCmd("a = 17");
		// System.out.println(va.result);
		match = match && va.result.equals("Failed to parse 'a'. Mismatch type assign.\n");

		// test invalid variable name
		match = match && !va.parseCmd("123 = 123");
		// System.out.println(va.result);
		match = match && va.result.equals("Failed to parse '123'. Invalid variable name.\n");

		// tests invalid value
		match = match && !va.parseCmd("m = 27ab");
		// System.out.println(va.result);
		match = match && va.result.equals("Failed to parse '27ab'. Invalid value to assign.\n");
		match = match && !va.parseCmd("n = abc");
		// System.out.println(va.result);
		match = match && va.result.equals("Failed to parse 'abc'. Invalid value to assign.\n");

		System.out.println();
		return match;
	}

	private static boolean testLine() {
		varTypes = new HashMap<>();
		funcs = new HashMap<>();
		line = new Line(varTypes, funcs);

		if (!testLineInts()) {
			System.out.println("Failed Line int assignment.\n");
			return false;
		}

		if (!testLineBools()) {
			System.out.println("Failed Line bool assignment.\n");
			return false;
		}

		if (!testLineStr()) {
			System.out.println("Failed Line string assignment.\n");
			return false;
		}

		if (!testLineVar()) {
			System.out.println("Failed Line variable assignment.\n");
			return false;
		}

		if (!testLineFuncCall()) {
			System.out.println("Failed Line function call assignment.\n");
			return false;
		}

		if (!testLineInvalid()) {
			System.out.println("Failed Line invalid assignments.\n");
			return false;
		}

		return true;
	}

	private static boolean testLineInts() {
		System.out.println("Testing Line int assignments...");

		boolean match = true;

		match = match && line.parseCmd("x = 515");
		// System.out.println(line.result);
		match = match && line.translated.equals("int x = 515;\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("y =10/x");
		// System.out.println(line.result);
		match = match && line.translated.equals("int y = 10/x;\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("z= x*y");
		// System.out.println(line.result);
		match = match && line.translated.equals("int z = x*y;\n");
		// System.out.println(line.translated);

		System.out.println();
		return match;
	}

	private static boolean testLineBools() {
		System.out.println("Testing Line bool assignments...");

		boolean match = true;

		match = match && line.parseCmd("a = true");
		// System.out.println(line.result);
		match = match && line.translated.equals("boolean a = true;\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("b= a and false");
		// System.out.println(line.result);
		match = match && line.translated.equals("boolean b = a&&false;\n");
		System.out.println(line.translated);

		match = match && line.parseCmd("c = a or b");
		// System.out.println(line.result);
		match = match && line.translated.equals("boolean c = a||b;\n");
		// System.out.println(line.translated);

		System.out.println();
		return match;
	}

	private static boolean testLineStr() {
		System.out.println("Testing string assignments...");

		boolean match = true;

		match = match && line.parseCmd("str1 = \"hello world\"");
		// System.out.println(line.result);
		match = match && line.translated.equals("String str1 = \"hello world\";\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("str2 = \"123\"");
		// System.out.println(line.result);
		match = match && line.translated.equals("String str2 = \"123\";\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("str2 = \"true\"");
		// System.out.println(line.result);
		match = match && line.translated.equals("str2 = \"true\";\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("str3 = \"\"");
		// System.out.println(line.result);
		match = match && line.translated.equals("String str3 = \"\";\n");
		// System.out.println(line.translated);

		match = match && !line.parseCmd("str4 = \"\"\"");
		// System.out.println(line.result);
		// match = match && line.translated.equals("String str1 = \"\";\n");
		// System.out.println(line.translated);

		System.out.println();
		return match;
	}

	private static boolean testLineVar() {
		System.out.println("Testing var assignments...");

		boolean match = true;

		match = match && line.parseCmd("z = x");
		// System.out.println(line.result);
		match = match && line.translated.equals("z = x;\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("d = a");
		// System.out.println(line.result);
		match = match && line.translated.equals("boolean d = a;\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("str5 = str1");
		// System.out.println(line.result);
		match = match && line.translated.equals("String str5 = str1;\n");
		// System.out.println(line.translated);

		System.out.println();
		return match;
	}

	private static boolean testLineFuncCall() {
		System.out.println("Testing function call assignments...");

		boolean match = true;
		FuncInfo foo = new FuncInfo();
		foo.name = "foo";
		foo.type = "int";
		foo.params = new ArrayList<>();
		FuncInfo bar = new FuncInfo();
		bar.name = "bar";
		bar.type = "boolean";
		bar.params = new ArrayList<>();
		FuncInfo baz = new FuncInfo();
		baz.name = "baz";
		baz.type = "String";
		baz.params = new ArrayList<>();
		funcs.put("foo", foo);
		funcs.put("bar", bar);
		funcs.put("baz", baz);

		match = match && line.parseCmd("foo = foo()");
		// System.out.println(line.result);
		match = match && line.translated.equals("int foo = foo();\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("bar = bar()");
		// System.out.println(line.result);
		match = match && line.translated.equals("boolean bar = bar();\n");
		// System.out.println(line.translated);

		match = match && line.parseCmd("baz = baz()");
		// System.out.println(line.result);
		match = match && line.translated.equals("String baz = baz();\n");
		// System.out.println(line.translated);

		System.out.println();
		return match;
	}

	private static boolean testLineInvalid() {
		System.out.println("Testing invalid assignments...");

		boolean match = true;

		// test not a var assign
		match = match && !line.parseCmd("3+7");
		System.out.println(line.result);
		// match = match && line.result.equals("");

		// tests mismatch types
		match = match && !line.parseCmd("a = 17");
		System.out.println(line.result);
		// match = match && line.result.equals("Failed to parse 'a'. Mismatch type assign.\n");

		// test invalid variable name
		match = match && !line.parseCmd("123 = 123");
		System.out.println(line.result);
		// match = match && line.result.equals("Failed to parse '123'. Invalid variable name.\n");

		// tests invalid value
		match = match && !line.parseCmd("m = 27ab");
		System.out.println(line.result);
		// match = match && line.result.equals("Failed to parse '27ab'. Invalid value to assign.\n");
		match = match && !line.parseCmd("n = abc");
		System.out.println(line.result);
		// match = match && line.result.equals("Failed to parse 'abc'. Invalid value to assign.\n");

		System.out.println();
		return match;
	}

}
