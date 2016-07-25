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

    protected final Sad sad;
    protected final Happy happy;
    protected final Exception technicalFailure;

    protected BusinessFlow(Sad sad, Happy happy, Exception technicalFailure) {
        this.sad = sad;
        this.happy = happy;
        this.technicalFailure = technicalFailure;
    }

    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        if (isHappy()) {
            return happyJoiner.apply(happy);
        } else if (isSad()) {
            return sadJoiner.apply(sad);
        } else if (isTechnicalFailure()) {
            return technicalFailureJoiner.apply(technicalFailure);
        } else {
            throw new IllegalStateException("Impossible scenario. There must always be a happy or sad or technical failure.");
        }
    }

    private boolean isTechnicalFailure() {
        return technicalFailure != null;
    }

    private boolean isSad() {
        return sad != null;
    }

    private boolean isHappy() {
        return happy != null;
    }

    public abstract Optional<Bias> toOptional();

    public Bias get() {
        return orElseThrow(() -> new RuntimeException(format("Not present. Happy path was '%s'. Sad path was '%s'. Exception was '%s'.", happy, sad, technicalFailure)));
    }

    public Bias orElse(Bias alternative) {
        return toOptional().orElse(alternative);
    }

    public Bias orElseGet(Supplier<Bias> alternativeSupplier) {
        return toOptional().orElseGet(alternativeSupplier);
    }

    public <X extends Throwable> Bias orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    public TechnicalFailure<Sad, Happy> ifTechnicalFailure() {
        return new TechnicalFailure<>(sad, happy, technicalFailure);
    }

    public SadPath<Sad, Happy> ifSad() {
        return new SadPath<>(sad, happy, technicalFailure);
    }

    public HappyPath<Sad, Happy> ifHappy() {
        return new HappyPath<>(sad, happy, technicalFailure);
    }
}
