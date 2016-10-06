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


import java.util.Arrays;
import java.util.List;

import static io.github.theangrydev.businessflows.Mapping.identity;

/**
 * A {@link ValidationPath} is a special kind of {@link HappyPath} that can accumulate several validation failures into
 * a list of {@link Sad}, if there are any failures.
 *
 * @param <SadAggregate> The type that the list of {@link Sad} validation errors will be aggregated into
 *
 * {@inheritDoc}
 */
public interface ValidationPath<Happy, Sad, SadAggregate> extends HappyPath<Happy, SadAggregate> {

    /**
     * Provides a {@link ValidationPath} view over a known {@link Happy} object.
     *
     * @param happy The happy object to initiate the flow with
     * @param sadAggregateMapping The list of {@link Sad} validation errors will be mapped to the {@link SadAggregate}
     * @param <Happy> The type of happy object the resulting {@link ValidationPath} may contain
     * @param <Sad> The type of validation errors the resulting {@link ValidationPath} may contain
     * @param <SadAggregate> The type that the list of {@link Sad} validation errors will be aggregated into
     * @return A {@link ValidationPath} that is happy on the inside
     */
    static <Happy, Sad, SadAggregate> ValidationPath<Happy, Sad, SadAggregate> validationPathInto(Happy happy, Mapping<List<Sad>, SadAggregate> sadAggregateMapping) {
        return new HappyCaseValidationPath<>(happy, sadAggregateMapping);
    }

    /**
     * Provides a {@link ValidationPath} view over a known {@link Happy} object, aggregated into a list of {@link Sad}.
     *
     * @param happy The happy object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link ValidationPath} may contain
     * @param <Sad> The type of validation errors the resulting {@link ValidationPath} may contain
     * @return A {@link ValidationPath} that is happy on the inside
     */
    static <Happy, Sad> ValidationPath<Happy, Sad, List<Sad>> validationPath(Happy happy) {
        return validationPathInto(happy, identity());
    }

    /**
     * Provides a {@link ValidationPath} view over a known {@link Happy} object.
     *
     * @param validationFailures The validation failures to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link ValidationPath} may contain
     * @param <Sad> The type of validation errors the resulting {@link ValidationPath} may contain
     * @param <SadAggregate> The type that the list of {@link Sad} validation errors will be aggregated into
     * @return A {@link ValidationPath} that has failed validation
     */
    static <Happy, Sad, SadAggregate> ValidationPath<Happy, Sad, SadAggregate> validationFailure(SadAggregate validationFailures) {
        return new SadCaseValidationPath<>(validationFailures);
    }

    /**
     * Provides a {@link ValidationPath} view over a known {@link Exception} object.
     *
     * @param technicalFailure The technical failure to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link ValidationPath} may contain
     * @param <Sad> The type of validation errors the resulting {@link ValidationPath} may contain
     * @param <SadAggregate> The type that the list of {@link Sad} validation errors will be aggregated into
     * @return A {@link ValidationPath} that is a technical failure
     */
    static <Happy, Sad, SadAggregate> ValidationPath<Happy, Sad, SadAggregate> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailureCaseValidationPath<>(technicalFailure);
    }

    /**
     * Validate the given {@link Happy} object by running the given list of validators over it.
     * All validators that fail will be accumulated into the {@link SadAggregate} result.
     * The first technical failure encountered will result in a technical failure overall.
     *
     * @param happy The {@link Happy} object to validate
     * @param sadAggregateMapping The list of {@link Sad} validation errors will be mapped to the {@link SadAggregate}
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @param <Happy> The type of happy  object the resulting {@link ValidationPath} may represent
     * @param <Sad> The type of sad object the resulting {@link ValidationPath} may represent
     * @param <SadAggregate> The type that the list of {@link Sad} validation errors will be aggregated into
     * @return The result of applying all the validators
     */
     static <Happy, Sad, SadAggregate> ValidationPath<Happy, Sad, SadAggregate> validateAllInto(Happy happy, Mapping<List<Sad>, SadAggregate> sadAggregateMapping, List<? extends Validator<Happy, Sad>> validators) {
         ValidationPath<Happy, Sad, SadAggregate> happyPath = ValidationPath.validationPathInto(happy, sadAggregateMapping);
         return happyPath.validateAll(validators);
    }

    /**
     * Validate the given {@link Happy} object by running the given list of validators over it.
     * All validators that fail will be accumulated into a list of {@link Sad} results.
     * The first technical failure encountered will result in a technical failure overall.
     *
     * @param happy The {@link Happy} object to validate
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @param <Happy> The type of happy  object the resulting {@link ValidationPath} may represent
     * @param <Sad> The type of sad object the resulting {@link ValidationPath} may represent
     * @return The result of applying all the validators
     */
    static <Happy, Sad> ValidationPath<Happy, Sad, List<Sad>> validateAll(Happy happy, List<? extends Validator<Happy, Sad>> validators) {
        return validateAllInto(happy, identity(), validators);
    }

    /**
     * Validate the given {@link Happy} object by running the given list of validators over it.
     * All validators that fail will be accumulated into the {@link SadAggregate} result.
     * The first technical failure encountered will result in a technical failure overall.
     *
     * @param happy The {@link Happy} object to validate
     * @param sadAggregateMapping The list of {@link Sad} validation errors will be mapped to the {@link SadAggregate}
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @param <Happy> The type of happy  object the resulting {@link ValidationPath} may represent
     * @param <Sad> The type of sad object the resulting {@link ValidationPath} may represent
     * @param <SadAggregate> The type that the list of {@link Sad} validation errors will be aggregated into
     * @return The result of applying all the validators
     */
    @SafeVarargs
    static <Happy, Sad, SadAggregate> ValidationPath<Happy, Sad, SadAggregate> validateAllInto(Happy happy, Mapping<List<Sad>, SadAggregate> sadAggregateMapping, Validator<Happy, Sad>... validators) {
        return validateAllInto(happy, sadAggregateMapping, Arrays.asList(validators));
    }


    /**
     * Validate the given {@link Happy} object by running the given list of validators over it.
     * All validators that fail will be accumulated into a list of {@link Sad} results.
     * The first technical failure encountered will result in a technical failure overall.
     *
     * @param happy The {@link Happy} object to validate
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @param <Happy> The type of happy  object the resulting {@link ValidationPath} may represent
     * @param <Sad> The type of sad object the resulting {@link ValidationPath} may represent
     * @return The result of applying all the validators
     */
    @SafeVarargs
    static <Happy, Sad> ValidationPath<Happy, Sad, List<Sad>> validateAll(Happy happy, Validator<Happy, Sad>... validators) {
        return validateAll(happy, Arrays.asList(validators));
    }

    /**
     * Perform a subsequent round of validation, which will take place if the previous one succeeded.
     * This can be useful when you want e.g. expensive validators to run after cheap ones, or if you want to group validators together.
     *
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @return The result of applying all the validators
     */
    ValidationPath<Happy, Sad, SadAggregate> validateAll(List<? extends Validator<Happy, Sad>> validators);

    /**
     * Perform a subsequent round of validation, which will take place if the previous one succeeded.
     * This can be useful when you want e.g. expensive validators to run after cheap ones, or if you want to group validators together.
     *
     * This method changes the {@link SadAggregate} mapping. This can be useful for e.g. rounds of validation that are
     * aggregated with different messages such as "Missing: A, B" in one round and "Invalid: A, B"  in another round.
     *
     * @param sadAggregateMapping The list of {@link Sad} validation errors will be mapped to the {@link SadAggregate}
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @return The result of applying all the validators
     */
    ValidationPath<Happy, Sad, SadAggregate> validateAllInto(Mapping<List<Sad>, SadAggregate> sadAggregateMapping, List<? extends Validator<Happy, Sad>> validators);

    /**
     * Helper method to turn an array of {@link Validator} into a list of {@link Validator}.
     *
     * @param validators The validators
     * @param <Happy> The type of happy object the list of {@link Validator} can validate
     * @param <Sad> The type of validation error the list of {@link Validator} can produce
     * @return A list of {@link Validator}
     */
    @SafeVarargs
    static <Happy, Sad> List<Validator<Happy, Sad>> validators(Validator<Happy, Sad>... validators) {
        return Arrays.asList(validators);
    }
}
