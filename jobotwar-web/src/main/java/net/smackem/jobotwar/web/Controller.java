package net.smackem.jobotwar.web;

import net.smackem.jobotwar.web.beans.PersistableBean;
import net.smackem.jobotwar.web.persist.BeanRepository;

class Controller<T extends PersistableBean> {

    private final BeanRepository<T> repository;

    Controller(BeanRepository<T> repository) {
        this.repository = repository;
    }

    BeanRepository<T> repository() {
        return this.repository;
    }
}
