package annotility;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static annotility.TestHelpers.assertThatTarget;

public class ComplexTest {
	
	@Test
	public void success() {
		assertThatTarget(JavaFileObjects.forResource("ComplexSuccess.java"))
				.compilesWithoutWarnings();
		
	}
	
	@Test
	public void failure() {
		assertThatTarget(JavaFileObjects.forResource("ComplexFailure.java"))
				.failsToCompile();
		
	}
}
