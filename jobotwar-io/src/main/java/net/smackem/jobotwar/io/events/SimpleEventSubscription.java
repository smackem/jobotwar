package net.smackem.jobotwar.io.events;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicLong;

class SimpleEventSubscription<T> implements EventSubscription {
    final AtomicLong requestCount = new AtomicLong();
    final SimpleEventPublisher<T> publisher;
    final Flow.Subscriber<? super T> subscriber;
    private volatile boolean cancelled;

    SimpleEventSubscription(SimpleEventPublisher<T> publisher, Flow.Subscriber<? super T> subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        if (this.cancelled) {
            throw new UnsupportedOperationException("The subscription has been cancelled");
        }
        this.requestCount.addAndGet(n);
    }

    @Override
    public void cancel() {
        if (this.cancelled) {
            return;
        }
        this.publisher.cancelSubscription(this);
        this.cancelled = true;
    }

    @Override
    public void suspend() {
        if (this.cancelled) {
            throw new UnsupportedOperationException("The subscription has been cancelled");
        }
        this.requestCount.set(0);
    }

    @Override
    public void resume() {
        if (this.cancelled) {
            throw new UnsupportedOperationException("The subscription has been cancelled");
        }
        request(1);
    }
}
