package OPA;

import OPA.Object.ObjectHandler;
import OPA.Output.ConsoleHandler;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Dirk
 */
public class IOHandler {
   
    public enum TestType{
        COMPILE,EXECUTE,OTHER
    }
    private TestType testType;
    private static String trenner ="@@";
    ConsoleHandler consoleHandler;
    private LinkedList<String[]> ios;
    
    public IOHandler(String fileName, ObjectHandler objHandler, ConsoleHandler consoleHandler){
        ios= new LinkedList<String[]>();
        this.consoleHandler = consoleHandler;
        try {
            FileReader fr = new FileReader(objHandler.getData("OOPS_PROGRAMM_PATH_IN").getStringValue()+System.getProperty("file.separator")+fileName+".io");
            BufferedReader br = new BufferedReader(fr);  
            try {
                String line = br.readLine();
                //Kommentare am Anfang überspringen
                while(line.startsWith("//")){
                    line = br.readLine();
                }
                //Kompiliertyp auslesen
                String testTypeString = "TESTTYPE := ";
                if (line.startsWith(testTypeString)){
                    String typeString = line.substring(testTypeString.length(), line.length());
                    if(typeString.toUpperCase().equals("COMPILE")){
                        testType = TestType.COMPILE;
                    }else if(typeString.toUpperCase().equals("EXECUTE")){
                        testType = TestType.EXECUTE;
                    }else{
                        testType = TestType.OTHER;
                    }
                }else{
                    consoleHandler.write("IO ERROR: ", Color.CYAN.darker(), true);
                    consoleHandler.write("kann ErrorType von "+fileName+".io nicht lesen!\n",Color.RED);
                }
                line = br.readLine();
                if(testType != TestType.OTHER){
                    while(line != null){
                        if(!line.startsWith("//")){
                            String[] lineFields = line.split(trenner);
                            if(lineFields.length ==2&&(lineFields[1].equals("VALID") ||lineFields[1].equals("ERROR"))){
                                    String[] newTest = {lineFields[0],lineFields[1],""};
                                    ios.add(newTest);
                            }else
                            if(lineFields.length == 3&&(lineFields[1].equals("VALID") ||lineFields[1].equals("ERROR"))){
                                ios.add(lineFields);
                            }else{
                                String out = "";
                                for (String s : lineFields){
                                    out += ", "+s;
                                }
                               consoleHandler.write("IO ERROR :",Color.CYAN.darker(),true);
                               consoleHandler.write("kann Zeile nicht verstehen:",Color.RED);
                               consoleHandler.write(out+"\n",Color.ORANGE);
                            }
                        }
                        line = br.readLine();
                    }
               
                }

            } catch (IOException ex) {
                consoleHandler.write("IO ERROR: ",Color.CYAN.darker(),true);
                consoleHandler.write("kann "+fileName+".io nicht öffnen.\n",Color.RED);
            }
        } catch (FileNotFoundException ex) {
            consoleHandler.write("IO ERROR: ",Color.CYAN.darker(),true);
            consoleHandler.write ("kann "+fileName+".io nicht finden.\n",Color.RED);
        }
    }
    
    public TestType getTestType(){
        return testType;
    }

    public String[]getTests(){
        String[] tests = new String[ios.size()];
        for(int i=0; i< ios.size(); i++){
            tests[i] = ios.get(i)[0];
        }
        return tests;
    }
    
   public void checkTest(String in, Scanner os) throws IOException{
       //Testfall hohlen
        int index = 0;
        for(;index< ios.size(); index++){
            if(ios.get(index)[0].equals(in)){
                break;
            }
        }
        //in Zeilen aufteilen
        String[] lines= ios.get(index)[2].split("\\\\n ");
        
        //Schreibe Testheader
        consoleHandler.write("Test("+(index+1)+")["+in+"]: ",Color.BLACK,true);
        
        //wird keine Ausgabe erwartet?
        if(ios.get(index)[2].equals("")){
            if(os.hasNext()){
                consoleHandler.write("FEHLER",Color.CYAN.darker(),true);
                consoleHandler.write("erwarte keine Ausgabe, bekomme aber:\n",Color.RED.darker());
                consoleHandler.addScanner(os);
            }else{
                consoleHandler.write("Erfolgreich\n",Color.BLACK);
            }
        }else
        if(ios.get(index)[1].equals("ERROR")){
        System.out.println("ERROR Test");    
            for(String line: lines){
                String isLine = os.nextLine();
                consoleHandler.write("bekomme:\" "+isLine+"\" erwarte: \""+line+"\"",Color.BLACK);
            }
        }else{
            System.out.println("VALID Test");
            boolean errors = false;
            LinkedList<String> correct = new LinkedList<String>();
            for(String line: lines){
                try{
                    String isLine= os.nextLine();
                    if(isLine.equals(line)){
                        correct.add(line);
                    }else{
                        consoleHandler.write("FEHLER\n",Color.RED,true);
                        consoleHandler.write("erwarte:",Color.MAGENTA);
                        for(String lime : lines){
                            consoleHandler.write(lime+"\n",Color.BLACK);
                        }
                        consoleHandler.write("bekomme:\n",Color.MAGENTA);
                        for(String cLine :correct){
                            consoleHandler.write(cLine,Color.BLACK);
                        }
                        consoleHandler.write(isLine,Color.RED);
                        consoleHandler.addScanner(os);
                        errors = true;
                        break;
                    }
                }catch(NoSuchElementException e){
                //TODO was passiert wenn zuwenig zeilen
                }

            }
            if(!errors){
                consoleHandler.write("Erfolgreich\n",Color.BLACK);
            }
            
        }
        
    }
}

