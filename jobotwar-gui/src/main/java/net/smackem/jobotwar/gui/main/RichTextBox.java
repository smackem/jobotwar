package net.smackem.jobotwar.gui.main;

import org.fxmisc.richtext.InlineCssTextArea;

public class RichTextBox extends InlineCssTextArea {

    public void appendText(String text, String inlineCss) {
        final int end = getText().length();
        replace(end, end, text, inlineCss);
        showParagraphAtBottom(getParagraphs().size() - 1);
    }

    @Override
    public void appendText(String text) {
        appendText(text, "-fx-fill: #c0c0c0");
    }
}
