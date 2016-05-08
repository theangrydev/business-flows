package io.github.theangrydev.businessflows;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.github.theangrydev.businessflows.BusinessFlow.happyPath;

public class ValidationExampleTest {

    private static class ValidationError {
        private final String error;

        private ValidationError(String error) {
            this.error = error;
        }

        public static ValidationError technicalFailure(Exception exception) {
            return new ValidationError(exception.getMessage());
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
            return registrationForm -> validator.validate(field.apply(registrationForm));
        }
    }

    @FunctionalInterface
    private interface Validator<TypeToValidate> {
        Optional<ValidationError> validate(TypeToValidate typeToValidate);
    }

    private class NotBlankValidator implements Validator<String> {

        @Override
        public Optional<ValidationError> validate(String string) {
            if (string == null || string.trim().isEmpty()) {
                return Optional.of(new ValidationError("Field was empty"));
            }
            return Optional.empty();
        }
    }

    @Test
    public void validateRegistrationForm() {
        ValidationFlow<ValidationError, RegistrationForm> validate = registrationForm()
                .validate(ageValidator()::validate)
                .validate(registrationForm -> Optional.empty());

        BusinessFlow<ValidationError, RegistrationForm> attempt = registrationForm()
                .attempt(registrationForm -> Optional.empty())
                .attempt(registrationForm -> Optional.empty())
                .attempt(registrationForm -> Optional.empty());

//        validateNames(registrationForm(businessFlows)
//            .then(registrationForm -> validateNames(registrationForm));
//        BusinessFlow<List<ValidationError>, RegistrationForm> names = validateNames(registrationForm);
//        names
//            .then(registrationForm -> validateAge(businessFlows, registrationForm))
//            .then(registrationForm -> validateAge(businessFlows, registrationForm));

    }

    private BusinessFlow<ValidationError, RegistrationForm> registrationForm() {
        return happyPath(new RegistrationForm("first", "last", "25"));
    }

//    private BusinessFlow<List<ValidationError>, RegistrationForm> validateAge(BusinessFlows businessFlows, RegistrationForm registrationForm) {
//        return businessFlows.happyPath(registrationForm, ValidationError::technicalFailure).validate(ageValidator()::validate);
//    }

    private Validator<RegistrationForm> ageValidator() {
        return RegistrationForm.validator(x -> x.age, new NotBlankValidator());
    }

//    private BusinessFlow<List<ValidationError>, RegistrationForm> validateNames(BusinessFlow<ValidationError, RegistrationForm> registrationForm) {
//        Validator<RegistrationForm> firstName = RegistrationForm.validator(x -> x.firstName, new NotBlankValidator());
//        Validator<RegistrationForm> lastName = RegistrationForm.validator(x -> x.lastName, new NotBlankValidator());
//        return registrationForm.validate(firstName::validate, lastName::validate);
//    }
}
