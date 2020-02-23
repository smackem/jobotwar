package net.smackem.jobotwar.lang.v2;

class VariableDecl extends Declaration {

    private int address;

    VariableDecl(String name, int order) {
        super(name, order);
    }

    public int getAddress() {
        return this.address;
    }

    public void setAddress(int value) {
        this.address = value;
    }
}
