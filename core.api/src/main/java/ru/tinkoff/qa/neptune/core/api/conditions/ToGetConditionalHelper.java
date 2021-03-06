package ru.tinkoff.qa.neptune.core.api.conditions;

import com.google.common.base.Preconditions;
import ru.tinkoff.qa.neptune.core.api.AsIsCondition;
import ru.tinkoff.qa.neptune.core.api.StoryWriter;
import ru.tinkoff.qa.neptune.core.api.utils.IsDescribedUtil;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Objects;
import java.util.function.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * This is the util which helps to crate function with given conditions.
 */
@SuppressWarnings("unchecked")
final class ToGetConditionalHelper {

    private ToGetConditionalHelper() {
        super();
    }

    static <T> Predicate<T> checkCondition(Predicate<T> condition) {
        checkArgument(condition != null, "Predicate is not defined.");
        Preconditions.checkArgument(IsDescribedUtil.isDescribed(condition),
                "Condition is not described. " +
                        "Use StoryWriter.conditions to describe it.");
        return condition;
    }

    static String checkDescription(String description) {
        checkArgument(!isBlank(description), "Description should not be empty or null value");
        return description;
    }

    static <T, R>  Function<T, R> checkFunction(Function<T, R> function) {
        checkArgument(function != null, "Function is not defined.");
        return function;
    }

    static Duration checkSleepingTime(Duration duration) {
        checkArgument(duration != null, "Time of the sleeping is not defined");
        return duration;
    }

    static Duration checkWaitingTime(Duration duration) {
        checkArgument(duration != null, "Time of the waiting for some " +
                "valuable result is not defined");
        return duration;
    }

    static <T> Predicate<T> notNullAnd(Predicate<T> condition) {
        return ((Predicate<T>) StoryWriter.condition("is not null value", Objects::nonNull))
                .and(condition);
    }

    static boolean returnFalseOrThrowException(Throwable t, boolean ignoreExceptionOnConditionCheck) {
        String message = format("%s was caught. Message: %s", t.getClass().getName(), t.getMessage());
        if (!ignoreExceptionOnConditionCheck) {
            throw new CheckConditionException(message, t);
        }

        System.err.println(message);
        t.printStackTrace();
        return false;
    }

    static Supplier<? extends RuntimeException> checkExceptionSupplier(Supplier<? extends RuntimeException> exceptionSupplier) {
        checkArgument(exceptionSupplier != null,
                "Supplier of an exception to be thrown is not defined");
        return exceptionSupplier;
    }

    static String getDescription(String description, Predicate<?> condition) {
        String resultDescription = description;

        if (!AsIsCondition.AS_IS.equals(condition)) {
            resultDescription = format("%s. Criteria: %s", resultDescription, condition).trim();
        }

        return resultDescription;
    }

    static <T, F> Function<T, F> fluentWaitFunction(String description,
                                                    Function<T, F> originalFunction,
                                                    @Nullable Duration waitingTime,
                                                    @Nullable Duration sleepingTime,
                                                    Predicate<F> till,
                                                    @Nullable Supplier<? extends RuntimeException> exceptionOnTimeOut) {
        Duration timeOut = ofNullable(waitingTime).orElseGet(() -> ofMillis(0));
        Duration sleeping = ofNullable(sleepingTime).orElseGet(() -> ofMillis(50));

        return StoryWriter.toGet(description, t -> {

            long currentMillis = currentTimeMillis();
            long endMillis = currentMillis + timeOut.toMillis() + 100;
            F f = null;
            boolean suitable = false;
            while (currentTimeMillis() < endMillis && !(suitable)) {
                suitable = till.test(f = originalFunction.apply(t));
                try {
                    sleep(sleeping.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }

            if (suitable) {
                return f;
            }

            return (F) ofNullable(exceptionOnTimeOut).map(exceptionSupplier1 -> {
                throw exceptionOnTimeOut.get();
            }).orElse(f);
        });
    }


    private static class CheckConditionException extends RuntimeException {
        CheckConditionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
