package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.beans.PersistableBean;

import java.util.Collection;
import java.util.Objects;

public class BeanRepositories {
    private BeanRepositories() {}

    public static <T extends PersistableBean> BeanRepository<T> inMemory() {
        return new InMemoryBeanRepository<>();
    }

    public static <T extends PersistableBean> BeanRepository<T> inMemory(Collection<T> beans) {
        return new InMemoryBeanRepository<>(Objects.requireNonNull(beans));
    }
}
