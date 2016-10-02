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

/**
 * A {@link HappyCaseSadPath} is a {@link SadPath} that is actually a {@link HappyCase}.
 *
 * {@inheritDoc}
 */
class HappyCaseSadPath<Happy, Sad> extends HappyCase<Happy, Sad> implements SadPath<Happy, Sad> {

    HappyCaseSadPath(Happy happy) {
        super(happy);
    }

    @Override
    public Optional<Sad> toOptional() {
        return Optional.empty();
    }

    @Override
    public TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return TechnicalFailure.happyPath(happy);
    }

    @Override
    public HappyPath<Happy, Sad> ifHappy() {
        return HappyPath.happyPath(happy);
    }

    @SuppressWarnings("unchecked") // Only the Sad changes and it is not present so all that changes is the types
    @Override
    public <NewSad> SadPath<Happy, NewSad> then(Mapping<Sad, SadPath<Happy, NewSad>> action) {
        return (SadPath<Happy, NewSad>) this;
    }

    @SuppressWarnings("unchecked") // Only the Sad changes and it is not present so all that changes is the types
    @Override
    public <NewSad> SadPath<Happy, NewSad> map(Mapping<Sad, NewSad> mapping) {
        return (SadPath<Happy, NewSad>) this;
    }

    @Override
    public HappyPath<Happy, Sad> recover(Mapping<Sad, Happy> recovery) {
        return HappyPath.happyPath(happy);
    }

    @Override
    public HappyPath<Happy, Sad> recover(Attempt<Happy> recovery) {
        return HappyPath.happyPath(happy);
    }

    @Override
    public SadPath<Happy, Sad> peek(Peek<Sad> peek) {
        return this;
    }
}
