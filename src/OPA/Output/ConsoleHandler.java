/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA.Output;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Dirk
 */
public class ConsoleHandler {
    private JTextPane console;
    
    private LinkedList<ScannerHandler> scanner;

    public ConsoleHandler(JTextPane pane){
        this.console = pane;
        this.scanner = new LinkedList<ScannerHandler>();
    }
    
    public void clear(){
        for(ScannerHandler s : scanner){
            s.close();
        }
        console.setStyledDocument(new DefaultStyledDocument( new StyleContext()));
    }
    
    public void write(String text) {
        write(text,Color.BLACK,false);
    }
    
    public void write(String text, Color color) {
        write(text,color,false);
    }
        
    public void write(final String text, Color front, Boolean bold){
        final Style style = console.addStyle("color", null);
        StyleConstants.setForeground(style, front);
        if(bold){
        StyleConstants.setBold(style, true);    
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override 
            public void run() {
                try {
                    int start = console.getStyledDocument().getLength();
                    console.getStyledDocument().insertString(start, text, style);
                    console.getStyledDocument().setCharacterAttributes(start, text.length(), style, false);
                } catch (BadLocationException ex) {
                    Logger.getLogger(ConsoleHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }
    
    public void addScanner(Scanner scanner){
        addScanner(scanner, Color.BLACK);
    }
    
    public void addScanner(Scanner scanner, Color color){
        ScannerHandler sh = new ScannerHandler(scanner,this,color);
        this.scanner.add(sh);
        sh.start();
    }
    
    public void removeScanner(ScannerHandler scanner){
        this.scanner.remove(scanner);
    }
    
}
