import annotility.Utility;

public interface ComplexSuccess {
	
	@Utility
	class A implements B.C.D {
	}
	
	class B {
		int x; // Not in a utility class
		static class C {
			@Utility
			interface D {
				class E extends C {
					@Utility
					interface F {
						int x = 0;
					}
				}
			}
		}
	}
}

class G extends ComplexSuccess.A {
	@Utility
	static class H {
		static class I extends G {
		}
	}
}
