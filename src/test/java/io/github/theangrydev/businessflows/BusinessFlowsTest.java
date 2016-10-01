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

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.List;

import static io.github.theangrydev.businessflows.PotentialFailure.failure;
import static io.github.theangrydev.businessflows.PotentialFailure.success;
import static java.lang.String.format;
import static java.util.Arrays.asList;


public class BusinessFlowsTest implements WithAssertions {

    private class Sad {
    }

    private class Happy {

    }

    @Test
    public void getBiasThatIsPresentReturnsIt() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = HappyPath.happyPath(expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void getBiasThatIsNotPresentThrowsIllegalStateException() {
        Sad sad = new Sad();
        assertThatThrownBy(() -> SadPath.sadPath(sad).ifHappy().get())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage(format("Not present. Business case is: 'Sad: %s'.", sad));
    }

    @Test
    public void orElseBiasThatIsNotPresentReturnsTheAlternative() {
        Happy expectedHappy = new Happy();
        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad()).ifHappy().orElse(expectedHappy);

        assertThat(actualHappy).isEqualTo(expectedHappy);
    }

    @Test
    public void orElseGetBiasThatIsNotPresentReturnsTheAlternative() {
        Happy expectedHappy = new Happy();
        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad()).ifHappy().orElseGet(() -> expectedHappy);

        assertThat(actualHappy).isEqualTo(expectedHappy);
    }

    @Test
    public void validateWithMultipleFailureTurnsSad() {
        Sad firstSad = new Sad();
        Sad secondSad = new Sad();

        List<? extends ActionThatMightFail<Happy, Sad>> validators = asList(happy -> failure(firstSad), happy -> failure(secondSad));

        List<Sad> actualSads = ValidationPath.validate(new Happy(), validators)
                .ifSad().get();

        assertThat(actualSads).containsExactly(firstSad, secondSad);
    }

    @Test
    public void validateWithMultiplePassesStaysHappy() {
        Happy originalHappy = new Happy();

        List<? extends ActionThatMightFail<Happy, Sad>> validators = asList(happy -> success(), happy -> success());

        Happy actualHappy = ValidationPath.validate(originalHappy, validators)
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }
}
