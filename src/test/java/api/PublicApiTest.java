/*
 * Copyright 2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of business-flows.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package api;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class PublicApiTest implements WithAssertions {

    @Test
    public void publicApiHasNotChanged() throws IOException {
        List<Class<?>> publicClasses = publicClasses(classesInPackage("io.github.theangrydev.businessflows"));

        StringBuilder report = new StringBuilder();
        for (Class<?> publicClass : publicClasses) {
            checkApiRules(publicClass);

            appendField(report, "Class", publicClass.toGenericString());
            appendField(report, "Generic Superclass", String.valueOf(publicClass.getGenericSuperclass()));
            appendField(report, "Generic Interfaces", Arrays.toString(publicClass.getGenericInterfaces()));
            appendField(report, "Public Fields", fieldsToString(publicFields(publicClass.getDeclaredFields())));
            appendField(report, "Public Methods", methodsToString(publicMethods(publicClass.getDeclaredMethods())));
            appendField(report, "Annotations", Arrays.toString(publicClass.getDeclaredAnnotations()));
            report.append('\n');
        }

        assertEquals(EXPECTED_PUBLIC_API, report.toString());
    }

    private String fieldsToString(List<Field> fields) {
        return fields.stream().map(Field::toGenericString).collect(toList()).toString();
    }

    private String methodsToString(List<Method> methods) {
        return methods.stream().map(Method::toGenericString).collect(toList()).toString();
    }

    private void appendField(StringBuilder report, String fieldName, String fieldValue) {
        report.append(fieldName);
        report.append(": ");
        report.append(fieldValue);
        report.append("\n");
    }

    private void checkApiRules(Class<?> publicClass) {
        List<Class<?>> declaredClasses = publicClasses(publicClass.getDeclaredClasses());
        if (!declaredClasses.isEmpty()) {
            throw new IllegalStateException("There are no inner classes in the public API");
        }

        Class<?> componentType = publicClass.getComponentType();
        if (componentType != null) {
            throw new IllegalStateException("There are no array classes in the public API");
        }

        AnnotatedType annotatedSuperclass = publicClass.getAnnotatedSuperclass();
        if (annotatedSuperclass != null && annotatedSuperclass.getAnnotations().length > 0) {
            throw new IllegalStateException("There are no annotated super classes in the public API");
        }

        AnnotatedType[] annotatedInterfaces = publicClass.getAnnotatedInterfaces();
        for (AnnotatedType annotatedInterface : annotatedInterfaces) {
            if (annotatedInterface.getAnnotations().length > 0) {
                throw new IllegalStateException("There are no annotated interfaces in the public API");
            }
        }

        List<Constructor<?>> declaredConstructors = publicConstructors(publicClass.getDeclaredConstructors());
        if (!declaredConstructors.isEmpty()) {
            throw new IllegalStateException("There are no constructors in the public API");
        }

        Method[] declaredMethods = publicClass.getDeclaredMethods();
        if (existProtectedElements(stream(declaredMethods), Method::getModifiers)) {
            throw new IllegalStateException("There are no protected methods in the public API");
        }

        Field[] declaredFields = publicClass.getDeclaredFields();
        if (existProtectedElements(stream(declaredFields), Field::getModifiers)) {
            throw new IllegalStateException("There are no protected fields in the public API");
        }
    }

    private static <T> List<T> publicElements(Stream<T> elements, Function<T, Integer> modifiers) {
        return elements
                .filter(element -> isPublic(modifiers.apply(element)))
                .sorted(comparing(Object::toString))
                .collect(toList());
    }

    private static <T> boolean existProtectedElements(Stream<T> elements, Function<T, Integer> modifiers) {
        return elements.anyMatch(element -> isProtected(modifiers.apply(element)));
    }

    private static List<Method> publicMethods(Method[] allMethods) {
        return publicElements(stream(allMethods), Method::getModifiers);
    }

    private static List<Field> publicFields(Field[] allFields) {
        return publicElements(stream(allFields), Field::getModifiers)
                .stream().filter(field -> !field.getName().endsWith("$jacocoData")) // JaCoCo instrumentation adds public fields
                .collect(toList());
    }

    private static List<Constructor<?>> publicConstructors(Constructor<?>[] allConstructors) {
        return publicElements(stream(allConstructors), Constructor::getModifiers);
    }

    private static List<Class<?>> publicClasses(Class<?>[] allClasses) {
        return publicElements(stream(allClasses), Class::getModifiers);
    }

    private static List<Class<?>> publicClasses(List<Class<?>> allClasses) {
        return publicElements(allClasses.stream(), Class::getModifiers);
    }

    private static List<Class<?>> classesInPackage(String packageName) throws IOException {
        return ClassPath.from(PublicApiTest.class.getClassLoader())
                .getTopLevelClasses(packageName)
                .stream()
                .map(ClassInfo::load)
                .filter(PublicApiTest::notATestClass)
                .sorted(comparing(Class::getName))
                .collect(toList());
    }

    private static boolean notATestClass(Class<?> aClass) {
        return !aClass.getName().endsWith("Test");
    }

    private static final String EXPECTED_PUBLIC_API = "Class: public final enum io.github.theangrydev.businessflows.ApiFeatureStability\n" +
            "Generic Superclass: java.lang.Enum<io.github.theangrydev.businessflows.ApiFeatureStability>\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: [public static final io.github.theangrydev.businessflows.ApiFeatureStability io.github.theangrydev.businessflows.ApiFeatureStability.BETA, public static final io.github.theangrydev.businessflows.ApiFeatureStability io.github.theangrydev.businessflows.ApiFeatureStability.DEPRECATED, public static final io.github.theangrydev.businessflows.ApiFeatureStability io.github.theangrydev.businessflows.ApiFeatureStability.EXPERIMENTAL, public static final io.github.theangrydev.businessflows.ApiFeatureStability io.github.theangrydev.businessflows.ApiFeatureStability.STABLE]\n" +
            "Public Methods: [public static io.github.theangrydev.businessflows.ApiFeatureStability io.github.theangrydev.businessflows.ApiFeatureStability.valueOf(java.lang.String), public static io.github.theangrydev.businessflows.ApiFeatureStability[] io.github.theangrydev.businessflows.ApiFeatureStability.values()]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, stability=EXPERIMENTAL, since=10.2.0)]\n" +
            "\n" +
            "Class: public final enum io.github.theangrydev.businessflows.ApiVersionHistory\n" +
            "Generic Superclass: java.lang.Enum<io.github.theangrydev.businessflows.ApiVersionHistory>\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: [public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_10_2_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_1_0_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_2_3_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_2_5_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_2_7_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_3_0_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_3_1_1, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_4_0_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_5_0_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_5_1_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_6_0_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_6_1_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_0_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_2_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_3_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_4_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_5_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_6_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_8_2_0, public static final io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_8_3_0]\n" +
            "Public Methods: [public java.lang.String io.github.theangrydev.businessflows.ApiVersionHistory.toString(), public static io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiVersionHistory.valueOf(java.lang.String), public static io.github.theangrydev.businessflows.ApiVersionHistory[] io.github.theangrydev.businessflows.ApiVersionHistory.values()]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, stability=EXPERIMENTAL, since=10.2.0)]\n" +
            "\n" +
            "Class: public class io.github.theangrydev.businessflows.FieldValidator<Happy,Sad,Field>\n" +
            "Generic Superclass: class java.lang.Object\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.Validator<Happy, Sad>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public io.github.theangrydev.businessflows.PotentialFailure<Sad> io.github.theangrydev.businessflows.FieldValidator.attempt(Happy) throws java.lang.Exception, public static <Happy,Sad,Field> io.github.theangrydev.businessflows.FieldValidator<Happy, Sad, Field> io.github.theangrydev.businessflows.FieldValidator.fieldValidator(io.github.theangrydev.businessflows.Mapping<Happy, Field>,io.github.theangrydev.businessflows.Validator<Field, Sad>)]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=5.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.ActionThatMightFail<Happy,Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.PotentialFailure<Sad> io.github.theangrydev.businessflows.ActionThatMightFail.attempt(Happy) throws java.lang.Exception]\n" +
            "Annotations: [@java.lang.FunctionalInterface(), @io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract @interface io.github.theangrydev.businessflows.ApiFeature\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [interface java.lang.annotation.Annotation]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.ApiFeatureStability io.github.theangrydev.businessflows.ApiFeature.stability(), public abstract io.github.theangrydev.businessflows.ApiVersionHistory io.github.theangrydev.businessflows.ApiFeature.since(), public abstract java.lang.String io.github.theangrydev.businessflows.ApiFeature.comments()]\n" +
            "Annotations: [@java.lang.annotation.Documented(), @java.lang.annotation.Target(value=[TYPE, METHOD, FIELD, CONSTRUCTOR]), @java.lang.annotation.Retention(value=RUNTIME), @io.github.theangrydev.businessflows.ApiFeature(comments=, stability=EXPERIMENTAL, since=10.2.0)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.Attempt<Result>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract Result io.github.theangrydev.businessflows.Attempt.attempt() throws java.lang.Exception]\n" +
            "Annotations: [@java.lang.FunctionalInterface(), @io.github.theangrydev.businessflows.ApiFeature(comments=, since=2.5.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.BusinessFlow<Happy,Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.BusinessCase<Happy, Sad>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.BusinessFlow.ifHappy(), public abstract io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.BusinessFlow.ifSad(), public abstract io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.BusinessFlow.ifTechnicalFailure(), public default boolean io.github.theangrydev.businessflows.BusinessFlow.isHappy(), public default boolean io.github.theangrydev.businessflows.BusinessFlow.isSad(), public default Happy io.github.theangrydev.businessflows.BusinessFlow.getHappy(), public default Sad io.github.theangrydev.businessflows.BusinessFlow.getSad()]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.HappyPath<Happy,Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.BusinessFlow<Happy, Sad>, io.github.theangrydev.businessflows.WithOptional<Happy>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.attempt(io.github.theangrydev.businessflows.ActionThatMightFail<Happy, Sad>), public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.attemptAll(java.util.List<? extends io.github.theangrydev.businessflows.ActionThatMightFail<Happy, Sad>>), public abstract <NewHappy> io.github.theangrydev.businessflows.HappyPath<NewHappy, Sad> io.github.theangrydev.businessflows.HappyPath.map(io.github.theangrydev.businessflows.Mapping<Happy, NewHappy>), public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.peek(io.github.theangrydev.businessflows.Peek<Happy>), public abstract <NewHappy> io.github.theangrydev.businessflows.HappyPath<NewHappy, Sad> io.github.theangrydev.businessflows.HappyPath.then(io.github.theangrydev.businessflows.Mapping<Happy, ? extends io.github.theangrydev.businessflows.BusinessFlow<NewHappy, Sad>>), public default io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.ifHappy(), public static <Happy,Sad> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.happyAttempt(io.github.theangrydev.businessflows.Attempt<Happy>), public static <Happy,Sad> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.happyAttempt(io.github.theangrydev.businessflows.Attempt<Happy>,io.github.theangrydev.businessflows.Mapping<java.lang.Exception, Sad>), public static <Happy,Sad> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.happyPath(Happy), public static <Happy,Sad> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.happyPathAttempt(io.github.theangrydev.businessflows.Attempt<? extends io.github.theangrydev.businessflows.BusinessFlow<Happy, Sad>>), public static <Happy,Sad> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.sadPath(Sad), public static <Happy,Sad> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.HappyPath.technicalFailure(java.lang.Exception), public static <Happy,Sad> java.util.List<io.github.theangrydev.businessflows.ActionThatMightFail<Happy, Sad>> io.github.theangrydev.businessflows.HappyPath.actions(io.github.theangrydev.businessflows.ActionThatMightFail<Happy, Sad>...)]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.Mapping<Old,New>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract New io.github.theangrydev.businessflows.Mapping.map(Old) throws java.lang.Exception, public static <Old> io.github.theangrydev.businessflows.Mapping<Old, Old> io.github.theangrydev.businessflows.Mapping.identity()]\n" +
            "Annotations: [@java.lang.FunctionalInterface(), @io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.Peek<T>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract void io.github.theangrydev.businessflows.Peek.peek(T) throws java.lang.Exception]\n" +
            "Annotations: [@java.lang.FunctionalInterface(), @io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.PotentialFailure<Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.WithOptional<Sad>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract <Happy> io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.PotentialFailure.toHappyPath(Happy), public static <Sad> io.github.theangrydev.businessflows.PotentialFailure<Sad> io.github.theangrydev.businessflows.PotentialFailure.failure(Sad), public static <Sad> io.github.theangrydev.businessflows.PotentialFailure<Sad> io.github.theangrydev.businessflows.PotentialFailure.success()]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=3.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.SadPath<Happy,Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.BusinessFlow<Happy, Sad>, io.github.theangrydev.businessflows.WithOptional<Sad>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.recover(io.github.theangrydev.businessflows.Attempt<Happy>), public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.recover(io.github.theangrydev.businessflows.Mapping<Sad, Happy>), public abstract <NewSad> io.github.theangrydev.businessflows.SadPath<Happy, NewSad> io.github.theangrydev.businessflows.SadPath.map(io.github.theangrydev.businessflows.Mapping<Sad, NewSad>), public abstract io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.peek(io.github.theangrydev.businessflows.Peek<Sad>), public abstract <NewSad> io.github.theangrydev.businessflows.SadPath<Happy, NewSad> io.github.theangrydev.businessflows.SadPath.then(io.github.theangrydev.businessflows.Mapping<Sad, ? extends io.github.theangrydev.businessflows.BusinessFlow<Happy, NewSad>>), public default io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.ifSad(), public static <Happy,Sad> io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.happyPath(Happy), public static <Happy,Sad> io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.sadPath(Sad), public static <Happy,Sad> io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.SadPath.technicalFailure(java.lang.Exception)]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.TechnicalFailure<Happy,Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.BusinessFlow<Happy, Sad>, io.github.theangrydev.businessflows.WithOptional<java.lang.Exception>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.recover(io.github.theangrydev.businessflows.Attempt<Happy>), public abstract io.github.theangrydev.businessflows.HappyPath<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.recover(io.github.theangrydev.businessflows.Mapping<java.lang.Exception, Happy>), public abstract io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.mapToSadPath(io.github.theangrydev.businessflows.Attempt<Sad>), public abstract io.github.theangrydev.businessflows.SadPath<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.mapToSadPath(io.github.theangrydev.businessflows.Mapping<java.lang.Exception, Sad>), public abstract io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.map(io.github.theangrydev.businessflows.Mapping<java.lang.Exception, java.lang.Exception>), public abstract io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.peek(io.github.theangrydev.businessflows.Peek<java.lang.Exception>), public abstract io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.then(io.github.theangrydev.businessflows.Mapping<java.lang.Exception, io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad>>), public abstract io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.throwIt() throws java.lang.Exception, public abstract io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.throwItAsARuntimeException() throws java.lang.RuntimeException, public default io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.ifTechnicalFailure(), public static <Happy,Sad> io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.happyPath(Happy), public static <Happy,Sad> io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.sadPath(Sad), public static <Happy,Sad> io.github.theangrydev.businessflows.TechnicalFailure<Happy, Sad> io.github.theangrydev.businessflows.TechnicalFailure.technicalFailure(java.lang.Exception)]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.ValidationPath<Happy,Sad,SadAggregate>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.HappyPath<Happy, SadAggregate>]\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.validateAll(java.util.List<? extends io.github.theangrydev.businessflows.Validator<Happy, Sad>>), public abstract io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.validateAllInto(io.github.theangrydev.businessflows.Mapping<java.util.List<Sad>, SadAggregate>,java.util.List<? extends io.github.theangrydev.businessflows.Validator<Happy, Sad>>), public static <Happy,Sad,SadAggregate> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.technicalFailure(java.lang.Exception), public static <Happy,Sad> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, java.util.List<Sad>> io.github.theangrydev.businessflows.ValidationPath.validateAll(Happy,io.github.theangrydev.businessflows.Validator<Happy, Sad>...), public static <Happy,Sad> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, java.util.List<Sad>> io.github.theangrydev.businessflows.ValidationPath.validateAll(Happy,java.util.List<? extends io.github.theangrydev.businessflows.Validator<Happy, Sad>>), public static <Happy,Sad,SadAggregate> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.validateAllInto(Happy,io.github.theangrydev.businessflows.Mapping<java.util.List<Sad>, SadAggregate>,io.github.theangrydev.businessflows.Validator<Happy, Sad>...), public static <Happy,Sad,SadAggregate> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.validateAllInto(Happy,io.github.theangrydev.businessflows.Mapping<java.util.List<Sad>, SadAggregate>,java.util.List<? extends io.github.theangrydev.businessflows.Validator<Happy, Sad>>), public static <Happy,Sad,SadAggregate> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.validationFailure(SadAggregate), public static <Happy,Sad> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, java.util.List<Sad>> io.github.theangrydev.businessflows.ValidationPath.validationPath(Happy), public static <Happy,Sad,SadAggregate> io.github.theangrydev.businessflows.ValidationPath<Happy, Sad, SadAggregate> io.github.theangrydev.businessflows.ValidationPath.validationPathInto(Happy,io.github.theangrydev.businessflows.Mapping<java.util.List<Sad>, SadAggregate>), public static <Happy,Sad> java.util.List<io.github.theangrydev.businessflows.Validator<Happy, Sad>> io.github.theangrydev.businessflows.ValidationPath.validators(io.github.theangrydev.businessflows.Validator<Happy, Sad>...)]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=1.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.Validator<Happy,Sad>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: [io.github.theangrydev.businessflows.ActionThatMightFail<Happy, Sad>]\n" +
            "Public Fields: []\n" +
            "Public Methods: []\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=7.0.0, stability=STABLE)]\n" +
            "\n" +
            "Class: public abstract interface io.github.theangrydev.businessflows.WithOptional<Content>\n" +
            "Generic Superclass: null\n" +
            "Generic Interfaces: []\n" +
            "Public Fields: []\n" +
            "Public Methods: [public abstract java.util.Optional<Content> io.github.theangrydev.businessflows.WithOptional.toOptional(), public default boolean io.github.theangrydev.businessflows.WithOptional.isPresent(), public default Content io.github.theangrydev.businessflows.WithOptional.get(), public default Content io.github.theangrydev.businessflows.WithOptional.orElse(Content), public default Content io.github.theangrydev.businessflows.WithOptional.orElseGet(java.util.function.Supplier<Content>), public default <X> Content io.github.theangrydev.businessflows.WithOptional.orElseThrow(java.util.function.Supplier<? extends X>) throws X, public default void io.github.theangrydev.businessflows.WithOptional.ifPresent(java.util.function.Consumer<Content>)]\n" +
            "Annotations: [@io.github.theangrydev.businessflows.ApiFeature(comments=, since=7.6.0, stability=STABLE), @java.lang.FunctionalInterface()]\n" +
            "\n";
}
