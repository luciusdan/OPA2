/*
 * Diese Klasse dient zum speichern und laden von Werten.
 * Da der Handler die .cfg nach beenden neu schreibt, sind die manuellen
 * Einträge stark eingeschrenkt.
 */
package OPA;

import java.io.*;
import java.util.LinkedList;

/**
 * Klasse zum laden und speichern von Werten.
 * @author Dirk
 */
public class ConfigHandler {
    private LinkedList<String[]> attributes;
    private String trenner ="@@"; 
    
    /**
     * Konstruktor,
     * ließt automaitsch die config.cfg aus.
     */
    public ConfigHandler(){
        attributes= new LinkedList<String[]>();
        try {
            FileReader fr = new FileReader("config.cfg");
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
                    }else if(!line.startsWith("//")){
                        String[] lineFields = line.split(trenner);
                        if(lineFields.length==2){
                            if(lineFields[0].isEmpty()){
                                fails++;
                                System.out.println("Fehler "+fails+": Bezeichner Leer! Zeile"+i);
                            }else{
                                attributes.add(lineFields);
                            }
                        }else{
                            fails++;
                            System.out.println("Fehler "+fails+": Zu viele Trennungszeichen! Zeile"+i);
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("CfgFile lässt sich nicht öffnen!");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("CfgFile kann nicht gefunden werden!");
        }
        
    }
    
    /**
     * Ersetzt den Wert des Attributes, bzw legt ein neues An
     * @param name Name des Attributs
     * @param value Zuzuweisender Wert des Attribute
     */
    public void setAttribute(String name, String value){
        String[] a = findAttribute(name);
        if(a == null){
            System.out.println("CfgHandler Failure: attribute beim setzen nicht gefunden:"+name);
            String[] attribute = new String[2];
            attribute[0] = name;
            attribute[1] = value;
            attributes.add(attribute);
        }else{
            a[1] = value;
        }
    }
    
    /**
     * Gibt den zugewiesenen Wert eines Attributes als String zurück
     * @param name Name des Attributs
     * @return Zuzuweisender Wert des Attribute
     * @exception UnsupportedOperationException Wenn für den Namen weder ein 
     * Wert noch ein Standartwert vorhanden ost.
     */
    public String getAttribute(String name)throws UnsupportedOperationException{
        String[] a = findAttribute(name);
        if(a == null){
            System.out.println("CfgHandler Failure: attribute beim lesen nicht gefunden:"+name);
            return getStandart(name);
        }else{
            return a[1];
        }
    }
    
    /**
     * Gibt den zugewiesenen Wert eines Attributes als Boolean zurück
     * @param name Name des Attributs
     * @return Zuzuweisender Wert des Attribute
     * @exception UnsupportedOperationException Wenn für den Namen weder ein 
     * Wert noch ein Standartwert vorhanden ost.
     */
    public Boolean getAttributeBoolean(String name)throws UnsupportedOperationException{
        String value = getAttribute(name);
        if(value.equals("TRUE")){
            return true;
        }else{
            return false;
        }
        
    }
    
    /**
     * Gibt den zugewiesenen Wert eines Attributes als Integer zurück
     * @param name Name des Attributs
     * @return Zuzuweisender Wert des Attribute
     * @exception UnsupportedOperationException Wenn für den Namen weder ein 
     * Wert noch ein Standartwert vorhanden ost.
     */
    public int getAttributeInt(String name)throws UnsupportedOperationException{
        String value = getAttribute(name);
        try{
            return Integer.parseInt(value);
        }catch(NumberFormatException  e){
            throw new UnsupportedOperationException("ConfigHandler: Attribut "+name+" kann Wert nicht auslesen: "+value);
        }
    }
    
    /**
     * Sucht nach Attribute mit dem Übergebenen Namen
     * @param name Name des zusuchenden Attribute
     * @return das Attribute, wenn vorhanden
     * @return NULL wenn kein entsprechendes Attribute gefunden
     */
    private String[] findAttribute(String name){
        for(String[] a : attributes){
            if(a[0].equals(name)){
                return a;
            }
        }
        return null;
    }

    /**
     * Schreibt eine neue ConfigFile.
     */
    public void writeConfig(){
        try{
            FileWriter writer = new FileWriter(new File("config.cfg"));
            String nxtLine =System.getProperty("line.separator");
            writer.write("// Compiler CfgFile AUTOGENERATED "+nxtLine);
            writer.write("// Attributename "+trenner+" Attributevalue"+nxtLine);
            writer.write("// *********************************"+nxtLine);
            String newLine;
            for(String[] a: attributes){
                newLine= a[0]+trenner+a[1];
                writer.write(newLine+nxtLine);       
            // Platformunabhängiger Zeilenumbruch wird in den Stream geschrieben
            }
            writer.flush();
            writer.close();
            System.out.println("CfgFile geschrieben");
        } catch (IOException e) {
            System.out.println("Konnte CfgFile nicht schreiben");
        }
    }
    
    /**
     * gibt den als Standartwert für ein Attribute an.
     * Wenn kein Standartwert vorhanden ist, wird eine Fehlermeldung
     * zurück gegeben.
     * @param name Name des Attributs
     * @return der Wert des Attributs, wenn vorhanden
     * @exception UnsupportedOperationException Wenn für den Namen weder ein 
     * Wert noch ein Standartwert vorhanden ost.
     */
    private String getStandart(String name) throws UnsupportedOperationException {
        String[] attribute = new String[2];
        attribute[0] = name;
        
        if(name.equals("JIP")||name.equals("JOP")||name.equals("OIP")||name.equals("OOP")||name.equals("VMIP")||name.equals("VMOP")){
            String sep = System.getProperty("file.separator");
            attribute[1] = System.getProperty("user.dir")+sep;
            if(name.equals("JIP")){
                attribute[1] += "OOPSC"+sep+"src";
            }else if(name.equals("JOP")){
                attribute[1] += "OOPSC"+sep+"bin";
            }else if(name.equals("VMIP")){
                attribute[1] += "OOPSVM"+sep+"src";
            }else if(name.equals("VMOP")){
                attribute[1] += "OOPSVM"+sep+"bin";
            }else if(name.equals("OIP")){
                attribute[1] += "TestsOpen";
            }else{
                attribute[1] += "TestsOpen"+sep+"bin";
            }
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("-SS")||name.equals("-HS")){
            attribute[1] = "100";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("-R2")||name.equals("-R4")){
            attribute[1] = "NONE";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("-AS")){
            attribute[1] = "ALL";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("FULL")||name.equals("-C")||name.equals("-L")||name.equals("-S")||name.equals("-I")){
            attribute[1] = "FALSE";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("JP")){
            attribute[1] = "TRUE";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("OOPSC_NAME")){
            attribute[1] = "OOPSC";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("OOPSVM_NAME")){
            attribute[1] = "OOPSVM";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("PROGRAM_NAME")){
            attribute[1] = ".oops";
            attributes.add(attribute);
            return attribute[1];
        }
        if(name.equals("KOMPILE_NAME")){
            attribute[1] = ".asm";
            attributes.add(attribute);
            return attribute[1];
        }
        throw new UnsupportedOperationException("ConfigHandler: Attribut "+name+" nicht gefunden.");
    }
   
}
