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

import java.util.List;
import java.util.Optional;

/**
 * A {@link HappyCaseHappyPath} is a {@link HappyPath} that is actually a {@link HappyCase}.
 * <p>
 * {@inheritDoc}
 */
class HappyCaseHappyPath<Happy, Sad> extends HappyCase<Happy, Sad> implements HappyPath<Happy, Sad> {

    HappyCaseHappyPath(Happy happy) {
        super(happy);
    }

    @Override
    public Optional<Happy> toOptional() {
        return Optional.of(happy);
    }

    @Override
    public TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return TechnicalFailure.happyPath(happy);
    }

    @Override
    public SadPath<Happy, Sad> ifSad() {
        return SadPath.happyPath(happy);
    }

    @Override
    public <NewHappy> HappyPath<NewHappy, Sad> then(Mapping<Happy, BusinessFlow<NewHappy, Sad, ?>> action) {
        try {
            return action.map(happy).ifHappy();
        } catch (Exception e) {
            return HappyPath.technicalFailure(e);
        }
    }

    @Override
    public <NewHappy> HappyPath<NewHappy, Sad> map(Mapping<Happy, NewHappy> mapping) {
        try {
            return HappyPath.happyPath(mapping.map(happy));
        } catch (Exception e) {
            return HappyPath.technicalFailure(e);
        }
    }

    @Override
    public HappyPath<Happy, Sad> peek(Peek<Happy> peek) {
        try {
            peek.peek(happy);
            return this;
        } catch (Exception e) {
            return HappyPath.technicalFailure(e);
        }
    }

    @Override
    public HappyPath<Happy, Sad> attempt(ActionThatMightFail<Happy, Sad> actionThatMightFail) {
        try {
            return actionThatMightFail.attempt(happy).toHappyPath(happy);
        } catch (Exception e) {
            return HappyPath.technicalFailure(e);
        }
    }

    @Override
    public HappyPath<Happy, Sad> attemptAll(List<? extends ActionThatMightFail<Happy, Sad>> actionsThatMightFail) {
        HappyPath<Happy, Sad> result = this;
        for (ActionThatMightFail<Happy, Sad> actionThatMightFail : actionsThatMightFail) {
            result = result.attempt(actionThatMightFail);
        }
        return result;
    }
}
