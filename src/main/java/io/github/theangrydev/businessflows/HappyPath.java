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

public class HappyPath<Happy, Sad> extends BusinessFlow<Happy, Sad, Happy> {

    HappyPath(BusinessCase<Happy, Sad> businessCase) {
        super(BusinessCase::happyOptional, businessCase);
    }

    public static <Happy, Sad> HappyPath<Happy, Sad> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        try {
            return happyPath(happyAttempt.happy());
        } catch (Exception technicalFailure) {
            return technicalFailure(technicalFailure);
        }
    }

    public static <Happy, Sad> HappyPath<Happy, Sad> happyAttempt(HappyAttempt<Happy> happyAttempt, Mapping<Exception, Sad> failureMapping) {
        try {
            return happyPath(happyAttempt.happy());
        } catch (Exception technicalFailure) {
            try {
                return sadPath(failureMapping.map(technicalFailure));
            } catch (Exception technicalFailureDuringFailureMapping) {
                return technicalFailure(technicalFailureDuringFailureMapping);
            }
        }
    }

    public static <Happy, Sad> HappyPath<Happy, Sad> happyPath(Happy happy) {
        return new HappyPath<>(new HappyCase<>(happy));
    }

    public <NewHappy> HappyPath<NewHappy, Sad> then(Mapping<Happy, BusinessFlow<NewHappy, Sad, ?>> action) {
        return join(happy -> {
            try {
                return action.map(happy).ifHappy();
            } catch (Exception technicalFailure) {
                return HappyPath.technicalFailure(technicalFailure);
            }
        }, HappyPath::sadPath, HappyPath::technicalFailure);
    }

    public <NewHappy> HappyPath<NewHappy, Sad> map(Mapping<Happy, NewHappy> mapping) {
        return then(mapping.andThen(HappyPath::happyPath));
    }

    public HappyPath<Happy, Sad> attempt(ActionThatMightFail<Happy, Sad> actionThatMightFail) {
        return then(happy -> actionThatMightFail.attempt(happy).map(HappyPath::<Happy, Sad>sadPath).orElse(HappyPath.happyPath(happy)));
    }

    public HappyPath<Happy, Sad> peek(Peek<Happy> peek) {
        return then(happy -> {
            peek.peek(happy);
            return this;
        });
    }

    private static <Happy, Sad> HappyPath<Happy, Sad> sadPath(Sad sad) {
        return new HappyPath<>(new SadCase<>(sad));
    }

    private static <Happy, Sad> HappyPath<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new HappyPath<>(new TechnicalFailureCase<>(technicalFailure));
    }
}
