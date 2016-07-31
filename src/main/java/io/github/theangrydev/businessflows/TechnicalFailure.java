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

/**
 * {@inheritDoc}
 */
public class TechnicalFailure<Happy, Sad> extends BusinessFlow<Happy, Sad, Exception> {
    TechnicalFailure(BusinessCase<Happy, Sad> businessCase) {
        super(BusinessCase::technicalFailureOptional, businessCase);
    }

    public static <Happy, Sad> TechnicalFailure<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailure<>(new TechnicalFailureCase<>(technicalFailure));
    }

    public TechnicalFailure<Happy, Sad> then(Mapping<Exception, TechnicalFailure<Happy, Sad>> action) {
        return join(TechnicalFailure::happyPath, TechnicalFailure::sadPath, technicalFailure1 -> {
            try {
                return action.map(technicalFailure1);
            } catch (Exception technicalFailureDuringAction) {
                return TechnicalFailure.technicalFailure(technicalFailureDuringAction);
            }
        });
    }

    public HappyPath<Happy, Sad> recover(Mapping<Exception, Happy> recovery) {
        return then(technicalFailure -> happyPath(recovery.map(technicalFailure))).ifHappy();
    }

    public SadPath<Happy, Sad> mapToSadPath(Mapping<Exception, Sad> mapping) {
        return then(mapping.andThen(TechnicalFailure::sadPath)).ifSad();
    }

    public TechnicalFailure<Happy, Sad> map(Mapping<Exception, Exception> mapping) {
        return then(mapping.andThen(TechnicalFailure::technicalFailure));
    }

    public TechnicalFailure<Happy, Sad> peek(Peek<Exception> peek) {
        return then(technicalFailure -> {
            peek.peek(technicalFailure);
            return this;
        });
    }

    private static <Happy, Sad> TechnicalFailure<Happy, Sad> sadPath(Sad sad) {
        return new TechnicalFailure<>(new SadCase<>(sad));
    }

    private static <Happy, Sad> TechnicalFailure<Happy, Sad> happyPath(Happy happy) {
        return new TechnicalFailure<>(new HappyCase<>(happy));
    }
}
