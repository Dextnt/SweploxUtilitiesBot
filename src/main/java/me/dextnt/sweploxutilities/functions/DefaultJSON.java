/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import java.io.IOException;
import me.dextnt.sweploxutilities.config.ManageJSON;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class DefaultJSON {

    public static void run(GuildMessageReceivedEvent event) {

        try {

            ManageJSON json = new ManageJSON();

             if (!json.userIsPermitted(event, event.getAuthor().getId(), "defaultjson")) { //Check for permissions
                event.getChannel().sendMessage(json.readLineString("messages", "lackofpermisions", event)).queue();
                return;
            } 

            json.writeDefault();
            event.getChannel().sendMessage("**CONFIG SUCCESSFULLY DEFAULTED**").queue();
            ReloadJSON.run(event);
        } catch (IOException e) {
            event.getChannel().sendMessage(e + " @ DefaultJSON.set" + "\n**Make sure the bot can write to config.json**").queue();
        }
    }

}
