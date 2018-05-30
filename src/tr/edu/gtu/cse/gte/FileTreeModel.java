package tr.edu.gtu.cse.gte;

import java.io.File;
import java.util.ArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author segilmez
 *
 * /**
 * The methods in this class allow the JTree component to traverse the file
 * system tree and display the files and directories.
 *
 */
class FileTreeModel implements TreeModel {

    /**
     * listener for tree model changing
     */
    private final ArrayList<TreeModelListener> mListeners = new ArrayList<>();
    /**
     * We specify the root directory when we create the model.
     */
    protected FileHelper root;

    /**
     *
     * @param root
     */
    public FileTreeModel(File root) {
        FileHelper myFile = new FileHelper(root);
        this.root = myFile;
    }

    /**
     * The model knows how to return the root object of the tree
     *
     * @return
     */
    @Override
    public Object getRoot() {
        return root;
    }

    /**
     * Tell JTree whether an object in the tree is a leaf
     *
     * @param node
     * @return
     */
    @Override
    public boolean isLeaf(Object node) {
        return !((FileHelper) node).isDirectory();
    }

    /**
     * Tell JTree how many children a node has
     *
     * @param parent
     * @return
     */
    @Override
    public int getChildCount(Object parent) {
        return ((FileHelper) parent).listFiles().length;
    }

    /**
     * Fetch any numbered child of a node for the JTree. Our model returns File
     * objects for all nodes in the tree. The JTree displays these by calling
     * the File.toString() method.
     */
    @Override
    public Object getChild(Object parent, int index) {
        return ((FileHelper) parent).listFiles()[index];

    }

    /**
     * Figure out a child's position in its parent node.
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        final FileHelper[] files = ((FileHelper) parent).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i] == child) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method is invoked by the JTree only for editable trees. This
     * TreeModel does not allow editing, so we do not implement this method. The
     * JTree editable property is false by default.
     */
    @Override
    public void valueForPathChanged(final TreePath pPath, final Object pNewValue) {

    }

    /**
     * Since this is not an editable tree model, we never fire any events, so we
     * don't actually have to keep track of interested listeners
     */
    @Override
    public void addTreeModelListener(final TreeModelListener pL) {
        mListeners.add(pL);
    }

    /**
     *
     * @param pL
     */
    @Override
    public void removeTreeModelListener(final TreeModelListener pL) {
        mListeners.remove(pL);
    }

    /**
     * taken from
     * http://developer.classpath.org/doc/javax/swing/tree/DefaultTreeModel-source.html
     *
     * <p>
     * Invoke this method if you've modified the TreeNodes upon which this model
     * depends. The model will notify all of its listeners that the model has
     * changed. It will fire the events, necessary to update the layout caches
     * and repaint the tree. The tree will <i>not</i> be properly refreshed if
     * you call the JTree.repaint instead.
     * </p>
     * <p>
     * This method will refresh the information about whole tree from the root.
     * If only part of the tree should be refreshed, it is more effective to
     * call {@link #reload(TreeNode)}.
     * </p>
     */
    public void reload() {
        // Need to duplicate the code because the root can formally be
        // no an instance of the TreeNode.
        final int n = getChildCount(getRoot());
        final int[] childIdx = new int[n];
        final Object[] children = new Object[n];

        for (int i = 0; i < n; i++) {
            childIdx[i] = i;
            children[i] = getChild(getRoot(), i);
        }

        fireTreeStructureChanged(this, new Object[]{getRoot()}, childIdx, children);
    }

    /**
     * taken from
     * http://developer.classpath.org/doc/javax/swing/tree/DefaultTreeModel-source.html
     *
     * fireTreeStructureChanged
     *
     * @param source the node where the model has changed
     * @param path the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     */
    protected void fireTreeStructureChanged(final Object source, final Object[] path, final int[] childIndices, final Object[] children) {
        final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        for (final TreeModelListener l : mListeners) {
            l.treeStructureChanged(event);
        }
    }
}

/**
 * This class take File object and perform special operations.
 * @author segilmez
 */
class FileHelper {

    private final File file;

    public FileHelper(final File f) {
        file = f;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public FileHelper[] listFiles() {
        final File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        if (files.length < 1) {
            return new FileHelper[0];
        }

        final FileHelper[] arr = new FileHelper[files.length];
        for (int i = 0; i < arr.length; i++) {
            final File f = files[i];
            arr[i] = new FileHelper(f);
        }
        return arr;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getName();
    }
}
