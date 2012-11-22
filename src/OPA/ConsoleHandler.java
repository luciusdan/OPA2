/*
 * Diese Klasse erstellt einen Thread, indem auf einen Stream gehocht wird und
 * dieser in ein JTextPane geschrieben wird.
 */
package OPA;

import java.awt.Color;
import java.util.Scanner;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;

/**
 * Klasse zum verbinden eines Stream zu einem JTextPane
 * @author Dirk
 */
public class ConsoleHandler extends Thread {
    private JEditorPane console;
    private Scanner scanner;
    private Color color;
    
    ConsoleHandler( Scanner scanner, JEditorPane console){
        this.console = console;
        this.scanner = scanner;
        this.color = Color.BLACK;
    }
    
    ConsoleHandler( Scanner scanner, JEditorPane console,Color color){
        this.console = console;
        this.scanner = scanner;
        this.color = color;
    }


    @Override
    public void run(){
        if(scanner != null){
            while(scanner.hasNext()){
                String line = scanner.next();
                write(line, color);
            }
            System.out.println("Scanner end.");
        }
    }
    
    public JEditorPane getConsole(){
        return console;
    }
    
    public void write(String nextLine, Color color){
        write(nextLine, color, new String[0]);
    }
    
    public synchronized void write(final String nextLine, final Color color, final String[] attributes){
            SwingUtilities.invokeLater(new Runnable() {
            @Override 
                public void run() {
                    String text = console.getText();
                    text = text.split("<body>")[1];
                    text = (text.split("</body>"))[0];
                    String colorString = "<font color=\"#"+colorName(color)+"\">";

                    int lrf = text.lastIndexOf(colorString);
                    int lf = text.lastIndexOf("<font color");
                    if(lrf != -1 && lrf == lf){
                    text = text.substring(0 ,text.length()-7);
                    }else{
                        text += colorString;
                    }
                    text += colorString;
                    String newLine = nextLine.replace("&", "&amp");
                    newLine = newLine.replace("<", "&lt");
                    newLine = newLine.replace(">", "&gt");
                    newLine = newLine.replace("\n", "<br>");
                    for(String attribute : attributes){
                        if(!attribute.equals("<br>")){
                            newLine = attribute+newLine;
                            attribute = attribute.replaceFirst("<", "</");
                        }
                            newLine += attribute;
                    }
                    text +=newLine+"</font>";
                    console.setText(text);
                    
                    console.revalidate();
                }
            });
    }
    
    private String colorName(Color color){
        String hexString = Integer.toHexString(color.getRGB());
        hexString =hexString.substring(2);
        return hexString;
    }
}