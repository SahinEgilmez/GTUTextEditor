/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.edu.gtu.cse.gte;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author efkandurakli
 */
public class Execute {
    
    private static final String executionLogFile = "execution.log";
    public static boolean isFinish = false;
    
    public Execute(String filePath) {
        workingDirectory = filePath;
    }
    
    
    public static Process execute(String currentDir, JTextArea textArea, String... args) {
        
        
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(new File(currentDir));
        
        pb.redirectOutput(new File(executionLogFile));
        pb.redirectError(new File(executionLogFile));
        
        Process process = null;
        try {
            process = pb.start();
            
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = false;
                    while (true) {
                        FileInputStream fstream = null;
                        try {
                            
                            BufferedReader reader;
                            String line = null;
                            fstream = new FileInputStream(executionLogFile);
                            reader = new BufferedReader(new InputStreamReader(fstream));
                            StringBuilder text = new StringBuilder();
                            while ((line = reader.readLine()) != null)
                                text.append(line + System.lineSeparator());
                            textArea.setText(text.toString());
                        } catch (FileNotFoundException e) {
                            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, e);
                        } catch (IOException ex) {
                            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                fstream.close();
                            } catch(IOException e) {
                                Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                        try {
                            Thread.sleep(5000);
                        } catch(InterruptedException e) {
                            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, e);
                        }
                        if (flag)
                            break;
                        if (isFinish)
                            flag = true;
                    }
                }
            });
            
            process.waitFor();
            
            
        } catch(IOException ex) {
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
        } catch(InterruptedException ex) {
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
        }


        return process;
    }
    
    
 
}
