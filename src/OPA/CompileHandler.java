/**
 * In dieser Klasse werden alle Prozesse erzeugt, die mit dem konvertieren
 * in Ausführbaren Code zutun haben.
 * Also vom kompilieren von: Compiler, VM und den OOPS-Pogramm
 * @author Dirk
 */
package OPA;

import OPA.Object.ObjectHandler;
import OPA.Output.ConsoleHandler;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * In dieser Klasse werden alle Prozesse erzeugt, die mit dem konvertieren
 * in Ausführbaren Code zutun haben.
 * Also vom kompilieren von: Compiler, VM und den OOPS-Pogramm.
 * @author Dirk
 */
public class CompileHandler {
    //Objekte zum Darstellen der Ausgabe
    private ConsoleHandler console;
    private ObjectHandler objHandler;

    //Objekte zum Kompilieren
    private ProcessBuilder builder;
    private LinkedList<String> oopsFiles = new LinkedList<String>();

   /**
    * Konstruktor
    * @param cfgHandler cfgHandler, zum laden und setzen von Attributen.
    * @param jPane Swing-Objekt zum darstellen der Console.
    * @param files OPPS-Programm Liste, die mit ExecHandler geteilt wird.
    */
    public CompileHandler(ObjectHandler objHandler, ConsoleHandler console){
        this.console = console;
        this.builder = new ProcessBuilder();
        this.objHandler = objHandler;
        this.oopsFiles = objHandler.getOOPSFiles();
    }
    
    private void compile(String fileName,LinkedList<String> cmd) {
        Process compileProcess;
        String cmdOut= "";
        builder.command(cmd);
        for(String str: cmd){
            cmdOut += " "+str;
        }
        try {
            compileProcess = builder.start();
            Scanner errorScanner = new Scanner( compileProcess.getErrorStream()).useDelimiter( "\\Z" );
            Scanner outputScanner= new Scanner( compileProcess.getInputStream()).useDelimiter( "\\Z" );
            console.addScanner(outputScanner, new Color(1,0.25f,0));
            console.addScanner(errorScanner, new Color(0.93f,0,0.2f));
            
            
            console.write("COMPILE: ", Color.cyan.darker(), true);
            System.out.println("Value: "+objHandler.getData("SHORT_NAME").getStringValue());
            if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                console.write(fileName+".java\n");
            }else{
                console.write(cmdOut+"\n");
            }
        } catch (IOException e) {
            console.write("ERROR", Color.cyan.darker(), true);
            console.write(" Can't execute:",Color.RED);
            if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                console.write(fileName+".java\n",Color.RED);
            }else{
                console.write(cmdOut+"\n",Color.RED);
            }
        }
    }

    public void compileOOPSC(){
        LinkedList<String>cmd = new LinkedList<String>();
        File fromPath = new File(objHandler.getData("OOPS_C_PATH_IN").getStringValue());
        if(fromPath == null){
            console.write("ERROR", Color.cyan.darker(), true);
            console.write(" Can't find Compiler-sourcepath:",Color.RED);
            console.write(objHandler.getData("OOPS_C_PATH_IN").getStringValue()+"\n",Color.RED);
        }else{
            builder.directory(fromPath);
            cmd.add("javac");
            cmd.add("-d");
            cmd.add(objHandler.getData("OOPS_C_PATH_OUT").getStringValue());
            String fileName =objHandler.getData("OOPS_COMPILER_NAME").getStringValue();
            cmd.add(fileName+".java");
            compile(fileName,cmd);
        }
    }

    /**
     * Diese Funktion compiliert OOPSVM, mit den vom CfgHandler geführten Attributen.
     */
    public void compileOOPSVM(){
        LinkedList<String>cmd = new LinkedList<String>();
        File fromPath = new File(objHandler.getData("OOPS_VM_PATH_IN").getStringValue());
        if(fromPath == null){
            console.write("ERROR", Color.cyan.darker(), true);
            console.write(" Can't find VM-sourcepath:",Color.RED);
            console.write(objHandler.getData("OOPS_VM_PATH_IN").getStringValue()+"\n",Color.RED);
        }else{
            builder.directory(fromPath);
            cmd.add("javac");
            cmd.add("-d");
            cmd.add(objHandler.getData("OOPS_VM_PATH_OUT").getStringValue());
            String fileName = objHandler.getData("OOPS_VM_NAME").getStringValue();
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
                IOHandler ioHandler = new IOHandler(fileName, objHandler, console);
                if(ioHandler.getTestType() == IOHandler.TestType.COMPILE){
                    testOOPS(fileName,commandOOPS(fileName),ioHandler);
                }else{
                    compile(fileName,commandOOPS(fileName));
                }
            }
        }else{
            console.write("ERROR", Color.cyan.darker(), true);
            console.write(" No selected OOPS-Program!\n",Color.RED);
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
        cmd.add(objHandler.getData("OOPS_C_PATH_OUT").getStringValue());
        cmd.add(objHandler.getData("OOPS_COMPILER_NAME").getStringValue());
        
        HashMap<String,String> cmds =  objHandler.getData("OOPSC_PARAMETERS").getValues();
        for(String key :cmds.keySet()){
            cmd.add(key);
            String val = cmds.get(key);
            if(val!=null){
                cmd.add(val);
            }
        }
        cmd.add(objHandler.getData("OOPS_PROGRAMM_PATH_IN").getStringValue()+System.getProperty("file.separator")+fileName+objHandler.getData("OOPS_PROGRAMM_TYPE").getStringValue());
        cmd.add(objHandler.getData("OOPS_PROGRAMM_PATH_OUT").getStringValue()+System.getProperty("file.separator")+fileName+objHandler.getData("OOPS_KOMPILE_TYPE").getStringValue());
        return(cmd);
    }

    private void testOOPS(String fileName, LinkedList<String> cmd, IOHandler ioHandler) {
        Process compileProcess;
        builder.command(cmd);
        
        String cmdOut= "";
        for(String str: cmd){
            cmdOut += " "+str;
        }
        try {
            compileProcess = builder.start();
            Scanner errorStream = new Scanner( compileProcess.getErrorStream()).useDelimiter( "\\Z" );
            Scanner outputStream = new Scanner( compileProcess.getInputStream()).useDelimiter( "\\Z" );
            console.write("TEST-COMPILE", Color.cyan.darker(), true);
            if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                console.write(fileName+".java\n",Color.BLUE);
            }else{
                console.write(cmdOut+"\n",Color.BLUE);
            }
            console.addScanner(errorStream, Color.RED);
            String test = ioHandler.getTests()[0];
            ioHandler.checkTest(test, outputStream);

        } catch (IOException e) {
            console.write("ERROR", Color.cyan.darker(), true);
            console.write(" Can't execute:",Color.RED);
            if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                console.write(fileName+".java\n",Color.RED);
            }else{
                console.write(cmdOut+"\n",Color.RED);
            }
        }
    }
}
