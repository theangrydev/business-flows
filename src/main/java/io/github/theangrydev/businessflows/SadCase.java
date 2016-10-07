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

import java.util.function.Function;

/**
 * A {@link SadCase} is a {@link BusinessCase} that is actually in a sad state.
 * <p>
 * {@inheritDoc}
 */
class SadCase<Happy, Sad> implements BusinessCase<Happy, Sad> {

    final Sad sad;

    SadCase(Sad sad) {
        this.sad = sad;
    }

    @Override
    public PotentialFailure<Sad> toPotentialFailure(Function<Exception, Sad> technicalFailureMapping) {
        return PotentialFailure.failure(sad);
    }

    @Override
    public <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner) {
        try {
            return sadJoiner.map(sad);
        } catch (Exception technicalFailure) {
            return technicalFailureJoiner.apply(technicalFailure);
        }
    }

    @Override
    public <Result> Result joinOrThrow(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws Exception {
        return sadJoiner.map(sad);
    }

    @Override
    public String toString() {
        return "Sad: " + sad;
    }
}
