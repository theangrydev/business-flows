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

public class TechnicalFailure<Sad, Happy> extends BusinessFlow<Sad, Happy, Exception> {
    protected TechnicalFailure(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public static <Sad, Happy> TechnicalFailure<Sad, Happy> sadPath(Sad sad) {
        return new TechnicalFailure<>(sad, null, null);
    }

    public static <Sad, Happy> TechnicalFailure<Sad, Happy> happyPath(Happy happy) {
        return new TechnicalFailure<>(null, happy, null);
    }

    public static <Sad, Happy> TechnicalFailure<Sad, Happy> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailure<>(null, null, technicalFailure);
    }

    @Override
    public Optional<Exception> toOptional() {
        return Optional.ofNullable(technicalFailure);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException") // This is intentional to ensure that all exceptions are converted to technical failures
    public TechnicalFailure<Sad, Happy> then(Mapping<Exception, TechnicalFailure<Sad, Happy>> action) {
        return join(TechnicalFailure::sadPath, TechnicalFailure::happyPath, technicalFailure1 -> {
            try {
                return action.map(technicalFailure1);
            } catch (Exception technicalFailureDuringAction) {
                return TechnicalFailure.technicalFailure(technicalFailureDuringAction);
            }
        });
    }

    public HappyPath<Sad, Happy> recover(Mapping<Exception, Happy> recovery) {
        return then(technicalFailure -> happyPath(recovery.map(technicalFailure))).ifHappy();
    }

    public SadPath<Sad, Happy> mapToSadPath(Mapping<Exception, Sad> mapping) {
        return then(mapping.andThen(TechnicalFailure::sadPath)).ifSad();
    }

    public TechnicalFailure<Sad, Happy> map(Mapping<Exception, Exception> mapping) {
        return then(mapping.andThen(TechnicalFailure::technicalFailure));
    }

    public TechnicalFailure<Sad, Happy> peek(Peek<Exception> peek) {
        return then(technicalFailure -> {
            peek.peek(technicalFailure);
            return technicalFailure(technicalFailure);
        });
    }
}
