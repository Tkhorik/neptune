package ru.tinkoff.qa.neptune.selenium.test.steps.tests.searching.widgets.link;

import ru.tinkoff.qa.neptune.selenium.api.widget.Name;
import ru.tinkoff.qa.neptune.selenium.api.widget.drafts.Link;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static ru.tinkoff.qa.neptune.selenium.test.FakeDOMModel.HREF;
import static ru.tinkoff.qa.neptune.selenium.test.FakeDOMModel.LINK_TAG;
import static ru.tinkoff.qa.neptune.selenium.test.steps.tests.searching.widgets.WidgetNames.SIMPLE_LINK;

@Name(SIMPLE_LINK)
@FindBy(tagName = LINK_TAG)
public class SimpleLink extends Link {

    public SimpleLink(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public String getReference() {
        return getWrappedElement().getAttribute(HREF);
    }

    @Override
    public void click() {
        getWrappedElement().click();
    }
}
