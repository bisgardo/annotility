import annotility.Utility;

public interface ComplexFailure {
	
	class A implements B.C.D {
	}
	
	class B {
		static class C {
			int x; // <- Fail here through chain H -> I -> G -> A -> D -> E -> C.
			interface D {
				class E extends C {
					static class F {
					}
				}
			}
		}
	}
}

class G extends ComplexFailure.A {
	@Utility
	static class H {
		static class I extends G {
		}
	}
}
