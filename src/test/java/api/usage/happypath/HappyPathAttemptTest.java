/*
 * Copyright 2016-2017 Liam Williams <liam.williams@zoho.com>.
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
package api.usage.happypath;

import api.usage.Happy;
import api.usage.Sad;
import io.github.theangrydev.businessflows.Attempt;
import io.github.theangrydev.businessflows.HappyPath;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

/**
 * Sometimes it makes sense to attempt an action that will either produce a happy path or throw an exception.
 */
public class HappyPathAttemptTest implements WithAssertions {

    /**
     * An attempt can fail and turn into a technical failure.
     */
    @Test
    public void happyPathAttemptCanFail() {
        RuntimeException technicalFailure = new RuntimeException();
        Attempt<HappyPath<Happy, Sad>> attempt = () -> {throw technicalFailure;};

        HappyPath<Happy, Sad> happyPath = HappyPath.happyPathAttempt(attempt);

        assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
    }

    /**
     * An attempt can succeed and produce a happy path
     */
    @Test
    public void happyPathAttemptCanSucceed() {
        Happy happy = new Happy();
        Attempt<HappyPath<Happy, Sad>> attempt = () -> HappyPath.happyPath(happy);

        HappyPath<Happy, Sad> happyPath = HappyPath.happyPathAttempt(attempt);

        assertThat(happyPath.getHappy()).isSameAs(happy);
    }
}