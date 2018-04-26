
package tr.edu.gtu.cse.gte;

import java.io.File;

/**
 * @author cengo
 */
public class FileStructure {
    public FileTreeModel getModel(String pathString){
        File root=new File(pathString);
        root= new File(root.getParent());

        // Create a TreeModel object to represent our tree of files
        FileTreeModel model = new FileTreeModel(root);

        return model;
    }
}

