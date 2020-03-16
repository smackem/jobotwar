package net.smackem.jobotwar.gui.main;

import org.fxmisc.richtext.InlineCssTextArea;

public class RichTextBox extends InlineCssTextArea {

    public void appendText(String text, String inlineCss) {
        final int end = getText().length();
        replace(end, end, text, inlineCss);
    }
}
