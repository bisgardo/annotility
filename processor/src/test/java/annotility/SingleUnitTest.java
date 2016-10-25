package annotility;

import org.junit.Test;

public class SingleUnitTest {
	
	@Test
	public void emptyInterface() {
		TestHelpers.assertThatSource("@Utility interface X {}")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void emptyClass() {
		TestHelpers.assertThatSource("@Utility class X {}")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void singleNonStaticField() {
		TestHelpers.assertThatSource("@Utility class X { int x; }")
				.failsToCompile();
	}
	
	@Test
	public void singleStaticField() {
		TestHelpers.assertThatSource("@Utility class X { static int x; }")
				.failsToCompile();
	}
	
	@Test
	public void singleFinalField() {
		TestHelpers.assertThatSource("@Utility class X { final int x = 0; }")
				.failsToCompile();
	}
	
	@Test
	public void singleStaticFinalField() {
		TestHelpers.assertThatSource("@Utility class X { static final int x = 0; }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void staticFinalAndNonStaticField() {
		TestHelpers.assertThatSource("@Utility class X { static final int x = 0; int y; }")
				.failsToCompile();
	}
	
	@Test
	public void emptyInnerInterface() {
		TestHelpers.assertThatSource("@Utility class X { interface Y {} }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void innerInterfaceWithImplicitlyStaticMember() {
		TestHelpers.assertThatSource("@Utility class X { interface Y { int x = 0; } }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void emptyNonStaticInnerClass() {
		TestHelpers.assertThatSource("@Utility class X { class Y {} }")
				.failsToCompile();
	}
	
	@Test
	public void emptyStaticInnerClass() {
		TestHelpers.assertThatSource("@Utility class X { static class Y {} }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void innerClassWithNonStaticMember() {
		TestHelpers.assertThatSource("@Utility class X { static class Y { int x; } }")
				.failsToCompile();
	}
	
	@Test
	public void innerClassWithStaticFinalMember() {
		TestHelpers.assertThatSource("@Utility class X { static class Y { static final int x = 0; } }")
				.compilesWithoutWarnings();
	}
	
	@Test
	public void innerUtilityClass() {
		TestHelpers.assertThatSource("@Utility class X { @Utility static class Y { } }")
				.compilesWithoutWarnings();
	}
}
