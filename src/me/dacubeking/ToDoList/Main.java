package me.dacubeking.ToDoList;


import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    static Plugin plugin;


    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        plugin = this;
        ConfigHandler.createConfig(this);
        CommandHandler commandExecutor = new CommandHandler();
        getCommand("todolist").setExecutor(commandExecutor);
        getCommand("todolist").setTabCompleter(commandExecutor);

    }
}
