package ru.tinkoff.qa.neptune.core.api;

import org.testng.annotations.Test;
import ru.tinkoff.qa.neptune.core.api.proxy.ProxyFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.fail;

public class NegativeTest {

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Class to substitute should be assignable from " +
                    "ru.tinkoff.qa.neptune.core.api.GetStep " +
                    "and/or ru.tinkoff.qa.neptune.core.api.PerformActionStep.")
    public void testOfIllegalClass() {
        ProxyFactory.getProxied(Object.class, ConstructorParameters.params());
        fail("The exception throwing was expected");
    }

    @Test(expectedExceptions = NoSuchMethodException.class)
    public void testOfCompletelyMismatchingParameters() {
        ProxyFactory.getProxied(GetStepStub.class, ConstructorParameters.params()).get(StoryWriter.toGet("Something", getStep -> new Object()));
        fail("The exception throwing was expected");
    }

    @Test(expectedExceptions = NoSuchMethodException.class,
            dependsOnMethods = "testOfCompletelyMismatchingParameters")
    public void testOfPartiallyMismatchingParameters() {
        ProxyFactory.getProxied(GetStepStub.class, ConstructorParameters.params("12345", 1, 1.5F, false))
                .get(StoryWriter.toGet("Something", getStep -> new Object()));
        fail("The exception throwing was expected");
    }

    @Test(dependsOnMethods = {"testOfIllegalClass",
            "testOfCompletelyMismatchingParameters",
            "testOfPartiallyMismatchingParameters"})
    public void positiveTest() {
        assertThat(ProxyFactory.getProxied(GetStepStub.class, ConstructorParameters.params("12345", 1, 1, false))
                .get(StoryWriter.toGet("Something", getStep -> new Object())), not(nullValue()));
    }

    static class GetStepStub implements GetStep<GetStepStub>, PerformActionStep<GetStepStub> {
        private final CharSequence sequence;
        private final Integer integer2;
        private final int integer1;
        private final boolean b;

       GetStepStub(CharSequence sequence, int integer1, Integer integer2, boolean b) {
            this.sequence = sequence;
            this.integer1 = integer1;
            this.integer2 = integer2;
            this.b = b;
        }
    }
}
