package ch.dragon252525.connectFour;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class Messages {
        private ResourceBundle prop;
        private final JavaPlugin instance;
    public Messages(JavaPlugin plugin){
        this(plugin,new Locale("en"));
    }


    public Messages(JavaPlugin plugin, Locale locale){
        instance = plugin;
        InputStream input = null;
        File datafolder = instance.getDataFolder();
        try {
            File mFile = new File(datafolder, "messages.properties");
            if(!mFile.exists()){
                input = new FileInputStream(mFile);
                prop = new PropertyResourceBundle(input);
            }else{
                prop = PropertyResourceBundle.getBundle("messages", locale);
                Properties tprop =  new Properties();
                Enumeration<String> keys = prop.getKeys();
                while(keys.hasMoreElements()){
                    String key = keys.nextElement();
                    tprop.setProperty(key, prop.getString(key));
                }
                FileOutputStream out = new FileOutputStream(new File(datafolder, "messages.properties"));
                tprop.store(out,"Default Resources");
                out.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getMessage(String key){
        return (prop.getString(key) !=null)?prop.getString(key):"";
    }

    public String getMessage(String key, String arg){
        String[] args = null;
        args[0] = arg;
        return getMessage(key,args);
    }


    public String getMessage(String key, String[] args) {
        String message = getMessage(key);
        if (message != null) {
            message = MessageFormat.format(message, (Object[]) args);
        } else {
            instance.getLogger().info("Message Null at key :" + key);
            message = "NULL";
        }
        return message;
    }

}
