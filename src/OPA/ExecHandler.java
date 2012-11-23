/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA;

import java.awt.Color;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JEditorPane;

/**
 *
 * @author Dirk
 */
public class ExecHandler {
    ConsoleHandler outputConsole;
    ConsoleHandler errorConsole;
    private ConsoleHandler deadConsole;
    
    Process execProcess;
    ProcessBuilder builder;
    
    ConfigHandler cfgHandler;
    JEditorPane jPane;
    
    LinkedList<String> oopsFiles = new LinkedList<String>();
    
    BufferedWriter writeStream;
    public ExecHandler(ConfigHandler cfgHandler, JEditorPane jPane, LinkedList<String> files){
        this.cfgHandler = cfgHandler;
        this.jPane = jPane;
        this.oopsFiles = files;
        this.builder = new ProcessBuilder();
        this.deadConsole = new ConsoleHandler(null, jPane, Color.RED);
    }
    
    public void ioManuell(String input){
        if (!oopsFiles.isEmpty()){
            try {
                writeStream.write(input);
                writeStream.flush();
            } catch (IOException ex) {
            }
        }
    }
    public boolean execManuell(){
        if (oopsFiles.isEmpty()){
            return false;
        }else{
            String fileName = oopsFiles.getFirst();
            if(oopsFiles.size()>1){
                return false;
            }
            builder.directory(new File(cfgHandler.getAttribute("OOP")));
            LinkedList<String> cmd = command(fileName);
            String cmdOut= "";
            for(String str: cmd){
                cmdOut += " "+str;
            }
            builder.command(cmd);
            try {
                execProcess = builder.start();
                writeStream = new BufferedWriter(new OutputStreamWriter(execProcess.getOutputStream()));
                Scanner es = new Scanner( execProcess.getErrorStream()).useDelimiter( "\\z" );
                Scanner os = new Scanner( execProcess.getInputStream()).useDelimiter( "\\z" );
                outputConsole = new ConsoleHandler(os, jPane, Color.ORANGE);
                errorConsole = new ConsoleHandler(es, jPane, Color.RED);
                String[] attr = {"<p>","<b>"};
                if(cfgHandler.getAttributeBoolean("FULL")){
                    outputConsole.write("Exec:"+ cmdOut,Color.black,attr);
                }else{
                    outputConsole.write("Exec:"+ fileName,Color.black,attr);
                }
                outputConsole.start();
                //errorConsole.start();
            } catch (IOException e) {
                String[] attr = {"<p>"};
                if(cfgHandler.getAttributeBoolean("FULL")){
                    deadConsole.write("ERROR Can't exec:"+cmdOut,Color.RED,attr);
                }else{
                    deadConsole.write("ERROR Can't exec:"+fileName,Color.RED,attr);
                }
            }
        }
        return true;
    }
    
    public void abbort(){
        if (!oopsFiles.isEmpty()){
            execProcess.destroy();
        }
    }

    public boolean execAuto(){
        if(oopsFiles.size()==0){
             String[] attr = {"<p>"};
             deadConsole.write("ERROR No selected OOPS-Program",Color.RED,attr);
        }
        for(String file : oopsFiles){
            execAuto(file);
        }
        return true;
    }
    
    private void execAuto(String fileName){
        IOHandler ioHandler = new IOHandler(fileName, cfgHandler,deadConsole);
        if(ioHandler.getTestType() == IOHandler.TestType.EXECUTE){
            String[] attri = {"<p>"};
            errorConsole.write(fileName+".io hat anderen Testtyp.",Color.black,attri);
        }else{
           builder.directory(new File(cfgHandler.getAttribute("OOP")));
            LinkedList<String> cmd = command(fileName);
            String[] tests = ioHandler.getTests();
            for(String test : tests){
                String[] testLines = test.split(" \\\\n ");
                builder.command(cmd);
                try {
                    execProcess = builder.start();
                    Scanner es = new Scanner( execProcess.getErrorStream()).useDelimiter( "\\z" );
                    Scanner outputStream = new Scanner( execProcess.getInputStream()).useDelimiter( "\\z" );
                    errorConsole = new ConsoleHandler(es, jPane, Color.RED);

                    writeStream = new BufferedWriter(new OutputStreamWriter(execProcess.getOutputStream()));  
                    try{
                        execProcess.exitValue();
                        while(outputStream.hasNext()){
                            deadConsole.write(outputStream.next(),Color.RED);
                        }
                    }catch(IllegalThreadStateException e){
                        for(String line :testLines){
                            writeStream.write(line+'\n');
                        }
                        writeStream.flush();
                        ioHandler.checkTest(test, outputStream);
                    }
                } catch (IOException e) {
                    String[] attri = {"<p>"};
                    String cmdOut = "";
                    for(String c :cmd){
                        cmdOut += c;
                    }
                    if(cfgHandler.getAttributeBoolean("FULL")){
                        errorConsole.write("ERROR\n Can't exec:"+cmdOut,Color.RED,attri);
                    }else{
                        errorConsole.write("ERROR\n Can't exec:"+fileName,Color.RED,attri);
                    }
                }
            }
        }          
    }
    
    private LinkedList<String>command(String fileName){
        LinkedList<String> cmd = new LinkedList<String>();
        cmd.add("java");
        cmd.add("-cp");
        cmd.add(cfgHandler.getAttribute("VMOP"));
        cmd.add(cfgHandler.getAttribute("OOPSVM_NAME"));
        String as = cfgHandler.getAttribute("-AS");
        if(as.equals("1")){
            cmd.add("-1");
        }else if(as.equals("2")){
            cmd.add("-2");
        }
        cmd.add(fileName+cfgHandler.getAttribute("KOMPILE_NAME"));
        return cmd;
    }
}
