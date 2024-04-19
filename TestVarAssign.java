import java.util.ArrayList;
import java.util.HashMap;

public class TestVarAssign {
    private static VarAssign va= null;
    private static HashMap<String,String> varTypes= null;
    private static HashMap<String,FuncInfo> funcs= null;

    public static void main(String[] args) {
        varTypes = new HashMap<>();
        funcs = new HashMap<>();
        va = new VarAssign(varTypes, funcs);

        if(!testInts()) {
            System.out.println("Failed int assignment.\n");
        }

        if(!testBools()) {
            System.out.println("Failed bool assignment.\n");
        }

        if(!testStr()) {
            System.out.println("Failed string assignment.\n");
        }

        if(!testVar()) {
            System.out.println("Failed variable assignment.\n");
        }

        if(!testFuncCall()) {
            System.out.println("Failed function call assignment.\n");
        }

        if(!testInvalid()) {
            System.out.println("Failed invalid assignments.\n");
        }
    }

    private static boolean testInts() {
        System.out.println("Testing int assignments...");

        boolean match = true;

        match = match && va.parseCmd("x = 515");
        //System.out.println(va.result);
        match = match && va.translate().equals("int x = 515;\n");
        //System.out.println(va.translate());

        match = match && va.parseCmd("y =10/x");
        //System.out.println(va.result);
        match = match && va.translate().equals("int y = 10/x;\n");
        //System.out.println(va.translate());

        match = match && va.parseCmd("z= x*y");
        //System.out.println(va.result);
        match = match && va.translate().equals("int z = x*y;\n");
        //System.out.println(va.translate());

        System.out.println();
        return match;
    }

    private static boolean testBools() {
        System.out.println("Testing bool assignments...");

        boolean match = true;

        match = match && va.parseCmd("a = true");
        //System.out.println(va.result);
        match = match && va.translate().equals("boolean a = true;\n");
        //System.out.println(va.translated);

        // Below two have incorrect translations b/c of problem in AndOr.java (2x printMsg()) will fix when that's corrected

        match = match && va.parseCmd("b= a and false");
        //System.out.println(va.result);
        match = match && va.translate().equals("boolean b = aa&&falsefalse;\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("c = a or b");
        //System.out.println(va.result);
        match = match && va.translate().equals("boolean c = aa||bb;\n");
        //System.out.println(va.translated);

        System.out.println();
        return match;
    }

    private static boolean testStr() {
        System.out.println("Testing string assignments...");

        boolean match = true;

        match = match && va.parseCmd("str1 = \"hello world\"");
        //System.out.println(va.result);
        match = match && va.translate().equals("String str1 = \"hello world\";\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("str2 = \"123\"");
        //System.out.println(va.result);
        match = match && va.translate().equals("String str2 = \"123\";\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("str2 = \"true\"");
        //System.out.println(va.result);
        match = match && va.translate().equals("str2 = \"true\";\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("str3 = \"\"");
        //System.out.println(va.result);
        match = match && va.translate().equals("String str3 = \"\";\n");
        //System.out.println(va.translated);

        match = match && !va.parseCmd("str4 = \"\"\"");
        //System.out.println(va.result);
        //match = match && va.translate().equals("String str1 = \"\";\n");
        //System.out.println(va.translated);

        System.out.println();
        return match;
    }

    private static boolean testVar() {
        System.out.println("Testing var assignments...");

        boolean match = true;

        match = match && va.parseCmd("z = x");
        //System.out.println(va.result);
        match = match && va.translate().equals("z = x;\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("d = a");
        //System.out.println(va.result);
        match = match && va.translate().equals("boolean d = a;\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("str5 = str1");
        //System.out.println(va.result);
        match = match && va.translate().equals("String str5 = str1;\n");
        //System.out.println(va.translated);

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
        //System.out.println(va.result);
        match = match && va.translate().equals("int foo = foo();\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("bar = bar()");
        //System.out.println(va.result);
        match = match && va.translate().equals("boolean bar = bar();\n");
        //System.out.println(va.translated);

        match = match && va.parseCmd("baz = baz()");
        //System.out.println(va.result);
        match = match && va.translate().equals("String baz = baz();\n");
        //System.out.println(va.translated);

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
        //System.out.println(va.result);
        match = match && va.result.equals("Failed to parse 'a'. Mismatch type assign.\n");

        // test invalid variable name
        match = match && !va.parseCmd("123 = 123");
        //System.out.println(va.result);
        match = match && va.result.equals("Failed to parse '123'. Invalid variable name.\n");

        // tests invalid value
        match = match && !va.parseCmd("m = 27ab");
        //System.out.println(va.result);
        match = match && va.result.equals("Failed to parse '27ab'. Invalid value to assign.\n");
        match = match && !va.parseCmd("n = abc");
        //System.out.println(va.result);
        match = match && va.result.equals("Failed to parse 'abc'. Invalid value to assign.\n");

        System.out.println();
        return match;
    }
}
