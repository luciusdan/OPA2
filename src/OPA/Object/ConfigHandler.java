/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA.Object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Dirk
 */
public class ConfigHandler {
    private String trenner ="@@"; 
    private String configName = "test.cfg";
    private LinkedList<LineInfo> lineInfos;
    
    public ConfigHandler(){
        lineInfos = new LinkedList<LineInfo>();
    }
    
    public HashMap<String,Data> read(){
        HashMap<String,Data> datas= new HashMap<String,Data>();
        try {
            FileReader fr = new FileReader(configName);
            BufferedReader br = new BufferedReader(fr);
            try {
                System.out.println("Lese Config");
                String line;
                int fails=0;
                for(int i = 1; i>0 ; i++){
                    line = br.readLine();
                    if(line == null){
                        System.out.println("Config gelesen: "+(i-1)+" Zeilen "+fails+" Fehler");
                        i = -1;
                    }else if(line.startsWith("//")){
                        lineInfos.add(new LineInfo(line,false));
                    }else{
                        String[] lineFields = line.split(trenner);
                        if(lineFields.length==2){
                            if(lineFields[0].isEmpty()){
                                fails++;
                                System.out.println("Fehler "+fails+": Bezeichner Leer! Zeile"+i);
                        lineInfos.add(new LineInfo(line,false));
                            }else{
                                datas.put(lineFields[0], new Data(lineFields[1]));
                                lineInfos.add(new LineInfo(lineFields[0]));
                            }
                        }else{
                            fails++;
                            System.out.println("Fehler "+fails+": Zu viele Trennungszeichen! Zeile"+i);
                        lineInfos.add(new LineInfo(line,false));
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("CfgFile lässt sich nicht öffnen!");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("CfgFile kann nicht gefunden werden!");
        }
        return datas;
    }
    
     /**
     * Schreibt eine neue ConfigFile.
     */
    public void writeConfig(HashMap<String,Data> datas){
        try{
            HashMap<String,Data> copy = (HashMap<String,Data>)datas.clone();
            FileWriter writer = new FileWriter(new File(configName));
            String breakLine =System.getProperty("line.separator");
            
            String line ="";
            if(!lineInfos.isEmpty()){
                line = lineInfos.getFirst().line;
            }
            String newLine = "// Compiler CfgFile";
            writer.write(newLine+breakLine);
            if(newLine.equals(line)){
                lineInfos.removeFirst();
                if(!lineInfos.isEmpty()){
                    line = lineInfos.getFirst().line;
                }
            }
            newLine = "// Attributename "+trenner+" Attributevalue";
            writer.write(newLine+breakLine);
            if(newLine.equals(line)){
                lineInfos.removeFirst();
                if(!lineInfos.isEmpty()){
                    line = lineInfos.getFirst().line;
                }
            }
            newLine = "// *********************************";
            writer.write(newLine+breakLine);
            if(newLine.equals(line)){
                lineInfos.removeFirst();
            }    
            
            for(LineInfo lineInfo: lineInfos){
                if(lineInfo.isValue){
                    writer.write(lineInfo.line+trenner+copy.get(lineInfo.line).getStringValue()+breakLine);
                    copy.remove(lineInfo.line);
                }else{
                    writer.write(lineInfo.line+breakLine);
                }
            }
            for(String key : copy.keySet()){
                writer.write(key+trenner+copy.get(key).getStringValue()+breakLine);
            }
            writer.flush();
            writer.close();
            System.out.println("CfgFile geschrieben");
        } catch (IOException e) {
            System.out.println("Konnte CfgFile nicht schreiben");
        }
    }
    
    class LineInfo{
        boolean isValue;
        String line;
        
        LineInfo(String line){
            this(line,true);
        }
        
        LineInfo(String line, boolean isValue){
            this.line = line;
            this.isValue = isValue;
        }
    }
}
