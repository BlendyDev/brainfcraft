package me.blendy.brainfcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Brainfcraft extends JavaPlugin {
    public static String prefix = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Brainfcraft" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) return true;
        new BukkitRunnable() {
            @Override
            public void run() {
                Interpreter interpreter = new InterpreterImpl(1000, 1000, args[0].toCharArray(), args[1].toCharArray());
                Bukkit.broadcastMessage(prefix + interpreter.process());
            }
        }.runTaskAsynchronously(this);
        return true;
    }
}
