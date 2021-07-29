/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import java.io.FileNotFoundException;
import me.dextnt.sweploxutilities.config.ManageJSON;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class ReloadJSON {

    public static void run(GuildMessageReceivedEvent event) {

        try {

            ManageJSON json = new ManageJSON();

            if (!json.userIsPermitted(event, event.getAuthor().getId(), "reloadjson")) { //Check for permissions
                event.getChannel().sendMessage(json.readLineString("messages", "lackofpermisions", event)).queue();
                return;
            }

            ManageJSON.reloadJSON();
            event.getChannel().sendMessage("**CONFIG SUCCESSFULLY RELOADED**").queue();
        } catch (FileNotFoundException e) {
            event.getChannel().sendMessage(e + " @ ReloadJSON.run" + "\n**Make sure the bot can read config.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ReloadJSON.run" + "\n**Make sure the bot can read config.json**");
        }
    }

}
