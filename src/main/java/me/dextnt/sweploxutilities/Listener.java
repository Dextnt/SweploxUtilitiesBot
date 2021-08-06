/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities;

import javax.security.auth.login.LoginException;
import me.dextnt.sweploxutilities.functions.DefaultJSON;
import me.dextnt.sweploxutilities.functions.HelpCommand;
import me.dextnt.sweploxutilities.functions.MoveCommand;
import me.dextnt.sweploxutilities.functions.NitroColor;
import me.dextnt.sweploxutilities.functions.ReloadJSON;
import me.dextnt.sweploxutilities.functions.Test;
import me.dextnt.sweploxutilities.functions.VerifyCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 *
 * @author Stryk3r
 */
public class Listener extends ListenerAdapter {

    public static void run(String botID) throws LoginException {

        JDA jda = JDABuilder.create(botID, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_TYPING)
                .setActivity(Activity.playing("\".help\", rip Harmaa Ghetto")).build();

        jda.addEventListener(new Listener());

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        try {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());
        } catch (Exception e) {
            //FIX THIS OMG
        }

        String arr[] = event.getMessage().getContentRaw().split(" ", 2);
        String firstWord = arr[0].toLowerCase(); //Gets first word of string

        switch (firstWord) {
            case ".verify":
                VerifyCommand verify = new VerifyCommand();
                verify.run(event);
                break;
            case ".test":
                Test.run(event);
                break;
            case ".defaultjson":
                DefaultJSON.run(event);
                break;
            case ".reloadjson":
                ReloadJSON.run(event);
                break;
            case ".nitrocolor":
                NitroColor.run(event);
                break;
            case ".nitrocolour":
                NitroColor.run(event);
                break;
            case ".nc":
                NitroColor.run(event);
                break;
            case ".adminnitrocolor":
                NitroColor.adminrun(event);
                break;
            case ".anc":
                NitroColor.adminrun(event);
                break;
            case ".help":
                HelpCommand.run(event);
                break;
            case ".adminmove":
                MoveCommand.adminrun(event);
                break;
            case ".amv":
                MoveCommand.adminrun(event);
                break;
            case ".move":
                MoveCommand.run(event);
                break;
            case ".mv":
                MoveCommand.run(event);
                break;
                
        }

    }

}
