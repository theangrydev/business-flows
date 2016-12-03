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
 * A {@link SadCaseSadPath} is a {@link SadPath} that is actually a {@link SadCase}.
 */
class SadCaseSadPath<Happy, Sad> extends SadCase<Happy, Sad> implements SadPath<Happy, Sad> {

    SadCaseSadPath(Sad sad) {
        super(sad);
    }

    @Override
    public Optional<Sad> toOptional() {
        return Optional.of(sad);
    }

    @Override
    public TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return TechnicalFailure.sadPath(sad);
    }

    @Override
    public HappyPath<Happy, Sad> ifHappy() {
        return HappyPath.sadPath(sad);
    }

    @Override
    public <NewSad> SadPath<Happy, NewSad> then(Mapping<Sad, ? extends BusinessFlow<Happy, NewSad>> action) {
        try {
            return action.map(sad).ifSad();
        } catch (Exception e) {
            return SadPath.technicalFailure(e);
        }
    }

    @Override
    public <NewSad> SadPath<Happy, NewSad> map(Mapping<Sad, NewSad> mapping) {
        try {
            return SadPath.sadPath(mapping.map(sad));
        } catch (Exception e) {
            return SadPath.technicalFailure(e);
        }
    }

    @Override
    public HappyPath<Happy, Sad> recover(Mapping<Sad, Happy> recovery) {
        try {
            return HappyPath.happyPath(recovery.map(sad));
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
    public SadPath<Happy, Sad> peek(Peek<Sad> peek) {
        try {
            peek.peek(sad);
            return this;
        } catch (Exception e) {
            return SadPath.technicalFailure(e);
        }
    }
}
