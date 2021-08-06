/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.config;

import java.io.FileNotFoundException;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class ReadJSON {

    public static void read(String jsonObject, String jsonValue, GuildMessageReceivedEvent event) {

        try {

            ManageJSON json = new ManageJSON();

            event.getChannel().sendMessage(json.readLineString(jsonObject, jsonValue, event)).queue();

        } catch (FileNotFoundException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**");
        } catch (NullPointerException | java.lang.IllegalArgumentException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+", In object: "+jsonObject).queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+" in "+jsonObject);
        }
            

    }
    
    public static void read(String jsonObject, String jsonValue, GuildMessageReceivedEvent event, String extraInfo) {

        try {

            ManageJSON json = new ManageJSON();

            event.getChannel().sendMessage(json.readLineString(jsonObject, jsonValue, event, extraInfo)).queue();

        } catch (FileNotFoundException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**");
        } catch (NullPointerException | java.lang.IllegalArgumentException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+", In object: "+jsonObject).queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+" in "+jsonObject);
        }
            

    }
    
    public static void readToLog(String jsonObject, String jsonValue, GuildMessageReceivedEvent event, String extraInfo) {

        try {

            ManageJSON json = new ManageJSON();

            String logChannel = json.readLineString("config", "logchannel");
            
            event.getGuild().getTextChannelsByName(logChannel, true).get(0).sendMessage(json.readLineString(jsonObject, jsonValue, event, extraInfo)).queue();

        } catch (FileNotFoundException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**");
        } catch (NullPointerException | java.lang.IllegalArgumentException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+", In object: "+jsonObject).queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+" in "+jsonObject);
        }
            

    }
    
    public static void readToLog(String jsonObject, String jsonValue, GuildMessageReceivedEvent event) {

        try {

            ManageJSON json = new ManageJSON();

            String logChannel = json.readLineString("config", "logchannel");
            
            event.getGuild().getTextChannelsByName(logChannel, true).get(0).sendMessage(json.readLineString(jsonObject, jsonValue, event)).queue();

        } catch (FileNotFoundException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Make sure the bot can read to config**");
        } catch (NullPointerException | java.lang.IllegalArgumentException e) {
            event.getChannel().sendMessage(e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+", In object: "+jsonObject).queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReadJSON/read" + "\n**Probably invalid json value**\nTried to find: "+jsonValue+" in "+jsonObject);
        }
            

    }

}
