package net.smackem.jobotwar.gui.edit;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.smackem.jobotwar.lang.Compiler;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends CodeArea {

    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\s+");
    private final ObjectProperty<Compiler.Language> syntax = new SimpleObjectProperty<>(Compiler.Language.V1);
    private SyntaxHighlighting syntaxHighlighting = new SyntaxHighlightingV1();
    private Pattern pattern;

    public CodeEditor() {
        // show line numbers
        setParagraphGraphicFactory(LineNumberFactory.get(this));

        // replace tab with four spaces
        final InputMap<KeyEvent> im = InputMap.consume(
                EventPattern.keyPressed(KeyCode.TAB),
                e -> replaceSelection("    ")
        );
        Nodes.addInputMap(this, im);

        // recompute the syntax highlighting 500 ms after user stops editing area
        // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
        // multi plain changes = save computation by not rerunning the code multiple times
        //   when making multiple changes (e.g. renaming a method at multiple parts in file)
        multiPlainChanges()
                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(100))
                // run the following code block when previous stream emits an event
                .subscribe(ignore -> setStyleSpans(0, computeHighlighting(getText())));

        addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        getStylesheets().add(getClass().getResource("codeeditor.css").toExternalForm());

        this.syntax.addListener(
                (prop, old, val) -> {
                    switch (val) {
                        case V1:
                            this.syntaxHighlighting = new SyntaxHighlightingV1();
                            break;
                        case V2:
                            this.syntaxHighlighting = new SyntaxHighlightingV2();
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported language " + val);
                    }
                    this.pattern = null;
                    setStyleSpans(0, computeHighlighting(getText()));
                });
    }

    public ObjectProperty<Compiler.Language> syntaxProperty() {
        return this.syntax;
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            int caretPosition = getCaretPosition();
            int currentParagraph = getCurrentParagraph();
            final Matcher matcher = PATTERN_WHITESPACE.matcher(
                    getParagraph(currentParagraph - 1).getSegments().get(0));
            if (matcher.find()) {
                Platform.runLater(() -> insertText(caretPosition, matcher.group()));
            }
        }
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        final Matcher matcher = ensurePattern().matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;

        while(matcher.find()) {
            final String styleClass = this.syntaxHighlighting.getStyleClass(matcher);
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private Pattern ensurePattern() {
        if (this.pattern == null) {
            this.pattern = this.syntaxHighlighting.getPattern();
        }
        return this.pattern;
    }

    private interface SyntaxHighlighting {
        Pattern getPattern();
        String getStyleClass(Matcher matcher);
    }

    private static class SyntaxHighlightingV1 implements SyntaxHighlighting {
        private static final String[] KEYWORDS = new String[] {
                "and", "or", "not", "abs", "sin", "cos",
                "if", "tan", "asin", "acos", "goto", "unless", "def",
                "gosub", "endsub", "trunc",
        };
        private static final String[] REGISTERS = new String[] {
                "AIM", "RADAR", "X", "Y", "DAMAGE", "SPEEDX", "SPEEDY", "SHOT", "RANDOM", "OUT",
        };

        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String LABEL_PATTERN = "[a-zA-Z_][0-9a-zA-Z_]*:";
        private static final String REGISTER_PATTERN = "\\b(" + String.join("|", REGISTERS) + ")\\b";
        private static final String GOES_TO_PATTERN = "->";
        private static final String NUMBER_PATTERN = "\\b\\d+(\\.\\d*)?\\b";
        private static final String COMMENT_PATTERN = "//[^\n]*";

        @Override
        public Pattern getPattern() {
            return Pattern.compile(
                    "(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
                    "|(?<LABEL>" + LABEL_PATTERN + ")" +
                    "|(?<REGISTER>" + REGISTER_PATTERN + ")" +
                    "|(?<GOESTO>" + GOES_TO_PATTERN + ")" +
                    "|(?<NUMBER>" + NUMBER_PATTERN + ")" +
                    "|(?<COMMENT>" + COMMENT_PATTERN + ")");
        }

        @Override
        public String getStyleClass(Matcher matcher) {
            return matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("LABEL") != null ? "label" :
                    matcher.group("REGISTER") != null ? "register" :
                    matcher.group("GOESTO") != null ? "goesTo" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; // never happens
        }
    }

    private static class SyntaxHighlightingV2 implements SyntaxHighlighting {
        private static final String[] KEYWORDS = new String[] {
                "and", "or", "not", "abs", "sin", "cos",
                "if", "tan", "asin", "acos", "def",
                "return", "while", "else", "true", "false", "exit"
        };

        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String LABEL_PATTERN = "\\b(state|yield)\\b";
        private static final String REGISTER_PATTERN = "@\\w+\\b";
        private static final String NUMBER_PATTERN = "\\b\\d+(\\.\\d*)?\\b";
        private static final String COMMENT_PATTERN = "//[^\n]*";

        @Override
        public Pattern getPattern() {
            return Pattern.compile(
                    "(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
                    "|(?<LABEL>" + LABEL_PATTERN + ")" +
                    "|(?<REGISTER>" + REGISTER_PATTERN + ")" +
                    "|(?<NUMBER>" + NUMBER_PATTERN + ")" +
                    "|(?<COMMENT>" + COMMENT_PATTERN + ")");
        }

        @Override
        public String getStyleClass(Matcher matcher) {
            return matcher.group("KEYWORD") != null ? "keyword" :
                matcher.group("LABEL") != null ? "label" :
                matcher.group("REGISTER") != null ? "register" :
                matcher.group("NUMBER") != null ? "number" :
                matcher.group("COMMENT") != null ? "comment" :
                null; // never happens
        }
    }
}
