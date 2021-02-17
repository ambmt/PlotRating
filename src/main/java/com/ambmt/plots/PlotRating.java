package com.ambmt.plots;

import com.ambmt.plots.commands.ReloadConfig;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.events.PlotRateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public final class PlotRating extends JavaPlugin implements CommandExecutor {
    List<String> list = OwnerList.getList();


    @Override
    public void onEnable() {

        PlotAPI plotApi = new PlotAPI();
        plotApi.registerListener(new P2Listener());
        System.out.println("Plot Rating hooked into plotsquared");
        final FileConfiguration config = getConfig();
        System.out.println("Config files loaded");
        saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        this.getCommand("plotratingreload").setExecutor(new ReloadConfig(this));
        System.out.println("Loaded reload command");
        System.out.println("Plot rating loaded!");



    }
    public static class Cooldown{

        static HashMap<Player, Long> cooldownmanager = new HashMap<>();
    }





    public class P2Listener {



        @Subscribe
        public void onPlotRateEvent(PlotRateEvent event) {
            String senderCommand = getConfig().getString("SenderCommand");
            String plotOwnerCommand = getConfig().getString("PlotOwnerCommand");
            String plotOwnerCommandAfter = getConfig().getString("PlotOwnerCommandAfter");
            String senderCommandAfter = getConfig().getString("SenderCommandAfter");
            String offlineOwner = getConfig().getString("PlotOwnerOffline");
            String offlineOwnerAfter = getConfig().getString("PlotOwnerOfflineAfter");
            String CooldownTime = getConfig().getString("CooldownTime");
            int Cooldownint = Integer.parseInt(CooldownTime);
            UUID owner = event.getPlot().getOwner();
            UUID rater = event.getRater().getUUID();


            try {
                if (Cooldown.cooldownmanager.get(Bukkit.getPlayer(rater)) > System.currentTimeMillis()) {
                    long timeRemaining = Cooldown.cooldownmanager.get(Bukkit.getPlayer(rater)) - System.currentTimeMillis();
                    int intRemaining = (int) (timeRemaining / 1000);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say" + " " + Bukkit.getPlayer(rater));


                    event.getRater().sendMessage(ChatColor.DARK_RED + "You are on reward cooldown, you will not give or receive rewards for "+ ChatColor.RED +ChatColor.BOLD + intRemaining+ " seconds");
                    event.setEventResult(null); }
                }catch (NullPointerException exception) {


                    if (getConfig().getBoolean("SendRatingMessage")) {
                        event.getRater().sendMessage(ChatColor.LIGHT_PURPLE + "Thank you for rating the plot ");
                        Cooldown.cooldownmanager.put(Bukkit.getPlayer(rater), System.currentTimeMillis() + (Cooldownint * 1000));
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), senderCommand + " " + event.getRater() + " " + senderCommandAfter);

                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plotOwnerCommand + " " + Bukkit.getPlayer(owner).getName() + " " + plotOwnerCommandAfter);
                    } catch (NullPointerException e) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), offlineOwner + " " + Bukkit.getOfflinePlayer(owner).getName() + " " + offlineOwnerAfter);


                    }
                }


            }
        }



        @Override
        public void onDisable() {
            System.out.println("Shutting Down Rating Plugin");
        }
}