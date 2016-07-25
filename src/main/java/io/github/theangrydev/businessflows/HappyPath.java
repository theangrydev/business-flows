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

public class HappyPath<Sad, Happy> extends BusinessFlow<Sad, Happy, Happy> {

    HappyPath(BusinessCase<Sad, Happy> businessCase) {
        super(businessCase);
    }

    public static <Sad, Happy> HappyPath<Sad, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        return happyAttempt(happyAttempt.andThen(HappyPath::happyPath), HappyPath::technicalFailure);
    }

    public static <Result> Result happyAttempt(HappyAttempt<Result> happyAttempt, Function<Exception, Result> failureHandler) {
        try {
            return happyAttempt.happy();
        } catch (Exception technicalFailure) {
            return failureHandler.apply(technicalFailure);
        }
    }

    public static <Sad, Happy> HappyPath<Sad, Happy> happyPath(Happy happy) {
        return new HappyPath<>(new HappyCase<>(happy));
    }

    public <NewHappy> HappyPath<Sad, NewHappy> then(Mapping<Happy, BusinessFlow<Sad, NewHappy, ?>> action) {
        return join(HappyPath::sadPath, happy -> {
            try {
                return action.map(happy).ifHappy();
            } catch (Exception technicalFailure) {
                return HappyPath.technicalFailure(technicalFailure);
            }
        }, HappyPath::technicalFailure);
    }

    public <NewHappy> HappyPath<Sad, NewHappy> map(Mapping<Happy, NewHappy> mapping) {
        return then(mapping.andThen(HappyPath::happyPath));
    }

    public HappyPath<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return then(happy -> actionThatMightFail.attempt(happy).map(HappyPath::<Sad, Happy>sadPath).orElse(HappyPath.happyPath(happy)));
    }

    public HappyPath<Sad, Happy> peek(Peek<Happy> peek) {
        return then(happy -> {
            peek.peek(happy);
            return this;
        });
    }

    private static <Sad, Happy> HappyPath<Sad, Happy> sadPath(Sad sad) {
        return new HappyPath<>(new SadCase<>(sad));
    }

    private static <Sad, Happy> HappyPath<Sad, Happy> technicalFailure(Exception technicalFailure) {
        return new HappyPath<>(new TechnicalFailureCase<>(technicalFailure));
    }

    @Override
    Function<BusinessCase<Sad, Happy>, Optional<Happy>> bias() {
        return BusinessCase::happyOptional;
    }
}
