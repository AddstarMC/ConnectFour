package ch.dragon252525.connectFour;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

class ConfigAccessor
{
    private final String fileName;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigAccessor(JavaPlugin plugin, String fileName)
    {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    private void reloadConfig()
    {
        if (this.configFile == null)
        {
            File dataFolder = this.plugin.getDataFolder();
            if (dataFolder == null) {
                throw new IllegalStateException();
            }
            this.configFile = new File(dataFolder, this.fileName);
        }
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defConfigStream = this.plugin.getResource(this.fileName);
        if (defConfigStream != null)
        {
            InputStreamReader reader = new InputStreamReader(defConfigStream);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            this.fileConfiguration.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig()
    {
        if (this.fileConfiguration == null) {
            reloadConfig();
        }
        return this.fileConfiguration;
    }

    public void saveConfig()
    {
        if ((this.fileConfiguration == null) || (this.configFile == null)) {
            return;
        }
        try
        {
            getConfig().save(this.configFile);
        }
        catch (IOException ex)
        {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
        }
    }

    public void saveDefaultConfig()
    {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }
    }

    public void removeFile()
    {
        this.configFile.delete();
    }
}
