package io.github.theangrydev.businessflows;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.github.theangrydev.businessflows.HappyFlow.happyPath;

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

    private interface Validator<TypeToValidate> extends ActionThatMightFail<ValidationError, TypeToValidate> {
    }

    private class NotBlankValidator implements Validator<String> {

        @Override
        public Optional<ValidationError> attempt(String string) {
            if (string == null || string.trim().isEmpty()) {
                return Optional.of(new ValidationError("Field was empty"));
            }
            return Optional.empty();
        }
    }

    @Test
    public void validateRegistrationForm() {
        registrationForm()
                .then(this::validate)
                .ifHappy(this::registerUser)
                .ifFailure().peek(this::logFailure)
                .join(this::renderValidationErrors, this::renderJoinedPage, this::renderFailure);
    }

    private ValidationFlow<ValidationError, RegistrationForm> validate(RegistrationForm registrationForm) {
        return happyPath(registrationForm).validate(cheapValidators()).validate(expensiveValidators());
    }

    private ActionThatMightFail<ValidationError, RegistrationForm> cheapValidators() {
        return ageValidator();
    }

    private List<ActionThatMightFail<ValidationError, RegistrationForm>> expensiveValidators() {
        return Arrays.asList(lastNameValidator(), firstNameValidator());
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
        return "There was a technical technicalFailure. Please try again.";
    }

    private void registerUser(RegistrationForm registrationForm) {
        System.out.println("Register in database");
    }

    private HappyFlow<RegistrationForm> registrationForm() {
        return happyPath(new RegistrationForm("first", "last", "25"));
    }

    private Validator<RegistrationForm> ageValidator() {
        return RegistrationForm.validator(x -> x.age, new NotBlankValidator());
    }

    private Validator<RegistrationForm> lastNameValidator() {
        return RegistrationForm.validator(x -> x.lastName, new NotBlankValidator());
    }

    private Validator<RegistrationForm> firstNameValidator() {
        return RegistrationForm.validator(x -> x.firstName, new NotBlankValidator());
    }
}
