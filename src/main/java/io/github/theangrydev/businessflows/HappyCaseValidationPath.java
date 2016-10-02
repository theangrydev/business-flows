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
import java.util.List;

/**
 * A {@link HappyCaseValidationPath} is a {@link ValidationPath} that is actually a {@link HappyCase}.
 *
 * {@inheritDoc}
 */
class HappyCaseValidationPath<Happy, Sad> extends HappyCaseHappyPath<Happy, List<Sad>> implements ValidationPath<Happy, Sad> {

    HappyCaseValidationPath(Happy happy) {
        super(happy);
    }

    @Override
    public ValidationPath<Happy, Sad> validate(List<? extends Validator<Happy, Sad>> validators) {
        List<Sad> validationFailures = new ArrayList<>(validators.size());
        for (Validator<Happy, Sad> validator : validators) {
            try {
                validator.attempt(happy).ifSad(validationFailures::addAll);
            } catch (Exception technicalFailure) {
                return ValidationPath.technicalFailure(technicalFailure);
            }
        }
        if (validationFailures.isEmpty()) {
            return ValidationPath.validationPath(happy);
        } else {
            return ValidationPath.validationFailure(validationFailures);
        }
    }
}
