package ru.tinkoff.qa.neptune.selenium.test.steps.tests.searching.widgets.flags;

import ru.tinkoff.qa.neptune.selenium.api.widget.Labeled;
import ru.tinkoff.qa.neptune.selenium.api.widget.Name;
import ru.tinkoff.qa.neptune.selenium.api.widget.Priority;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

import static ru.tinkoff.qa.neptune.selenium.test.FakeDOMModel.LABEL_TAG;
import static ru.tinkoff.qa.neptune.selenium.test.steps.tests.searching.widgets.WidgetNames.LABELED_CHECKBOX;

@Name(LABELED_CHECKBOX)
@Priority(3)
public class LabeledCheckBox extends SimpleCheckbox implements Labeled {

    @FindBy(tagName = LABEL_TAG)
    private List<WebElement> labels;

    public LabeledCheckBox(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public List<String> labels() {
        return labels.stream().map(WebElement::getText).collect(Collectors.toList());
    }
}
