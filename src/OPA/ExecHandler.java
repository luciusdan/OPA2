/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA;

import OPA.Object.ObjectHandler;
import OPA.Output.ConsoleHandler;
import java.awt.Color;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Dirk
 */
public class ExecHandler {
    Process execProcess;
    ProcessBuilder builder;
    
    ObjectHandler objHandler;
    ConsoleHandler console;
    
    BufferedWriter writeStream;
    public ExecHandler(LinkedList<String> files, ObjectHandler objHandler, ConsoleHandler console){
        this.objHandler = objHandler;
        this.console = console;
        this.builder = new ProcessBuilder();
    }
    
    public void ioManuell(String input){
        if (!objHandler.getOOPSFiles().isEmpty()){
            try {
                writeStream.write(input);
                writeStream.flush();
            } catch (IOException ex) {
            }
        }
    }
    public boolean execManuell(){
        if (objHandler.getOOPSFiles().isEmpty()){
            return false;
        }else{
            String fileName = objHandler.getOOPSFiles().getFirst();
            if(objHandler.getOOPSFiles().size()>1){
                return false;
            }
            builder.directory(new File(objHandler.getData("OOPS_PROGRAMM_PATH_OUT").getStringValue()));
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
                if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                   console.write("Exec: ",Color.CYAN.darker(),true);
                   console.write(cmdOut+"\n",Color.black);
                }else{
                    console.write("Exec: ",Color.CYAN.darker(),true);
                    console.write(fileName+"\n",Color.black);
                }
                console.addScanner(os, Color.BLUE.darker());
                console.addScanner(es, Color.RED);
                
            } catch (IOException e) {
                if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                    console.write("ERROR",Color.CYAN.darker(), true);
                    console.write("Can't exec: "+cmdOut+"\n",Color.RED);
                }else{
                    console.write("ERROR",Color.CYAN.darker(), true);
                    console.write("Can't exec: "+fileName+"\n",Color.RED);
                }
            }
        }
        return true;
    }
    
    public void abbort(){
        if (!objHandler.getOOPSFiles().isEmpty()){
            execProcess.destroy();
        }
    }

    public boolean execAuto(){
        if(objHandler.getOOPSFiles().size()==0){
             console.write("ERROR ",Color.CYAN.darker(),true);
             console.write("No selected OOPS-Program\n",Color.RED);
        }
        for(String file : objHandler.getOOPSFiles()){
            execAuto(file);
        }
        return true;
    }
    
    private void execAuto(String fileName){
        IOHandler ioHandler = new IOHandler(fileName, objHandler, console);
        if(ioHandler.getTestType() != IOHandler.TestType.EXECUTE){
            console.write(fileName+".io hat anderen Testtyp\n.",Color.CYAN.darker());
        }else{
           builder.directory(new File(objHandler.getData("OOPS_PROGRAMM_PATH_OUT").getStringValue()));
            LinkedList<String> cmd = command(fileName);
            String[] tests = ioHandler.getTests();
            for(String test : tests){
                String[] testLines = test.split(" \\\\n ");
                builder.command(cmd);
                try {
                    execProcess = builder.start();
                    Scanner es = new Scanner( execProcess.getErrorStream()).useDelimiter( "\\z" );
                    Scanner outputStream = new Scanner( execProcess.getInputStream()).useDelimiter( "\\z" );
                    console.addScanner(es);

                    writeStream = new BufferedWriter(new OutputStreamWriter(execProcess.getOutputStream()));  
                    try{
                        execProcess.exitValue();
                        while(outputStream.hasNext()){
                            console.write(outputStream.next(),Color.RED);
                        }
                    }catch(IllegalThreadStateException e){
                        for(String line :testLines){
                            writeStream.write(line+'\n');
                        }
                        writeStream.flush();
                        ioHandler.checkTest(test, outputStream);
                    }
                } catch (IOException e) {
                    String cmdOut = "";
                    for(String c :cmd){
                        cmdOut += c;
                    }
                    if(objHandler.getData("SHORT_NAME").getBooleanValue()){
                        console.write("ERROR ",Color.CYAN.darker(),true);
                        console.write("Can't exec:"+cmdOut+"\n",Color.RED);
                    }else{
                        console.write("ERROR ",Color.CYAN.darker(),true);
                        console.write("Can't exec:"+fileName+"\n",Color.RED);
                    }
                }
            }
        }          
    }
    
    private LinkedList<String>command(String fileName){
        LinkedList<String> cmd = new LinkedList<String>();
        cmd.add("java");
        cmd.add("-cp");
        cmd.add(objHandler.getData("OOPS_VM_PATH_OUT").getStringValue());
        cmd.add(objHandler.getData("OOPS_VM_NAME").getStringValue());
        HashMap<String,String> cmds = objHandler.getData("OOPSVM_PARAMETERS").getValues();
        for(String key : cmds.keySet()){
            cmd.add(key);
            String val = cmds.get(key);
            if(val!=null){
                cmd.add(val);
            }
        }
        cmd.add(fileName+objHandler.getData("OOPS_KOMPILE_TYPE").getStringValue());
        return cmd;
    }
}
