package annotility;

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import annotility.UtilityProcessor;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class TestHelpers {
	
	public static CompileTester assertThatSource(String classSource) {
		return assertThatSource("X", classSource);
	}
	
	public static CompileTester assertThatSource(String qualifiedName, String source) {
		String replacedSource = source.replace("@Utility", "@annotility.Utility");
		
		// Verify that code compiles without annotation processing.
		JavaFileObject target = JavaFileObjects.forSourceString(qualifiedName, replacedSource);
		return assertThatTarget(target);
	}
	
	public static CompileTester assertThatTarget(JavaFileObject target) {
		assert_().about(javaSource())
				.that(target)
				.compilesWithoutWarnings();
		
		return assert_().about(javaSource())
				.that(target)
				.processedWith(new UtilityProcessor());
	}
}
