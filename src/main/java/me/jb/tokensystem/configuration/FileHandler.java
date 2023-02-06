package me.jb.tokensystem.configuration;

import com.sun.istack.internal.NotNull;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileHandler {

    private CustomConfig mainConfig;

    private final Plugin plugin;

    public FileHandler(@NotNull Plugin plugin) {
        this.plugin = plugin;

        // Initialize main configuration file.
        this.setupMainConfigFile();
    }

    @NotNull
    public CustomConfig getMainConfigFile() {
        return this.mainConfig;
    }

    public void reloadFiles() {
        this.setupMainConfigFile();
    }

    private void setupMainConfigFile() {
        File mainConfigFile = FileEngine.fileCreator(this.plugin, "config.yml");
        this.mainConfig = new CustomConfig(mainConfigFile);
    }


}