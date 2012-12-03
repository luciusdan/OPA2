/*
 * Diese Klasse erstellt einen Thread, indem auf einen Stream gehocht wird und
 * dieser in ein JTextPane geschrieben wird.
 */
package OPA.Output;

import java.awt.Color;
import java.util.Scanner;

/**
 * Klasse zum verbinden eines Stream zu einem JTextPane
 * @author Dirk
 */
public class ScannerHandler extends Thread {
    private ConsoleHandler console;
    private Scanner scanner;
    private Color color;
    private Boolean closed;
    
    ScannerHandler( Scanner scanner, ConsoleHandler master){
        this(scanner, master, Color.BLACK);
    }
    
    ScannerHandler( Scanner scanner, ConsoleHandler master,Color color){
        this.console = master;
        this.scanner = scanner;
        this.color = color;
        this.closed = false;
    }

    public void close(){
        closed= false;
        scanner.close();
    }
    
    @Override
    public void run(){
        if(scanner != null){
            while(scanner.hasNext() && !closed){
                final String line = scanner.next();
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {                
                        console.write(line, color);
                    }
                });
                
            }
            console.removeScanner(this);
            System.out.println("Scanner end.");
        }
    }
}