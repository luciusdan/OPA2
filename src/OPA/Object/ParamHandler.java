package OPA.Object;

import OPA.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JButton;

/**
 *
 * @author Dirk
 */
public class ParamHandler {
   
    public enum TestType{
        COMPILE,EXECUTE,OTHER
    }
    private String trenner ="@@";
    
    public ParamHandler(String fileName , ObjectHandler objHandler){
        LinkedList<JButton> buttons= new LinkedList<JButton>();
        try {
            FileReader fr = new FileReader(fileName+".set");
            BufferedReader br = new BufferedReader(fr);  
            try {
                String line = br.readLine();
                //Kommentare am Anfang überspringen
                while(line.startsWith("//")){
                    line = br.readLine();
                }
                //Kompiliertyp auslesen
            } catch (IOException ex) {
                String[] attr = {"<p>"};
            }
        } catch (FileNotFoundException ex) {

        }
    }
    
}

