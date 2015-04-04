package myteste;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;

public class Test {

	private void firstMethod() {
		String a = methodOne();
		String b = methodTwo(a);
		methodThree();
	}
	

	private void secondMethod() {
		String a = methodOne();
		String b = methodTwo(a);
		methodThree(b);
	}
	
	private void thirdMethod() {
		String a = methodOne();
		String b = methodTwo(a);
		methodThree(a,b);
	}
	
	private void forthMethod() {
		Foo f = methodOne();
		methodTwo(f.getAtribute());
	}
	
}
