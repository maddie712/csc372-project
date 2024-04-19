public class testFunc{
int foo () {
return 5;
}

int bar (int x, int y) {
x = 10;
y = foo();
return x*y;
}

boolean baz (boolean x, boolean y) {
x = true;
y = false||x;
return x&&y;
}

}
