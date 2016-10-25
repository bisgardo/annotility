package annotility;

import org.junit.Test;

import static annotility.TestHelpers.assertThatSource;

public class MultipleUnitsTest {
	
	@Test
	public void extendsEmpty() {
		assertThatSource("@Utility class X extends Y {} ; class Y {}")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void implementsEmpty() {
		assertThatSource("@Utility class X implements Y {} ; interface Y {}")
				.compilesWithoutWarnings();
	}
	
	
	@Test
	public void extendsNonUtility() {
		assertThatSource("@Utility class X extends Y {} ; class Y { int x; }")
				.failsToCompile();
	}
	
	@Test
	public void extendsImplicitUtility() {
		assertThatSource("@Utility class X extends Y {} ; class Y { static final int x = 0; }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void extendsEmptyUtility() {
		assertThatSource("@Utility class X extends Y {} ; @Utility class Y {}")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void implementsEmptyUtility() {
		assertThatSource("@Utility class X implements Y {} ; @Utility interface Y {}")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void extendsFailingUtility() {
		assertThatSource("@Utility class X extends Y {} ; @Utility class Y { int x; }")
				.failsToCompile();
	}
	
	@Test
	public void extendsUtility() {
		assertThatSource("@Utility class X extends Y {} ; @Utility class Y { static final int x = 0; }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void implementsUtility() {
		assertThatSource("@Utility class X implements Y {} ; @Utility interface Y { int x = 0; }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void failingUtilityExtendsUtility() {
		assertThatSource("@Utility class X extends Y { int x; } ; @Utility class Y { static final int x = 0; }")
				.failsToCompile();
	}
	
	@Test
	public void failingUtilityImplementsUtility() {
		assertThatSource("@Utility class X implements Y { int x; } ; @Utility interface Y { int x = 0; }")
				.failsToCompile();
	}
	
	@Test
	public void nonUtilityExtendsUtility() {
		assertThatSource("class X extends Y { int x; } ; @Utility class Y { static final int x = 0; }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void nonUtilityImplementsUtility() {
		assertThatSource("class X implements Y { int x; } ; @Utility interface Y { int x = 0; }")
				.compilesWithoutWarnings();
	}
}
