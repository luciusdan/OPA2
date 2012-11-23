/**
 * In dieser Klasse werden alle Prozesse erzeugt, die mit dem konvertieren
 * in Ausführbaren Code zutun haben.
 * Also vom kompilieren von: Compiler, VM und den OOPS-Pogramm
 * @author Dirk
 */
package OPA;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JEditorPane;

/**
 * In dieser Klasse werden alle Prozesse erzeugt, die mit dem konvertieren
 * in Ausführbaren Code zutun haben.
 * Also vom kompilieren von: Compiler, VM und den OOPS-Pogramm.
 * @author Dirk
 */
public class CompileHandler {
    //Objekte zum Darstellen der Ausgabe
    private JEditorPane jPane;
    private ConsoleHandler outputConsole;
    private ConsoleHandler errorConsole;
    private ConsoleHandler deadConsole;
    
    //Objekte zum Kompilieren
    private ConfigHandler cfgHandler;
    private ProcessBuilder builder;
    private LinkedList<String> oopsFiles = new LinkedList<String>();

   /**
    * Konstruktor
    * @param cfgHandler cfgHandler, zum laden und setzen von Attributen.
    * @param jPane Swing-Objekt zum darstellen der Console.
    * @param files OPPS-Programm Liste, die mit ExecHandler geteilt wird.
    */
    public CompileHandler(ConfigHandler cfgHandler, JEditorPane jPane, LinkedList<String> files){
        this.cfgHandler = cfgHandler; 
        this.jPane = jPane;
        this.oopsFiles = files;
        this.builder = new ProcessBuilder();
        this.deadConsole = new ConsoleHandler(null, jPane, Color.RED);
    }
    
    /**
     * Funktion zum ausführen eines Kommandos.
     * @param cmd Kommando das ausgeführt werden soll.
     */
    private void compile(String fileName,LinkedList<String> cmd) {
        Process compileProcess;
        String cmdOut= "";
        builder.command(cmd);
        for(String str: cmd){
            cmdOut += " "+str;
        }
        try {
            compileProcess = builder.start();
            Scanner es = new Scanner( compileProcess.getErrorStream()).useDelimiter( "\\Z" );
            Scanner os = new Scanner( compileProcess.getInputStream()).useDelimiter( "\\Z" );
            outputConsole = new ConsoleHandler(os, jPane, Color.ORANGE);
            errorConsole = new ConsoleHandler(es, jPane, Color.RED);
            String[] attr = {"<p>","<b>"};
            if(cfgHandler.getAttributeBoolean("FULL")){
                outputConsole.write("COMPILE:"+cmdOut,Color.BLACK,attr);
            }else{
                outputConsole.write("COMPILE:"+fileName,Color.BLACK,attr);
            }
            if(os.hasNext()){
                outputConsole.start();
            }
            if(es.hasNext()){
                errorConsole.start();
            }
        } catch (IOException e) {
             String[] attr = {"<p>"};
            if(cfgHandler.getAttributeBoolean("FULL")){
                deadConsole.write("ERROR Can't exec:"+cmdOut,Color.RED,attr);
            }else{
                deadConsole.write("ERROR Can't exec:"+fileName,Color.RED,attr);
            }
        }
    }

    /**
     * Diese Funktion compiliert OOPSC, mit den vom CfgHandler geführten Attributen.
     */
    public void compileOOPSC(){
        LinkedList<String>cmd = new LinkedList<String>();
        File fromPath = new File(cfgHandler.getAttribute("JIP"));
        if(fromPath == null){
            String[] attr = {"<p>"};
            if(cfgHandler.getAttributeBoolean("FULL")){
                deadConsole.write("ERROR Can't find Compiler-sourcepath:"+cfgHandler.getAttribute("JIP"),Color.RED,attr);
            }else{
                deadConsole.write("ERROR Can't find Compiler-sourcepath!",Color.RED,attr);
            }
        }else{
            builder.directory(fromPath);
            if(cfgHandler.getAttributeBoolean("JP")){
                //TODO JAVA Pfad ermitteln und cmd adden
            }
            cmd.add("javac");
            cmd.add("-d");
            cmd.add(cfgHandler.getAttribute("JOP"));
            String fileName = cfgHandler.getAttribute("OOPSC_NAME");
            cmd.add(fileName+".java");
            compile(fileName,cmd);
        }
    }

    /**
     * Diese Funktion compiliert OOPSVM, mit den vom CfgHandler geführten Attributen.
     */
    public void compileOOPSVM(){
        LinkedList<String>cmd = new LinkedList<String>();
        File fromPath = new File(cfgHandler.getAttribute("VMIP"));
        if(fromPath == null){
            String[] attr = {"<p>"};
            if(cfgHandler.getAttributeBoolean("FULL")){
                deadConsole.write("ERROR Can't find Compiler-sourcepath:"+cfgHandler.getAttribute("JIP"),Color.RED,attr);
            }else{
                deadConsole.write("ERROR Can't find Compiler-sourcepath!",Color.RED,attr);
            }
        }else{
            builder.directory(fromPath);
            if(cfgHandler.getAttributeBoolean("JP")){
                //TODO JAVA Pfad ermitteln und cmd adden
            }
            cmd.add("javac");
            cmd.add("-d");
            cmd.add(cfgHandler.getAttribute("VMOP"));
            String fileName = cfgHandler.getAttribute("OOPSVM_NAME");
            cmd.add(fileName+".java");
            compile(fileName,cmd);
        }
    }
    
    /**
     * Diese Funktion compiliert die OOPS-Programme, mit den vom CfgHandler geführten Attributen.
     * Und mit den aus der files-Liste gegebenen Datein.
     */
    public void compileOOPS(){
        if(oopsFiles.size()>0){
            for(String fileName: oopsFiles){
                IOHandler ioHandler = new IOHandler(fileName, cfgHandler,deadConsole);
                if(ioHandler.getTestType() == IOHandler.TestType.COMPILE){
                    testOOPS(fileName,commandOOPS(fileName),ioHandler);
                }else{
                    compile(fileName,commandOOPS(fileName));
                }
            }
        }else{
            String[] attr = {"<p>"};
             deadConsole.write("ERROR No selected OOPS-Program",Color.RED,attr);
        }
    }
    /**
     * Diese Funktion compiliert ein OOPS-Programm, mit den vom CfgHandler geführten Attributen.
     * @param fileName Dateiname des Programms
     */
    private LinkedList<String> commandOOPS(String fileName){
        LinkedList<String> cmd = new LinkedList<String>();
        builder.directory(new File(System.getProperty("user.dir")));
        //java -jar OOPSC.jar [-c] [-h] [-hs <n>] [-i] [-l] [-s] [-ss <n>] <quelldatei>.oops [<ausgabedatei>.asm]
        cmd.add("java");
        cmd.add("-cp");
        cmd.add(cfgHandler.getAttribute("JOP"));
        cmd.add(cfgHandler.getAttribute("OOPSC_NAME"));
        if(cfgHandler.getAttributeBoolean("-C")){
            cmd.add("-c");
        }
        cmd.add("-hs");
        cmd.add(cfgHandler.getAttribute("-HS"));

        if(cfgHandler.getAttributeBoolean("-I")){
            cmd.add("-i");
        }
        if(cfgHandler.getAttributeBoolean("-L")){
            cmd.add("-l");
        }
        if(cfgHandler.getAttributeBoolean("-S")){
            cmd.add("-s");
        }

        cmd.add("-ss");
        cmd.add(cfgHandler.getAttribute("-SS"));
        cmd.add(cfgHandler.getAttribute("OIP")+System.getProperty("file.separator")+fileName+cfgHandler.getAttribute("PROGRAM_NAME"));
        cmd.add(cfgHandler.getAttribute("OOP")+System.getProperty("file.separator")+fileName+cfgHandler.getAttribute("KOMPILE_NAME"));
        return(cmd);
    }

    private void testOOPS(String fileName, LinkedList<String> cmd, IOHandler ioHandler) {
        Process compileProcess;
        String cmdOut= "";
        builder.command(cmd);
        for(String str: cmd){
            cmdOut += " "+str;
        }
        try {
            compileProcess = builder.start();
            Scanner errorStream = new Scanner( compileProcess.getErrorStream()).useDelimiter( "\\Z" );
            Scanner outputStream = new Scanner( compileProcess.getInputStream()).useDelimiter( "\\Z" );
            errorConsole = new ConsoleHandler(errorStream, jPane, Color.RED);
                        String[] attr = {"<p>","<b>"};
            if(cfgHandler.getAttributeBoolean("FULL")){
                outputConsole.write("COMPILE-TEST:"+cmdOut,Color.BLUE,attr);
            }else{
                outputConsole.write("COMPILE-TEST:"+fileName,Color.BLUE,attr);
            }
            errorConsole.start();
            String test = ioHandler.getTests()[0];
            ioHandler.checkTest(test, outputStream);

        } catch (IOException e) {
             String[] attr = {"<p>"};
             if(cfgHandler.getAttributeBoolean("FULL")){
                deadConsole.write("ERROR Can't exec:"+cmdOut,Color.RED,attr);
             }else{
                 deadConsole.write("ERROR Can't exec:"+fileName,Color.RED,attr);
             }

        }
    }
}
