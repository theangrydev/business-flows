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

import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.theangrydev.businessflows.FlowTracker.endFlow;

/**
 * A {@link HappyCase} is a {@link BusinessCase} that is actually in a happy state.
 * <p>
 * {@inheritDoc}
 */
class HappyCase<Happy, Sad> implements BusinessCase<Happy, Sad> {

    final Happy happy;

    HappyCase(Happy happy) {
        this.happy = happy;
    }

    @Override
    public PotentialFailure<Sad> toPotentialFailure(Function<Exception, Sad> technicalFailureMapping) {
        return PotentialFailure.success();
    }

    @Override
    public <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner) {
        endFlow(this);
        try {
            return happyJoiner.map(happy);
        } catch (Exception technicalFailure) {
            return technicalFailureJoiner.apply(technicalFailure);
        }
    }

    @Override
    public <Result> Result joinOrThrow(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws Exception {
        endFlow(this);
        return happyJoiner.map(happy);
    }

    @Override
    public void consumeOrThrow(Peek<Happy> happyConsumer, Peek<Sad> sadConsumer) throws Exception {
        endFlow(this);
        happyConsumer.peek(happy);
    }

    @Override
    public void consume(Peek<Happy> happyConsumer, Peek<Sad> sadConsumer, Consumer<Exception> technicalFailureConsumer) {
        endFlow(this);
        try {
            happyConsumer.peek(happy);
        } catch (Exception technicalFailure) {
            technicalFailureConsumer.accept(technicalFailure);
        }
    }

    @Override
    public String toString() {
        return "Happy: " + happy;
    }
}
