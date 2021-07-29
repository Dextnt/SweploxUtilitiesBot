/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class ConvertJSON {

    public String getAffectedMemberID(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] msgs = msg.split(" ");
        String member = msgs[1].replaceAll("\\D+", "");
        return member;
    }
    
    public String getGenericReason(GuildMessageReceivedEvent event) {
        
        
        String msg = event.getMessage().getContentRaw();
        String[] msgs = msg.split(" ");
        
        try {
        if (msgs[2].equals("")) {} //Checks if theres any message, otherwise it crashes due to ArrayOutOfBoundsException
        }catch (Exception e){
            return "No reason";
        }
            
        
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < msgs.length; i++) {
            sb.append(msgs[i]).append(" ");
        }

        String finalMessage = sb.substring(0, sb.length() - 1);
        
        return finalMessage;
        
    }

    public String toString(String JSON, GuildMessageReceivedEvent event) {

        String[] filter = JSON.split(" "); //Splits all words into different pieces

        for (int i = 0; i < filter.length; i++) {
            if (filter[i].startsWith("{{") & filter[i].endsWith("}}")) { //Checks for {{}} in config

                switch (filter[i]) { //If it finds a match it'll convert it into the neccessary information
                    case "{{affecteduserbymention}}":
                        filter[i] = event.getGuild().getMemberById(getAffectedMemberID(event)).getAsMention();
                        break;
                    case "{{sender}}":
                        filter[i] = event.getAuthor().getAsMention();
                        break;
                    case "{{currenttime}}":
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        filter[i] = date.toString();
                        break;
                    case "{{genericreason}}":
                        filter[i] = getGenericReason(event);
                        break;
                    case "{{permittedroles}}":
                        filter[i] = "[ADMIN, MODERATOR, OWNER, NEBULA, test FIXA SEN]";
                        break;
                    default:
                        filter[i] = "{INVALID TAG}";
                }

            }
        }

        StringBuilder sb = new StringBuilder();
        for (String str : filter) {
            sb.append(str).append(" ");
        }

        String finalMessage = sb.substring(0, sb.length() - 1);

        return finalMessage;

    }

}
