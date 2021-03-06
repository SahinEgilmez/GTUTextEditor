package tr.edu.gtu.cse.gte;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledDocument;

/**
 *
 * @author enes
 */
public class Editor extends javax.swing.JFrame {

    final String KEYWORDS_FILE = "keywords";
    final String SNIPPETS_FILE = "snippets";
    final String SNIPPET_SEP = "'0?-18\"|\\@5~9|,@*^7=";
    private List<String> keywords;
    private List<JTextPane> panes = new ArrayList<>();
    private HashMap<String, String> snippets = new HashMap<>();
    private List<IOHelper> iohelpers = new ArrayList<>();
    private SuggestionManager sm = new SuggestionManager(new ArrayList<String>());

    private ArrayList<Stack<String>> histories = new ArrayList<>();
    private ArrayList<Stack<Integer>> cursors = new ArrayList<>();

    private ArrayList<String> lastTextes = new ArrayList<>();
    private ArrayList<Integer> lastCursor = new ArrayList<>();

    /**
     * Creates new form Editor
     */
    public Editor() {
        initComponents();
        loadKeywords();
        loadSnippets();
        addANewTab();
        takeBackUp();

        //These actions come from the default editor kit.
        //Get the ones we want and stick them in the menu.
        menuItemEditCut.setAction(new DefaultEditorKit.CutAction());
        menuItemEditCut.setText("Cut");
        menuItemEditCopy.setAction(new DefaultEditorKit.CopyAction());
        menuItemEditCopy.setText("Copy");
        menuItemEditPaste.setAction(new DefaultEditorKit.PasteAction());
        menuItemEditPaste.setText("Paste");

        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panes.get(0).requestFocusInWindow();
    }

    /**
     * Adds a new tab and selects the new created tab.
     */
    private void addANewTab() {
        JTextPane tempPane = new JTextPane();
        setDocumentFilter(tempPane);
        histories.add(new Stack<String>());
        cursors.add(new Stack<Integer>());
        lastTextes.add("");
        lastCursor.add(0);

        tempPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                int activeTabIndex = tabbedPane.getSelectedIndex();
                JTextPane activeTextPane = panes.get(activeTabIndex);

                int caretPos = activeTextPane.getCaretPosition();
                AbstractDocument doc
                        = (AbstractDocument) activeTextPane.getDocument();

                String text = null;
                String fullText = null;

                try {
                    text = doc.getText(0, caretPos);
                    fullText = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                }

                // extract last line
                int startOfLine = text.lastIndexOf("\n") + 1;
                String lastLine = text.substring(startOfLine, caretPos);

                if (lastLine.trim().length() > 1) {
                    // create a regex pattern and matcher for current word
                    String currentWordPattern = ".*\\b(\\w+)";

                    Pattern pattern = Pattern.compile(currentWordPattern);
                    Matcher matcher = pattern.matcher(lastLine);

                    matcher.find();

                    String currentWord = matcher.group(1);

                    String[] linesArray = fullText.split("\n");

                    ArrayList<String> lines = new ArrayList<>();

                    for (String line : linesArray) {
                        lines.add(line);
                    }

                    sm.update(lines);

                    ArrayList<String> suggestions = sm.search(currentWord);

                    suggestionList.setModel(new ListModel<String>() {
                        @Override
                        public int getSize() {
                            return suggestions.size();
                        }

                        @Override
                        public String getElementAt(int index) {
                            return suggestions.get(index);
                        }

                        @Override
                        public void addListDataListener(ListDataListener l) {}

                        @Override
                        public void removeListDataListener(ListDataListener l) {}
                    });

                    MouseListener[] mListeners
                            = suggestionList.getMouseListeners();

                    for (int i = 2; i < mListeners.length; i += 1) {
                        suggestionList.removeMouseListener(mListeners[i]);
                    }

                    suggestionList.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                doc.replace(
                                        caretPos - currentWord.length(),
                                        currentWord.length(),
                                        suggestionList.getSelectedValue(),
                                        null);

                                int activeTabIndex
                                        = tabbedPane.getSelectedIndex();
                                JTextPane activeTextPane
                                        = panes.get(activeTabIndex);

                                activeTextPane.requestFocusInWindow();

                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {}

                        @Override
                        public void mouseReleased(MouseEvent e) {}

                        @Override
                        public void mouseEntered(MouseEvent e) {}

                        @Override
                        public void mouseExited(MouseEvent e) {}
                    });
                }
            }
        });

        LineNumber lineNumber = new LineNumber(tempPane);
        lineNumber.setPreferredSize(2);

        panes.add(tempPane);

        JScrollPane scrollPane = new JScrollPane(tempPane);

        scrollPane.setRowHeaderView(lineNumber);

        tabbedPane.addTab("new_file", scrollPane);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        IOHelper tempHelper = new IOHelper(this);
        iohelpers.add(tempHelper);
    }

    /**
     * Loads C&C++ keywords from KEYWORDS_FILE.
     */
    private void loadKeywords() {
        try {
            keywords = Files.readAllLines(Paths.get(KEYWORDS_FILE),
                    StandardCharsets.UTF_8);
        } catch (IOException ignore) {
        }
    }

    /**
     * Sets document filter to our special implementation GTEDocumentFilter.
     */
    private void setDocumentFilter(JTextPane textPane) {
        StyledDocument styledDoc = textPane.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) styledDoc;
            doc.setDocumentFilter(new GTEDocumentFilter(keywords, textPane));
        } else {
            System.err.println("Text pane's document"
                    + " isn't an AbstractDocument!");
            System.exit(1);
        }
    }

    /**
     * Loads snippets from SNIPPETS_FILE.
     */
    private void loadSnippets() {
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(SNIPPETS_FILE), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] pair = line.split(":");
                snippets.put(pair[0], pair[1].replace(SNIPPET_SEP, "\n"));
            }
        } catch (IOException ignore) {
        }
    }

    /**
     * Saves snippets to SNIPPETS_FILE.
     */
    private void saveSnippets() {
        List<String> lines = new ArrayList<>();
        for (String key : snippets.keySet()) {
            String line = key + ":" + snippets.get(key).replace("\n", SNIPPET_SEP);
            lines.add(line);
        }
        try {
            Files.write(Paths.get(SNIPPETS_FILE), lines);
        } catch (IOException ignore) {
        }
    }

    private ArrayList<String> extractLines() {
        // get active tab index
        int activeTabIndex = tabbedPane.getSelectedIndex();

        ArrayList<String> lines = new ArrayList<>();

        // get active tab and AbstractDocument
        JTextPane activeTextPane = panes.get(activeTabIndex);
        AbstractDocument doc = (AbstractDocument) activeTextPane.getDocument();
        String text = null;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        String[] linesArray = text.split("\n");

        for (String line : linesArray) {
            lines.add(line);
        }

        return lines;
    }

    synchronized void takeBackUp() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                for ( ; ; ) {

                    int activeTabIndex = tabbedPane.getSelectedIndex();
                    if (activeTabIndex != -1 ) {

                        JTextPane activeTextPane = panes.get(activeTabIndex);
                        if ((histories.get(activeTabIndex).isEmpty() ||
                            !histories.get(activeTabIndex).peek().equals(activeTextPane.getText()))
                              &&  !activeTextPane.getText().equals("")) {

                            histories.get(activeTabIndex).push(activeTextPane.getText());
                            cursors.get(activeTabIndex).push(activeTextPane.getCaretPosition());
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        fileStructure = new javax.swing.JTree();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        suggestionList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItemFileNew = new javax.swing.JMenuItem();
        menuItemFileOpen = new javax.swing.JMenuItem();
        menuItemFileOpenInDir = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemFileSave = new javax.swing.JMenuItem();
        menuItemFileSaveAs = new javax.swing.JMenuItem();
        menuItemFileClose = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemFileExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuItemEditUndo = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuItemEditCut = new javax.swing.JMenuItem();
        menuItemEditCopy = new javax.swing.JMenuItem();
        menuItemEditPaste = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuItemRunRunFile = new javax.swing.JMenuItem();
        menuItemRunGenCode = new javax.swing.JMenuItem();
        menuItemRunRemCode = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        menuItemSnippetsSaveSnippet = new javax.swing.JMenuItem();
        menuItemSnippetsLoadSnippet = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        menuItemIncsShowDeps = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        menuItemAboutAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GTU Text Editor");

        jSplitPane2.setDividerLocation(220);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.8);
        jSplitPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSplitPane2.setOneTouchExpandable(true);

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setResizeWeight(0.1);
        jSplitPane1.setOneTouchExpandable(true);

        tabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabbedPaneMouseClicked(evt);
            }
        });
        jSplitPane1.setRightComponent(tabbedPane);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        fileStructure.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        fileStructure.setAutoscrolls(true);
        fileStructure.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fileStructure.setDebugGraphicsOptions(javax.swing.DebugGraphics.BUFFERED_OPTION);
        fileStructure.setEditable(true);
        fileStructure.setPreferredSize(new java.awt.Dimension(120, 0));
        fileStructure.setRootVisible(false);
        fileStructure.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                fileStructureValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(fileStructure);

        jSplitPane1.setLeftComponent(jScrollPane4);

        jSplitPane2.setLeftComponent(jSplitPane1);

        jSplitPane3.setDividerLocation(150);
        jSplitPane3.setResizeWeight(0.1);

        jScrollPane1.setViewportView(suggestionList);

        jSplitPane3.setLeftComponent(jScrollPane1);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextArea1.setEnabled(false);
        jTextArea1.setFocusable(false);
        jScrollPane3.setViewportView(jTextArea1);

        jSplitPane3.setRightComponent(jScrollPane3);

        jSplitPane2.setBottomComponent(jSplitPane3);

        jMenu1.setText("File");

        menuItemFileNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileNew.setText("New");
        menuItemFileNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileNewActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileNew);

        menuItemFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileOpen.setText("Open");
        menuItemFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileOpenActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileOpen);

        menuItemFileOpenInDir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileOpenInDir.setText("Open In Directory");
        menuItemFileOpenInDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileOpenInDirActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileOpenInDir);
        jMenu1.add(jSeparator1);

        menuItemFileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileSave.setText("Save");
        menuItemFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileSaveActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileSave);

        menuItemFileSaveAs.setText("Save As");
        menuItemFileSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileSaveAs);

        menuItemFileClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFileClose.setText("Close");
        menuItemFileClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileCloseActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileClose);
        jMenu1.add(jSeparator2);

        menuItemFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        menuItemFileExit.setText("Exit");
        menuItemFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFileExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemFileExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        menuItemEditUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        menuItemEditUndo.setText("Undo");
        menuItemEditUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditUndoActionPerformed(evt);
            }
        });
        jMenu2.add(menuItemEditUndo);
        jMenu2.add(jSeparator3);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Find");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Replace");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);
        jMenu2.add(jSeparator4);

        menuItemEditCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        menuItemEditCut.setText("Cut");
        jMenu2.add(menuItemEditCut);

        menuItemEditCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        menuItemEditCopy.setText("Copy");
        jMenu2.add(menuItemEditCopy);

        menuItemEditPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        menuItemEditPaste.setText("Paste");
        jMenu2.add(menuItemEditPaste);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Run");

        menuItemRunRunFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        menuItemRunRunFile.setText("Run File");
        menuItemRunRunFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRunRunFileActionPerformed(evt);
            }
        });
        jMenu3.add(menuItemRunRunFile);

        menuItemRunGenCode.setText("Generate Debug Code");
        menuItemRunGenCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRunGenCodeActionPerformed(evt);
            }
        });
        jMenu3.add(menuItemRunGenCode);

        menuItemRunRemCode.setText("Remove All Debug Codes");
        menuItemRunRemCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRunRemCodeActionPerformed(evt);
            }
        });
        jMenu3.add(menuItemRunRemCode);

        jMenuBar1.add(jMenu3);

        jMenu6.setText("Snippets");

        menuItemSnippetsSaveSnippet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSnippetsSaveSnippet.setText("Save Snippet");
        menuItemSnippetsSaveSnippet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSnippetsSaveSnippetActionPerformed(evt);
            }
        });
        jMenu6.add(menuItemSnippetsSaveSnippet);

        menuItemSnippetsLoadSnippet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSnippetsLoadSnippet.setText("Load Snippet");
        menuItemSnippetsLoadSnippet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSnippetsLoadSnippetActionPerformed(evt);
            }
        });
        jMenu6.add(menuItemSnippetsLoadSnippet);

        jMenuBar1.add(jMenu6);

        jMenu4.setText("Includes");

        menuItemIncsShowDeps.setText("Show Dependencies");
        menuItemIncsShowDeps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemIncsShowDepsActionPerformed(evt);
            }
        });
        jMenu4.add(menuItemIncsShowDeps);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("About");

        menuItemAboutAbout.setText("About");
        menuItemAboutAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutAboutActionPerformed(evt);
            }
        });
        jMenu5.add(menuItemAboutAbout);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemFileExitActionPerformed

    private void menuItemSnippetsSaveSnippetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSnippetsSaveSnippetActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        JTextPane activeTextPane = panes.get(activeTabIndex);

        String value = activeTextPane.getSelectedText();
        String key = JOptionPane.showInputDialog("Enter key for this snippet");

        if (key != null) {
            snippets.put(key, value);
            saveSnippets();
        }
    }//GEN-LAST:event_menuItemSnippetsSaveSnippetActionPerformed

    private void menuItemSnippetsLoadSnippetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSnippetsLoadSnippetActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        JTextPane activeTextPane = panes.get(activeTabIndex);

        int caretPos = activeTextPane.getCaretPosition();
        StyledDocument doc = activeTextPane.getStyledDocument();

        String key = JOptionPane.showInputDialog("Enter key for this snippet");

        try {
            doc.insertString(caretPos, snippets.getOrDefault(key, ""), null);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_menuItemSnippetsLoadSnippetActionPerformed

    private void menuItemFileNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileNewActionPerformed
        addANewTab();
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        String path = helper.getPath();
        if (path != null) {
            FileStructure fileStructures = new FileStructure();
            FileTreeModel model = fileStructures.getModel(path);
            fileStructure.setModel(model);
        }
    }//GEN-LAST:event_menuItemFileNewActionPerformed

    private void menuItemFileCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileCloseActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        if (activeTabIndex != -1) {
            tabbedPane.remove(activeTabIndex);
            panes.remove(activeTabIndex);
            histories.remove(activeTabIndex);
            cursors.remove(activeTabIndex);
            iohelpers.remove(activeTabIndex);
            activeTabIndex = tabbedPane.getSelectedIndex();

            if (activeTabIndex != -1) {
                IOHelper helper = iohelpers.get(activeTabIndex);
                String path = helper.getPath();
                if (path != null) {
                    FileStructure fileStructures = new FileStructure();
                    FileTreeModel model = fileStructures.getModel(path);
                    fileStructure.setModel(model);
                } else {
                    fileStructure.setModel(new FileTreeModel(null));
                }
            } else {
                fileStructure.setModel(new FileTreeModel(null));
            }
        }
    }//GEN-LAST:event_menuItemFileCloseActionPerformed

    private void menuItemFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileOpenActionPerformed
        addANewTab();

        // open a file and read its contents
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        ArrayList<String> lines = helper.open();

        if (lines != null) {
            try {
                String text = "";
                // build text
                for (String line : lines) {
                    text += line;
                }

                JTextPane activeTextPane = panes.get(activeTabIndex);
                tabbedPane.setTitleAt(activeTabIndex, helper.getFileName());

                // insert text
                AbstractDocument doc
                        = (AbstractDocument) activeTextPane.getDocument();
                doc.insertString(0, text, null);


                lastTextes.set(activeTabIndex, text);
                lastCursor.set(activeTabIndex, activeTextPane.getCaretPosition());

            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            String path = helper.getPath();
            if (path != null) {
                FileStructure fileStructures = new FileStructure();
                FileTreeModel model = fileStructures.getModel(path);
                fileStructure.setModel(model);
            }
        } else {
            panes.remove(activeTabIndex);
            iohelpers.remove(activeTabIndex);
            tabbedPane.remove(activeTabIndex);
        }
    }//GEN-LAST:event_menuItemFileOpenActionPerformed

    private void menuItemFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileSaveActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        ArrayList<String> lines = extractLines();

        helper.save(lines);

        tabbedPane.setTitleAt(activeTabIndex, helper.getFileName());

        // update file structure
        String path = helper.getPath();
        if (path != null) {
            FileStructure fileStructures = new FileStructure();
            FileTreeModel model = fileStructures.getModel(path);
            fileStructure.setModel(model);
        } else {
            fileStructure.setModel(null);
        }
    }//GEN-LAST:event_menuItemFileSaveActionPerformed

    private void menuItemFileSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileSaveAsActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        ArrayList<String> lines = extractLines();

        helper.saveAs(lines);

        tabbedPane.setTitleAt(activeTabIndex, helper.getFileName());

        // update file structure
        String path = helper.getPath();
        if (path != null) {
            FileStructure fileStructures = new FileStructure();
            FileTreeModel model = fileStructures.getModel(path);
            fileStructure.setModel(model);
        } else {
            fileStructure.setModel(null);
        }
    }//GEN-LAST:event_menuItemFileSaveAsActionPerformed

    private void menuItemRunRunFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRunRunFileActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        jTextArea1.setText("");
        if (helper.getPath() != null) {
            Process compile = null;
            if (helper.getFileName().endsWith(".c")) {
                compile =
                    Execute.execute(".", jTextArea1, "gcc", helper.getPath());
            } else if (helper.getFileName().endsWith(".cpp")) {
                compile =
                    Execute.execute(".", jTextArea1, "g++", helper.getPath());
            }
            else {
                jTextArea1.setText("Error: editor cannot run this file");
            }
            if (compile != null && compile.exitValue() == 0) {
                Process p = Execute.execute(".", jTextArea1, "./a.out");
                jTextArea1.append("\nProcess exit value: " + p.exitValue());
                try {
                    Files.delete(Paths.get("./a.out"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            jTextArea1.setText("Error: please save the file first");
        }
    }//GEN-LAST:event_menuItemRunRunFileActionPerformed

    private void tabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        String path = helper.getPath();
        if (path != null) {
            FileStructure fileStructures = new FileStructure();
            FileTreeModel model = fileStructures.getModel(path);
            fileStructure.setModel(model);
        } else {
            fileStructure.setModel(null);
        }
    }

    private void fileStructureValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_fileStructureValueChanged
        String path = ( (FileHelper) fileStructure.getSelectionPath().getPathComponent(1)).getFile().getAbsolutePath();

        addANewTab();
        // open a file and read its contents
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);
        ArrayList<String> lines = helper.open(path);


        if (lines != null) {
            try {
                String text = "";
                // build text
                for (String line : lines) {
                    text += line;
                }

                JTextPane activeTextPane = panes.get(activeTabIndex);
                tabbedPane.setTitleAt(activeTabIndex, helper.getFileName());

                // insert text
                AbstractDocument doc
                        = (AbstractDocument) activeTextPane.getDocument();
                doc.insertString(0, text, null);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            if (path != null) {
                FileStructure fileStructures = new FileStructure();
                FileTreeModel model = fileStructures.getModel(path);
                fileStructure.setModel(model);
            }
        } else {
            panes.remove(activeTabIndex);
            iohelpers.remove(activeTabIndex);
            tabbedPane.remove(activeTabIndex);
        }
    }//GEN-LAST:event_fileStructureValueChanged

    private void menuItemEditUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditUndoActionPerformed
        // TODO add your handling code here:
        int activeTabIndex = tabbedPane.getSelectedIndex();
        JTextPane activeTextPane = panes.get(activeTabIndex);


        if (!histories.get(activeTabIndex).isEmpty())
            histories.get(activeTabIndex).pop();

        if (!cursors.get(activeTabIndex).isEmpty())
            cursors.get(activeTabIndex).pop();


        if (!histories.get(activeTabIndex).isEmpty())
            activeTextPane.setText(histories.get(activeTabIndex).pop());

        if (!cursors.get(activeTabIndex).isEmpty())
            activeTextPane.setCaretPosition(cursors.get(activeTabIndex).pop());

        else {
            activeTextPane.setText(lastTextes.get(activeTabIndex));
            activeTextPane.setCaretPosition(lastCursor.get(activeTabIndex));

        }


    }//GEN-LAST:event_menuItemEditUndoActionPerformed

    /**
     * @param args the command line argumentste
     * Generate debug code.
     * @param evt
     */
    private void menuItemRunGenCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRunGenCodeActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        JTextPane activeTextPane = panes.get(activeTabIndex);
        IOHelper helper = iohelpers.get(activeTabIndex);
        int cursorPos = activeTextPane.getCaretPosition();
        String text = activeTextPane.getText();

        int start = text.lastIndexOf("\n", cursorPos);
        int end = text.indexOf("\n", cursorPos);
        String line = text.substring(start, end);

        // find strings and remove
        Pattern strs = Pattern.compile("'.*?'|\".*?\"");
        Matcher matcher = strs.matcher(line);

        while (matcher.find()) {
            line = line.substring(0,
                    matcher.start()) + line.substring(matcher.end()+1);
        }

        // find function names and remove
        strs = Pattern.compile("\\b([\\w\\.::]*)\\(.*?\\)");
        matcher = strs.matcher(line);

        while (matcher.find()) {
            line = line.substring(0,
                    matcher.start(1)) + line.substring(matcher.end(1)+1);
        }

        // find variable names
        ArrayList<String> variableNames = new ArrayList<>();
        Pattern vars = Pattern.compile("\\w+");
        matcher = vars.matcher(line);

        while (matcher.find()) {
            String varName = matcher.group();
            if (!keywords.contains(varName))
                variableNames.add(varName);
        }

        String debugLine = "";
        // find current indentation
        Pattern precedingWhitespacePattern = Pattern.compile("\\s*");
        matcher = precedingWhitespacePattern.matcher(line);

        matcher.find();

        // preserve current indentation level
        String currentIndentation = matcher.group();

        debugLine += currentIndentation;

        if (helper.getFileName().endsWith(".c")) {
            debugLine += "fprinf(stderr, \"";
            for (String name : variableNames) {
                debugLine += name + ": %d, ";
            }
            debugLine = debugLine.substring(0, debugLine.length()-2);
            debugLine += "\", ";
            for (String name : variableNames) {
                debugLine += name + ", ";
            }
            debugLine = debugLine.substring(0, debugLine.length()-2);
            debugLine += ");  // debug\n";
        }
        else if (helper.getFileName().endsWith(".cpp")) {
            debugLine += "std::cout << \"";
            for (String name : variableNames) {
                debugLine += name + ": \" << " + name + " << \" ";
            }
            debugLine = debugLine.substring(0, debugLine.length()-3);
            debugLine += " std::endl;  // debug\n";
        }

        int cursor = activeTextPane.getCaretPosition();
        activeTextPane.setText(text.substring(0, start) + debugLine +
                text.substring(start+1));
        activeTextPane.setCaretPosition(cursor);

    }//GEN-LAST:event_menuItemRunGenCodeActionPerformed

    private void menuItemRunRemCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRunRemCodeActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        JTextPane activeTextPane = panes.get(activeTabIndex);
        ArrayList<String> lines = extractLines();

        String text = "";

        for (String line : lines) {
            if (!line.endsWith("// debug"))
                text += line + "\n";
        }

        int cursor = activeTextPane.getCaretPosition();
        activeTextPane.setText(text);
        activeTextPane.setCaretPosition(cursor);
    }//GEN-LAST:event_menuItemRunRemCodeActionPerformed

    private void menuItemIncsShowDepsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemIncsShowDepsActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);

        Includes.showIncludes(helper.getPath());
    }//GEN-LAST:event_menuItemIncsShowDepsActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
         JTextField findWhat = new JTextField();
         JTextField replaceWith = new JTextField();
         Object[] fields = {"Find What : ",findWhat,
                            "Replace With : ", replaceWith};

         JOptionPane.showConfirmDialog(null,fields,"Find & Replace",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
         String findKey = findWhat.getText();
         String targetValue = replaceWith.getText();

         int activeTabIndex = tabbedPane.getSelectedIndex();
                JTextPane activeTextPane = panes.get(activeTabIndex);

                int caretPos = activeTextPane.getCaretPosition();
                AbstractDocument doc =
                        (AbstractDocument) activeTextPane.getDocument();

                String text = null;
                String fullText = null;

                try {
                    text = doc.getText(0, caretPos);
                    fullText = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                }



         String changedText = replace(fullText,findKey,targetValue);
         activeTextPane.setText(changedText);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
         int i = 0;

        JTextField findWhat = new JTextField();
         int option = JOptionPane.showConfirmDialog(null,findWhat,"Find & Replace",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
         String targetValue = findWhat.getText();

        int activeTabIndex = tabbedPane.getSelectedIndex();
                JTextPane activeTextPane = panes.get(activeTabIndex);

                int caretPos = activeTextPane.getCaretPosition();
                AbstractDocument doc =
                        (AbstractDocument) activeTextPane.getDocument();

                String text = null;
                String fullText = null;

                try {
                    text = doc.getText(0, caretPos);
                    fullText = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                }

          ArrayList<Integer> indexes =  find(fullText,targetValue);
         if(indexes == null)
             JOptionPane.showMessageDialog(null,
    "0 result Found",
    "Couldn't Find",
    JOptionPane.ERROR_MESSAGE);
        while(option == JOptionPane.OK_OPTION){
            if(i>=indexes.size())
                i=0;
              activeTextPane.setSelectionStart(indexes.get(i));
              activeTextPane.setSelectionEnd(indexes.get(i)+targetValue.length());
          ++i;
          String oldValue = targetValue;
           option = JOptionPane.showConfirmDialog(null,findWhat,"Find & Replace",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
           targetValue = findWhat.getText();
           if(oldValue!=targetValue){
               indexes = find(targetValue);
               if(indexes == null){
             JOptionPane.showMessageDialog(null,
            "0 result Found",
            "Couldn't Find",
             JOptionPane.ERROR_MESSAGE);
               return;}
           }
        }
        //JTextComponent.setSelectionStart(int), JTextComponent.setSelectionEnd(int)

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void menuItemFileOpenInDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileOpenInDirActionPerformed
        int activeTabIndex = tabbedPane.getSelectedIndex();
        IOHelper helper = iohelpers.get(activeTabIndex);

        if (helper.getPath() != null) {
            String key = JOptionPane.showInputDialog("Enter a file name");

            File tempFile = new File(helper.getPath());
            File parentDir = tempFile.getParentFile();

            BinarySearchTree<String> bst = new BinarySearchTree<>();
            for (String fileName : parentDir.list()) {
                bst.add(fileName);
            }

            String targetFile = bst.find(key);
            if (targetFile != null) {
                File tf = new File(parentDir, targetFile);
                String path = tf.getPath();
                addANewTab();
                // open a file and read its contents
                activeTabIndex = tabbedPane.getSelectedIndex();
                helper = iohelpers.get(activeTabIndex);
                ArrayList<String> lines = helper.open(path);


                if (lines != null) {
                    try {
                        String text = "";
                        // build text
                        for (String line : lines) {
                            text += line;
                        }

                        JTextPane activeTextPane = panes.get(activeTabIndex);
                        tabbedPane.setTitleAt(activeTabIndex, helper.getFileName());

                        // insert text
                        AbstractDocument doc
                                = (AbstractDocument) activeTextPane.getDocument();
                        doc.insertString(0, text, null);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    if (path != null) {
                        FileStructure fileStructures = new FileStructure();
                        FileTreeModel model = fileStructures.getModel(path);
                        fileStructure.setModel(model);
                    }
                } else {
                    panes.remove(activeTabIndex);
                    iohelpers.remove(activeTabIndex);
                    tabbedPane.remove(activeTabIndex);
                }
            }
        }

    }//GEN-LAST:event_menuItemFileOpenInDirActionPerformed

    private void menuItemAboutAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutAboutActionPerformed
        JFrame frame = new JFrame("About");

        JTextArea aboutText = new JTextArea("Bu proje Gebze Teknik Üniversitesi Bilgisayar Mühendisliği\n"
                + "Veri Yapıları ve Algoritmalar dersi kapsamında\n"
                + "geliştirilmiştir. Proje metin düzenleyicisi olup bütün\n"
                + "metin dosyalarını ve programlama dilleri olarak da\n"
                + "C ve C++ dillerini desteklemektedir.\n\n"
                + "Bu projenin  amacı öncelikle veri yapılarını uygun olan\n"
                + "yerlerde kullanarak bir proje geliştirilmesidir.\n"
                + "Ayrıca programlamaya yeni başlamış olan geliştiricilerin\n"
                + "hızlı ve daha kolay program yazabilmelerini sağlamaktır.\n\n"
                + "Emeği geçenler:\n"
                + "Enes GÖNÜLTAŞ\n"
                + "Bayram Utku UZUNLAR\n"
                + "Şahin EĞİLMEZ\n"
                + "Efkan DURAKLI\n"
                + "Burak ÖZDEMİR\n"
                + "Cengiz TOPRAK\n"
        );
        aboutText.setEditable(false);
        frame.getContentPane().add(aboutText);
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_menuItemAboutAboutActionPerformed

    private ArrayList<Integer> find(String word){
            int activeTabIndex = tabbedPane.getSelectedIndex();
                JTextPane activeTextPane = panes.get(activeTabIndex);

                int caretPos = activeTextPane.getCaretPosition();
                AbstractDocument doc =
                        (AbstractDocument) activeTextPane.getDocument();

                String text = null;
                String fullText = null;

                try {
                    text = doc.getText(0, caretPos);
                    fullText = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                }
                return find(fullText,word);
    }
    /**
     * This function find all indexes of word in document.
     * @param document source for finding all indexes of word.
     * @param word will be find its of all indexes in document.
     * @return an array list of all indexes of given word in document.
     */
    private static  ArrayList<Integer> find(String document,String word) {
        ArrayList<Integer> allIndex = new ArrayList<>();
        int index = document.indexOf(word, 0);
        while(index > -1) {
            allIndex.add(index);
            index =  document.indexOf(word,index + word.length());
        }
        if (allIndex.size() > 0)
            return allIndex;
        return null;
    }
    /**
     * This function replace replaced string into all word strings in document.
     * @param document
     * @param word
     * @param replaced
     * @return changed string.
     */
    private static String replace(String document,String word,String replaced) {
        String newString = new String("");
        ArrayList<Integer> indexes = find(document, word);
        int start = 0;
        Character character;
        if (indexes == null) return document;
        for (Integer index : indexes) {
            for (int i = start; i < index; ++i) {
                character = document.charAt(i);
                newString += character.toString();
            }
            newString += replaced;
            start = index + word.length();
        }
        newString += document.substring(start);
        return newString;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Editor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree fileStructure;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JMenuItem menuItemAboutAbout;
    private javax.swing.JMenuItem menuItemEditCopy;
    private javax.swing.JMenuItem menuItemEditCut;
    private javax.swing.JMenuItem menuItemEditPaste;
    private javax.swing.JMenuItem menuItemEditUndo;
    private javax.swing.JMenuItem menuItemFileClose;
    private javax.swing.JMenuItem menuItemFileExit;
    private javax.swing.JMenuItem menuItemFileNew;
    private javax.swing.JMenuItem menuItemFileOpen;
    private javax.swing.JMenuItem menuItemFileOpenInDir;
    private javax.swing.JMenuItem menuItemFileSave;
    private javax.swing.JMenuItem menuItemFileSaveAs;
    private javax.swing.JMenuItem menuItemIncsShowDeps;
    private javax.swing.JMenuItem menuItemRunGenCode;
    private javax.swing.JMenuItem menuItemRunRemCode;
    private javax.swing.JMenuItem menuItemRunRunFile;
    private javax.swing.JMenuItem menuItemSnippetsLoadSnippet;
    private javax.swing.JMenuItem menuItemSnippetsSaveSnippet;
    private javax.swing.JList<String> suggestionList;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
