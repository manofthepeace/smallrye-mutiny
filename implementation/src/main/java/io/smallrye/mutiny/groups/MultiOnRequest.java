package io.smallrye.mutiny.groups;

import static io.smallrye.mutiny.helpers.ParameterValidation.nonNull;

import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.MultiOnRequestInvoke;
import io.smallrye.mutiny.operators.multi.MultiOnRequestInvokeUni;

public class MultiOnRequest<T> {

    private final Multi<T> upstream;

    public MultiOnRequest(Multi<T> upstream) {
        this.upstream = nonNull(upstream, "upstream");
    }

    /**
     * Action when items are being requested.
     * The request is propagated upstream when the action has completed.
     * An error is forwarded downstream if the action throws an exception.
     * 
     * @param consumer the action
     * @return the new {@link Multi}
     */
    public Multi<T> invoke(LongConsumer consumer) {
        return Infrastructure.onMultiCreation(new MultiOnRequestInvoke<>(upstream, nonNull(consumer, "consumer")));
    }

    /**
     * Action when items are being requested.
     * The request is propagated upstream when the action has completed.
     * An error is forwarded downstream if the action throws an exception.
     *
     * @param action the action
     * @return the new {@link Multi}
     */
    public Multi<T> invoke(Runnable action) {
        Runnable actual = nonNull(action, "action");
        return invoke(ignored -> actual.run());
    }

    /**
     * Action when items are being requested.
     * The request is propagated upstream when the {@link Uni} has completed.
     * If the {@link Uni} fails then the error is forwarded downstream.
     * Also the {@link Uni} will receive a tentative cancellation event if the subscription of this {@link Multi} is
     * being cancelled.
     *
     * @param mapper the action, returns a non-{@code null} {@link Uni}
     * @return the new {@link Multi}
     */
    public Multi<T> call(LongFunction<Uni<?>> mapper) {
        return Infrastructure.onMultiCreation(new MultiOnRequestInvokeUni<>(upstream, nonNull(mapper, "mapper")));
    }

    /**
     * Action when items are being requested.
     * The request is propagated upstream when the {@link Uni} has completed.
     * If the {@link Uni} fails then the error is forwarded downstream.
     * Also the {@link Uni} will receive a tentative cancellation event if the subscription of this {@link Multi} is
     * being cancelled.
     *
     * @param supplier the action, returns a non-{@code null} {@link Uni}
     * @return the new {@link Multi}
     */
    public Multi<T> call(Supplier<Uni<?>> supplier) {
        Supplier<Uni<?>> actual = nonNull(supplier, "supplier");
        return call(ignored -> actual.get());
    }

    /**
     * Action when items are being requested.
     * The request is propagated upstream when the {@link Uni} has completed.
     * If the {@link Uni} fails then the error is forwarded downstream.
     * Also the {@link Uni} will receive a tentative cancellation event if the subscription of this {@link Multi} is
     * being cancelled.
     * 
     * @param mapper the action, returns a non-{@code null} {@link Uni}
     * @return the new {@link Multi}
     * @deprecated Use {@link #call(LongFunction)}
     */
    @Deprecated
    public Multi<T> invokeUni(LongFunction<Uni<?>> mapper) {
        return call(mapper);
    }

}
