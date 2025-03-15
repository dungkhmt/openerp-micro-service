package com.hust.openerp.taskmanagement.hr_management.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("AutoMapped")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AutoMapperProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoMapped.class)) {
            AutoMapped autoMapped = element.getAnnotation(AutoMapped.class);
            String className = element.getSimpleName().toString();
            String mapperClassName = className + "Mapper"; // Using the name of the target class
            String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();

            // Generate the fromModel method
            MethodSpec fromModelMethod = MethodSpec.methodBuilder("fromModel")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ClassName.get(packageName, className)) // Use the original class name
                    .addParameter(autoMapped.target(), "source")
                    .addStatement("return $T.INSTANCE.fromModel(source)", ClassName.get(packageName, mapperClassName))
                    .build();

            // Generate the response class (without "Response" suffix)
            TypeSpec classSpec = TypeSpec.classBuilder(className) // No "Response" suffix here
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(fromModelMethod)
                    .build();

            // Generate the getInstance method for the mapper
            MethodSpec getInstanceMethod = MethodSpec.methodBuilder("INSTANCE")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .returns(ClassName.get(packageName, mapperClassName))
                    .addStatement("return $T.getMapper($T.class)", Mappers.class, ClassName.get(packageName, mapperClassName))
                    .build();

            // Generate the EntityMapper interface
            TypeSpec mapperInterface = TypeSpec.interfaceBuilder(mapperClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Mapper.class)
                    .addMethod(getInstanceMethod)
                    .addMethod(MethodSpec.methodBuilder("fromModel")
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(ClassName.get(packageName, className)) // Same as the class name
                            .addParameter(autoMapped.target(), "source")
                            .build())
                    .build();

            // Write the generated classes to the file system
            try {
                JavaFile javaFileClass = JavaFile.builder(packageName, classSpec).build();
                javaFileClass.writeTo(processingEnv.getFiler());

                JavaFile javaFileMapper = JavaFile.builder(packageName, mapperInterface).build();
                javaFileMapper.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
