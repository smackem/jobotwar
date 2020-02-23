package net.smackem.jobotwar.lang.v2;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

class DeclarationsExtractor extends JobotwarV2BaseListener {

    final List<StateDecl> states = new ArrayList<>();
    final List<FunctionDecl> functions = new ArrayList<>();
    final List<VariableDecl> variables = new ArrayList<>();

    @Override
    public void enterStateDecl(JobotwarV2Parser.StateDeclContext ctx) {
        final StateDecl state = new StateDecl(ctx.Ident().getText());
        ctx.parameters().Ident().stream()
                .map(ParseTree::getText)
                .forEach(state.parameters::add);
    }

    @Override
    public void enterVariableDecl(JobotwarV2Parser.VariableDeclContext ctx) {
        ctx.declarator().stream()
                .map(decl -> decl.Ident().getText())
                .map(VariableDecl::new)
                .forEach(this.variables::add);
    }

    @Override
    public void enterFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        final FunctionDecl function = new FunctionDecl(ctx.Ident().getText());
        ctx.parameters().Ident().stream()
                .map(ParseTree::getText)
                .forEach(function.parameters::add);
    }
}
