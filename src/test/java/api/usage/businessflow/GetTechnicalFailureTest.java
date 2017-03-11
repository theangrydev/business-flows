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
package api.usage.businessflow;

import api.usage.Happy;
import api.usage.Sad;
import io.github.theangrydev.businessflows.HappyPath;
import io.github.theangrydev.businessflows.TechnicalFailure;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class GetTechnicalFailureTest implements WithAssertions {

    @Test
    public void canGetTechnicalFailure() {
        Exception exception = new Exception();

        TechnicalFailure<Happy, Sad> technicalFailure = TechnicalFailure.technicalFailure(exception);

        assertThat(technicalFailure.getTechnicalFailure()).isEqualTo(exception);
    }

    @Test
    public void attemptingToGetTechnicalFailureThatIsNotThereThrowsAnIllegalStateException() {
        HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(new Happy("name"));

        assertThatThrownBy(happyPath::getTechnicalFailure)
                .hasMessage("Not present. This is: 'Happy: name'.")
                .isInstanceOf(IllegalStateException.class);
    }
}
