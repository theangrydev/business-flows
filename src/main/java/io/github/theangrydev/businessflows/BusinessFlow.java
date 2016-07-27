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

public class BusinessFlow<Happy, Sad, Bias> {

    private final Function<BusinessCase<Happy, Sad>, Optional<Bias>> bias;
    private final BusinessCase<Happy, Sad> businessCase;

    BusinessFlow(Function<BusinessCase<Happy, Sad>, Optional<Bias>> bias, BusinessCase<Happy, Sad> businessCase) {
        this.bias = bias;
        this.businessCase = businessCase;
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner) {
        return businessCase.join(happyJoiner, sadJoiner, technicalFailureJoiner);
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Sad, Result> sadJoiner) throws Exception {
        return businessCase.join(happyJoiner, sadJoiner);
    }

    public Optional<Bias> toOptional() {
        return bias.apply(businessCase);
    }

    public Bias get() {
        return orElseThrow(() -> new IllegalStateException(format("Not present. Business case is: '%s'.", businessCase)));
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

    public TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return new TechnicalFailure<>(businessCase);
    }

    public SadPath<Happy, Sad> ifSad() {
        return new SadPath<>(businessCase);
    }

    public HappyPath<Happy, Sad> ifHappy() {
        return new HappyPath<>(businessCase);
    }
}
