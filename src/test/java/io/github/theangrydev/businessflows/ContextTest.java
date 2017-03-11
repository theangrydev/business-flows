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
package io.github.theangrydev.businessflows;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class ContextTest implements WithAssertions {

    class Foo {

    }

    class Bar {

    }

    class FooAndBar {
        private final Foo foo;
        private final Bar bar;

        FooAndBar(Foo foo, Bar bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    class MutableContext {
        private Foo foo;
        private Bar bar;

        public void save(Foo foo) {
            this.foo = foo;
        }

        public void save(Bar bar) {
            this.bar = bar;
        }
    }

    class GenericContext {
        private Map<Class<?>, Object> context = new HashMap<>();

        public void put(Object instance) {
            if (context.containsKey(instance.getClass())) {
                throw new IllegalArgumentException(format("Context already contains a '%s'", instance.getClass().getSimpleName()));
            }
            context.put(instance.getClass(), instance);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Class<T> type) {
            Object instance = context.get(type);
            if (instance == null) {
                throw new IllegalArgumentException(format("Context did not contain a '%s'", type.getSimpleName()));
            }
            return (T) instance;
        }
    }

    @Test
    public void exitEarlyContext() {
        Foo originalFoo = new Foo();
        BusinessFlow<Foo, Object> flow1 = HappyPath.happyPath(originalFoo);
        assertThat(flow1.isHappy()).isTrue();
        if (flow1.isSad()) {
            flow1.getSad();
        }
        assertThat(flow1.getHappy()).isEqualTo(originalFoo);

        Bar originalBar = new Bar();
        BusinessFlow<Bar, Object> flow2 = SadPath.sadPath(originalBar);
        assertThat(flow2.isSad()).isTrue();
        if (flow2.isSad()) {
            assertThat(flow2.getSad()).isEqualTo(originalBar);
        }
    }

    @Test
    public void passingAlongAContext() {
        Foo originalFoo = new Foo();
        Bar originalBar = new Bar();
        FooAndBar fooAndBar = HappyPath.happyPath(originalFoo)
                .map(foo -> new FooAndBar(foo, originalBar))
                .get();

        assertThat(fooAndBar.foo).isEqualTo(originalFoo);
        assertThat(fooAndBar.bar).isEqualTo(originalBar);
    }

    @Test
    public void savingInMutableContext() {
        MutableContext context = new MutableContext();
        Foo originalFoo = new Foo();
        Bar originalBar = new Bar();
        HappyPath.happyPath(originalFoo)
                .peek(context::save)
                .map(foo -> originalBar)
                .peek(context::save);


        assertThat(context.foo).isEqualTo(originalFoo);
        assertThat(context.bar).isEqualTo(originalBar);
    }

    @Test
    public void genericContext() {
        GenericContext genericContext = new GenericContext();
        Foo originalFoo = new Foo();
        Bar originalBar = new Bar();
        HappyPath.happyPath(originalFoo)
                .peek(genericContext::put)
                .map(foo -> originalBar)
                .peek(genericContext::put);

        assertThat(genericContext.get(Foo.class)).isEqualTo(originalFoo);
        assertThat(genericContext.get(Bar.class)).isEqualTo(originalBar);
    }
}
