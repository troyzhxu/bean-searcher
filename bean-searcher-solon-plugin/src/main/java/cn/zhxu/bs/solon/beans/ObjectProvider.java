package cn.zhxu.bs.solon.beans;

import org.noear.solon.lang.Nullable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author noear 2023/2/18 created
 */

public interface ObjectProvider<T> extends ObjectFactory<T>, Iterable<T> {
    T getObject(Object... args) throws BeansException;

    @Nullable
    T getIfAvailable() throws BeansException;

    default T getIfAvailable(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = this.getIfAvailable();
        return dependency != null ? dependency : defaultSupplier.get();
    }

    default void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = this.getIfAvailable();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }

    }

    @Nullable
    T getIfUnique() throws BeansException;

    default T getIfUnique(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = this.getIfUnique();
        return dependency != null ? dependency : defaultSupplier.get();
    }

    default void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = this.getIfUnique();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }

    }

    default Iterator<T> iterator() {
        return this.stream().iterator();
    }

    default Stream<T> stream() {
        throw new UnsupportedOperationException("Multi element access not supported");
    }

    default Stream<T> orderedStream() {
        throw new UnsupportedOperationException("Ordered element access not supported");
    }
}