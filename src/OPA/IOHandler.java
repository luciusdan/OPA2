package OPA;

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
    private String trenner ="@@";
    ConsoleHandler consoleHandler;
    private LinkedList<String[]> ios;
    
    public IOHandler(String fileName , ConfigHandler cfgHandler, ConsoleHandler consoleHandler){
        ios= new LinkedList<String[]>();
        this.consoleHandler = consoleHandler;
        try {
            FileReader fr = new FileReader(cfgHandler.getAttribute("OIP")+System.getProperty("file.separator")+fileName+".io");
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
                    String[] attr = {"<p>"};
                    consoleHandler.write("IO ERROR: kann ErrorType von "+fileName+".io nicht lesen!",Color.RED,attr);
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
                                String[] attr = {"<p>"};
                               consoleHandler.write("IO ERROR: kann Zeile nicht verstehen:",Color.RED,attr);
                               consoleHandler.write(out,Color.ORANGE);
                            }
                        }
                        line = br.readLine();
                    }
                }

            } catch (IOException ex) {
                String[] attr = {"<p>"};
                consoleHandler.write("IO ERROR: kann "+fileName+".io nicht öffnen.",Color.RED,attr);
            }
        } catch (FileNotFoundException ex) {
            String[] attr = {"<p>"};
            consoleHandler.write("IO ERROR: kann "+fileName+".io nicht finden.",Color.RED,attr);
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
        String[] attr = {"<p>"};
        consoleHandler.write("Test("+(index+1)+")["+in+"]: ",Color.BLACK,attr);
        System.out.println("-------- Test("+(index+1)+"):"+in+" --------");
        
        //wird keine Ausgabe erwartet?
        if(ios.get(index)[2].equals("")){
            if(os.hasNext()){
                ConsoleHandler errorConsole= new ConsoleHandler(os,consoleHandler.getConsole(), Color.BLACK);
                String[] attr1 = {"<p>"};
                errorConsole.write("FEHLER",Color.RED,attr1);
                String[] attr2 = {"<br>"};
                consoleHandler.write("erwarte keine Ausgabe, bekomme aber:",Color.MAGENTA,attr2);
                errorConsole.start();
            }else{
                consoleHandler.write("Erfolgreich",Color.BLACK);
            }
        }else
        if(ios.get(index)[1].equals("ERROR")){
        System.out.println("ERROR Test");    
            for(String line: lines){
                System.out.print("want:"+line);
                String isLine = os.nextLine();
                System.out.println(" become:"+isLine);
                consoleHandler.write("bekomme:\" "+isLine+"\" erwarte: \""+line+"\"",Color.BLACK);
            }
        }else{
            System.out.println("VALID Test");
            boolean errors = false;
            for(String line: lines){
                System.out.print("want:"+line);
                try{
                    String isLine= os.nextLine();
                System.out.println(" become:"+isLine);
                if(isLine.equals(line)){
                    System.out.println("No div");
                }else{
                    System.out.println("Find div!");
                    String[] attr1 = {"<p>"};
                    consoleHandler.write("FEHLER",Color.RED,attr1);
                    String[] attr2 = {"<br>"};
                    consoleHandler.write("erwarte:",Color.MAGENTA,attr2);
                    for(String lime : lines){
                    consoleHandler.write(lime,Color.BLACK,attr2);
                    }
                    consoleHandler.write("bekomme:",Color.MAGENTA,attr2);
                    ConsoleHandler errorConsole= new ConsoleHandler(os,consoleHandler.getConsole(), Color.BLACK);
                    errorConsole.write(isLine+"\n",Color.BLACK);
                    errorConsole.start();
                    errors = true;
                    break;
                }
                }catch(NoSuchElementException e){
                //TODO was passiert wenn zuwenig zeilen
                }

            }
            if(!errors){
                consoleHandler.write("Erfolgreich",Color.BLACK);
            }
            
        }
        
    }
}

