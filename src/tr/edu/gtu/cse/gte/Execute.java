/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.edu.gtu.cse.gte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

    public static Process execute(String currentDir, JTextArea textArea, String... args) {

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(new File(currentDir));

        pb.redirectOutput(new File(executionLogFile));
        pb.redirectError(new File(executionLogFile));

        for (String a : args) {
            System.out.println(a);
        }

        Process process = null;
        try {
            process = pb.start();

            process.waitFor();

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
                fstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch(IOException ex) {
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
        } catch(InterruptedException ex) {
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
        }


        return process;
    }



}
