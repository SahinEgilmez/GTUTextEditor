package tr.edu.gtu.cse.gte;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Handles code highlighting for the text in the document.
 *
 * @author enes
 */
public class GTEDocumentFilter extends DocumentFilter {

    // default style attributes
    private final SimpleAttributeSet defaultAttributes;

    // language keywords for highlighting
    private final List<String> keywords;

    // text pane that is filtered by this filter
    private final JTextPane textPane;

    public GTEDocumentFilter(List<String> keywords, JTextPane textPane) {
        this.keywords = keywords;
        this.textPane = textPane;

        // create default attribute set for editor
        defaultAttributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(defaultAttributes, "Monospaced");
        StyleConstants.setFontSize(defaultAttributes, 12);
    }

    /**
     * Refines the text typed in by user. Preserves indentation level if
     * user adds a newline. Increases indentation level if user enters a block
     * and decreases indentation level if user exits a block.
     *
     * @param fb filter bypass for getting text in document
     * @param offset offset of change
     * @param str changed text
     * @return refined text
     */
    private String refineText(FilterBypass fb, int offset, String str) {
        // if user entered a new line
        if (str.equals("\n")) {
            // get the text in the document from start to 'offset'
            String text = null;
            try {
                text = fb.getDocument().getText(0, offset);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }

            // extract last line
            int startOfLine = text.lastIndexOf("\n") + 1;
            String lastLine = text.substring(startOfLine, offset);

            // find current indentation
            Pattern precedingWhitespacePattern = Pattern.compile("\\s*");
            Matcher matcher = precedingWhitespacePattern.matcher(lastLine);

            matcher.find();

            // preserve current indentation level
            String currentIndentation = matcher.group();
            str = "\n" + currentIndentation;

            // if user entering a block increase indentation level
            String enterBlockPattern = ".*\\{\\s*\\z";
            String exitBlockPattern = ".*\\}\\s*\\z";
            if (lastLine.matches(enterBlockPattern)) {
                str += currentIndentation + "\t";
            }
            // if user exits a block decrease indentation level
            else if (lastLine.matches(exitBlockPattern)) {
                int indentationLength = currentIndentation.length();

                // check if line length is sufficient for substring operation
                if (indentationLength > 5) {  // 4 + 1 for '\n'
                    str += currentIndentation
                            .substring(0, indentationLength - 4);
                }
                else {
                    str = "\n";
                }
            }
        }

        // replace tabs with four spaces for consistency
        str = str.replace("\t", "    ");

        return str;
    }

    /**
     * Resets all text styling to default.
     *
     * @param fb filter bypass for getting the document
     */
    private void resetStyles(FilterBypass fb) {
        StyledDocument styledDoc = (StyledDocument) fb.getDocument();

        // reset all styling by replacing character attributes with defaults
        styledDoc.setCharacterAttributes(0, styledDoc.getLength(),
                defaultAttributes, true);
    }

    /**
     * Highlights function names in the text. Gets the text in the document
     * then searches for function names. Highlights function names by
     * changing the attribute sets of the matches.
     *
     * @param fb
     * @throws BadLocationException
     */
    private void highlightFunctionNames(FilterBypass fb) throws BadLocationException {
        AbstractDocument doc = (AbstractDocument) fb.getDocument();

        // create an attribute set for keywords
        SimpleAttributeSet keywordAttrs =
                new SimpleAttributeSet(defaultAttributes);
        StyleConstants.setForeground(keywordAttrs, new Color(151, 51, 151));

        // get the text in the document
        String text = doc.getText(0, doc.getLength());

        // create a regex pattern and matcher for function names
        String functionNamePattern = "\\b([\\w\\.::]*)\\(.*?\\)";

        Pattern pattern = Pattern.compile(functionNamePattern);
        Matcher matcher = pattern.matcher(text);

        // find all function names and change their attributes
        int start, end, len;
        String match;

        while (matcher.find()) {
            start = matcher.start(1);
            end   = matcher.end(1);
            len   = end - start;
            match = matcher.group(1);

            super.replace(fb, start, len, match, keywordAttrs);
        }
    }

    /**
     * Highlights C&C++ keywords in the text. Gets the text in the document
     * then searches for keywords provided by the Editor. Highlights keywords
     * by changing the attribute sets of the matches.
     *
     * @param fb filter bypass for getting document
     * @throws BadLocationException
     */
    private void highlightKeywords(FilterBypass fb) throws BadLocationException {
        AbstractDocument doc = (AbstractDocument) fb.getDocument();

        // create an attribute set for keywords
        SimpleAttributeSet keywordAttrs =
                new SimpleAttributeSet(defaultAttributes);
        StyleConstants.setForeground(keywordAttrs, Color.BLUE);

        // get the text in the document
        String text = doc.getText(0, doc.getLength());

        // create a regex pattern and matcher by using keywords
        String keywordsPattern = "\\b(" + keywords.get(0);
        for (int i = 1; i < keywords.size(); i += 1) {
            keywordsPattern += "|" + keywords.get(i);
        }
        keywordsPattern += ")\\b";

        Pattern pattern = Pattern.compile(keywordsPattern);
        Matcher matcher = pattern.matcher(text);

        // find all keywords and change their attributes
        int start, end, len;
        String match;

        while (matcher.find()) {
            start = matcher.start(1);
            end   = matcher.end(1);
            len   = end - start;
            match = matcher.group(1);

            super.replace(fb, start, len, match, keywordAttrs);
        }
    }

    /**
     * Highlights comments in the text. Gets the text in the document then
     * searches for comments. Highlights comments by changing the attribute
     * sets of the matches.
     *
     * @param fb filter bypass for getting document
     * @throws BadLocationException
     */
    private void highlightComments(FilterBypass fb)
            throws BadLocationException {
        AbstractDocument doc = (AbstractDocument) fb.getDocument();

        // create an attribute set for comments
        SimpleAttributeSet keywordAttrs =
                new SimpleAttributeSet(defaultAttributes);
        StyleConstants.setForeground(keywordAttrs, Color.GRAY);

        // get the text in the document
        String text = doc.getText(0, doc.getLength());

        // create a regex pattern and matcher
        String commentsPattern = "//.*?\n|/\\*.*?\\*/";

        Pattern pattern = Pattern.compile(commentsPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        // find all comments and change their attributes
        int start, end, len;
        String match;

        while (matcher.find()) {
            start = matcher.start();
            end   = matcher.end();
            len   = end - start;
            match = matcher.group();

            super.replace(fb, start, len, match, keywordAttrs);
        }
    }

    /**
     * Highlights strings in the text. Gets the text in the document then
     * searches for strings (namely text between single quotes and double
     * quotes). Highlights comments by changing the attribute sets of the
     * matches.
     *
     * @param fb filter bypass for getting document
     * @throws BadLocationException
     */
    private void highlightStrings(FilterBypass fb)
            throws BadLocationException {
        AbstractDocument doc = (AbstractDocument) fb.getDocument();

        // create an attribute set for strings
        SimpleAttributeSet keywordAttrs =
                new SimpleAttributeSet(defaultAttributes);
        StyleConstants.setForeground(keywordAttrs, new Color(151, 151, 51));

        // get the text in the document
        String text = doc.getText(0, doc.getLength());

        // create a regex pattern and matcher
        String commentsPattern = "'*?'|\".*?\"";

        Pattern pattern = Pattern.compile(commentsPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        // find all strings and change their attributes
        int start, end, len;
        String match;

        while (matcher.find()) {
            start = matcher.start();
            end   = matcher.end();
            len   = end - start;
            match = matcher.group();

            super.replace(fb, start, len, match, keywordAttrs);
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string,
            AttributeSet attr) throws BadLocationException {
        string = refineText(fb, offset, string);
        super.insertString(fb, offset, string, attr);
        int caretPos = textPane.getCaretPosition();
        resetStyles(fb);
        highlightFunctionNames(fb);
        highlightKeywords(fb);
        highlightComments(fb);
        highlightStrings(fb);
        textPane.setCaretPosition(caretPos);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {

        text = refineText(fb, offset, text);
        super.replace(fb, offset, length, text, attrs);
        int caretPos = textPane.getCaretPosition();
        resetStyles(fb);
        highlightFunctionNames(fb);
        highlightKeywords(fb);
        highlightComments(fb);
        highlightStrings(fb);
        textPane.setCaretPosition(caretPos);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        super.remove(fb, offset, length);
        int caretPos = textPane.getCaretPosition();
        resetStyles(fb);
        highlightFunctionNames(fb);
        highlightKeywords(fb);
        highlightComments(fb);
        highlightStrings(fb);
        textPane.setCaretPosition(caretPos);
    }
}
