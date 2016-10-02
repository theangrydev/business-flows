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
 * A {@link HappyCaseTechnicalFailure} is a {@link TechnicalFailure} that is actually a {@link HappyCase}.
 *
 * {@inheritDoc}
 */
class HappyCaseTechnicalFailure<Happy, Sad> extends HappyCase<Happy, Sad> implements TechnicalFailure<Happy, Sad> {

    HappyCaseTechnicalFailure(Happy happy) {
        super(happy);
    }

    @Override
    public Optional<Exception> toOptional() {
        return Optional.empty();
    }

    @Override
    public TechnicalFailure<Happy, Sad> then(Mapping<Exception, TechnicalFailure<Happy, Sad>> action) {
        return this;
    }

    @Override
    public TechnicalFailure<Happy, Sad> map(Mapping<Exception, Exception> mapping) {
        return this;
    }

    @Override
    public HappyPath<Happy, Sad> recover(Mapping<Exception, Happy> recovery) {
        return HappyPath.happyPath(happy);
    }

    @Override
    public HappyPath<Happy, Sad> recover(Attempt<Happy> recovery) {
        return HappyPath.happyPath(happy);
    }

    @Override
    public SadPath<Happy, Sad> mapToSadPath(Mapping<Exception, Sad> mapping) {
        return SadPath.happyPath(happy);
    }

    @Override
    public SadPath<Happy, Sad> mapToSadPath(Attempt<Sad> mapping) {
        return SadPath.happyPath(happy);
    }

    @Override
    public TechnicalFailure<Happy, Sad> peek(Peek<Exception> peek) {
        return this;
    }

    @Override
    public HappyPath<Happy, Sad> ifHappy() {
        return HappyPath.happyPath(happy);
    }

    @Override
    public SadPath<Happy, Sad> ifSad() {
        return SadPath.happyPath(happy);
    }
}
