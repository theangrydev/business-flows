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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public abstract class BusinessFlow<Sad, Happy, Bias> {

    private final BusinessCase<Sad, Happy> businessCase;

    BusinessFlow(BusinessCase<Sad, Happy> businessCase) {
        this.businessCase = businessCase;
    }

    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        return businessCase.join(sadJoiner, happyJoiner, technicalFailureJoiner);
    }

    public Optional<Bias> toOptional() {
        return bias().apply(businessCase);
    }

    public Bias get() {
        return orElseThrow(() -> new RuntimeException(format("Not present. Business case is: '%s'.", businessCase)));
    }

    public Bias orElse(Bias alternative) {
        return toOptional().orElse(alternative);
    }

    public Bias orElseGet(Supplier<Bias> alternativeSupplier) {
        return toOptional().orElseGet(alternativeSupplier);
    }

    public <X extends Exception> Bias orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    public TechnicalFailure<Sad, Happy> ifTechnicalFailure() {
        return new TechnicalFailure<>(businessCase);
    }

    public SadPath<Sad, Happy> ifSad() {
        return new SadPath<>(businessCase);
    }

    public HappyPath<Sad, Happy> ifHappy() {
        return new HappyPath<>(businessCase);
    }

    protected abstract Function<BusinessCase<Sad, Happy>, Optional<Bias>> bias();
}
