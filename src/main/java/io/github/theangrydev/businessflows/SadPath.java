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
public class SadPath<Happy, Sad> extends BusinessFlow<Happy, Sad, Sad> {

    SadPath(BusinessCase<Happy, Sad> businessCase) {
        super(BusinessCase::sadOptional, businessCase);
    }

    public static <Happy, Sad> SadPath<Happy, Sad> sadPath(Sad sad) {
        return new SadPath<>(new SadCase<>(sad));
    }

    public SadPath<Happy, Sad> peek(Peek<Sad> peek) {
        return then(sad -> {
            peek.peek(sad);
            return this;
        });
    }

    public <NewSad> SadPath<Happy, NewSad> then(Mapping<Sad, SadPath<Happy, NewSad>> action) {
        return join(SadPath::happyPath, sad -> {
            try {
                return action.map(sad);
            } catch (Exception technicalFailure) {
                return technicalFailure(technicalFailure);
            }
        }, SadPath::technicalFailure);
    }

    public HappyPath<Happy, Sad> recover(Mapping<Sad, Happy> recovery) {
        return this.<Sad>then(sad -> happyPath(recovery.map(sad))).ifHappy();
    }

    public <NewSad> SadPath<Happy, NewSad> map(Mapping<Sad, NewSad> mapping) {
        return then(mapping.andThen(SadPath::sadPath));
    }

    private static <Happy, Sad> SadPath<Happy, Sad> happyPath(Happy happy) {
        return new SadPath<>(new HappyCase<>(happy));
    }

    private static <Happy, Sad> SadPath<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new SadPath<>(new TechnicalFailureCase<>(technicalFailure));
    }
}
