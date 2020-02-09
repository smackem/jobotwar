// Generated from net/smackem/jobotwar/lang/Jobotwar.g4 by ANTLR 4.7.2
package net.smackem.jobotwar.lang;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JobotwarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		AIM=25, SHOT=26, RADAR=27, DAMAGE=28, SPEEDX=29, SPEEDY=30, RANDOM=31, 
		X=32, Y=33, ABS=34, NOT=35, TAN=36, SIN=37, COS=38, ATAN=39, ASIN=40, 
		ACOS=41, SQRT=42, TRUNC=43, OUT=44, ID=45, NUMBER=46, COMMENT=47, EOL=48, 
		WS=49;
	public static final int
		RULE_program = 0, RULE_declLine = 1, RULE_declaration = 2, RULE_line = 3, 
		RULE_label = 4, RULE_statement = 5, RULE_gotoStatement = 6, RULE_gosubStatement = 7, 
		RULE_endsubStatement = 8, RULE_assignStatement = 9, RULE_assignTarget = 10, 
		RULE_ifClause = 11, RULE_unlessClause = 12, RULE_condition = 13, RULE_conditionOperator = 14, 
		RULE_comparison = 15, RULE_term = 16, RULE_termOperator = 17, RULE_product = 18, 
		RULE_productOperator = 19, RULE_comparator = 20, RULE_molecule = 21, RULE_atom = 22, 
		RULE_register = 23, RULE_func = 24, RULE_specialAssignTarget = 25, RULE_number = 26, 
		RULE_comment = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "declLine", "declaration", "line", "label", "statement", "gotoStatement", 
			"gosubStatement", "endsubStatement", "assignStatement", "assignTarget", 
			"ifClause", "unlessClause", "condition", "conditionOperator", "comparison", 
			"term", "termOperator", "product", "productOperator", "comparator", "molecule", 
			"atom", "register", "func", "specialAssignTarget", "number", "comment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'def'", "','", "':'", "'goto'", "'gosub'", "'endsub'", "'->'", 
			"'if'", "'unless'", "'or'", "'and'", "'+'", "'-'", "'*'", "'/'", "'%'", 
			"'<'", "'<='", "'>'", "'>='", "'='", "'!='", "'('", "')'", "'AIM'", "'SHOT'", 
			"'RADAR'", "'DAMAGE'", "'SPEEDX'", "'SPEEDY'", "'RANDOM'", "'X'", "'Y'", 
			"'abs'", "'not'", "'tan'", "'sin'", "'cos'", "'atan'", "'asin'", "'acos'", 
			"'sqrt'", "'trunc'", "'OUT'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "AIM", "SHOT", "RADAR", "DAMAGE", "SPEEDX", "SPEEDY", "RANDOM", 
			"X", "Y", "ABS", "NOT", "TAN", "SIN", "COS", "ATAN", "ASIN", "ACOS", 
			"SQRT", "TRUNC", "OUT", "ID", "NUMBER", "COMMENT", "EOL", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Jobotwar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JobotwarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public List<DeclLineContext> declLine() {
			return getRuleContexts(DeclLineContext.class);
		}
		public DeclLineContext declLine(int i) {
			return getRuleContext(DeclLineContext.class,i);
		}
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(56);
					declLine();
					}
					} 
				}
				setState(61);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__11) | (1L << T__12) | (1L << T__22) | (1L << AIM) | (1L << SHOT) | (1L << RADAR) | (1L << DAMAGE) | (1L << SPEEDX) | (1L << SPEEDY) | (1L << RANDOM) | (1L << X) | (1L << Y) | (1L << ABS) | (1L << NOT) | (1L << TAN) | (1L << SIN) | (1L << COS) | (1L << ATAN) | (1L << ASIN) | (1L << ACOS) | (1L << SQRT) | (1L << TRUNC) | (1L << ID) | (1L << NUMBER) | (1L << COMMENT) | (1L << EOL))) != 0)) {
				{
				{
				setState(62);
				line();
				}
				}
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclLineContext extends ParserRuleContext {
		public TerminalNode EOL() { return getToken(JobotwarParser.EOL, 0); }
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public DeclLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterDeclLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitDeclLine(this);
		}
	}

	public final DeclLineContext declLine() throws RecognitionException {
		DeclLineContext _localctx = new DeclLineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declLine);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMMENT:
				{
				setState(68);
				comment();
				}
				break;
			case T__0:
				{
				setState(69);
				declaration();
				}
				break;
			case EOL:
				break;
			default:
				break;
			}
			setState(72);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(JobotwarParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(JobotwarParser.ID, i);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(T__0);
			setState(75);
			match(ID);
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(76);
				match(T__1);
				setState(77);
				match(ID);
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TerminalNode EOL() { return getToken(JobotwarParser.EOL, 0); }
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitLine(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(83);
				label();
				}
				break;
			case 2:
				{
				setState(84);
				comment();
				}
				break;
			case 3:
				{
				setState(85);
				statement();
				}
				break;
			}
			setState(88);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(JobotwarParser.ID, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitLabel(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(ID);
			setState(91);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public AssignStatementContext assignStatement() {
			return getRuleContext(AssignStatementContext.class,0);
		}
		public GotoStatementContext gotoStatement() {
			return getRuleContext(GotoStatementContext.class,0);
		}
		public GosubStatementContext gosubStatement() {
			return getRuleContext(GosubStatementContext.class,0);
		}
		public EndsubStatementContext endsubStatement() {
			return getRuleContext(EndsubStatementContext.class,0);
		}
		public IfClauseContext ifClause() {
			return getRuleContext(IfClauseContext.class,0);
		}
		public UnlessClauseContext unlessClause() {
			return getRuleContext(UnlessClauseContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
			case T__12:
			case T__22:
			case AIM:
			case SHOT:
			case RADAR:
			case DAMAGE:
			case SPEEDX:
			case SPEEDY:
			case RANDOM:
			case X:
			case Y:
			case ABS:
			case NOT:
			case TAN:
			case SIN:
			case COS:
			case ATAN:
			case ASIN:
			case ACOS:
			case SQRT:
			case TRUNC:
			case ID:
			case NUMBER:
				{
				setState(93);
				assignStatement();
				}
				break;
			case T__3:
				{
				setState(94);
				gotoStatement();
				}
				break;
			case T__4:
				{
				setState(95);
				gosubStatement();
				}
				break;
			case T__5:
				{
				setState(96);
				endsubStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(101);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				{
				setState(99);
				ifClause();
				}
				break;
			case T__8:
				{
				setState(100);
				unlessClause();
				}
				break;
			case EOL:
				break;
			default:
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GotoStatementContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(JobotwarParser.ID, 0); }
		public GotoStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gotoStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterGotoStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitGotoStatement(this);
		}
	}

	public final GotoStatementContext gotoStatement() throws RecognitionException {
		GotoStatementContext _localctx = new GotoStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_gotoStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			match(T__3);
			setState(104);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GosubStatementContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(JobotwarParser.ID, 0); }
		public GosubStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gosubStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterGosubStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitGosubStatement(this);
		}
	}

	public final GosubStatementContext gosubStatement() throws RecognitionException {
		GosubStatementContext _localctx = new GosubStatementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_gosubStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(T__4);
			setState(107);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndsubStatementContext extends ParserRuleContext {
		public EndsubStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endsubStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterEndsubStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitEndsubStatement(this);
		}
	}

	public final EndsubStatementContext endsubStatement() throws RecognitionException {
		EndsubStatementContext _localctx = new EndsubStatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_endsubStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignStatementContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public List<AssignTargetContext> assignTarget() {
			return getRuleContexts(AssignTargetContext.class);
		}
		public AssignTargetContext assignTarget(int i) {
			return getRuleContext(AssignTargetContext.class,i);
		}
		public AssignStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterAssignStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitAssignStatement(this);
		}
	}

	public final AssignStatementContext assignStatement() throws RecognitionException {
		AssignStatementContext _localctx = new AssignStatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_assignStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			term(0);
			setState(114); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(112);
				match(T__6);
				setState(113);
				assignTarget();
				}
				}
				setState(116); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__6 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignTargetContext extends ParserRuleContext {
		public RegisterContext register() {
			return getRuleContext(RegisterContext.class,0);
		}
		public SpecialAssignTargetContext specialAssignTarget() {
			return getRuleContext(SpecialAssignTargetContext.class,0);
		}
		public TerminalNode ID() { return getToken(JobotwarParser.ID, 0); }
		public AssignTargetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignTarget; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterAssignTarget(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitAssignTarget(this);
		}
	}

	public final AssignTargetContext assignTarget() throws RecognitionException {
		AssignTargetContext _localctx = new AssignTargetContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_assignTarget);
		try {
			setState(121);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AIM:
			case SHOT:
			case RADAR:
			case DAMAGE:
			case SPEEDX:
			case SPEEDY:
			case RANDOM:
			case X:
			case Y:
				enterOuterAlt(_localctx, 1);
				{
				setState(118);
				register();
				}
				break;
			case OUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(119);
				specialAssignTarget();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(120);
				match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfClauseContext extends ParserRuleContext {
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public IfClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterIfClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitIfClause(this);
		}
	}

	public final IfClauseContext ifClause() throws RecognitionException {
		IfClauseContext _localctx = new IfClauseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ifClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(T__7);
			setState(124);
			condition(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnlessClauseContext extends ParserRuleContext {
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public UnlessClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unlessClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterUnlessClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitUnlessClause(this);
		}
	}

	public final UnlessClauseContext unlessClause() throws RecognitionException {
		UnlessClauseContext _localctx = new UnlessClauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_unlessClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(T__8);
			setState(127);
			condition(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionContext extends ParserRuleContext {
		public ComparisonContext comparison() {
			return getRuleContext(ComparisonContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public ConditionOperatorContext conditionOperator() {
			return getRuleContext(ConditionOperatorContext.class,0);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitCondition(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		return condition(0);
	}

	private ConditionContext condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConditionContext _localctx = new ConditionContext(_ctx, _parentState);
		ConditionContext _prevctx = _localctx;
		int _startState = 26;
		enterRecursionRule(_localctx, 26, RULE_condition, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(130);
			comparison();
			}
			_ctx.stop = _input.LT(-1);
			setState(138);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_condition);
					setState(132);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(133);
					conditionOperator();
					setState(134);
					comparison();
					}
					} 
				}
				setState(140);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ConditionOperatorContext extends ParserRuleContext {
		public ConditionOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterConditionOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitConditionOperator(this);
		}
	}

	public final ConditionOperatorContext conditionOperator() throws RecognitionException {
		ConditionOperatorContext _localctx = new ConditionOperatorContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_conditionOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			_la = _input.LA(1);
			if ( !(_la==T__9 || _la==T__10) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public ComparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitComparison(this);
		}
	}

	public final ComparisonContext comparison() throws RecognitionException {
		ComparisonContext _localctx = new ComparisonContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_comparison);
		try {
			setState(148);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(143);
				term(0);
				setState(144);
				comparator();
				setState(145);
				term(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				term(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public ProductContext product() {
			return getRuleContext(ProductContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TermOperatorContext termOperator() {
			return getRuleContext(TermOperatorContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		return term(0);
	}

	private TermContext term(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TermContext _localctx = new TermContext(_ctx, _parentState);
		TermContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_term, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(151);
			product(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(159);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TermContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_term);
					setState(153);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(154);
					termOperator();
					setState(155);
					product(0);
					}
					} 
				}
				setState(161);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TermOperatorContext extends ParserRuleContext {
		public TermOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterTermOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitTermOperator(this);
		}
	}

	public final TermOperatorContext termOperator() throws RecognitionException {
		TermOperatorContext _localctx = new TermOperatorContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_termOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			_la = _input.LA(1);
			if ( !(_la==T__11 || _la==T__12) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProductContext extends ParserRuleContext {
		public MoleculeContext molecule() {
			return getRuleContext(MoleculeContext.class,0);
		}
		public ProductContext product() {
			return getRuleContext(ProductContext.class,0);
		}
		public ProductOperatorContext productOperator() {
			return getRuleContext(ProductOperatorContext.class,0);
		}
		public ProductContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_product; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterProduct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitProduct(this);
		}
	}

	public final ProductContext product() throws RecognitionException {
		return product(0);
	}

	private ProductContext product(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ProductContext _localctx = new ProductContext(_ctx, _parentState);
		ProductContext _prevctx = _localctx;
		int _startState = 36;
		enterRecursionRule(_localctx, 36, RULE_product, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(165);
			molecule();
			}
			_ctx.stop = _input.LT(-1);
			setState(173);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ProductContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_product);
					setState(167);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(168);
					productOperator();
					setState(169);
					molecule();
					}
					} 
				}
				setState(175);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ProductOperatorContext extends ParserRuleContext {
		public ProductOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_productOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterProductOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitProductOperator(this);
		}
	}

	public final ProductOperatorContext productOperator() throws RecognitionException {
		ProductOperatorContext _localctx = new ProductOperatorContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_productOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__14) | (1L << T__15))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparatorContext extends ParserRuleContext {
		public ComparatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterComparator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitComparator(this);
		}
	}

	public final ComparatorContext comparator() throws RecognitionException {
		ComparatorContext _localctx = new ComparatorContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_comparator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MoleculeContext extends ParserRuleContext {
		public FuncContext func() {
			return getRuleContext(FuncContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public MoleculeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_molecule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterMolecule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitMolecule(this);
		}
	}

	public final MoleculeContext molecule() throws RecognitionException {
		MoleculeContext _localctx = new MoleculeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_molecule);
		try {
			setState(186);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ABS:
			case NOT:
			case TAN:
			case SIN:
			case COS:
			case ATAN:
			case ASIN:
			case ACOS:
			case SQRT:
			case TRUNC:
				enterOuterAlt(_localctx, 1);
				{
				setState(180);
				func();
				setState(181);
				match(T__22);
				setState(182);
				condition(0);
				setState(183);
				match(T__23);
				}
				break;
			case T__11:
			case T__12:
			case T__22:
			case AIM:
			case SHOT:
			case RADAR:
			case DAMAGE:
			case SPEEDX:
			case SPEEDY:
			case RANDOM:
			case X:
			case Y:
			case ID:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public RegisterContext register() {
			return getRuleContext(RegisterContext.class,0);
		}
		public TerminalNode ID() { return getToken(JobotwarParser.ID, 0); }
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_atom);
		try {
			setState(195);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
			case T__12:
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(188);
				number();
				}
				break;
			case AIM:
			case SHOT:
			case RADAR:
			case DAMAGE:
			case SPEEDX:
			case SPEEDY:
			case RANDOM:
			case X:
			case Y:
				enterOuterAlt(_localctx, 2);
				{
				setState(189);
				register();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(190);
				match(ID);
				}
				break;
			case T__22:
				enterOuterAlt(_localctx, 4);
				{
				setState(191);
				match(T__22);
				setState(192);
				condition(0);
				setState(193);
				match(T__23);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RegisterContext extends ParserRuleContext {
		public TerminalNode AIM() { return getToken(JobotwarParser.AIM, 0); }
		public TerminalNode SHOT() { return getToken(JobotwarParser.SHOT, 0); }
		public TerminalNode RADAR() { return getToken(JobotwarParser.RADAR, 0); }
		public TerminalNode SPEEDX() { return getToken(JobotwarParser.SPEEDX, 0); }
		public TerminalNode SPEEDY() { return getToken(JobotwarParser.SPEEDY, 0); }
		public TerminalNode RANDOM() { return getToken(JobotwarParser.RANDOM, 0); }
		public TerminalNode DAMAGE() { return getToken(JobotwarParser.DAMAGE, 0); }
		public TerminalNode X() { return getToken(JobotwarParser.X, 0); }
		public TerminalNode Y() { return getToken(JobotwarParser.Y, 0); }
		public RegisterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_register; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterRegister(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitRegister(this);
		}
	}

	public final RegisterContext register() throws RecognitionException {
		RegisterContext _localctx = new RegisterContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_register);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AIM) | (1L << SHOT) | (1L << RADAR) | (1L << DAMAGE) | (1L << SPEEDX) | (1L << SPEEDY) | (1L << RANDOM) | (1L << X) | (1L << Y))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncContext extends ParserRuleContext {
		public TerminalNode ABS() { return getToken(JobotwarParser.ABS, 0); }
		public TerminalNode NOT() { return getToken(JobotwarParser.NOT, 0); }
		public TerminalNode TAN() { return getToken(JobotwarParser.TAN, 0); }
		public TerminalNode SIN() { return getToken(JobotwarParser.SIN, 0); }
		public TerminalNode COS() { return getToken(JobotwarParser.COS, 0); }
		public TerminalNode ATAN() { return getToken(JobotwarParser.ATAN, 0); }
		public TerminalNode ASIN() { return getToken(JobotwarParser.ASIN, 0); }
		public TerminalNode ACOS() { return getToken(JobotwarParser.ACOS, 0); }
		public TerminalNode SQRT() { return getToken(JobotwarParser.SQRT, 0); }
		public TerminalNode TRUNC() { return getToken(JobotwarParser.TRUNC, 0); }
		public FuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_func; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitFunc(this);
		}
	}

	public final FuncContext func() throws RecognitionException {
		FuncContext _localctx = new FuncContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_func);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABS) | (1L << NOT) | (1L << TAN) | (1L << SIN) | (1L << COS) | (1L << ATAN) | (1L << ASIN) | (1L << ACOS) | (1L << SQRT) | (1L << TRUNC))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SpecialAssignTargetContext extends ParserRuleContext {
		public TerminalNode OUT() { return getToken(JobotwarParser.OUT, 0); }
		public SpecialAssignTargetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specialAssignTarget; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterSpecialAssignTarget(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitSpecialAssignTarget(this);
		}
	}

	public final SpecialAssignTargetContext specialAssignTarget() throws RecognitionException {
		SpecialAssignTargetContext _localctx = new SpecialAssignTargetContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_specialAssignTarget);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			match(OUT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(JobotwarParser.NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11 || _la==T__12) {
				{
				setState(203);
				_la = _input.LA(1);
				if ( !(_la==T__11 || _la==T__12) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(206);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(JobotwarParser.COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JobotwarListener ) ((JobotwarListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			match(COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 13:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		case 16:
			return term_sempred((TermContext)_localctx, predIndex);
		case 18:
			return product_sempred((ProductContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean condition_sempred(ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean term_sempred(TermContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean product_sempred(ProductContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\63\u00d5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\7\2<\n\2\f\2\16\2?\13\2\3"+
		"\2\7\2B\n\2\f\2\16\2E\13\2\3\3\3\3\5\3I\n\3\3\3\3\3\3\4\3\4\3\4\3\4\7"+
		"\4Q\n\4\f\4\16\4T\13\4\3\5\3\5\3\5\5\5Y\n\5\3\5\3\5\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\5\7d\n\7\3\7\3\7\5\7h\n\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\6\13u\n\13\r\13\16\13v\3\f\3\f\3\f\5\f|\n\f\3\r\3\r\3\r"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u008b\n\17\f\17"+
		"\16\17\u008e\13\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\5\21\u0097\n\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u00a0\n\22\f\22\16\22\u00a3\13"+
		"\22\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\7\24\u00ae\n\24\f\24"+
		"\16\24\u00b1\13\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\5"+
		"\27\u00bd\n\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00c6\n\30\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\34\5\34\u00cf\n\34\3\34\3\34\3\35\3\35\3\35"+
		"\2\5\34\"&\36\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\668\2\b\3\2\f\r\3\2\16\17\3\2\20\22\3\2\23\30\3\2\33#\3\2$-\2\u00d1\2"+
		"=\3\2\2\2\4H\3\2\2\2\6L\3\2\2\2\bX\3\2\2\2\n\\\3\2\2\2\fc\3\2\2\2\16i"+
		"\3\2\2\2\20l\3\2\2\2\22o\3\2\2\2\24q\3\2\2\2\26{\3\2\2\2\30}\3\2\2\2\32"+
		"\u0080\3\2\2\2\34\u0083\3\2\2\2\36\u008f\3\2\2\2 \u0096\3\2\2\2\"\u0098"+
		"\3\2\2\2$\u00a4\3\2\2\2&\u00a6\3\2\2\2(\u00b2\3\2\2\2*\u00b4\3\2\2\2,"+
		"\u00bc\3\2\2\2.\u00c5\3\2\2\2\60\u00c7\3\2\2\2\62\u00c9\3\2\2\2\64\u00cb"+
		"\3\2\2\2\66\u00ce\3\2\2\28\u00d2\3\2\2\2:<\5\4\3\2;:\3\2\2\2<?\3\2\2\2"+
		"=;\3\2\2\2=>\3\2\2\2>C\3\2\2\2?=\3\2\2\2@B\5\b\5\2A@\3\2\2\2BE\3\2\2\2"+
		"CA\3\2\2\2CD\3\2\2\2D\3\3\2\2\2EC\3\2\2\2FI\58\35\2GI\5\6\4\2HF\3\2\2"+
		"\2HG\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\7\62\2\2K\5\3\2\2\2LM\7\3\2\2MR\7/"+
		"\2\2NO\7\4\2\2OQ\7/\2\2PN\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2S\7\3\2"+
		"\2\2TR\3\2\2\2UY\5\n\6\2VY\58\35\2WY\5\f\7\2XU\3\2\2\2XV\3\2\2\2XW\3\2"+
		"\2\2XY\3\2\2\2YZ\3\2\2\2Z[\7\62\2\2[\t\3\2\2\2\\]\7/\2\2]^\7\5\2\2^\13"+
		"\3\2\2\2_d\5\24\13\2`d\5\16\b\2ad\5\20\t\2bd\5\22\n\2c_\3\2\2\2c`\3\2"+
		"\2\2ca\3\2\2\2cb\3\2\2\2dg\3\2\2\2eh\5\30\r\2fh\5\32\16\2ge\3\2\2\2gf"+
		"\3\2\2\2gh\3\2\2\2h\r\3\2\2\2ij\7\6\2\2jk\7/\2\2k\17\3\2\2\2lm\7\7\2\2"+
		"mn\7/\2\2n\21\3\2\2\2op\7\b\2\2p\23\3\2\2\2qt\5\"\22\2rs\7\t\2\2su\5\26"+
		"\f\2tr\3\2\2\2uv\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\25\3\2\2\2x|\5\60\31\2y"+
		"|\5\64\33\2z|\7/\2\2{x\3\2\2\2{y\3\2\2\2{z\3\2\2\2|\27\3\2\2\2}~\7\n\2"+
		"\2~\177\5\34\17\2\177\31\3\2\2\2\u0080\u0081\7\13\2\2\u0081\u0082\5\34"+
		"\17\2\u0082\33\3\2\2\2\u0083\u0084\b\17\1\2\u0084\u0085\5 \21\2\u0085"+
		"\u008c\3\2\2\2\u0086\u0087\f\4\2\2\u0087\u0088\5\36\20\2\u0088\u0089\5"+
		" \21\2\u0089\u008b\3\2\2\2\u008a\u0086\3\2\2\2\u008b\u008e\3\2\2\2\u008c"+
		"\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\35\3\2\2\2\u008e\u008c\3\2\2"+
		"\2\u008f\u0090\t\2\2\2\u0090\37\3\2\2\2\u0091\u0092\5\"\22\2\u0092\u0093"+
		"\5*\26\2\u0093\u0094\5\"\22\2\u0094\u0097\3\2\2\2\u0095\u0097\5\"\22\2"+
		"\u0096\u0091\3\2\2\2\u0096\u0095\3\2\2\2\u0097!\3\2\2\2\u0098\u0099\b"+
		"\22\1\2\u0099\u009a\5&\24\2\u009a\u00a1\3\2\2\2\u009b\u009c\f\4\2\2\u009c"+
		"\u009d\5$\23\2\u009d\u009e\5&\24\2\u009e\u00a0\3\2\2\2\u009f\u009b\3\2"+
		"\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"#\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\t\3\2\2\u00a5%\3\2\2\2\u00a6"+
		"\u00a7\b\24\1\2\u00a7\u00a8\5,\27\2\u00a8\u00af\3\2\2\2\u00a9\u00aa\f"+
		"\4\2\2\u00aa\u00ab\5(\25\2\u00ab\u00ac\5,\27\2\u00ac\u00ae\3\2\2\2\u00ad"+
		"\u00a9\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2"+
		"\2\2\u00b0\'\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b3\t\4\2\2\u00b3)\3"+
		"\2\2\2\u00b4\u00b5\t\5\2\2\u00b5+\3\2\2\2\u00b6\u00b7\5\62\32\2\u00b7"+
		"\u00b8\7\31\2\2\u00b8\u00b9\5\34\17\2\u00b9\u00ba\7\32\2\2\u00ba\u00bd"+
		"\3\2\2\2\u00bb\u00bd\5.\30\2\u00bc\u00b6\3\2\2\2\u00bc\u00bb\3\2\2\2\u00bd"+
		"-\3\2\2\2\u00be\u00c6\5\66\34\2\u00bf\u00c6\5\60\31\2\u00c0\u00c6\7/\2"+
		"\2\u00c1\u00c2\7\31\2\2\u00c2\u00c3\5\34\17\2\u00c3\u00c4\7\32\2\2\u00c4"+
		"\u00c6\3\2\2\2\u00c5\u00be\3\2\2\2\u00c5\u00bf\3\2\2\2\u00c5\u00c0\3\2"+
		"\2\2\u00c5\u00c1\3\2\2\2\u00c6/\3\2\2\2\u00c7\u00c8\t\6\2\2\u00c8\61\3"+
		"\2\2\2\u00c9\u00ca\t\7\2\2\u00ca\63\3\2\2\2\u00cb\u00cc\7.\2\2\u00cc\65"+
		"\3\2\2\2\u00cd\u00cf\t\3\2\2\u00ce\u00cd\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf"+
		"\u00d0\3\2\2\2\u00d0\u00d1\7\60\2\2\u00d1\67\3\2\2\2\u00d2\u00d3\7\61"+
		"\2\2\u00d39\3\2\2\2\22=CHRXcgv{\u008c\u0096\u00a1\u00af\u00bc\u00c5\u00ce";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}