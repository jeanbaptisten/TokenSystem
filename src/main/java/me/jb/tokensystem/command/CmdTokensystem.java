package me.jb.tokensystem.command;

import com.sun.istack.internal.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;
import me.jb.tokensystem.TokenSystem;
import me.jb.tokensystem.enums.Permission;
import me.jb.tokensystem.enums.config.Message;
import me.jb.tokensystem.tokens.PlayerTokenData;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenService;
import me.jb.tokensystem.configuration.FileHandler;
import me.jb.tokensystem.utils.IntUtils;
import me.jb.tokensystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdTokensystem implements CommandExecutor {

    private final TokenSystem tokenSystem;
    private final FileHandler fileHandler;
    private final PlayerTokenService playerTokenService;

    public CmdTokensystem(@NotNull TokenSystem tokenSystem) {
        this.tokenSystem = tokenSystem;
        this.fileHandler = tokenSystem.getFileHandler();
        this.playerTokenService = this.tokenSystem.getServer().getServicesManager().load(PlayerTokenService.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration mainConfig = this.fileHandler.getMainConfigFile().getConfig();

        // Check perm
        if (!sender.hasPermission(Permission.USE.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return true;
        }

        // Check args
        if (args.length == 0) {
            String errorMess = mainConfig.getString(Message.ERROR.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
            return true;
        }

        switch (args[0]) {
            case "add":
                this.onTokenAdd(sender, mainConfig, args);
                break;
            case "remove":
                this.onTokenRemove(sender, mainConfig, args);
                break;
            case "set":
                this.onTokenSet(sender, mainConfig, args);
                break;
            case "get":
                this.onTokenGet(sender, mainConfig, args);
                break;
            case "reload":
                this.onReload(sender, mainConfig, args);
                break;
            default:
                String errorMess = mainConfig.getString(Message.ERROR.getKey());
                sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
                break;
        }
        return true;
    }

    // /arthasiastore add <player> <tokens>
    private void onTokenAdd(CommandSender sender, FileConfiguration mainConfig, String[] args) {

        if (!sender.hasPermission(Permission.ADD.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return;
        }

        if (args.length != 3) {
            String errorMess = mainConfig.getString(Message.ERROR.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
            return;
        }

        this.playerTokenService.checkPlayer(args[1], boolCallback -> {

            if (!boolCallback) {
                String unknownPlayerMess = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%player%", args[1]);
                sender.sendMessage(MessageUtils.setColorsMessage(unknownPlayerMess));
                return;
            }

            if (IntUtils.isntStringInt(args[2])) {
                String notIntMess = mainConfig.getString(Message.NOT_INT.getKey()).replace("%arg%", args[2]);
                sender.sendMessage(MessageUtils.setColorsMessage(notIntMess));
                return;
            }

            int addedPoints = Integer.parseInt(args[2]);

            if (addedPoints < 0) {
                String negativeIntMess = mainConfig.getString(Message.NEGATIVE_INT.getKey()).replace("%arg%", args[2]);
                sender.sendMessage(MessageUtils.setColorsMessage(negativeIntMess));
                return;
            }

            this.playerTokenService.addTokens(args[1], addedPoints, booleanCallback -> {

                if (!booleanCallback) {
                    String errorAddMess = mainConfig.getString(Message.ERROR_ADD_TOKEN.getKey());
                    sender.sendMessage(MessageUtils.setColorsMessage(errorAddMess));
                    return;
                }

                String tokenAddedMess = mainConfig.getString(Message.TOKEN_ADDED.getKey())
                        .replace("%player%", args[1])
                        .replace("%tokens%", args[2]);
                sender.sendMessage(MessageUtils.setColorsMessage(tokenAddedMess));

                Player receiverPlayer = Bukkit.getPlayer(args[1]);
                if (receiverPlayer != null) {
                    String playerTokenAddedMess = mainConfig.getString(Message.PLAYER_TOKEN_ADDED.getKey())
                            .replace("%tokens%", args[2]);
                    receiverPlayer.sendMessage(MessageUtils.setColorsMessage(playerTokenAddedMess));
                }

            });
        });
    }

    // /arthasiastore remove <player> <tokens>
    private void onTokenRemove(CommandSender sender, FileConfiguration mainConfig, String[] args) {

        if (!sender.hasPermission(Permission.REMOVE.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return;
        }

        if (args.length < 3) {
            String errorMess = mainConfig.getString(Message.ERROR.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
            return;
        }

        this.playerTokenService.checkPlayer(args[1], boolCallback -> {

            if (!boolCallback) {
                String unknownPlayerMess = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%player%", args[1]);
                sender.sendMessage(MessageUtils.setColorsMessage(unknownPlayerMess));
                return;
            }

            if (IntUtils.isntStringInt(args[2])) {
                String notIntMess = mainConfig.getString(Message.NOT_INT.getKey()).replace("%arg%", args[2]);
                sender.sendMessage(MessageUtils.setColorsMessage(notIntMess));
                return;
            }

            this.playerTokenService.getPlayerTokenData(args[1], playerTokenDataCallback -> {

                if (!playerTokenDataCallback.isPresent()) {
                    String unknownPlayerMess = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%player%", args[1]);
                    sender.sendMessage(MessageUtils.setColorsMessage(unknownPlayerMess));
                    return;
                }

                Player player = Bukkit.getPlayer(args[1]);

                int actualTokens = playerTokenDataCallback.get().getTokens();
                int removedPoints = Integer.parseInt(args[2]);

                if (actualTokens - removedPoints < 0) {
                    String playerNegativeTokensMess = mainConfig.getString(Message.PLAYER_NEGATIVE_TOKENS.getKey()).replace("%player%", args[1]);
                    sender.sendMessage(MessageUtils.setColorsMessage(playerNegativeTokensMess));

                    String senderNegativeTokensMess = mainConfig.getString(Message.SENDER_NEGATIVE_TOKENS.getKey()).replace("%player%", args[1]);
                    player.sendMessage(MessageUtils.setColorsMessage(senderNegativeTokensMess));
                    return;
                }

                this.playerTokenService.removeTokens(args[1], removedPoints, booleanCallback -> {
                    if (!booleanCallback) {
                        String errorRemoveMess = mainConfig.getString(Message.ERROR_REMOVE_TOKEN.getKey());
                        sender.sendMessage(MessageUtils.setColorsMessage(errorRemoveMess));
                        return;
                    }

                    String tokenRemoveMess = mainConfig.getString(Message.TOKEN_REMOVED.getKey())
                            .replace("%player%", args[1])
                            .replace("%tokens%", args[2]);
                    sender.sendMessage(MessageUtils.setColorsMessage(tokenRemoveMess));

                    Player receiverPlayer = Bukkit.getPlayer(args[1]);
                    if (receiverPlayer != null) {
                        String playerTokenRemovedMess = mainConfig.getString(Message.PLAYER_TOKEN_REMOVED.getKey())
                                .replace("%tokens%", args[2]);
                        receiverPlayer.sendMessage(MessageUtils.setColorsMessage(playerTokenRemovedMess));

                        if (args.length > 3) {
                            List<String> cmdList = this.getCommands(receiverPlayer, args);

                            cmdList.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
                        }
                    }

                });
            });
        });
    }

    // /arthasiastore set <player> <tokens>
    private void onTokenSet(CommandSender sender, FileConfiguration mainConfig, String[] args) {

        if (!sender.hasPermission(Permission.SET.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return;
        }

        if (args.length != 3) {
            String errorMess = mainConfig.getString(Message.ERROR.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
            return;
        }

        this.playerTokenService.checkPlayer(args[1], boolCallback -> {

            if (!boolCallback) {
                String unknownPlayerMess = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%player%", args[1]);
                sender.sendMessage(MessageUtils.setColorsMessage(unknownPlayerMess));
                return;
            }

            if (IntUtils.isntStringInt(args[2])) {
                String notIntMess = mainConfig.getString(Message.NOT_INT.getKey()).replace("%arg%", args[2]);
                sender.sendMessage(MessageUtils.setColorsMessage(notIntMess));
                return;
            }
            this.playerTokenService.setTokens(args[1], Integer.parseInt(args[2]), booleanCallback -> {
                if (!booleanCallback) {
                    // Player doesn't exist or error while getting database
                    String errorRemoveMess = mainConfig.getString(Message.ERROR_SET_TOKEN.getKey());
                    sender.sendMessage(MessageUtils.setColorsMessage(errorRemoveMess));
                    return;
                }

                String tokenSetMess = mainConfig.getString(Message.TOKEN_SET.getKey())
                        .replace("%player%", args[1])
                        .replace("%tokens%", args[2]);
                ;
                sender.sendMessage(MessageUtils.setColorsMessage(tokenSetMess));

                Player receiverPlayer = Bukkit.getPlayer(args[1]);
                if (receiverPlayer != null) {
                    String playerTokenSetMess = mainConfig.getString(Message.PLAYER_TOKEN_SET.getKey())
                            .replace("%tokens%", args[2]);
                    receiverPlayer.sendMessage(MessageUtils.setColorsMessage(playerTokenSetMess));
                }
            });
        });
    }

    // /arthasiastore get <player>
    private void onTokenGet(CommandSender sender, FileConfiguration mainConfig, String[] args) {

        if (!sender.hasPermission(Permission.GET.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return;
        }

        if (args.length != 2) {
            String errorMess = mainConfig.getString(Message.ERROR.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
            return;
        }

        this.playerTokenService.getPlayerTokenData(args[1], playerTokenData -> {

            if (!playerTokenData.isPresent()) {
                // error while getting in db or playing unknown
                String errorMess = mainConfig.getString(Message.UNKNOWN_PLAYER.getKey()).replace("%player%", args[1]);
                sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
                return;
            }

            PlayerTokenData tokenData = playerTokenData.get();

            String tokenRemoveMess = mainConfig.getString(Message.TOKEN_GET.getKey())
                    .replace("%player%", args[1])
                    .replace("%tokens%", String.valueOf(tokenData.getTokens()));
            sender.sendMessage(MessageUtils.setColorsMessage(tokenRemoveMess));
        });
    }

    // /arthasiastore reload
    private void onReload(CommandSender sender, FileConfiguration mainConfig, String[] args) {
        if (!sender.hasPermission(Permission.RELOAD.getPermission())) {
            String noPermMess = mainConfig.getString(Message.NO_PERM.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(noPermMess));
            return;
        }

        if (args.length != 1) {
            String errorMess = mainConfig.getString(Message.ERROR.getKey());
            sender.sendMessage(MessageUtils.setColorsMessage(errorMess));
            return;
        }

        this.tokenSystem.reloadPlugin();

        String reloadMess = mainConfig.getString(Message.RELOAD.getKey());
        sender.sendMessage(MessageUtils.setColorsMessage(reloadMess));
    }

    private List<String> getCommands(Player player, String[] args) {

        List<String> allCmdStrings = Arrays.stream(args)
                .skip(3)
                .collect(Collectors.toList());

        StringBuilder cmdBuild = new StringBuilder();

        for (String cmdString : allCmdStrings)
            cmdBuild.append(cmdString).append(" ");

        String allCmd = cmdBuild.toString();

        List<String> allRowCmdList = Arrays.asList(allCmd.split(","));
        List<String> allCmdList = new ArrayList<>();
        allRowCmdList.forEach(cmd -> allCmdList.add(PlaceholderAPI.setPlaceholders(player, cmd.trim())));

        return allCmdList;
    }

}
