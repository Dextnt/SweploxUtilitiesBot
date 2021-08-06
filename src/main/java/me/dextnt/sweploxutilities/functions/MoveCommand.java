/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.dextnt.sweploxutilities.config.ManageJSON;
import me.dextnt.sweploxutilities.config.MoveCommandJSON;
import me.dextnt.sweploxutilities.config.ReadJSON;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class MoveCommand {

    private static JsonElement JSONObject;

    public static void run(GuildMessageReceivedEvent event) {

        String arr[] = event.getMessage().getContentRaw().split(" ");
        
        if (arr.length == 1) {
            ReadJSON.read("messages", "adminmoveusage", event);
            return;
        }
        
        
        
        MoveCommand mc = new MoveCommand();
        
        mc.move(event);
        
        

    }

    public static void adminrun(GuildMessageReceivedEvent event) {

        String arr[] = event.getMessage().getContentRaw().split(" ");

        String getFunction;

        try {
            getFunction = arr[1].toLowerCase(); //Checks for second word
        } catch (Exception e) {

            ReadJSON.read("messages", "adminmoveusage", event);
            return;

        }

        MoveCommand mc = new MoveCommand();
        switch (getFunction) {
            case "set":
                mc.set(event);
                break;
            case "remove":
                mc.remove(event);
                break;
            case "list":
                mc.list(event);
                break;
            default:
                ReadJSON.read("messages", "adminmoveusage", event);
        }

    }

    public String getIDFromAlias(String Alias) {

        updateJSON();

        String json = JSONObject.getAsJsonObject().get("channels").toString();

        Pattern channelIDs = Pattern.compile("[0-9]{18}"); //Finds all 16 number long strings
        Matcher matchChannel = channelIDs.matcher(json);


        while (matchChannel.find()) { //If a valid ID is found

            String filteredString = matchChannel.group(0).replaceAll("[\\\":\\[{]", ""); //Removes all unnecessary characters

            JsonArray jsonArr;

            try {


                jsonArr = JSONObject.getAsJsonObject().getAsJsonObject("channels").get(filteredString).getAsJsonArray();  //Gets all aliases from that ID

            } catch (NullPointerException e) {
                return null;
            }


            for (int i = 0; i < jsonArr.size(); i++) { //Checks if the alias from json match the alias the user sent

                String filteredJson = jsonArr.get(i).toString().replace("\"", "");

                if (filteredJson.equalsIgnoreCase(Alias)) {
                    return filteredString; //If so send back the ID from that channel with matching alias
                }

            }

        }
        return null;
    }

    public void move(GuildMessageReceivedEvent event) {

        ManageJSON json = new ManageJSON();

        try {
            if (!json.userIsPermitted(event, event.getAuthor().getId(), "movecommand.move")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }

        String msg = event.getMessage().getContentRaw();

        String channelID = "";

        Pattern userID = Pattern.compile("<@!.{18}>"); //Checks for users in message
        Matcher matchUserID = userID.matcher(msg);

        Pattern aliasInput = Pattern.compile("(\\s+([a-zA-Z0-9]+\\s+)+)");
        Matcher aliasInputMatcher = aliasInput.matcher(msg);

        String validAlias = "";

        if (aliasInputMatcher.find()) { //Goes though the alias

            validAlias = aliasInputMatcher.group(0);

            validAlias = validAlias.substring(1, validAlias.length() - 1); //Removes spaces before and after the alias

        }

        int userCount = 0;
        ArrayList<String> users = new ArrayList<>();

        try {

            channelID = getIDFromAlias(validAlias);

            if (!event.getGuild().getMemberById(event.getAuthor().getId()).hasPermission(event.getGuild().getVoiceChannelById(channelID), Permission.VOICE_CONNECT)) {

                ReadJSON.read("messages", "movecommandlackofpermissions", event);
                return;

            }

        } catch (IllegalArgumentException e) {

            ReadJSON.read("messages", "movecommandinvalidalias", event);
            return;

        }

        while (matchUserID.find()) {

            String ID = matchUserID.group(0).replaceAll("\\D+", ""); //Removes <@!> from user

            users.add(ID);

            userCount++;

        }

        String[] usersArr = new String[users.size()];

        if (userCount++ > 5) {
            ReadJSON.read("messages", "movecommandtoomanymembers", event);
            return;
        }

        boolean min1UserMoved = false; //Check for the "success" message later because my head just couldn't let the chance of all the users failing to move and it still posts "success"

        StringBuilder sb = new StringBuilder();

        for (String s : users.toArray(usersArr)) {

            String userByNickname;

            try {

                userByNickname = event.getGuild().getMemberById(s).getEffectiveName();

            } catch (java.lang.NullPointerException e) {

                event.getChannel().sendMessage("\n**UserID ``" + s + "`` is not a valid user!**").queue();
                System.out.println("<SWEPLOX UTILITIES> " + e + " @ MoveCommand/moveusers" + " **UserID (" + s + " ) is not a valid user!**");
                continue;

            }

            try {

                event.getGuild().moveVoiceMember(event.getGuild().getMemberById(s), event.getGuild().getVoiceChannelById(channelID)).queue();
                sb.append("``" + userByNickname + "`` ");
                min1UserMoved = true;

            } catch (java.lang.IllegalStateException e) {

                event.getChannel().sendMessage("\n**User ``" + userByNickname + "`` not in a voice channel!**").queue();
                System.out.println("<SWEPLOX UTILITIES> " + userByNickname + " @ MoveCommand/moveusers" + " **User (" + s + ")not in a voice channel!**");

            }

        }

        if (min1UserMoved) {
            ReadJSON.read("messages", "movecommandsuccess", event, sb.toString());
            ReadJSON.readToLog("messages", "movecommandsuccesslog", event, sb.toString());
        }

    }

    private void updateJSON() {

        try {
            JSONObject = new JsonParser().parse(new FileReader("movecommand.json"));
        } catch (FileNotFoundException e) {
            JSONObject = null;

        }

    }

    public void remove(GuildMessageReceivedEvent event) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "movecommand.adminremove")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }
        
        updateJSON();

        Map<String, String[]> channelMap = new HashMap() {
        };

        String json = JSONObject.getAsJsonObject().get("channels").toString();

        
        json = json.replaceAll("[\\[\\]]", "¤");
        json = json.replaceAll("[{}]", "");
        
        
        String[] splitJson = json.split("¤");

        Pattern pattern = Pattern.compile("[0-9]{18}");
        Matcher matcher2 = pattern.matcher(json);
        
        ArrayList<String> arrList = new ArrayList<>();
        
        while (matcher2.find()) {
            
            arrList.add(matcher2.group(0));
            
        }
        
        String[] idArr = new String[arrList.size()];
        arrList.toArray(idArr);
        
        String[] msg = event.getMessage().getContentRaw().split(" ");

        if (msg.length < 3) {
            ReadJSON.read("messages", "adminmoveusage", event);
            return;
        }

        String channelID = msg[2];

        Matcher matcher = pattern.matcher(channelID);

        if (matcher.find()) {
            channelID = matcher.group(0);
        } else {
            ReadJSON.read("messages", "adminmoveinvalidchannelid", event);
            return;
        }

        boolean itemRemoved = false;

        try {

            for (int i = 0; i < splitJson.length; i = i + 2) { //Getting the old information

                splitJson[i] = splitJson[i].replaceAll("[{}\\\"]", "");
                String jsonChannelID = idArr[i/2];

                splitJson[i + 1] = splitJson[i + 1].replaceAll("[{}\\\"]", "");
                splitJson[i + 1] = splitJson[i + 1].replaceAll("[,:]", "¤");
                String[] aliases = splitJson[i + 1].split("¤");
                
                if (channelID.equalsIgnoreCase(jsonChannelID)) {
                    itemRemoved = true;
                    continue;
                }

                channelMap.put(jsonChannelID, aliases);

            }

        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (!itemRemoved) {

            ReadJSON.read("messages", "genericnochanges", event);
            return;

        }

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("movecommand.json"));

            MoveCommandJSON preparedMap = new MoveCommandJSON(channelMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            ReadJSON.read("messages", "movecommandremovesuccess", event, channelID);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ MoveCommand/remove/savetojson" + "\n**Make sure the bot can write to MoveCommand.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ MoveCommand/remove/savetojson" + "\n**Make sure the bot can write to MoveCommand.json**");

        }
    }

    public void list(GuildMessageReceivedEvent event) {

        
        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "movecommand.adminlist")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }
        
        updateJSON();

        String json = JSONObject.getAsJsonObject().get("channels").toString();

        json = json.replaceAll("[\\[\\]]", "¤");
        json = json.replaceAll("[{}]", "");
        String[] splitJson = json.split("¤");

        StringBuilder sb = new StringBuilder();

        sb.append("**__Channel Alias List: __** \n\n");

        try {

            for (int i = 0; i < splitJson.length; i = i + 2) { //Getting the old information

                splitJson[i] = splitJson[i].replaceAll("[{}\\\"]", "");
                String jsonChannelID = splitJson[i].replaceAll("[,:]", "");

                splitJson[i + 1] = splitJson[i + 1].replaceAll("[{}\\\"]", "");
                splitJson[i + 1] = splitJson[i + 1].replaceAll("[,:]", "¤");
                String[] aliases = splitJson[i + 1].split("¤");

                sb.append("**" + jsonChannelID + ":** \n");

                for (String s : aliases) {
                    sb.append("\"" + s + "\"\n");
                }

                sb.append("\n\n");

            }

        } catch (ArrayIndexOutOfBoundsException e) {
        }

        event.getChannel().sendMessage(sb.toString()).queue();

    }

    public void set(GuildMessageReceivedEvent event) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "movecommand.adminset")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }
        
        updateJSON();

        Map<String, String[]> channelMap = new HashMap() {
        };

        String json = JSONObject.getAsJsonObject().get("channels").toString();

        json = json.replaceAll("[\\[\\]]", "¤");
        json = json.replaceAll("[{}]", "");
        String[] splitJson = json.split("¤");

        String[] msg = event.getMessage().getContentRaw().split(" ");

        if (msg.length < 4) {
            ReadJSON.read("messages", "adminmoveusage", event);
            return;
        }

        String channelID = msg[2];

        Pattern pattern = Pattern.compile("[0-9]{18}"); //Makes sure that the user has given a valid channel ID
        Matcher matcher = pattern.matcher(channelID);

        if (matcher.find()) {
            channelID = matcher.group(0);
        } else {
            ReadJSON.read("messages", "adminmoveinvalidchannelid", event);
            return;
        }

        ArrayList<String> aliasTot = new ArrayList<>();

        try {

            for (int i = 0; i < splitJson.length; i = i + 2) { //Getting the old information

                splitJson[i] = splitJson[i].replaceAll("[{}\\\"]", "");
                String jsonChannelID = splitJson[i].replaceAll("[,:]", "");

                splitJson[i + 1] = splitJson[i + 1].replaceAll("[{}\\\"]", "");
                splitJson[i + 1] = splitJson[i + 1].replaceAll("[,:]", "¤");
                String[] aliases = splitJson[i + 1].split("¤");

                for (String s : aliases) {
                    aliasTot.add(s);
                }

                if (channelID.equalsIgnoreCase(jsonChannelID)) {
                    ReadJSON.read("messages", "adminmovesetduplicate", event);
                    return;
                }

                channelMap.put(jsonChannelID, aliases);

            }

        } catch (ArrayIndexOutOfBoundsException e) {
        }

        ArrayList<String> aliases = new ArrayList<>();

        StringBuilder aliasesString = new StringBuilder(""); //SB for Regex to find aliases that the user sent

        for (int i = 3; i < msg.length; i++) {
            aliasesString.append(msg[i] + " ");
        }

        Pattern findString = Pattern.compile("\"(.*?)\"");
        Matcher matcher2 = findString.matcher(aliasesString);

        Pattern pattern1 = Pattern.compile("[,:{}\\[\\]¤\"]");

        while (matcher2.find()) {

            boolean duplicateCheck = false;

            String alias = matcher2.group(1);

            Matcher matcher1 = pattern1.matcher(alias);

            if (matcher1.find()) {
                ReadJSON.read("messages", "adminmoveinvalidinput", event, alias);
            } else {

                for (String s : aliasTot) {

                    if (s.equalsIgnoreCase(alias)) {
                        duplicateCheck = true;
                    }

                }

                if (!duplicateCheck) {
                    aliases.add(alias);
                }

            }

        }

        String[] aliasesArr = new String[aliases.size()];
        channelMap.put(channelID, aliases.toArray(aliasesArr));

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("movecommand.json"));

            MoveCommandJSON preparedMap = new MoveCommandJSON(channelMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            ReadJSON.read("messages", "movecommandsetsuccess", event, channelID);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ MoveCommand/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ MoveCommand/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }
    }

}
