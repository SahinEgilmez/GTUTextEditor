
package tr.edu.gtu.cse.gte;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 * Kullanım :
 * IOHelper helper = iohelpers.get(activeTabIndex);
 * String path=helper.getPath();
 * FileStructure fileStructures= new FileStructure();
 * FileTreeModel model=fileStructures.getModel(path);
 * fileStructure.setModel(model);
 * @author cengo
 */
public class FileStructure {
    public FileTreeModel getModel(String pathString){
        File root=new File(pathString);
        root= new File(root.getParent());
        
        //root = new File(System.getProperty("user.home"));
        System.out.println(root.getPath());
        // Create a TreeModel object to represent our tree of files
        FileTreeModel model = new FileTreeModel(root);
        
        return model;
    }
}

