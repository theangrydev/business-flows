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

public class SadCaseTest {

    private final Sad sad = new Sad();
    private final SadCase<?, Sad> sadCase = new SadCase<>(sad);

    class Sad {

    }

    @Test
    public void toStringIsSad() {
        assertThat(sadCase).hasToString("Sad: " + sad);
    }

    @Test
    public void joinsSad() {
        String join = sadCase.join(null, Object::toString, null);

        assertThat(join).isEqualTo(sad.toString());
    }

    @Test
    public void joinsHappyWithoutTechnicalFailureArgument() throws Exception {
        String join = sadCase.join(null, Object::toString);

        assertThat(join).isEqualTo(sad.toString());
    }

    @Test
    public void happyOptionalIsEmpty() {
        assertThat(sadCase.happyOptional()).isEmpty();
    }

    @Test
    public void sadOptionalIsPresent() {
        assertThat(sadCase.sadOptional()).contains(sad);
    }

    @Test
    public void technicalFailureOptionalIsEmpty() {
        assertThat(sadCase.technicalFailureOptional()).isEmpty();
    }
}