package tr.edu.gtu.cse.gte;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 *
 * @author cengo
 */
public class IOHelper {
    private File file;
    private  JFileChooser dialog;
    private Component parent;
    public String getPath() {
        if (file != null)
            return file.getAbsolutePath();
        return null;
    }
    public IOHelper(Component parent) {
        file = null;
        dialog = new JFileChooser();
        this.parent = parent;
        dialog.setCurrentDirectory(new File(System.getProperty("user.home")));
    }
    void save(ArrayList<String> lines) {
        if (file == null) {
            int result = dialog.showSaveDialog(parent);
            if (result ==  JFileChooser.APPROVE_OPTION ) {
               file = dialog.getSelectedFile();
            }
        }
        
        if (file != null) {
            try{
                FileWriter writer = new FileWriter(file, false);
                for (String line : lines) {
                    writer.append(line);
                    writer.append("\n");
                }
                writer.close();
            }catch(IOException param){
                param.printStackTrace();
            }
        }
        
    }
    
    void saveAs(ArrayList<String> lines) {

        int result = dialog.showSaveDialog(parent);
        if (result ==  JFileChooser.APPROVE_OPTION ) {
           file = dialog.getSelectedFile();
        }
        if (file != null) {      
            try{
                FileWriter writer = new FileWriter(file, false);
                for (String line : lines) {
                    writer.append(line);
                    writer.append("\n");
                }
                writer.close();
            }catch(IOException param){
                param.printStackTrace();
            }
        }
    }
    
    public ArrayList<String> open() {
        /*show the user directory */
        int result = dialog.showOpenDialog(parent);
        /*wheather file is selected or not */
        if (result ==  JFileChooser.APPROVE_OPTION ) {
           /* bring the selected file*/
            file = dialog.getSelectedFile();
        }
        /*if file is selected , it is read contents of file.*/
        if (file != null) {      
           
            return read(file.getAbsolutePath());
        }
        return null;
    }
    public static ArrayList<String> read(String path) {
        
        try{
            String line;
            ArrayList<String> total_lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while((line=reader.readLine())!=null)
                total_lines.add(line + "\n");
            return total_lines;
        } catch(IOException param) {
            param.printStackTrace();
             return null;
        }
    }
}
