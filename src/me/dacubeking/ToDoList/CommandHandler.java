package me.dacubeking.ToDoList;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args.length > 0){

            if (args[0].equalsIgnoreCase("list")) {
                if(commandSender.hasPermission("todolist.see")){
                    int index = 1;

                    if(Main.plugin.getConfig().getConfigurationSection("items") != null){
                        commandSender.sendMessage(ChatColor.BLUE + "------- To Do List -------");
                        for(String uniqueID : Main.plugin.getConfig().getConfigurationSection("items").getKeys(false)){
                            String item = Main.plugin.getConfig().getString("items."+ uniqueID + ".message");
                            if(item != null){
                                BaseComponent baseComponent = new TextComponent();

                                TextComponent number = new TextComponent(index + ". ");
                                number.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                                baseComponent.addExtra(number);

                                TextComponent message = new TextComponent(item);
                                message.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                                String playerId = Main.plugin.getConfig().getString("items."+ uniqueID + ".createdBy");
                                String creationDate = Main.plugin.getConfig().getString("items."+ uniqueID + ".createdOn");
                                if(playerId != null && creationDate!= null){
                                    String playerName;
                                    try{
                                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerId));
                                        playerName = offlinePlayer.getName();
                                    } catch(IllegalArgumentException e){
                                        playerName = playerId;
                                    }
                                    Text hoverText = new Text(ChatColor.BLUE + "Created by " + ChatColor.RESET + playerName +
                                            ChatColor.BLUE + " on " + ChatColor.RESET + creationDate
                                            + ("\n") + ChatColor.RED + "Click to delete");
                                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

                                } else{
                                    Text hoverText = new Text(ChatColor.RED + "Failed to create hover. Check that your config has not been corrupted");
                                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
                                }

                                baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/todolist removetodo " + uniqueID));

                                baseComponent.addExtra(message);




                                commandSender.spigot().sendMessage(baseComponent);
                                index++;
                            }
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.BLUE + "No items have been added to the todo list");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                }

            }

            if (args[0].equalsIgnoreCase("add")) {
                if (commandSender.hasPermission("todolist.edit")) {
                    if (args.length > 1) {
                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            message.append(args[i]).append(" ");
                        }

                        String uniqueID = UUID.randomUUID().toString();

                        Main.plugin.getConfig().set("items." + uniqueID + ".message", message.toString());
                        if(commandSender instanceof Player){
                            Main.plugin.getConfig().set("items." + uniqueID + ".createdBy", ((Player) commandSender).getUniqueId().toString());
                        } else{
                            Main.plugin.getConfig().set("items." + uniqueID + ".createdBy", "Console");
                        }

                        Date date = Calendar.getInstance().getTime();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String strDate = dateFormat.format(date);
                        Main.plugin.getConfig().set("items." + uniqueID + ".createdOn", strDate);

                        Main.plugin.saveConfig();
                        commandSender.sendMessage(ChatColor.GREEN + "Added Item Successfully");
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "You must include a message");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "You don't have permission to do that");


                }
            }

            if (args[0].equalsIgnoreCase("removetodo")) {
                if(commandSender.hasPermission("todolist.edit")){
                    if(args.length == 2){
                        Main.plugin.getConfig().set("items." + args[1], null);
                        commandSender.sendMessage(ChatColor.GREEN + "Removed Item Successfully");
                    } else{
                        commandSender.sendMessage(ChatColor.RED + "Invalid Args");
                    }

                } else {
                    commandSender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                }
            }




        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if(args.length < 2){
            if (commandSender.hasPermission("todolist.see")){
                completions.add("list");
            }

            if (commandSender.hasPermission("todolist.edit")){
                completions.add("add");
            }
        }
        return completions;

    }
}
