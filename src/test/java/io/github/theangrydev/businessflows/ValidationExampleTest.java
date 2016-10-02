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
package io.github.theangrydev.businessflows;

import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static io.github.theangrydev.businessflows.PotentialFailure.success;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationExampleTest {

    private static class ValidationError {
        private final String error;

        private ValidationError(String error) {
            this.error = error;
        }
    }

    private static class RegistrationForm {
        private final String firstName;
        private final String lastName;
        private final String age;

        private RegistrationForm(String firstName, String lastName, String age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public static <TypeToValidate> Validator<RegistrationForm> validator(Function<RegistrationForm, TypeToValidate> field, Validator<TypeToValidate> validator) {
            return registrationForm -> validator.attempt(field.apply(registrationForm));
        }
    }

    private interface Validator<TypeToValidate> extends ActionThatMightFail<TypeToValidate, ValidationError> {
    }

    private class NotBlankValidator implements Validator<String> {

        private final String fieldName;

        private NotBlankValidator(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public PotentialFailure<ValidationError> attempt(String fieldValue) {
            if (fieldValue == null || fieldValue.trim().isEmpty()) {
                return PotentialFailure.failure(new ValidationError(format("Field '%s' was empty", fieldName)));
            }
            return success();
        }
    }

    @Test
    public void validateRegistrationForm() {
        String result = validate(registrationForm())
                .peek(this::registerUser)
                .ifTechnicalFailure().peek(this::logFailure)
                .join(this::renderJoinedPage, this::renderValidationErrors, this::renderFailure);
        assertThat(result).isEqualTo("You joined!");
    }

    @Test
    public void validateRegistrationFormWithTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();

        Exception actualTechnicalFailure = validateWithTechnicalFailure(registrationForm(), expectedTechnicalFailure)
                .ifTechnicalFailure().get();

        assertThat(actualTechnicalFailure).isEqualTo(expectedTechnicalFailure);
    }

    private ValidationPath<RegistrationForm, ValidationError> validate(RegistrationForm registrationForm) {
        return ValidationPath
                .validate(registrationForm, singletonList(ageValidator()))
                .validate(asList(lastNameValidator(), firstNameValidator()));
    }

    private ValidationPath<RegistrationForm, ValidationError> validateWithTechnicalFailure(RegistrationForm registrationForm, Exception technicalFailure) {
        return ValidationPath.validate(registrationForm, singletonList(registrationForm1 -> {throw technicalFailure;}));
    }

    private void logFailure(Exception exception) {
        System.out.println("e = " + exception);
    }

    private String renderJoinedPage(RegistrationForm registrationForm) {
        return "You joined!";
    }

    private String renderValidationErrors(List<ValidationError> validationErrors) {
        return "Please fix the errors: " + validationErrors;
    }

    private String renderFailure(Exception e) {
        return "There was a technical failure. Please try again.";
    }

    private void registerUser(RegistrationForm registrationForm) {
        System.out.println("Register in database");
    }

    private RegistrationForm registrationForm() {
        return new RegistrationForm("first", "last", "25");
    }

    private Validator<RegistrationForm> ageValidator() {
        return RegistrationForm.validator(form -> form.age, new NotBlankValidator("Age"));
    }

    private Validator<RegistrationForm> lastNameValidator() {
        return RegistrationForm.validator(form -> form.lastName, new NotBlankValidator("Last Name"));
    }

    private Validator<RegistrationForm> firstNameValidator() {
        return RegistrationForm.validator(form -> form.firstName, new NotBlankValidator("First Name"));
    }
}
