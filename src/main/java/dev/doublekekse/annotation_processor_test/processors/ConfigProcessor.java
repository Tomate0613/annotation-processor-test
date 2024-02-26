package dev.doublekekse.annotation_processor_test.processors;

import dev.doublekekse.annotation_processor_test.annotations.Config;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("MyAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Config.class)) {
            if (element instanceof TypeElement typeElement) {

                // Generate code for the save and load methods
                generateSaveLoadMethods(typeElement);
            }
        }
        return true;
    }

    private void generateSaveLoadMethods(TypeElement typeElement) {
        try {
            // Get the package and class names
            String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
            String className = typeElement.getSimpleName().toString();

            // Generate code for save method
            String saveMethodCode = String.format(
                """
                        public void save() {
                            System.out.println("Saving configuration for %s");
                            // Add your save logic here
                        }
                    """, className);

            // Generate code for load method
            String loadMethodCode = String.format(
                """
                    public void load() {
                        System.out.println("Loading configuration for %s");
                        // Add your load logic here
                    }
                    """, className);

            // Create a new source file for the generated methods
            try (PrintWriter writer = new PrintWriter(
                processingEnv.getFiler().createSourceFile(packageName + ".Generated" + className).openWriter())) {

                // Add the package declaration
                if (!packageName.isEmpty()) {
                    writer.println("package " + packageName + ";\n");
                }

                // Add import statements if needed
                writer.println("import java.io.*;");
                writer.println("import java.util.*;\n");

                // Add the generated code
                writer.println("public class Generated" + className + " {");

                // Add the save and load methods
                writer.println(saveMethodCode);
                writer.println(loadMethodCode);

                // Close the class
                writer.println("}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}