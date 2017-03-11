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
package api;

import io.github.theangrydev.businessflows.Attempt;
import io.github.theangrydev.businessflows.HappyPath;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class HappyAttemptApiTest implements WithAssertions {

    /**
     * An attempt can fail and turn into a technical failure.
     */
    @Test
    public void happyAttemptCanFail() {
        RuntimeException technicalFailure = new RuntimeException();
        Attempt<Happy> attempt = () -> {throw technicalFailure;};

        HappyPath<Happy, Sad> happyPath = HappyPath.happyAttempt(attempt);

        assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
    }

    /**
     * An attempt that was once happy can be turned into a sad path.
     * The `happyAttempt` method is playing the role of the Try monad here, but is lifted into the Either monad immediately, which is why the `Sad` type has to be specified up front.
     */
    @Test
    public void happyAttemptCanIntroduceSadTypeViaThen() {
        Sad sad = new Sad();

        HappyPath<Happy, Sad> happyPath = HappyPath.<Happy, Sad>happyAttempt(Happy::new)
                .then(happy -> HappyPath.sadPath(sad));

        assertThat(happyPath.getSad()).isEqualTo(sad);
    }
}