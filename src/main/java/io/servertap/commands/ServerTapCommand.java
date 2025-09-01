package io.servertap.commands;

import io.servertap.ServerTapMain;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class ServerTapCommand implements CommandExecutor, TabCompleter {

    private final ServerTapMain main;

    public ServerTapCommand(ServerTapMain main) {
        this.main = main;

        PluginCommand pluginCommand = main.getCommand("servertap");
        if (pluginCommand != null) {
            pluginCommand.setTabCompleter(this);
            pluginCommand.setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!commandSender.hasPermission("servertap.admin")) {
            commandSender.sendMessage(String.format("%s[%sServerTap%s] %sYou do not have the permission to do that!", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
            return false;
        }

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (args.length != 1) {
                        commandSender.sendMessage(String.format("%s[%sServerTap%s] %sUsage: /servertap reload", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                        return false;
                    }
                    main.reload();
                    commandSender.sendMessage(String.format("%s[%sServerTap%s] %sServerTap reloaded!", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                    return true;
                    
                case "info":
                    if (args.length != 1) {
                        commandSender.sendMessage(String.format("%s[%sServerTap%s] %sUsage: /servertap info", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                        return false;
                    }
                    String version = main.getDescription().getVersion();
                    String website = main.getDescription().getWebsite();
                    String authors = String.join(", ", main.getDescription().getAuthors());
                    commandSender.sendMessage(String.format("%sServerTap Plugin Information:\n%sVersion: %s%s\n%sWebsite: %s%s\n%sAuthors: %s%s",
                            BLUE, BLUE, AQUA, version, BLUE, AQUA, website, BLUE, AQUA, authors));
                    return true;
                    
                case "tiktok":
                    if (!(commandSender instanceof Player)) {
                        commandSender.sendMessage(String.format("%s[%sServerTap%s] %sThis command can only be used by players", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                        return false;
                    }
                    
                    Player player = (Player) commandSender;
                    FileConfiguration config = main.getConfig();
                    
                    if (args.length < 2) {
                        player.sendMessage(String.format("%s[%sServerTap%s] %sUsage: /servertap tiktok <bind|register|status|setmanual|list> [args]", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                        return false;
                    }
                    
                    String subcommand = args[1].toLowerCase();
                    switch (subcommand) {
                        case "bind":
                            if (args.length < 3) {
                                player.sendMessage(String.format("%s[%sServerTap%s] %sUsage: /servertap tiktok bind <tiktok_username>", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                                return false;
                            }
                            String username = args[2];
                            
                            // Update bindings
                            config.set("tiktok.bindings." + player.getUniqueId(), username);
                            main.saveConfig();
                            
                            player.sendMessage(String.format("%s[%sServerTap%s] %sYour TikTok account has been bound to: %s", DARK_GRAY, BLUE, DARK_GRAY, AQUA, username));
                            return true;
                            
                        case "register":
                            if (args.length < 3) {
                                player.sendMessage(String.format("%s[%sServerTap%s] %sUsage: /servertap tiktok register <tiktok_username>", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                                return false;
                            }
                            String regUsername = args[2];
                            config.set("tiktok.streamers." + player.getUniqueId(), regUsername);
                            config.set("tiktok.streamers." + regUsername + ".streaming", false);
                            config.set("tiktok.streamers." + regUsername + ".manual", false);
                            config.set("tiktok.streamers." + regUsername + ".lastChecked", 0);
                            config.set("tiktok.streamers." + regUsername + ".isLive", false);
                            main.saveConfig();
                            player.sendMessage(String.format("%s[%sServerTap%s] %sYour TikTok account has been registered: %s", DARK_GRAY, BLUE, DARK_GRAY, AQUA, regUsername));
                            return true;
                            
                        case "status":
                            String tiktokUser = config.getString("tiktok.streamers." + player.getUniqueId());
                            if (tiktokUser == null) {
                                player.sendMessage(String.format("%s[%sServerTap%s] %sYou haven't registered a TikTok account yet", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                                return false;
                            }
                            boolean isStreaming = config.getBoolean("tiktok.streamers." + tiktokUser + ".streaming", false);
                            player.sendMessage(String.format("%s[%sServerTap%s] %sStream status: %s", DARK_GRAY, BLUE, DARK_GRAY, AQUA, isStreaming ? "ON AIR" : "offline"));
                            return true;
                            
                        case "setmanual":
                            if (args.length < 4) {
                                player.sendMessage(String.format("%s[%sServerTap%s] %sUsage: /servertap tiktok setmanual <username> <on|off>", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                                return false;
                            }
                            String targetUser = args[2];
                            String status = args[3].toLowerCase();
                            
                            if (!status.equals("on") && !status.equals("off")) {
                                player.sendMessage(String.format("%s[%sServerTap%s] %sInvalid status. Use 'on' or 'off'", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                                return false;
                            }
                            
                            config.set("tiktok.streamers." + targetUser + ".streaming", status.equals("on"));
                            config.set("tiktok.streamers." + targetUser + ".manual", true);
                            config.set("tiktok.streamers." + targetUser + ".lastChecked", System.currentTimeMillis());
                            main.saveConfig();
                            player.sendMessage(String.format("%s[%sServerTap%s] %sManual status set to %s for %s", DARK_GRAY, BLUE, DARK_GRAY, AQUA, status, targetUser));
                            return true;
                            
                        case "list":
                            ConfigurationSection streamers = config.getConfigurationSection("tiktok.streamers");
                            if (streamers == null || streamers.getKeys(false).isEmpty()) {
                                player.sendMessage(String.format("%s[%sServerTap%s] %sNo registered TikTok streamers", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                                return true;
                            }
                            
                            StringBuilder list = new StringBuilder();
                            list.append(String.format("%s[%sServerTap%s] %sRegistered TikTok streamers:%s\n", DARK_GRAY, BLUE, DARK_GRAY, AQUA, GREEN));
                            for (String key : streamers.getKeys(false)) {
                                if (key.length() > 36) { // UUID keys
                                    String user = config.getString("tiktok.streamers." + key);
                                    list.append(user).append("\n");
                                }
                            }
                            player.sendMessage(list.toString());
                            return true;
                            
                        default:
                            player.sendMessage(String.format("%s[%sServerTap%s] %sUnknown subcommand: %s", DARK_GRAY, BLUE, DARK_GRAY, AQUA, subcommand));
                            return false;
                    }
                    
                default:
                    commandSender.sendMessage(String.format("%s[%sServerTap%s] %sUnknown Command.", DARK_GRAY, BLUE, DARK_GRAY, AQUA));
                    return false;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!commandSender.hasPermission("servertap.admin")) {
            return null;
        }
        ArrayList<String> completions = new ArrayList<>();
        if (args.length == 0 || (args.length == 1 && args[0].length() == 0)) {
            completions.add("reload");
            completions.add("info");
        }
        return completions;
    }
}
