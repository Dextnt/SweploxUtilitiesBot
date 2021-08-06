/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import java.io.FileNotFoundException;
import me.dextnt.sweploxutilities.config.ManageJSON;
import me.dextnt.sweploxutilities.config.ReadJSON;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class VerifyCommand {

    public void run(GuildMessageReceivedEvent event) {

        String msg = event.getMessage().getContentRaw();
        String[] msgs = msg.split(" ");

        try {
            if (msgs[1].equals("")) {
            } //Checks if theres any message, otherwise it crashes due to ArrayOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {

            ReadJSON.read("messages", "verifiedusage", event);
            return; //return if messagecheck
            
        } 

        String member = msgs[1].replaceAll("\\D+", ""); //Removes the <@!> from the user tag

        try {

            ManageJSON json = new ManageJSON();

            try {
                if (!json.userIsPermitted(event, event.getAuthor().getId(), "verify")) { //Check for permissions
                    ReadJSON.read("messages", "lackofpermissions", event);
                    return;
                }
            } catch (Exception e) {
                event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
                System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
                return;
            }
            
            event.getGuild().addRoleToMember(member, event.getGuild().getRolesByName(json.readLineString("config", "defaultrole", event), true).get(0)).queue();
            try {
            event.getGuild().removeRoleFromMember(member, event.getGuild().getRolesByName(json.readLineString("config", "unverifiedrole"), true).get(0)).queue();
            } catch (Exception e) {     
            }
            
        } catch (java.lang.IllegalArgumentException e) {

            event.getChannel().sendMessage(e + " @ VerifyCommand/addrole" + "\n**UserID invalid, double-check your input.**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/addrole" + "\n**UserID invalid, double-check your input.**");
            return;

        } catch (FileNotFoundException e) {
            
            event.getChannel().sendMessage(e + " @ VerifyCommand/addrole" + "\n**Bot can probably not read from config.**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/addrole" + "\n**Bot can probably not read from config.**");
            return;
            
        }

        try {

            ManageJSON json = new ManageJSON();
            ReadJSON.read("messages", "verified", event);
            event.getGuild().getTextChannelsByName(json.readLineString("config", "verifiedchannel", event), true).get(0).sendMessage(json.readLineString("messages", "verifiedinverifiedchannel", event)).queue();

        } catch (Exception e) {

            System.out.println(e + " @ VerifyCommand/writeJSON" + "\n**Did you set the correct Verification channel in the config?**");
            event.getChannel().sendMessage(e + " @ VerifyCommand/writeJSON" + "\n**Probably an error in the config**").queue();

        }

    }

}
