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
 * A {@link TechnicalFailureCaseTechnicalFailure} is a {@link TechnicalFailure} that is actually a {@link TechnicalFailureCase}.
 * <p>
 * {@inheritDoc}
 */
class TechnicalFailureCaseTechnicalFailure<Happy, Sad> extends TechnicalFailureCase<Happy, Sad> implements TechnicalFailure<Happy, Sad> {

    TechnicalFailureCaseTechnicalFailure(Exception technicalFailure) {
        super(technicalFailure);
    }

    @Override
    public Optional<Exception> toOptional() {
        return Optional.of(technicalFailure);
    }

    @Override
    public SadPath<Happy, Sad> ifSad() {
        return SadPath.technicalFailure(technicalFailure);
    }

    @Override
    public TechnicalFailure<Happy, Sad> then(Mapping<Exception, TechnicalFailure<Happy, Sad>> action) {
        try {
            return action.map(technicalFailure);
        } catch (Exception e) {
            return TechnicalFailure.technicalFailure(e);
        }
    }

    @Override
    public TechnicalFailure<Happy, Sad> map(Mapping<Exception, Exception> mapping) {
        try {
            return TechnicalFailure.technicalFailure(mapping.map(technicalFailure));
        } catch (Exception e) {
            return TechnicalFailure.technicalFailure(e);
        }
    }

    @Override
    public HappyPath<Happy, Sad> recover(Mapping<Exception, Happy> recovery) {
        try {
            return HappyPath.happyPath(recovery.map(technicalFailure));
        } catch (Exception e) {
            return HappyPath.technicalFailure(e);
        }
    }

    @Override
    public HappyPath<Happy, Sad> recover(Attempt<Happy> recovery) {
        try {
            return HappyPath.happyPath(recovery.attempt());
        } catch (Exception e) {
            return HappyPath.technicalFailure(e);
        }
    }

    @Override
    public SadPath<Happy, Sad> mapToSadPath(Mapping<Exception, Sad> mapping) {
        try {
            return SadPath.sadPath(mapping.map(technicalFailure));
        } catch (Exception e) {
            return SadPath.technicalFailure(e);
        }
    }

    @Override
    public SadPath<Happy, Sad> mapToSadPath(Attempt<Sad> mapping) {
        try {
            return SadPath.sadPath(mapping.attempt());
        } catch (Exception e) {
            return SadPath.technicalFailure(e);
        }
    }

    @Override
    public TechnicalFailure<Happy, Sad> peek(Peek<Exception> peek) {
        try {
            peek.peek(technicalFailure);
            return this;
        } catch (Exception e) {
            return TechnicalFailure.technicalFailure(e);
        }
    }

    @Override
    public TechnicalFailure<Happy, Sad> throwIt() throws Exception {
        throw technicalFailure;
    }

    @Override
    public TechnicalFailure<Happy, Sad> throwItAsARuntimeException() throws RuntimeException {
        throw new RuntimeException(technicalFailure);
    }

    @Override
    public HappyPath<Happy, Sad> ifHappy() {
        return HappyPath.technicalFailure(technicalFailure);
    }
}
