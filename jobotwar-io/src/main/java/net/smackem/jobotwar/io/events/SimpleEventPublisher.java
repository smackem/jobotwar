package net.smackem.jobotwar.io.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public class SimpleEventPublisher<T> implements EventPublisher<T>, AutoCloseable {

    private final Object monitor = new Object();
    private final Collection<SimpleEventSubscription<T>> subscriptions = new ArrayList<>();

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber);
        internalSubscribe(subscriber);
    }

    @Override
    public EventSubscription subscribe(Consumer<T> handler) {
        Objects.requireNonNull(handler);
        final SimpleEventSubscriber<T> subscriber = new SimpleEventSubscriber<>(handler);
        return internalSubscribe(subscriber);
    }

    private EventSubscription internalSubscribe(Flow.Subscriber<? super T> subscriber) {
        final SimpleEventSubscription<T> subscription = new SimpleEventSubscription<>(this, subscriber);
        synchronized (this.monitor) {
            this.subscriptions.add(subscription);
        }
        subscriber.onSubscribe(subscription);
        return subscription;
    }

    public void submit(T item) {
        final Collection<SimpleEventSubscription<T>> subscriptions;
        synchronized (this.monitor) {
            subscriptions = List.copyOf(this.subscriptions);
        }
        for (final SimpleEventSubscription<T> subscription : subscriptions) {
            if (subscription.requestCount.getAndUpdate(n -> n > 0 ? n - 1 : n) > 0) {
                subscription.subscriber.onNext(item);
            }
        }
    }

    void cancelSubscription(SimpleEventSubscription<T> subscription) {
        synchronized (monitor) {
            subscriptions.remove(subscription);
        }
    }

    @Override
    public void close() {
        synchronized (this.monitor) {
            this.subscriptions.clear();
        }
    }
}
