package org.aztecmc.plugins.cron;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CronPlugin extends JavaPlugin {

    private BukkitRunnable bukkitRunnable = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        log("Plugin loaded");

        wireCrons();
        log("Crons loaded");
    }

    private void wireCrons() {
        final String timezone = getConfig().getString("bukkitRunnable-timezone", "America/Los_Angeles");
        final List<CronDef> cronDefs = CronDef.getFromConfig(
                timezone,
                getConfig().getMapList("crons"),
                (s) -> getLogger().info(s));

        if(bukkitRunnable != null)
            bukkitRunnable.cancel();

        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                CronDef.runCrons(cronDefs, timezone, (pair) -> {
                    try {
                        Bukkit.getLogger().info("Running: " + pair.getKey().name);
                        Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                pair.getValue()
                        );
                    } catch (Exception e) {
                        StringWriter s = new StringWriter();
                        e.printStackTrace(new PrintWriter(s));
                        Bukkit.getLogger().info(s.toString());
                    }
                });
            }
        };
        bukkitRunnable.runTaskTimer(this, 0L,
                getConfig().getLong("bukkitRunnable-poll-resolution", 20L));
    }
    
    @Override
    public boolean onCommand​(CommandSender sender, Command command, java.lang.String label, java.lang.String[] args) {
        if(command.getName().equalsIgnoreCase("cron")){//no other commands should be registered, but just to be sure...
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("reload")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if(!player.hasPermission("cron.reload"))
                            return false;
                    }
                    reloadConfig();
                    wireCrons();
                    log("Crons reloaded");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDisable() { }

    private void log(String s) {
        getLogger().info(s);
    }
}
