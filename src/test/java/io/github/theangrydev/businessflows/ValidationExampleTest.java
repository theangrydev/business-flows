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

import static io.github.theangrydev.businessflows.FieldValidator.fieldValidator;
import static io.github.theangrydev.businessflows.PotentialFailure.failures;
import static io.github.theangrydev.businessflows.PotentialFailure.success;
import static io.github.theangrydev.businessflows.ValidationPath.validators;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationExampleTest {

    private static class AggregateErrors {
        private final List<ValidationError> validationErrors;

        private AggregateErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors;
        }

        static AggregateErrors errorsWithMessage1(List<ValidationError> validationErrors) {
            return new AggregateErrors(validationErrors);
        }

        static AggregateErrors errorsWithMessage2(List<ValidationError> validationErrors) {
            return new AggregateErrors(validationErrors);
        }
    }
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
    }


    private class NotBlankValidator implements Validator<String, ValidationError> {

        private final String fieldName;

        private NotBlankValidator(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public PotentialFailure<List<ValidationError>> attempt(String fieldValue) {
            if (fieldValue == null || fieldValue.trim().isEmpty()) {
                return failures(new ValidationError(format("Field '%s' was empty", fieldName)));
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

    private HappyPath<RegistrationForm, AggregateErrors> validate(RegistrationForm registrationForm) {
        return message1Validation(registrationForm).then(this::message2Validation);
    }

    private HappyPath<RegistrationForm, AggregateErrors> message1Validation(RegistrationForm registrationForm) {
        return ValidationPath
                .validate(registrationForm, firstNameValidator(), lastNameValidator())
                .validate(validators(ageValidator()))
                .ifSad().map(AggregateErrors::errorsWithMessage1)
                .ifHappy();
    }

    private HappyPath<RegistrationForm, AggregateErrors> message2Validation(RegistrationForm registrationForm1) {
        return ValidationPath.validate(registrationForm1, validators(firstNameValidator(), lastNameValidator(), ageValidator()))
                .ifSad().map(AggregateErrors::errorsWithMessage2)
                .ifHappy();
    }

    private HappyPath<RegistrationForm, List<ValidationError>> validateWithTechnicalFailure(RegistrationForm registrationForm, Exception technicalFailure) {
        return ValidationPath.validate(registrationForm, singletonList(registrationForm1 -> {throw technicalFailure;}));
    }

    private void logFailure(Exception exception) {
        System.out.println("e = " + exception);
    }

    private String renderJoinedPage(RegistrationForm registrationForm) {
        return "You joined!";
    }

    private String renderValidationErrors(AggregateErrors validationErrors) {
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

    private Validator<RegistrationForm, ValidationError> ageValidator() {
        return fieldValidator(form -> form.age, "Age", NotBlankValidator::new);
    }

    private Validator<RegistrationForm, ValidationError> lastNameValidator() {
        return fieldValidator(form -> form.firstName, new NotBlankValidator("First Name"));
    }

    private Validator<RegistrationForm, ValidationError> firstNameValidator() {
        return fieldValidator(form -> form.lastName, new NotBlankValidator("Last Name"));
    }
}
