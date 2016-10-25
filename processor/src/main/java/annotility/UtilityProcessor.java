package annotility;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.NestingKind.LOCAL;
import static javax.lang.model.element.NestingKind.MEMBER;
import static javax.tools.Diagnostic.Kind.*;

public class UtilityProcessor extends AbstractProcessor {
	private final Set<TypeElement> processedElements = new HashSet<TypeElement>();
	
	private Messager messager;
	private Types typeUtils;
	
	private TypeMirror objectType;
	
	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);
		
		messager = environment.getMessager();
		typeUtils = environment.getTypeUtils();
		
		Elements elementUtils = environment.getElementUtils();
		objectType = elementUtils.getTypeElement(Object.class.getCanonicalName()).asType();
		
		messager.printMessage(NOTE, "Initializing '@Utility'-annotation processor");
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
		for (TypeElement annotation : annotations) {
			for (Element element : environment.getElementsAnnotatedWith(annotation)) {
				ElementKind kind = element.getKind();
				if (kind != INTERFACE && kind != CLASS) {
					// Should never happen due to the `@Target`-definition of the annotation...
					messager.printMessage(ERROR, String.format("'@Utility'-annotated non-type '%s'", element), element);
				}
				
				TypeElement typeElement = (TypeElement) element;
				NestingKind nestingKind = typeElement.getNestingKind();
				if (nestingKind == LOCAL) {
					messager.printMessage(ERROR, "'@Utility'-annotated local class", typeElement);
				} else if (nestingKind == MEMBER && !typeElement.getModifiers().contains(STATIC)) {
					messager.printMessage(ERROR, "'@Utility'-annotated non-static inner class", typeElement);
				} else {
					visitNestedElements(typeElement, typeElement);
				}
			}
		}
		
		return true;
	}
	
	private void visitNestedElements(TypeElement annotatedTypeElement, TypeElement enclosedTypeElement) {
		messager.printMessage(NOTE, String.format("Processing type '%s'", enclosedTypeElement), enclosedTypeElement);
		
		// Skip checking the type if it has already been processed (whether or not it failed) or is currently being
		// processed at a previous recursion level. This prevents duplicate work as well as infinite recursion when there
		// is an inheritance relation in the opposite direction of "enclosement".
		if (!processedElements.add(enclosedTypeElement)) {
			messager.printMessage(NOTE, String.format("Skipping previously processed type '%s'", enclosedTypeElement), enclosedTypeElement);
			return;
		}
		
		// Recursively check superclass and implemented interfaces (which may indirectly enclose non-utility classes).
		List<? extends TypeMirror> interfaces = enclosedTypeElement.getInterfaces();
		
		List<TypeElement> superTypeElements = new ArrayList<TypeElement>(interfaces.size() + 1);
		for (TypeMirror interfaceType : interfaces) {
			TypeElement interfaceTypeElement = (TypeElement) typeUtils.asElement(interfaceType);
			superTypeElements.add(interfaceTypeElement);
		}
		
		TypeMirror superclass = enclosedTypeElement.getSuperclass();
		Element superclassElement = typeUtils.asElement(superclass);
		if (superclassElement != null && !typeUtils.isSameType(superclass, objectType)) {
			TypeElement superclassTypeElement = (TypeElement) superclassElement;
			superTypeElements.add(superclassTypeElement);
		}
		
		for (TypeElement superType : superTypeElements) {
			// If the superclass is itself a `@Utility`, perform the processing of the class now with error messages
			// being reported relative to itself. Otherwise, report them relative to the subclass.
			if (superType.getAnnotation(Utility.class) != null) {
				visitNestedElements(superType, superType);
			} else {
				visitNestedElements(annotatedTypeElement, superType);
			}
		}
		
		for (Element enclosedElement : enclosedTypeElement.getEnclosedElements()) {
			ElementKind kind = enclosedElement.getKind();
			switch (kind) {
				case FIELD:
					// Check that field is static and final (interface members are always implicitly static and final).
					VariableElement field = (VariableElement) enclosedElement;
					if (!field.getModifiers().contains(STATIC)) {
						messager.printMessage(
								ERROR,
								String.format(
										"Non-static field '%s' enclosed in or inherited from '@Utility'-annotated type '%s'",
										field,
										annotatedTypeElement
								),
								field
						);
					}
					if (!field.getModifiers().contains(FINAL)) {
						messager.printMessage(
								ERROR,
								String.format(
										"Non-final field '%s' enclosed in or inherited from '@Utility'-annotated type '%s'",
										field,
										annotatedTypeElement
								),
								field
						);
					}
					break;
				case METHOD:
					// Check that method is static (interface members are always implicitly static).
					ExecutableElement method = (ExecutableElement) enclosedElement;
					if (!method.getModifiers().contains(STATIC)) {
						messager.printMessage(
								ERROR,
								String.format(
										"Non-static method '%s' enclosed in or inherited from '@Utility'-annotated type '%s'",
										method,
										annotatedTypeElement
								),
								method
						);
					}
					break;
				case CLASS:
					// Check that inner class is static.
					TypeElement classElement = (TypeElement) enclosedElement;
					if (!classElement.getModifiers().contains(STATIC)) {
						messager.printMessage(
								ERROR,
								String.format("Non-static inner class of '@Utility'-annotated type %s", annotatedTypeElement),
								classElement
						);
						
						// No need for visiting class members (by falling through to the next case) after having already
						// reported error on the class.
						break;
					}
					//fallthrough 
				case INTERFACE:
					// No checking needed for inner interface.
					// Recurse (note fallthrough from previous case).
					TypeElement typeElement = (TypeElement) enclosedElement;
					visitNestedElements(annotatedTypeElement, typeElement);
					break;
			}
		}
	}
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton(Utility.class.getCanonicalName());
	}
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
