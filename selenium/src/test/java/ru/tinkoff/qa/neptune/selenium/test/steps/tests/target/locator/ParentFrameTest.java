package ru.tinkoff.qa.neptune.selenium.test.steps.tests.target.locator;

import ru.tinkoff.qa.neptune.selenium.test.MockWebDriver;
import ru.tinkoff.qa.neptune.selenium.test.BaseWebDriverTest;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;

import static ru.tinkoff.qa.neptune.selenium.functions.target.locator.frame.parent.ParentFrameSupplier.parentFrame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ParentFrameTest extends BaseWebDriverTest {

    @Test
    public void parentFrameTest() {
        setStartBenchMark();
        assertThat(((MockWebDriver) seleniumSteps.get(parentFrame())).isSwitchedToParentFrame(),
                is(true));
        setEndBenchMark();
        assertThat(getTimeDifference(), lessThan(HALF_SECOND.toMillis()));
    }

    @Test
    public void parentFrameWithDefinedTimeTest() {
        setStartBenchMark();
        assertThat(((MockWebDriver) seleniumSteps.get(parentFrame(FIVE_SECONDS))).isSwitchedToParentFrame(),
                is(true));
        setEndBenchMark();
        assertThat(getTimeDifference(), lessThan(HALF_SECOND.toMillis()));
    }

    @Test(expectedExceptions = WebDriverException.class)
    public void parentFrameInSituationOfThrownExceptionWhenTimeIsNotDefined() {
        seleniumSteps.get(parentFrame());
        setStartBenchMark();
        try {
            seleniumSteps.get(parentFrame()); //mock is designed the special way
            //when it tries to switch to parent frame more than one time per session
            //then it throws the exception
        }
        finally {
            setEndBenchMark();
            assertThat(getTimeDifference(), lessThan(HALF_SECOND.toMillis()));
        }
    }

    @Test(expectedExceptions = WebDriverException.class)
    public void parentFrameInSituationOfThrownExceptionWhenTimeIsDefined() {
        seleniumSteps.get(parentFrame());
        setStartBenchMark();
        try {
            seleniumSteps.get(parentFrame(FIVE_SECONDS));
        }
        finally {
            setEndBenchMark();
            assertThat(getTimeDifference(), greaterThanOrEqualTo(FIVE_SECONDS.toMillis()));
            assertThat(getTimeDifference() - FIVE_SECONDS.toMillis(), lessThanOrEqualTo(HALF_SECOND.toMillis()));
        }
    }
}
