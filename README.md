# Annotility

Annotility is a simple annotation processor that extends the Java compiler
by providing compile time errors if types annotated as `@annotility.Utility`
fail to conform to the following recursive definition of being a utility
type:

1. All the type's fields are static and final.

2. All the type's methods are static (i.e. they are functions).

3. All the type's nested/inner types conform to being utility types.

4. All interfaces implemented by the type conform to being utility types.

5. If the type is a class and its superclass is not `java.lang.Object`,
   this superclass conforms to being a utility type.

Note that although all fields and methods of interfaces automatically
conform to these rules, they might contain nested types deeper down that
don't. Interfaces are therefore not automatically utility types as defined
above.
