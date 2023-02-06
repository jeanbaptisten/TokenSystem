package me.jb.tokensystem.command.tabcompleter;

import me.jb.tokensystem.enums.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TBTokensystem implements TabCompleter {

   /*
   /tokensystem add <player> <tokens>
   /tokensystem remove <player> <tokens>
   /tokensystem set <player> <tokens>
   /tokensystem get <player>
   /tokensystem reload
   */

    private static final List<String> ALL_CMD_LIST = Arrays.asList("add", "remove", "set", "get", "reload");

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> cmdList = new ArrayList<>();

        switch (args.length) {
            case 1:
                if (sender.hasPermission(Permission.ADD.getPermission()))
                    cmdList.add("add");
                if (sender.hasPermission(Permission.REMOVE.getPermission()))
                    cmdList.add("remove");
                if (sender.hasPermission(Permission.SET.getPermission()))
                    cmdList.add("set");
                if (sender.hasPermission(Permission.GET.getPermission()))
                    cmdList.add("get");
                if (sender.hasPermission(Permission.RELOAD.getPermission()))
                    cmdList.add("reload");
                return cmdList;

            case 2:
                if (args[0].equalsIgnoreCase("add")
                        || args[0].equalsIgnoreCase("remove")
                        || args[0].equalsIgnoreCase("set")
                        || args[0].equalsIgnoreCase("get")) {
                    Bukkit.getOnlinePlayers().forEach(player -> cmdList.add(player.getName()));
                    return cmdList;
                }

            case 3:
                if (args[0].equalsIgnoreCase("add")
                        || args[0].equalsIgnoreCase("remove")
                        || args[0].equalsIgnoreCase("get")
                        || args[0].equalsIgnoreCase("set"))
                    return IntStream.rangeClosed(0, 100).mapToObj(Integer::toString).collect(Collectors.toList());
        }

        return cmdList;
    }
}
