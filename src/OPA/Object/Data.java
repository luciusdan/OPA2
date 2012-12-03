/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA.Object;

import java.util.HashMap;

/**
 *
 * @author Dirk
 */
public class Data {
    private String value;
    private HashMap<String,String> values;
            
    public Data(String value){
        this.value = value;
    }      
    
    private String get(){
        return value;
    }
    
    public HashMap<String,String> getValues(){
        if(values==null){
            values = new HashMap<String,String>();
            if(value.startsWith("-")){
                value= " "+value;
            }
            while(value.startsWith(" -")){
                String val = "";
                String key;
                value= value.substring(2);
                int keyEnd = value.indexOf(" ");
                if(keyEnd<0){
                    key = "-"+value;
                    
                    System.out.println(key);
                }else{
                    key = "-"+value.substring(0,keyEnd);
                    value = value.substring(keyEnd+1);
                    if(value.startsWith("-")){
                        val= "";
                        value = " "+value;
                    }else if(value.contains(" -")){
                        int valEnd= value.indexOf(" -");
                        val = value.substring(0, valEnd);
                        System.out.println("/"+val);
                        value = value.substring(valEnd);
                    }else{
                        val = value;
                    }
                }
                    values.put(key, val);
                
            }
            value= null;
        }
        return values;
    }
    
    public String getStringValue(){
        if(values == null){
            return get();
        }else{
            String text ="";
            for(String key : values.keySet()){
                String val =values.get(key);
                text += " "+key+" "+(val==null?null:val);
            }
            if(text.isEmpty()){
                return " ";
            }else{
                return text;
            }
        }
    }

    public boolean getBooleanValue(){
        if(value.toLowerCase().equals("true")){
            return true;
        }else{
            return false;
        }
    }
    
    public int getIntValue(){
        return Integer.parseInt(value);
    }

    
    public void setValue(String text) {
        this.value = text;
    }
    
    
    public void setValue(int value) {
        setValue(""+value);
    }
        
    public void setValue(boolean value) {
        setValue(value?"true":"false");
    }
}
