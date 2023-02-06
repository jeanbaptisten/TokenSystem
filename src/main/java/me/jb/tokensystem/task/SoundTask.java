package me.jb.tokensystem.task;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class SoundTask implements Runnable {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundTask(Sound sound, float volume, float pitch) {
        this.sound = sound;

        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(
                onlinePlayer -> onlinePlayer.playSound(onlinePlayer.getLocation(), this.sound, this.volume, this.pitch)
        );
    }
}
