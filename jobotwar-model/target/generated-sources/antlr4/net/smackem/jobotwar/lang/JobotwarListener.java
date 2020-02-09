// Generated from net/smackem/jobotwar/lang/Jobotwar.g4 by ANTLR 4.7.2
package net.smackem.jobotwar.lang;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JobotwarParser}.
 */
public interface JobotwarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(JobotwarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(JobotwarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#declLine}.
	 * @param ctx the parse tree
	 */
	void enterDeclLine(JobotwarParser.DeclLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#declLine}.
	 * @param ctx the parse tree
	 */
	void exitDeclLine(JobotwarParser.DeclLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(JobotwarParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(JobotwarParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(JobotwarParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(JobotwarParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(JobotwarParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(JobotwarParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(JobotwarParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(JobotwarParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#gotoStatement}.
	 * @param ctx the parse tree
	 */
	void enterGotoStatement(JobotwarParser.GotoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#gotoStatement}.
	 * @param ctx the parse tree
	 */
	void exitGotoStatement(JobotwarParser.GotoStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#gosubStatement}.
	 * @param ctx the parse tree
	 */
	void enterGosubStatement(JobotwarParser.GosubStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#gosubStatement}.
	 * @param ctx the parse tree
	 */
	void exitGosubStatement(JobotwarParser.GosubStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#endsubStatement}.
	 * @param ctx the parse tree
	 */
	void enterEndsubStatement(JobotwarParser.EndsubStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#endsubStatement}.
	 * @param ctx the parse tree
	 */
	void exitEndsubStatement(JobotwarParser.EndsubStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#assignStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssignStatement(JobotwarParser.AssignStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#assignStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssignStatement(JobotwarParser.AssignStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#assignTarget}.
	 * @param ctx the parse tree
	 */
	void enterAssignTarget(JobotwarParser.AssignTargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#assignTarget}.
	 * @param ctx the parse tree
	 */
	void exitAssignTarget(JobotwarParser.AssignTargetContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#ifClause}.
	 * @param ctx the parse tree
	 */
	void enterIfClause(JobotwarParser.IfClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#ifClause}.
	 * @param ctx the parse tree
	 */
	void exitIfClause(JobotwarParser.IfClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#unlessClause}.
	 * @param ctx the parse tree
	 */
	void enterUnlessClause(JobotwarParser.UnlessClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#unlessClause}.
	 * @param ctx the parse tree
	 */
	void exitUnlessClause(JobotwarParser.UnlessClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(JobotwarParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(JobotwarParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#conditionOperator}.
	 * @param ctx the parse tree
	 */
	void enterConditionOperator(JobotwarParser.ConditionOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#conditionOperator}.
	 * @param ctx the parse tree
	 */
	void exitConditionOperator(JobotwarParser.ConditionOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(JobotwarParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(JobotwarParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(JobotwarParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(JobotwarParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#termOperator}.
	 * @param ctx the parse tree
	 */
	void enterTermOperator(JobotwarParser.TermOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#termOperator}.
	 * @param ctx the parse tree
	 */
	void exitTermOperator(JobotwarParser.TermOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#product}.
	 * @param ctx the parse tree
	 */
	void enterProduct(JobotwarParser.ProductContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#product}.
	 * @param ctx the parse tree
	 */
	void exitProduct(JobotwarParser.ProductContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#productOperator}.
	 * @param ctx the parse tree
	 */
	void enterProductOperator(JobotwarParser.ProductOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#productOperator}.
	 * @param ctx the parse tree
	 */
	void exitProductOperator(JobotwarParser.ProductOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator(JobotwarParser.ComparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator(JobotwarParser.ComparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#molecule}.
	 * @param ctx the parse tree
	 */
	void enterMolecule(JobotwarParser.MoleculeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#molecule}.
	 * @param ctx the parse tree
	 */
	void exitMolecule(JobotwarParser.MoleculeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(JobotwarParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(JobotwarParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#register}.
	 * @param ctx the parse tree
	 */
	void enterRegister(JobotwarParser.RegisterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#register}.
	 * @param ctx the parse tree
	 */
	void exitRegister(JobotwarParser.RegisterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#func}.
	 * @param ctx the parse tree
	 */
	void enterFunc(JobotwarParser.FuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#func}.
	 * @param ctx the parse tree
	 */
	void exitFunc(JobotwarParser.FuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#specialAssignTarget}.
	 * @param ctx the parse tree
	 */
	void enterSpecialAssignTarget(JobotwarParser.SpecialAssignTargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#specialAssignTarget}.
	 * @param ctx the parse tree
	 */
	void exitSpecialAssignTarget(JobotwarParser.SpecialAssignTargetContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(JobotwarParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(JobotwarParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link JobotwarParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(JobotwarParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link JobotwarParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(JobotwarParser.CommentContext ctx);
}