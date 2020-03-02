package net.smackem.jobotwar.lang.v2;

class StateDecl extends ProcedureDecl {

    public static final String MAIN_STATE_NAME = "main";

    protected StateDecl(String name, int order) {
        super(name, order);
    }

    @Override
    public int findLocalOrParameter(String name) {
        final int index = this.locals().indexOf(name);
        if (index >= 0) {
            return index;
        }
        return -1;
    }
}
