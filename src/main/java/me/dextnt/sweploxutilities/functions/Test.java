/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import me.dextnt.sweploxutilities.config.ManageJSON;
import me.dextnt.sweploxutilities.config.ReadJSON;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class Test {

    public static void run(GuildMessageReceivedEvent event) {
        
        MoveCommand owo = new MoveCommand();
        
        owo.move(event);

    }

}
