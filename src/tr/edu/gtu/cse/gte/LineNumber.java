
package tr.edu.gtu.cse.gte;

import java.awt.*;
import javax.swing.*;
 
/**
 * This class is taken from internet 
 * References : https://community.oracle.com/thread/1393939
 * 
 */
public class LineNumber extends JComponent
{
     private final static Color DEFAULT_BACKGROUND = new Color(213, 213, 234);
     private final static Color DEFAULT_FOREGROUND = Color.white;
     private final static Font DEFAULT_FONT = new Font("arial", Font.PLAIN, 11);
     
     // LineNumber height (abends when I use MAX_VALUE)
     private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
     
     // Set right/left margin
     private final static int MARGIN = 5;
     
     // Line height of this LineNumber component
     private int lineHeight;
     
     // Line height of this LineNumber component
     private int fontLineHeight;
     
     // With of the LineNumber component
     private int currentRowWidth;
     
     // Metrics of this LineNumber component
     private FontMetrics fontMetrics;
     
     /**
      * Convenience constructor for Text Components
      */
     public LineNumber(JComponent component)
     {
          if (component == null)
          {
               setBackground( DEFAULT_BACKGROUND );
               setForeground( DEFAULT_FOREGROUND );
               setFont( DEFAULT_FONT );
          }
          else
          {
               setBackground( DEFAULT_BACKGROUND );
               setForeground( DEFAULT_FOREGROUND );
               setFont( component.getFont() );
          }
          
          setPreferredSize( 99 );
     }
     
     public void setPreferredSize(int row)
     {
          int width = fontMetrics.stringWidth( String.valueOf(row) );
          
          if (currentRowWidth < width)
          {
               currentRowWidth = width;
               setPreferredSize( new Dimension(2 * MARGIN + width, HEIGHT) );
          }
     }
     
     public void setFont(Font font)
     {
          super.setFont(font);
          fontMetrics = getFontMetrics( getFont() );
          fontLineHeight = fontMetrics.getHeight();
     }
     
     /**
      * The line height defaults to the line height of the font for this
      * component. The line height can be overridden by setting it to a
      * positive non-zero value.
      */
     public int getLineHeight()
     {
          if (lineHeight == 0)
               return fontLineHeight;
          else
               return lineHeight;
     }
     
     public void setLineHeight(int lineHeight)
     {
          if (lineHeight > 0)
               this.lineHeight = lineHeight;
     }
     
     public int getStartOffset()
     {
          return 4;
     }
     
     public void paintComponent(Graphics g)
     {
           int lineHeight = getLineHeight();
           int startOffset = getStartOffset();
           Rectangle drawHere = g.getClipBounds();

           g.setColor( getBackground() );
           g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

           g.setColor( getForeground() );
           int startLineNumber = (drawHere.y / lineHeight) + 1;
           int endLineNumber = startLineNumber + (drawHere.height / lineHeight);

           int start = (drawHere.y / lineHeight) * lineHeight + lineHeight - startOffset;

           for (int i = startLineNumber; i <= endLineNumber; i++)
           {
           String lineNumber = String.valueOf(i);
           int width = fontMetrics.stringWidth( lineNumber );
           g.drawString(lineNumber, MARGIN + currentRowWidth - width, start);
           start += lineHeight;
           }

           setPreferredSize( endLineNumber );
     }
}