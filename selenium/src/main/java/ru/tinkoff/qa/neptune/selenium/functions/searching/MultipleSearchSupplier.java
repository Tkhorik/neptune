package ru.tinkoff.qa.neptune.selenium.functions.searching;

import ru.tinkoff.qa.neptune.core.api.SequentialGetStepSupplier;
import ru.tinkoff.qa.neptune.selenium.api.widget.Labeled;
import ru.tinkoff.qa.neptune.selenium.api.widget.Widget;
import ru.tinkoff.qa.neptune.selenium.api.widget.drafts.*;
import ru.tinkoff.qa.neptune.selenium.properties.WaitingProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import ru.tinkoff.qa.neptune.selenium.properties.FlagProperties;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static ru.tinkoff.qa.neptune.core.api.AsIsCondition.AS_IS;
import static ru.tinkoff.qa.neptune.core.api.conditions.ToGetSubIterable.getIterable;
import static ru.tinkoff.qa.neptune.selenium.api.widget.Widget.getWidgetName;
import static ru.tinkoff.qa.neptune.selenium.functions.searching.CommonConditions.defaultPredicate;
import static ru.tinkoff.qa.neptune.selenium.functions.searching.CommonConditions.shouldBeLabeledBy;
import static ru.tinkoff.qa.neptune.selenium.functions.searching.CommonConditions.shouldHaveText;
import static ru.tinkoff.qa.neptune.selenium.functions.searching.FindLabeledWidgets.labeledWidgets;
import static ru.tinkoff.qa.neptune.selenium.properties.WaitingProperties.ELEMENT_WAITING_DURATION;
import static java.lang.String.format;
import static java.util.List.of;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@SuppressWarnings({"unchecked", "unused"})
public final class MultipleSearchSupplier<R extends SearchContext> extends
        SequentialGetStepSupplier<SearchContext, List<R>, SearchContext, MultipleSearchSupplier<R>> {


    private MultipleSearchSupplier(Function<SearchContext, List<R>> function) {
        set(function);
        addIgnored(of(StaleElementReferenceException.class));
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of instances of {@link SearchContext} found from the input value.
     *
     * @param description is a description of a list of elements to find.
     * @param transformation is a function which performs the searching from some {@link SearchContext}
     *                       and transform the list of found items to another list of instances
     *                       of {@link SearchContext}
     * @param duration is the parameter of a time to find desired items
     * @param condition to specify the searching criteria
     * @param <T> is a type of a value to be returned by resulted function
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends SearchContext> MultipleSearchSupplier<T> items(String description,
                                                                            Function<SearchContext,List<T>> transformation,
                                                                            Duration duration, Predicate<? super T> condition) {
        return new MultipleSearchSupplier<>(getIterable(description, transformation, condition, duration, false, true));
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of instances of {@link SearchContext} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param description is a description of a list of elements to find.
     * @param transformation is a function which performs the searching from some {@link SearchContext}
     *                       and transform the list of found items to another list of instances
     *                       of {@link SearchContext}
     * @param condition to specify the searching criteria
     * @param <T> is a type of a value to be returned by resulted function
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends SearchContext> MultipleSearchSupplier<T> items(String description,
                                                                            Function<SearchContext,List<T>> transformation,
                                                                            Predicate<? super T> condition) {
        return items(description, transformation, ELEMENT_WAITING_DURATION.get(), condition);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     *
     * @param by locator strategy to find elements
     * @param duration is the parameter of a time to find elements
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Duration duration, Predicate<? super WebElement> predicate) {
        String criteriaDescription = predicate == AS_IS? EMPTY: predicate.toString();
        return items(format("List of web elements located %s", by), FindWebElements.webElements(by, criteriaDescription),
                duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     *
     * @param by locator strategy to find elements
     * @param text which the desired elements should have
     * @param duration is the parameter of a time to find elements
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, String text, Duration duration, Predicate<? super WebElement> predicate) {
        return webElements(by, duration, shouldHaveText(text).and(predicate));
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     *
     * @param by locator strategy to find elements
     * @param textPattern is a regExp to match text of desired elements
     * @param duration is the parameter of a time to find elements
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Pattern textPattern, Duration duration, Predicate<? super WebElement> predicate) {
        return webElements(by, duration, shouldHaveText(textPattern).and(predicate));
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value. The
     * result function will return a list of any found elements if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found elements which are displayed on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param by locator strategy to find elements
     * @param duration is the parameter of a time to find elements
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Duration duration) {
        return webElements(by, duration, defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value. The
     * result function will return a list of any found elements if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found elements which are displayed on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param by locator strategy to find elements
     * @param text which the desired elements should have
     * @param duration is the parameter of a time to find elements
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, String text, Duration duration) {
        return webElements(by, text, duration, defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value. The
     * result function will return a list of any found elements if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found elements which are displayed on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param by locator strategy to find elements
     * @param textPattern is a regExp to match text of desired elements
     * @param duration is the parameter of a time to find elements
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Pattern textPattern, Duration duration) {
        return webElements(by, textPattern, duration, defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param by locator strategy to find elements
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Predicate<? super WebElement> predicate) {
        String criteriaDescription = predicate == AS_IS? EMPTY: predicate.toString();
        return items(format("List of web elements located %s", by),
                FindWebElements.webElements(by, criteriaDescription), predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param by locator strategy to find elements
     * @param text which the desired elements should have
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, String text, Predicate<? super WebElement> predicate) {
        return webElements(by, CommonConditions.shouldHaveText(text).and(predicate));
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param by locator strategy to find elements
     * @param textPattern is a regExp to match text of desired elements
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Pattern textPattern, Predicate<? super WebElement> predicate) {
        return webElements(by, CommonConditions.shouldHaveText(textPattern).and(predicate));
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found elements if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found elements which are displayed on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param by locator strategy to find an element
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by) {
        return webElements(by, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found elements if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found elements which are displayed on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param by locator strategy to find an element
     * @param text which the desired elements should have
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, String text) {
        return webElements(by, text, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link WebElement} found from the input value.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found elements if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found elements which are displayed on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param by locator strategy to find an element
     * @param textPattern is a regExp to match text of desired elements
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<WebElement> webElements(By by, Pattern textPattern) {
        return webElements(by, textPattern, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     *
     * @param tClass is a class of {@link Widget} which instances should be returned
     * @param duration is the parameter of a time to find elements
     * @param predicate to specify the searching criteria
     * @param <T> the type of widget which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, Duration duration, Predicate<? super T> predicate) {
        String criteriaDescription = predicate == AS_IS? EMPTY: predicate.toString();
        return items(String.format("List of %s", getWidgetName(tClass)),
                FindWidgets.widgets(tClass, criteriaDescription), duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param labels (texts of some elements or attributes inside or beside the widget) which are used to
     *               find widgets.
     * @param duration is the parameter of a time to find elements
     * @param predicate to specify the searching criteria
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, List<String> labels, Duration duration,
                                                                       Predicate<? super T> predicate) {
        Predicate<? extends T> labeledBy = shouldBeLabeledBy(labels.toArray(new String[]{}));
        Predicate<? super T> resultPredicate = (Predicate<? super T>) labeledBy.and(predicate);
        return items(String.format("List of %s", Widget.getWidgetName(tClass)),
                labeledWidgets(tClass, resultPredicate.toString()), duration, resultPredicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param label (text of some element or attribute inside or beside the widget) which is used to
     *               find widgets.
     * @param duration is the parameter of a time to find elements
     * @param predicate to specify the searching criteria
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, String label, Duration duration,
                                                                       Predicate<? super T> predicate) {
        return widgets(tClass, List.of(label), duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value. The
     * result function will return a list of any found widgets if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found widgets which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned
     * @param duration is the parameter of a time to find elements
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, Duration duration) {
        return widgets(tClass, duration, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value. The
     * result function will return a list of any found widgets if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found widgets which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param labels (texts of some elements or attributes inside or beside the widget) which are used to
     *               find widgets.
     * @param duration is the parameter of a time to find elements
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, List<String> labels, Duration duration) {
        return widgets(tClass, labels, duration, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value. The
     * result function will return a list of any found widgets if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found widgets which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param label (text of some element or attribute inside or beside the widget) which is used to
     *               find widgets.
     * @param duration is the parameter of a time to find elements
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, String label, Duration duration) {
        return widgets(tClass, label, duration, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned
     * @param predicate to specify the searching criteria
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, Predicate<? super T> predicate) {
        String criteriaDescription = predicate == AS_IS? EMPTY: predicate.toString();
        return items(String.format("List of %s", Widget.getWidgetName(tClass)),
                FindWidgets.widgets(tClass, criteriaDescription), predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param labels (texts of some elements or attributes inside or beside the widget) which are used to
     *               find widgets.
     * @param predicate to specify the searching criteria
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, List<String> labels,
                                                                       Predicate<? super T> predicate) {
        Predicate<? extends T> labeledBy = shouldBeLabeledBy(labels.toArray(new String[]{}));
        Predicate<? super T> resultPredicate = (Predicate<? super T>) labeledBy.and(predicate);
        return items(String.format("List of %s", Widget.getWidgetName(tClass)),
                labeledWidgets(tClass, resultPredicate.toString()), resultPredicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param label (text of some element or attribute inside or beside the widget) which is used to
     *               find widgets.
     * @param predicate to specify the searching criteria
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, String label, Predicate<? super T> predicate) {
        return widgets(tClass, List.of(label), predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found widgets if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found widgets which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass) {
        return widgets(tClass, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found widgets if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found widgets which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param labels (texts of some elements or attributes inside or beside the widget) which are used to
     *               find widgets.
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, List<String> labels) {
        return widgets(tClass, labels, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of {@link Widget} found from the input value.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found widgets if the property
     * {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found widgets which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param tClass is a class of {@link Widget} which instances should be returned. tClass should have at least one
     *               not abstract subclass which also implements {@link Labeled} or be that class.
     * @param label (text of some element or attribute inside or beside the widget) which is used to
     *               find widgets.
     * @param <T> the type of widgets which should be found
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static <T extends Widget> MultipleSearchSupplier<T> widgets(Class<T> tClass, String label) {
        return widgets(tClass, label, CommonConditions.defaultPredicate());
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons.
     *
     * @param duration is the parameter of a time to find buttons
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(Duration duration, Predicate<? super Button> predicate) {
        return widgets(Button.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons.
     *
     * @param labels (texts of some elements or attributes inside or beside the button) which are used to
     *               find buttons.
     * @param duration is the parameter of a time to find buttons
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(List<String> labels, Duration duration, Predicate<? super Button> predicate) {
        return widgets(Button.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons.
     *
     * @param label (text of some element or attribute inside or beside the button) which is used to
     *               find buttons.
     * @param duration is the parameter of a time to find buttons
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(String label, Duration duration, Predicate<? super Button> predicate) {
        return widgets(Button.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons. The result function will return a list of any found buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find buttons
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(Duration duration) {
        return widgets(Button.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons. The result function will return a list of any found buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the button) which are used to
     *               find buttons.
     * @param duration is the parameter of a time to find buttons
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(List<String> labels, Duration duration) {
        return widgets(Button.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons. The result function will return a list of any found buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the button) which is used to
     *               find buttons.
     * @param duration is the parameter of a time to find buttons
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(String label, Duration duration) {
        return widgets(Button.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(Predicate<? super Button> predicate) {
        return widgets(Button.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the button) which are used to
     *               find buttons.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(List<String> labels, Predicate<? super Button> predicate) {
        return widgets(Button.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the button) which is used to
     *               find buttons.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(String label, Predicate<? super Button> predicate) {
        return widgets(Button.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons() {
        return widgets(Button.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the button) which are used to
     *               find buttons.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(List<String> labels) {
        return widgets(Button.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found buttons.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the button) which is used to
     *               find buttons.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Button> buttons(String label) {
        return widgets(Button.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags.
     *
     * @param duration is the parameter of a time to find flags
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(Duration duration, Predicate<? super Flag> predicate) {
        return widgets(Flag.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags.
     *
     * @param labels (texts of some elements or attributes inside or beside the flag) which are used to
     *               find flags.
     * @param duration is the parameter of a time to find flags
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(List<String> labels, Duration duration, Predicate<? super Flag> predicate) {
        return widgets(Flag.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags.
     *
     * @param label (text of some element or attribute inside or beside the flag) which is used to
     *               find flags.
     * @param duration is the parameter of a time to find flags
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(String label, Duration duration, Predicate<? super Flag> predicate) {
        return widgets(Flag.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags. The result function will return a list of any found flags
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found flags which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find flags
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(Duration duration) {
        return widgets(Flag.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags. The result function will return a list of any found flags
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found flags which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the flag) which are used to
     *               find flags.
     * @param duration is the parameter of a time to find flags
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(List<String> labels, Duration duration) {
        return widgets(Flag.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags. The result function will return a list of any found flags
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found flags which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the flag) which is used to
     *               find flags.
     * @param duration is the parameter of a time to find flags
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(String label, Duration duration) {
        return widgets(Flag.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(Predicate<? super Flag> predicate) {
        return widgets(Flag.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the flags) which are used to
     *               find flags.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(List<String> labels, Predicate<? super Flag> predicate) {
        return widgets(Flag.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the flag) which is used to
     *               find flags.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(String label, Predicate<? super Flag> predicate) {
        return widgets(Flag.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found flags
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found flags which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags() {
        return widgets(Flag.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found flags
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found flags which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the flag) which are used to
     *               find flags.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(List<String> labels) {
        return widgets(Flag.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found flags.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found flags
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found flags which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the flag) which is used to
     *               find flags.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag> flags(String label) {
        return widgets(Flag.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes.
     *
     * @param duration is the parameter of a time to find check boxes
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(Duration duration, Predicate<? super Flag.CheckBox> predicate) {
        return widgets(Flag.CheckBox.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes.
     *
     * @param labels (texts of some elements or attributes inside or beside the check box) which are used to
     *               find check boxes.
     * @param duration is the parameter of a time to find check boxes
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(List<String> labels, Duration duration,
                                                                   Predicate<? super Flag.CheckBox> predicate) {
        return widgets(Flag.CheckBox.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes.
     *
     * @param label (text of some element or attribute inside or beside the check box) which is used to
     *               find check boxes.
     * @param duration is the parameter of a time to find check boxes
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(String label, Duration duration,
                                                                   Predicate<? super Flag.CheckBox> predicate) {
        return widgets(Flag.CheckBox.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes. The result function will return a list of any found check boxes
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found check boxes which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find check boxes
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(Duration duration) {
        return widgets(Flag.CheckBox.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes. The result function will return a list of any found check boxes
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found check boxes which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the check box) which are used to
     *               find check boxes.
     * @param duration is the parameter of a time to find check box
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(List<String> labels, Duration duration) {
        return widgets(Flag.CheckBox.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes. The result function will return a list of any found check box
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found check boxes which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the check box) which is used to
     *               find check boxes.
     * @param duration is the parameter of a time to find check boxes
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> checkBoxes(String label, Duration duration) {
        return widgets(Flag.RadioButton.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(Predicate<? super Flag.CheckBox> predicate) {
        return widgets(Flag.CheckBox.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the check box) which are used to
     *               find check boxes.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(List<String> labels, Predicate<? super Flag.CheckBox> predicate) {
        return widgets(Flag.CheckBox.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the check box) which is used to
     *               find check boxes.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(String label, Predicate<? super Flag.CheckBox> predicate) {
        return widgets(Flag.CheckBox.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found check boxes
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found check boxes which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes() {
        return widgets(Flag.CheckBox.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found check boxes
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found check boxes which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the check box) which are used to
     *               find check boxes.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(List<String> labels) {
        return widgets(Flag.CheckBox.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found check boxes.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found check boxes
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found check boxes which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the check box) which is used to
     *               find check boxes.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.CheckBox> checkBoxes(String label) {
        return widgets(Flag.CheckBox.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons.
     *
     * @param duration is the parameter of a time to find radio buttons
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(Duration duration, Predicate<? super Flag.RadioButton> predicate) {
        return widgets(Flag.RadioButton.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons.
     *
     * @param labels (texts of some elements or attributes inside or beside the radio button) which are used to
     *               find radio buttons.
     * @param duration is the parameter of a time to find radio buttons
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(List<String> labels,
                                                                                  Duration duration,
                                                                                  Predicate<? super Flag.RadioButton> predicate) {
        return widgets(Flag.RadioButton.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons.
     *
     * @param label (text of some element or attribute inside or beside the radio button) which is used to
     *               find radio buttons.
     * @param duration is the parameter of a time to find radio buttons
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(String label,
                                                                                  Duration duration,
                                                                                  Predicate<? super Flag.RadioButton> predicate) {
        return widgets(Flag.RadioButton.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons. The result function will return a list of any found radio buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found radio buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find radio buttons
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(Duration duration) {
        return widgets(Flag.RadioButton.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons. The result function will return a list of any found radio buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found radio buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the radio button) which are used to
     *               find radio buttons.
     * @param duration is the parameter of a time to find radio buttons
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(List<String> labels,
                                                                                  Duration duration) {
        return widgets(Flag.RadioButton.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons. The result function will return a list of any found radio buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found radio buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the radio button) which is used to
     *               find radio buttons.
     * @param duration is the parameter of a time to find radio buttons
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(String label,
                                                                                  Duration duration) {
        return widgets(Flag.RadioButton.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(Predicate<? super Flag.RadioButton> predicate) {
        return widgets(Flag.RadioButton.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the radio button) which are used to
     *               find radio buttons.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(List<String> labels,
                                                                                  Predicate<? super Flag.RadioButton> predicate) {
        return widgets(Flag.RadioButton.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the radio button) which is used to
     *               find radio buttons.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(String label,
                                                                                  Predicate<? super Flag.RadioButton> predicate) {
        return widgets(Flag.RadioButton.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio button.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found radio buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found radio buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons() {
        return widgets(Flag.RadioButton.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found radio buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found radio buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the radio button) which are used to
     *               find radio buttons.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(List<String> labels) {
        return widgets(Flag.RadioButton.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found radio buttons.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found radio buttons
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found radio buttons which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the radio button) which is used to
     *               find radio buttons.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Flag.RadioButton> radioButtons(String label) {
        return widgets(Flag.RadioButton.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links.
     *
     * @param duration is the parameter of a time to find links
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(Duration duration,
                                                     Predicate<? super Link> predicate) {
        return widgets(Link.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links.
     *
     * @param labels (texts of some elements or attributes inside or beside the link) which are used to
     *               find links.
     * @param duration is the parameter of a time to find links
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(List<String> labels,
                                                               Duration duration,
                                                               Predicate<? super Link> predicate) {
        return widgets(Link.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links.
     *
     * @param label (text of some element or attribute inside or beside the link) which is used to
     *               find links.
     * @param duration is the parameter of a time to find links
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(String label,
                                                               Duration duration,
                                                               Predicate<? super Link> predicate) {
        return widgets(Link.class, label,
                duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links. The result function will return a list of any found links
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found links which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find links
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(Duration duration) {
        return widgets(Link.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links. The result function will return a list of any found links
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found links which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the links) which are used to
     *               find links.
     * @param duration is the parameter of a time to find links
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(List<String> labels,
                                                               Duration duration) {
        return widgets(Link.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links. The result function will return a list of any found links
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found links which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the link) which is used to
     *               find links.
     * @param duration is the parameter of a time to find links
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(String label,
                                                               Duration duration) {
        return widgets(Link.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(Predicate<? super Link> predicate) {
        return widgets(Link.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the link) which are used to
     *               find links.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(List<String> labels,
                                                               Predicate<? super Link> predicate) {
        return widgets(Link.class,
                labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the link) which is used to
     *               find links.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(String label,
                                                               Predicate<? super Link> predicate) {
        return widgets(Link.class,
                label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found links
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found links which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links() {
        return widgets(Link.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found links
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found links which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the link) which are used to
     *               find links.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(List<String> labels) {
        return widgets(Link.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found links.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found links
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found links which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the link) which is used to
     *               find links.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Link> links(String label) {
        return widgets(Link.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects.
     *
     * @param duration is the parameter of a time to find selects
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(Duration duration,
                                                         Predicate<? super Select> predicate) {
        return widgets(Select.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects.
     *
     * @param labels (texts of some elements or attributes inside or beside the select) which are used to
     *               find selects.
     * @param duration is the parameter of a time to find selects
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(List<String> labels,
                                                                   Duration duration,
                                                                   Predicate<? super Select> predicate) {
        return widgets(Select.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects.
     *
     * @param label (text of some element or attribute inside or beside the select) which is used to
     *               find selects.
     * @param duration is the parameter of a time to find selects
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(String label,
                                                                   Duration duration,
                                                                   Predicate<? super Select> predicate) {
        return widgets(Select.class, label,
                duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects. The result function will return a list of any found selects
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found selects which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find selects
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(Duration duration) {
        return widgets(Select.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects. The result function will return a list of any found selects
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found selects which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the select) which are used to
     *               find selects.
     * @param duration is the parameter of a time to find selects
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(List<String> labels,
                                                                   Duration duration) {
        return widgets(Select.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects. The result function will return a list of any found selects
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found selects which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the select) which is used to
     *               find selects.
     * @param duration is the parameter of a time to find selects
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(String label,
                                                                   Duration duration) {
        return widgets(Select.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(Predicate<? super Select> predicate) {
        return widgets(Select.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the select) which are used to
     *               find selects.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(List<String> labels,
                                                                   Predicate<? super Select> predicate) {
        return widgets(Select.class,
                labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the select) which is used to
     *               find selects.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(String label,
                                                                   Predicate<? super Select> predicate) {
        return widgets(Select.class,
                label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found selects
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found selects which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects() {
        return widgets(Select.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found selects
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found selects which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the select) which are used to
     *               find selects.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(List<String> labels) {
        return widgets(Select.class, labels);
    }

    /**selects
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found selects.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found selects
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found selects which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the select) which is used to
     *               find selects.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Select> selects(String label) {
        return widgets(Select.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs.
     *
     * @param duration is the parameter of a time to find tabs
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(Duration duration,
                                                   Predicate<? super Tab> predicate) {
        return widgets(Tab.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs.
     *
     * @param labels (texts of some elements or attributes inside or beside the tab) which are used to
     *               find tabs.
     * @param duration is the parameter of a time to find tabs
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(List<String> labels,
                                                             Duration duration,
                                                             Predicate<? super Tab> predicate) {
        return widgets(Tab.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs.
     *
     * @param label (text of some element or attribute inside or beside the tab) which is used to
     *               find tabs.
     * @param duration is the parameter of a time to find tabs
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(String label,
                                                             Duration duration,
                                                             Predicate<? super Tab> predicate) {
        return widgets(Tab.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs. The result function will return a list of any found tabs
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tabs which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find tabs
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(Duration duration) {
        return widgets(Tab.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs. The result function will return a list of any found tabs
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tabs which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the tab) which are used to
     *               find tabs.
     * @param duration is the parameter of a time to find tabs
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(List<String> labels,
                                                             Duration duration) {
        return widgets(Tab.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs. The result function will return a list of any found tabs
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tabs which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the tab) which is used to
     *               find tabs.
     * @param duration is the parameter of a time to find tabs
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(String label,
                                                             Duration duration) {
        return widgets(Tab.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(Predicate<? super Tab> predicate) {
        return widgets(Tab.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the tab) which are used to
     *               find tabs.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(List<String> labels,
                                                             Predicate<? super Tab> predicate) {
        return widgets(Tab.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the tab) which is used to
     *               find tabs.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(String label,
                                                             Predicate<? super Tab> predicate) {
        return widgets(Tab.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found tabs
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tabs which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs() {
        return widgets(Tab.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found tabs
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tabs which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the tab) which are used to
     *               find tabs.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(List<String> labels) {
        return widgets(Tab.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tabs.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found tabs
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tabs which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the tab) which is used to
     *               find tabs.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Tab> tabs(String label) {
        return widgets(Tab.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields.
     *
     * @param duration is the parameter of a time to find text fields
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(Duration duration,
                                                                         Predicate<? super TextField> predicate) {
        return widgets(TextField.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields.
     *
     * @param labels (texts of some elements or attributes inside or beside the text field) which are used to
     *               find text fields.
     * @param duration is the parameter of a time to find text fields
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(List<String> labels,
                                                                         Duration duration,
                                                                         Predicate<? super TextField> predicate) {
        return widgets(TextField.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields.
     *
     * @param label (text of some element or attribute inside or beside the text field) which is used to
     *               find text fields.
     * @param duration is the parameter of a time to find text fields
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(String label,
                                                                         Duration duration,
                                                                         Predicate<? super TextField> predicate) {
        return widgets(TextField.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields. The result function will return a list of any found text fields
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found text fields which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find text fields
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(Duration duration) {
        return widgets(TextField.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields. The result function will return a list of any found text fields
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found text fields which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the text field) which are used to
     *               find text fields.
     * @param duration is the parameter of a time to find text fields
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(List<String> labels,
                                                                         Duration duration) {
        return widgets(TextField.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields. The result function will return a list of any found text fields
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found text fields which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the text field) which is used to
     *               find text fields.
     * @param duration is the parameter of a time to find text fields
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(String label, Duration duration) {
        return widgets(TextField.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(Predicate<? super TextField> predicate) {
        return widgets(TextField.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the text fields) which are used to
     *               find text fields.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(List<String> labels,
                                                                         Predicate<? super TextField> predicate) {
        return widgets(TextField.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the text fields) which is used to
     *               find text fields.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(String label,
                                                                         Predicate<? super TextField> predicate) {
        return widgets(TextField.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found text fields
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found text fields which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields() {
        return widgets(TextField.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found text fields
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found text fields which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the text field) which are used to
     *               find text fields.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(List<String> labels) {
        return widgets(TextField.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found text fields.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found text fields
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found text fields which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the text field) which is used to
     *               find text fields.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TextField> textFields(String label) {
        return widgets(TextField.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables.
     *
     * @param duration is the parameter of a time to find tables
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(Duration duration,
                                                               Predicate<? super Table> predicate) {
        return widgets(Table.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables.
     *
     * @param labels (texts of some elements or attributes inside or beside the text field) which are used to
     *               find tables.
     * @param duration is the parameter of a time to find tables
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(List<String> labels,
                                                      Duration duration,
                                                      Predicate<? super Table> predicate) {
        return widgets(Table.class, labels, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables.
     *
     * @param label (text of some element or attribute inside or beside the text field) which is used to
     *               find tables.
     * @param duration is the parameter of a time to find tables
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(String label,
                                                       Duration duration,
                                                       Predicate<? super Table> predicate) {
        return widgets(Table.class, label, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables. The result function will return a list of any found tables
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tables which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find tables
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(Duration duration) {
        return widgets(Table.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables. The result function will return a list of any found tables
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tables which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the text field) which are used to
     *               find tables.
     * @param duration is the parameter of a time to find tables
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(List<String> labels,
                                                           Duration duration) {
        return widgets(Table.class, labels, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables. The result function will return a list of any found tables
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tables which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the text field) which is used to
     *               find tables.
     * @param duration is the parameter of a time to find tables
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(String label, Duration duration) {
        return widgets(Table.class, label, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(Predicate<? super Table> predicate) {
        return widgets(Table.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param labels (texts of some elements or attributes inside or beside the tables) which are used to
     *               find tables.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(List<String> labels,
                                                           Predicate<? super Table> predicate) {
        return widgets(Table.class, labels, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param label (text of some element or attribute inside or beside the tables) which is used to
     *               find tables.
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(String label,
                                                           Predicate<? super Table> predicate) {
        return widgets(Table.class, label, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found tables
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tables which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables() {
        return widgets(Table.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found tables
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tables which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param labels (texts of some elements or attributes inside or beside the text field) which are used to
     *               find tables.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(List<String> labels) {
        return widgets(Table.class, labels);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found tables.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found tables
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found tables which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param label (text of some element or attribute inside or beside the text field) which is used to
     *               find tables.
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<Table> tables(String label) {
        return widgets(Table.class, label);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table rows.
     *
     * @param duration is the parameter of a time to find table rows
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableRow> tableRows(Duration duration,
                                                             Predicate<? super TableRow> predicate) {
        return widgets(TableRow.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table rows. The result function will return a list of any found table rows
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table rows which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find table rows
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableRow> tableRows(Duration duration) {
        return widgets(TableRow.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table rows. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableRow> tableRows(Predicate<? super TableRow> predicate) {
        return widgets(TableRow.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table rows.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found table rows
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table rows which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableRow> tableRows() {
        return widgets(TableRow.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table headers.
     *
     * @param duration is the parameter of a time to find table headers
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableHeader> tableHeaders(Duration duration,
                                                                   Predicate<? super TableHeader> predicate) {
        return widgets(TableHeader.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table headers. The result function will return a list of any found table headers
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table headers which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find table headers
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableHeader> tableHeaders(Duration duration) {
        return widgets(TableHeader.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table headers. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableHeader> tableHeaders(Predicate<? super TableHeader> predicate) {
        return widgets(TableHeader.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table headers.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found table headers
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table headers which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableHeader> tableHeaders() {
        return widgets(TableHeader.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table footers.
     *
     * @param duration is the parameter of a time to find table footers
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableFooter> tableFooters(Duration duration,
                                                                   Predicate<? super TableFooter> predicate) {
        return widgets(TableFooter.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table footers. The result function will return a list of any found table footers
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table footers which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find table footers
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableFooter> tableFooters(Duration duration) {
        return widgets(TableFooter.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table footers. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableFooter> tableFooters(Predicate<? super TableFooter> predicate) {
        return widgets(TableFooter.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table footers.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found table footers
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table footers which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableFooter> tableFooters() {
        return widgets(TableFooter.class);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table cells.
     *
     * @param duration is the parameter of a time to find table cells
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableCell> tableCells(Duration duration,
                                                               Predicate<? super TableCell> predicate) {
        return widgets(TableCell.class, duration, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table cells. The result function will return a list of any found table cells
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table cells which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @param duration is the parameter of a time to find table cells
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableCell> tableCells(Duration duration) {
        return widgets(TableCell.class, duration);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table cells. About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * @param predicate to specify the searching criteria
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableCell> tableCells(Predicate<? super TableCell> predicate) {
        return widgets(TableCell.class, predicate);
    }

    /**
     * Returns an instance of {@link MultipleSearchSupplier} which wraps a function.
     * The wrapped function takes an instance of {@link SearchContext} for the searching
     * and returns some list of found table cells.
     *
     * About time which the searching takes
     * @see WaitingProperties#ELEMENT_WAITING_DURATION
     *
     * The result function will return a list of any found table cells
     * if the property {@code find.only.visible.elements.when.no.conditions} is not defined or has value {@code "false"}.
     * Otherwise it will return a list of found table cells which are visible on a page.
     * @see FlagProperties#FIND_ONLY_VISIBLE_ELEMENTS_WHEN_NO_CONDITION
     *
     * @return an instance of {@link MultipleSearchSupplier}
     */
    public static MultipleSearchSupplier<TableCell> tableCells() {
        return widgets(TableCell.class);
    }

    /**
     * Constructs the chained searching from some instance of {@link SearchContext}.
     *
     * @param from is how to find some elements from a parent element.
     * @param <Q> is a type of the parent element.
     * @return self-reference
     */
    public <Q extends SearchContext> MultipleSearchSupplier<R> foundFrom(SearchSupplier<Q> from) {
        return super.from(from);
    }

    /**
     * Constructs the chained searching from some instance of {@link SearchContext}.
     *
     * @param from is a parent element.
     * @param <Q> is a type of the parent element.
     * @return self-reference
     */
    public <Q extends SearchContext> MultipleSearchSupplier<R> foundFrom(Q from) {
        return super.from(from);
    }

    /**
     * Constructs the chained searching from result of some function applying. This function should take some
     * {@link SearchContext} as the input parameter and return some list of found instances of {@link SearchContext}.
     *
     * @param from is a function which takes some {@link SearchContext} as the input parameter and returns some
     *             list of found instances of {@link SearchContext}.
     * @param <Q> is a type of the parent element.
     * @return self-reference
     */
    public <Q extends SearchContext>  MultipleSearchSupplier<R> foundFrom(Function<SearchContext, Q> from) {
        return super.from(from);
    }

    @Override
    protected Function<SearchContext, List<R>> getEndFunction() {
        return get();
    }
}
