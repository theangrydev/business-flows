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

class SadCaseHappyPath<Happy, Sad> extends SadCase<Happy, Sad> implements HappyPath<Happy, Sad> {

    SadCaseHappyPath(Sad sad) {
        super(sad);
    }

    @SuppressWarnings("unchecked") // Only the Happy changes and it is not present so all that changes is the types
    @Override
    public <NewHappy> HappyPath<NewHappy, Sad> then(Mapping<Happy, BusinessFlow<NewHappy, Sad, ?>> action) {
        return (HappyPath<NewHappy, Sad>) this;
    }

    @SuppressWarnings("unchecked") // Only the Happy changes and it is not present so all that changes is the types
    @Override
    public <NewHappy> HappyPath<NewHappy, Sad> map(Mapping<Happy, NewHappy> mapping) {
        return (HappyPath<NewHappy, Sad>) this;
    }

    @Override
    public HappyPath<Happy, Sad> peek(Peek<Happy> peek) {
        return this;
    }

    @Override
    public HappyPath<Happy, Sad> attempt(ActionThatMightFail<Happy, Sad> actionThatMightFail) {
        return this;
    }

    @Override
    public Optional<Happy> toOptional() {
        return Optional.empty();
    }

    @Override
    public TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return TechnicalFailure.sadPath(sad);
    }

    @Override
    public SadPath<Happy, Sad> ifSad() {
        return SadPath.sadPath(sad);
    }
}
