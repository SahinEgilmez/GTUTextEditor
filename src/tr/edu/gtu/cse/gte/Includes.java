/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.edu.gtu.cse.gte;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author enes
 */
public class Includes extends JDialog {

    public static Graph<String> buildDepGraph(String filePath) {
        Graph<String> g = new Graph<>();
        File tempFile = new File(filePath);
        File parentDir = tempFile.getParentFile();

        // add vertices
        for (String fileName : parentDir.list()) {
            if (fileName.endsWith(".c")
                    || fileName.endsWith(".cpp")
                    || fileName.endsWith(".h")
                    || fileName.endsWith(".hpp")) {
                g.addVertex(fileName, fileName);
            }
        }

        for (File file : parentDir.listFiles()) {
            String fileName = file.getName();
            String path = file.getAbsolutePath();
            if (fileName.endsWith(".c")
                    || fileName.endsWith(".cpp")
                    || fileName.endsWith(".h")
                    || fileName.endsWith(".hpp")) {

                ArrayList<String> lines = IOHelper.read(path);
                Pattern strs = Pattern.compile("#include [<\"](.+)[>\"]");

                for (String line : lines) {
                    // find include lines and add edges to the graph
                    Matcher matcher = strs.matcher(line);

                    if (matcher.find()) {
                        String match = matcher.group(1);
                        g.addVertex(match, match);
                        g.addEdge(fileName, match, true);
                    }
                }
            }
        }

        return g;
    }

    public static void showIncludes(String filePath) {
        Graph<String> g = buildDepGraph(filePath);

        VisualizationViewer vs =
                new VisualizationViewer(
                        new CircleLayout(g),
                        new Dimension(800, 600));

        vs.getRenderContext().setVertexLabelTransformer(Object::toString);

        final DefaultModalGraphMouse<String,Number> graphMouse =
                new DefaultModalGraphMouse<String,Number>();
        vs.setGraphMouse(graphMouse);
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

        JFrame frame = new JFrame("Includes");
        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);
    }
}
