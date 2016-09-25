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
