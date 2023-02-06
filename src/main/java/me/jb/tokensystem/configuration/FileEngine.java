package me.jb.tokensystem.configuration;

import com.sun.istack.internal.NotNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class FileEngine {

    @NotNull
    public static File fileCreator(@NotNull Plugin plugin, String path, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder().getPath() + File.separator + path, fileName);
        return fileCreator(plugin, file);
    }

    @NotNull
    public static File fileCreator(@NotNull Plugin plugin, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder().getPath(), fileName);
        return fileCreator(plugin, file);
    }

    @NotNull
    public static File fileCreator(@NotNull Plugin plugin, @NotNull File file) {

        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (!file.exists())
            try (InputStream in = plugin.getResource(file.getName())) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Error generating the plugin file: " + file.getName());
                e.printStackTrace();
            }

        return file;
    }


    @NotNull
    public static FileConfiguration fileConfigurationLoader(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }
}