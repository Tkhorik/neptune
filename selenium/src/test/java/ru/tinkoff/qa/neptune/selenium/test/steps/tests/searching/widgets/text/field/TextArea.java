package ru.tinkoff.qa.neptune.selenium.test.steps.tests.searching.widgets.text.field;

import ru.tinkoff.qa.neptune.selenium.api.widget.Labeled;
import ru.tinkoff.qa.neptune.selenium.api.widget.Name;
import ru.tinkoff.qa.neptune.selenium.api.widget.drafts.TextField;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static ru.tinkoff.qa.neptune.selenium.test.FakeDOMModel.LABEL_TAG;
import static ru.tinkoff.qa.neptune.selenium.test.FakeDOMModel.TEXT_AREA_TAG;
import static ru.tinkoff.qa.neptune.selenium.test.FakeDOMModel.VALUE;
import static ru.tinkoff.qa.neptune.selenium.test.steps.tests.searching.widgets.WidgetNames.TEXT_AREA;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Name(TEXT_AREA)
@FindBy(tagName = TEXT_AREA_TAG)
public class TextArea extends TextField implements Labeled {

    @FindBy(tagName = LABEL_TAG)
    private List<WebElement> labels;

    public TextArea(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public void edit(List<CharSequence> valueToSet) {
        valueToSet.forEach(charSequence -> getWrappedElement().sendKeys(charSequence));
    }

    @Override
    public String getValue() {
        return ofNullable(getWrappedElement().getAttribute(VALUE))
                .orElse(EMPTY);
    }

    @Override
    public List<String> labels() {
        return labels.stream().map(WebElement::getText).collect(toList());
    }
}
