package net.smackem.jobotwar.lang.v2;

abstract class Declaration {
    final String name;
    final int order;

    protected Declaration(String name, int order) {
        this.name = name;
        this.order = order;
    }
}
