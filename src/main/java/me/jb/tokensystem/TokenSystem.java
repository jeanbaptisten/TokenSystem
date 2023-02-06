package me.jb.tokensystem;

import me.jb.tokensystem.tokens.dabatase.impl.CraftPlayerTokenService;
import me.jb.tokensystem.command.CmdTokensystem;
import me.jb.tokensystem.command.CmdSub;
import me.jb.tokensystem.command.tabcompleter.TBTokensystem;
import me.jb.tokensystem.configuration.FileHandler;
import me.jb.tokensystem.listener.PlayerConnectionListener;
import me.jb.tokensystem.placeholder.ArthasiaStoreExpansion;
import me.jb.tokensystem.sql.DbConnection;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenDAO;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenModel;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenService;
import me.jb.tokensystem.tokens.dabatase.impl.CraftPlayerTokenModel;
import me.jb.tokensystem.tokens.dabatase.impl.SQLPlayerTokenDAO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public final class TokenSystem extends JavaPlugin {

    private final FileHandler fileHandler = new FileHandler(this);
    private PlayerTokenService playerTokenService;
    private PlayerTokenModel playerTokenModel;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Setup token service
        try {
            this.setupTokenService();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.getServer().getServicesManager().register(PlayerTokenService.class,
                this.playerTokenService, this, ServicePriority.Highest);

        // Registering commands
        this.registerCommands();

        // Registering listeners
        this.registerListeners();

        // Register placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.registerPlaceholders();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    public void reloadPlugin() {
        this.fileHandler.reloadFiles();
        this.playerTokenService.reload();
    }

    public PlayerTokenModel getPlayerTokenModel() {
        return this.playerTokenModel;
    }

    private void registerCommands() {
        getCommand("tokensystem").setExecutor(new CmdTokensystem(this));
        getCommand("tokensystem").setTabCompleter(new TBTokensystem());
        getCommand("sub").setExecutor(new CmdSub(this, this.playerTokenService, this.fileHandler));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this.playerTokenService), this);
    }

    private void registerPlaceholders() {
        new ArthasiaStoreExpansion(this, this.playerTokenModel).register();
    }

    private void setupTokenService() throws SQLException, ClassNotFoundException {
        DbConnection dbConnection = new DbConnection(this);
        dbConnection.open(this.fileHandler.getMainConfigFile().getConfig());

        PlayerTokenDAO tokenDAO = new SQLPlayerTokenDAO(dbConnection);
        this.playerTokenModel = new CraftPlayerTokenModel();
        this.playerTokenService = new CraftPlayerTokenService(this, tokenDAO, this.playerTokenModel);

        this.playerTokenService.loadOnlinePlayerTokenData(bool -> {
            if (!bool) this.getLogger().log(Level.SEVERE, "Error while loading online player data !");
        });
    }
}
