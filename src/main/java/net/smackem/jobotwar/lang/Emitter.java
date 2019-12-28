package net.smackem.jobotwar.lang;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Emitter extends JobotwarBaseListener {
    private boolean ignoreIfAndUnless;

    @Override
    public void enterStatement(JobotwarParser.StatementContext ctx) {
        if (ctx.ifclause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.ifclause());
            this.ignoreIfAndUnless = true;
        }
    }

    @Override
    public void exitStatement(JobotwarParser.StatementContext ctx) {
        this.ignoreIfAndUnless = false;
    }

    @Override
    public void enterIfclause(JobotwarParser.IfclauseContext ctx) {
        if (this.ignoreIfAndUnless) {
            return;
        }
    }

    @Override
    public void enterUnlessclause(JobotwarParser.UnlessclauseContext ctx) {
        if (this.ignoreIfAndUnless) {
            return;
        }
    }
}
