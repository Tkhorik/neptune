package ru.tinkoff.qa.neptune.core.api;

import com.google.common.base.Preconditions;
import ru.tinkoff.qa.neptune.core.api.utils.IsDescribedUtil;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unchecked")
public interface GetStep<THIS extends GetStep<THIS>> {

    default  <T> T get(Function<THIS, T> function) {
        checkArgument(function != null,
                "The function is not defined");
        Preconditions.checkArgument(IsDescribedUtil.isDescribed(function),
                "The function which returns the goal value should be described " +
                        "by the StoryWriter.toGet method. Also you can override the toString method");

        StepFunction<THIS, T> stepFunction;
        if (StepFunction.class.isAssignableFrom(function.getClass())) {
            stepFunction = StepFunction.class.cast(function);
        }
        else {
            stepFunction = StepFunction.class.cast(StoryWriter.toGet(function.toString(), function));
        }

        return stepFunction.apply((THIS) this);
    }

    default  <T> T get(Supplier<Function<THIS, T>> functionSupplier) {
        checkNotNull(functionSupplier, "Supplier of the value to get was not defined");
        return get(functionSupplier.get());
    }
}
