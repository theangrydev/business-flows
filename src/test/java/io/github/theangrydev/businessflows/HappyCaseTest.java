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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HappyCaseTest {

    private final Exception technicalFailure = new Exception("technical failure");
    private final Happy happy = new Happy();
    private final HappyCase<Happy, ?> happyCase = new HappyCase<>(happy);

    class Happy {

    }

    @Test
    public void toStringIsHappy() {
        assertThat(happyCase).hasToString("Happy: " + happy);
    }

    @Test
    public void joinsHappy() {
        String join = happyCase.join(Object::toString, null, null);

        assertThat(join).isEqualTo(happy.toString());
    }

    @Test
    public void technicalFailureWhileJoiningHappyJoinsToTechnicalFailure() {
        String join = happyCase.join(happy -> {throw technicalFailure;}, null, Object::toString);

        assertThat(join).isEqualTo(technicalFailure.toString());
    }

    @Test
    public void joinsHappyWithoutTechnicalFailureArgument() throws Exception {
        String join = happyCase.joinOrThrow(Object::toString, null);

        assertThat(join).isEqualTo(happy.toString());
    }
}