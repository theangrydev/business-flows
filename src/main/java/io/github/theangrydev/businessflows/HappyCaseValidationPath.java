/*
 * Copyright 2016-2017 Liam Williams <liam.williams@zoho.com>.
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
 */
class HappyCaseValidationPath<Happy, Sad, SadAggregate> extends HappyCaseHappyPath<Happy, SadAggregate> implements ValidationPath<Happy, Sad, SadAggregate> {

    private final Mapping<List<Sad>, SadAggregate> sadAggregateMapping;

    HappyCaseValidationPath(Happy happy, Mapping<List<Sad>, SadAggregate> sadAggregateMapping) {
        super(happy);
        this.sadAggregateMapping = sadAggregateMapping;
    }

    @Override
    public ValidationPath<Happy, Sad, SadAggregate> validateAll(List<? extends Validator<Happy, Sad>> validators) {
        return validateAllInto(sadAggregateMapping, validators);
    }

    @Override
    public ValidationPath<Happy, Sad, SadAggregate> validateAllInto(Mapping<List<Sad>, SadAggregate> sadAggregateMapping, List<? extends Validator<Happy, Sad>> validators) {
        try {
            List<Sad> validationFailures = validationFailures(validators);
            if (validationFailures.isEmpty()) {
                return ValidationPath.validationPathInto(happy, sadAggregateMapping);
            }
            SadAggregate sadAggregate = sadAggregateMapping.map(validationFailures);
            return ValidationPath.validationFailure(sadAggregate);
        } catch (Exception technicalFailure) {
            return ValidationPath.technicalFailure(technicalFailure);
        }
    }

    private List<Sad> validationFailures(List<? extends Validator<Happy, Sad>> validators) throws Exception {
        List<Sad> validationFailures = new ArrayList<>(validators.size());
        for (Validator<Happy, Sad> validator : validators) {
            validator.attempt(happy).ifPresent(validationFailures::add);
        }
        return validationFailures;
    }
}
