/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OPA.Object;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Dirk
 */
public class ObjectHandler {
    private ConfigHandler cfgHandler;
    private HashMap<String,Data> datas;
    
    private LinkedList<String> oopsFiles;
    private Data oopsFilePath;
    
    public ObjectHandler(){
        this.cfgHandler = new ConfigHandler();
        this.oopsFiles = new LinkedList<String>();
        initDatas(cfgHandler.read());
        oopsFilePath = this.getData("OOPS_PROGRAMM_PATH_IN");
    }
    
    private void initDatas(HashMap<String,Data>cfgDatas){
        datas = new HashMap<String,Data>();
        String sep =File.pathSeparator;
        String userPath = System.getProperty("user.dir");
        //TODO werte in standart schreiben
        String path = userPath;
        if(new File(path+sep+"TestOpen").exists()){
            userPath += sep+"TestOpen";
        }
        datas.put("OOPS_PROGRAMM_PATH_IN",new Data(userPath));
        
        path = userPath;
        if(new File(path+sep+"TestOpen"+sep+"bin").exists()){
            userPath += sep+"TestOpen"+sep+"bin";
        }
        datas.put("OOPS_PROGRAMM_PATH_OUT",new Data(userPath));
        
        path = userPath;
        if(new File(path+sep+"OOPSC").exists()){
            userPath += sep+"OOPSC";
        }
        datas.put("OOPS_C_PATH_IN",new Data(userPath));
        
        path = userPath;
        if(new File(path+sep+"OOPSC"+sep+"bin").exists()){
            userPath += sep+"OOPSC"+sep+"bin";
        }
        datas.put("OOPS_C_PATH_OUT",new Data(userPath));
        
        path = userPath;
        if(new File(path+sep+"OOPSVM").exists()){
            userPath += sep+"OOPSVM";
        }
        datas.put("OOPS_VM_PATH_IN",new Data(userPath));
        path = userPath;
        if(new File(path+sep+"OOPSVM"+sep+"bin").exists()){
            userPath += sep+"OOPSVM"+sep+"bin";
        }
        datas.put("OOPS_VM_PATH_OUT",new Data(userPath));
        
        
        datas.put("OOPS_PROGRAMM_PATH_IN",new Data(userPath));
        datas.put("OOPS_PROGRAMM_TYPE", new Data(".oops"));
        datas.put("OOPS_KOMPILE_TYPE", new Data(".asm"));
        datas.put("SHORT_NAME", new Data("false"));
        
        datas.put("OOPS_COMPILER_NAME",new Data("OOPSC"));
        datas.put("OOPS_VM_NAME",new Data("OOPSVM"));
        
        datas.put("OOPSC_PARAMETERS",new Data(" -ss 100 -hs 100"));
        datas.put("OOPSVM_PARAMETERS",new Data(" "));
        
        datas.putAll(cfgDatas);
    }
    
    public Data getData(String name){
        return datas.get(name);
    }
    
    public HashMap<String,Data> getDatas(){
        return datas;
    }
    
    public LinkedList<String> getOOPSFiles(){
        return oopsFiles;
    }
    
    public void setOOPSFiles(File[] files){
        oopsFiles.clear();
        if(files.length>0){
            oopsFilePath.setValue(files[0].getParent());
            for(File file : files){
                String onlyName = file.getName();
                String nameEnd = getData("OOPS_PROGRAMM_TYPE").getStringValue();
                onlyName = onlyName.substring(0, onlyName.length()-nameEnd.length());
                if(!oopsFiles.contains(onlyName)){
                    oopsFiles.add(onlyName);
                }
            }
        }
    }

    public void save() {
        cfgHandler.writeConfig(datas);
    }
    
}
