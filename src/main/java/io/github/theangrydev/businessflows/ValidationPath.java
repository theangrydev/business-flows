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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class ValidationPath<Happy, Sad> extends HappyPath<Happy, List<Sad>> {

    private ValidationPath(BusinessCase<Happy, List<Sad>> businessCase) {
        super(businessCase);
    }

    @SafeVarargs
    public static <Happy, Sad> ValidationPath<Happy, Sad> validate(Happy happy, ActionThatMightFail<Happy, Sad>... validators) {
        return validate(happy, Arrays.asList(validators));
    }

    public static <Happy, Sad> ValidationPath<Happy, Sad> validate(Happy happy, List<ActionThatMightFail<Happy, Sad>> validators) {
        List<Sad> validationFailures = new ArrayList<>(validators.size());
        for (ActionThatMightFail<Happy, Sad> validator : validators) {
            try {
                validator.attempt(happy).ifPresent(validationFailures::add);
            } catch (Exception technicalFailure) {
                return technicalFailureDuringValidation(technicalFailure);
            }
        }
        if (validationFailures.isEmpty()) {
            return validationSuccess(happy);
        } else {
            return validationFailed(validationFailures);
        }
    }

    @SafeVarargs
    public final ValidationPath<Happy, Sad> validate(ActionThatMightFail<Happy, Sad>... validators) {
        return validate(Arrays.asList(validators));
    }

    public ValidationPath<Happy, Sad> validate(List<ActionThatMightFail<Happy, Sad>> validators) {
        return join(happy -> validate(happy, validators), ValidationPath::validationFailed, ValidationPath::technicalFailureDuringValidation);
    }

    private static <Happy, Sad> ValidationPath<Happy, Sad> validationSuccess(Happy happy) {
        return new ValidationPath<>(new HappyCase<>(happy));
    }

    private static <Happy, Sad> ValidationPath<Happy, Sad> validationFailed(List<Sad> sad) {
        return new ValidationPath<>(new SadCase<>(sad));
    }

    private static <Happy, Sad> ValidationPath<Happy, Sad> technicalFailureDuringValidation(Exception technicalFailure) {
        return new ValidationPath<>(new TechnicalFailureCase<>(technicalFailure));
    }
}
