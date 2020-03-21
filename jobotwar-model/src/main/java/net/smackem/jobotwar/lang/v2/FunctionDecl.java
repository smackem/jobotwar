package net.smackem.jobotwar.lang.v2;

class FunctionDecl extends ProcedureDecl {
    protected FunctionDecl(String name, int order) {
        super(name, order);
    }

    @Override
    public int stackParameterCount() {
        return this.parameters().size();
    }

    @Override
    public String toString() {
        return String.format("[%d] function %s(%s)", this.order, this.name, this.parameters());
    }
}
