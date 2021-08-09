/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.dextnt.sweploxutilities.config.ManageJSON;
import me.dextnt.sweploxutilities.config.NitroColorJSON;
import me.dextnt.sweploxutilities.config.ReadJSON;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 */
public class NitroColor {

    private static JsonElement JSONObject;

    public static void run(GuildMessageReceivedEvent event) {

        String arr[] = event.getMessage().getContentRaw().split(" ");

        String getFunction;

        try {
            getFunction = arr[1].toLowerCase(); //Checks for second word
        } catch (Exception e) {

            ReadJSON.read("messages", "nitrocolorusage", event);
            return;

        }

        NitroColor nc = new NitroColor();
        switch (getFunction) {
            case "set":
                nc.set(event);
                break;
            case "remove":
                nc.remove(event, true);
                break;
            case "forceupdate":
                nc.forceupdate(event);
                break;
            default:

                ReadJSON.read("messages", "nitrocolorusage", event);
        }

    }

    public static void adminrun(GuildMessageReceivedEvent event) {

        String arr[] = event.getMessage().getContentRaw().split(" ");

        String getFunction;

        try {
            getFunction = arr[1].toLowerCase(); //Checks for second word
        } catch (Exception e) {

            ReadJSON.read("messages", "nitrocolorusage", event);
            return;

        }

        NitroColor nc = new NitroColor();
        switch (getFunction) {
            case "set":
                try {
                nc.set(event, arr[2]);
                break;
            } catch (ArrayIndexOutOfBoundsException e) {
                ReadJSON.read("messages", "nitrocolorusage", event);
                return;
            }
            case "remove":  
                try {
                arr[2] = arr[2].substring(3, 21);
                nc.remove(event, arr[2], true);
                break;
            } catch (ArrayIndexOutOfBoundsException e) {
                ReadJSON.read("messages", "nitrocolorusage", event);
                return;
            }
            default:

                ReadJSON.read("messages", "nitrocolorusage", event);
        }

    }

    private void updateJSON() {

        try {
            JSONObject = new JsonParser().parse(new FileReader("nitrocolor.json"));
        } catch (FileNotFoundException e) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try {

                Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

                Map<String, String> colorMap = new HashMap() {
                };

                NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

                gson.toJson(preparedMap, writer);

                writer.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private void forceupdate(GuildMessageReceivedEvent event) {

        try {

            ManageJSON json = new ManageJSON();

            if (!json.userIsPermitted(event, event.getAuthor().getId(), "nitrocolor.forceupdate")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }

        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ ForceUpdate/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ ForceUpdate/Permissioncheck" + "\n**Check the config**");
            return;
        }

        update(event);

    }

    private void remove(GuildMessageReceivedEvent event, Boolean callUpdate) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "nitrocolor.remove")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }

        updateJSON();

        Map<String, String> colorMap = new HashMap() {
        };

        try {
            String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

            json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
            json = json.replaceAll(":", " ");
            json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regular expressions let me be
            String[] filteredjson = json.split(" ");

            String user = String.valueOf(event.getAuthor().getIdLong());
            boolean userExist = false;

            for (String s : filteredjson) {
                if (s.equalsIgnoreCase(user)) {
                    userExist = true;
                }
            }

            if (!userExist) {

                ReadJSON.read("messages", "nitrocolornotfound", event);
                return;

            }

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                if (!filteredjson[i].equalsIgnoreCase(String.valueOf(event.getAuthor().getIdLong()))) { //Only saves IDS that are not from the sender
                    colorMap.put(filteredjson[i], (filteredjson[i + 1]));
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            deleterole(event, String.valueOf(event.getAuthor().getIdLong()));

            if (callUpdate) {
                update(event);
            }

            ReadJSON.read("messages", "nitrocolorremoved", event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }

    }

    private void deleterole(GuildMessageReceivedEvent event, String User) {

        List<Role> roles = event.getGuild().getMemberById(User).getRoles();

        for (int i = 0; i < roles.size(); i++) {
            String[] split = roles.get(i).toString().split("[:()]");

            if (split[1].equals("NitroColor")) {

                event.getGuild().getRoleById(split[2]).delete().queue();

            }

        }

    }

    private void remove(GuildMessageReceivedEvent event, String User, Boolean callUpdate) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "nitrocolor.adminremove")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }

        updateJSON();

        Map<String, String> colorMap = new HashMap() {
        };
            
            String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

            json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
            json = json.replaceAll(":", " ");
            json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be
            String[] filteredjson = json.split(" ");

            boolean userExist = false;

            for (String s : filteredjson) {
                if (s.equalsIgnoreCase(User)) {
                    userExist = true;
                }
            }

            if (!userExist) {

                ReadJSON.read("messages", "nitrocolornotfound", event);
                return;

            }

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                if (!filteredjson[i].equalsIgnoreCase(User)) { //Only saves IDS that are not from the sender
                    System.out.println("asdasd: " + filteredjson[i]);
                    colorMap.put(filteredjson[i], (filteredjson[i + 1]));
                }
            }

      

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();
            deleterole(event, User);

            if (callUpdate) {
                update(event);
            }

            ReadJSON.read("messages", "nitrocolorremoved", event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }

    }

    private void update(GuildMessageReceivedEvent event) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "nitrocolor.forceupdate")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }

        
        
        
        List<Member> totMembers = event.getGuild().getMembers();

        for (int i = 0; i < totMembers.size(); i++) {

            List<Role> roles = totMembers.get(i).getRoles();

            Boolean nitroColor = false;
            Boolean nitroBooster = false;
            String nitroColorID = "";
            String userID = totMembers.get(i).getId();

            for (int x = 0; x < roles.size(); x++) { //Goes through all the members roles

                Role currentRole = roles.get(x);
                String name = currentRole.getName();

                if (name.equals("NitroColor")) {
                    System.out.println(event.getGuild().getMemberById(userID).getEffectiveName() + " HAS NITROCOLOR");
                    nitroColor = true;
                    nitroColorID = currentRole.getId();
                }

                if (name.equals("Nitro Booster")) {
                    System.out.println(event.getGuild().getMemberById(userID).getEffectiveName() + " HAS NITRO BOOSTER");
                    nitroBooster = true;
                }

            }

            updateJSON();
            JsonObject json = JSONObject.getAsJsonObject().getAsJsonObject("nitrocolor");

            if (nitroColor && !nitroBooster) {

                System.out.println("1");
                remove(event, userID, false);

            } else if (!nitroColor && nitroBooster) {

                try {
                    String crashTest = json.get(userID).getAsString();
                } catch (NullPointerException e) {
                    continue;
                }

                final String HEX = json.get(userID).getAsString();
                final String finalUserID = userID;

                event.getGuild().createRole()
                        .setName("NitroColor")
                        .setColor(Integer.parseInt(HEX, 16))
                        .setMentionable(false)
                        .setPermissions()
                        .queue(Role -> {

                            event.getGuild().modifyRolePositions(true).queue();

                            int startPos = event.getGuild().getRolesByName("NitroColorStart", false).get(0).getPosition(); //Gets the position of "NitroColorStart"

                            event.getGuild().addRoleToMember(finalUserID, Role).queue();
                            event.getGuild().modifyRolePositions().selectPosition(Role).moveTo(startPos - 1).queue();
                            event.getGuild().modifyRolePositions(false).queue();

                        });

            } else if (nitroColor && nitroBooster) {
                
                Color clr = event.getGuild().getRoleById(nitroColorID).getColor();
                String userHex;
                String jsonHex;

                try {
                    jsonHex = json.get(userID).getAsString();
                    System.out.println(jsonHex);
                } catch (NullPointerException e) {
                    continue;
                }

                if (clr != null) { //Checks that the role has a colour
                    userHex = String.format("%02x%02x%02x", clr.getRed(), clr.getGreen(), clr.getBlue()); //Converts color value from decimal to userHex
                } else {
                    userHex = "";
                }

                if (!userHex.equalsIgnoreCase(jsonHex)) {

                    int hexindecimal = Integer.parseInt(jsonHex, 16); //converts it back
                    event.getGuild().getRoleById(nitroColorID).getManager().setColor(hexindecimal).queue(); //Sets the colour

                }

            }
        }

    }

    
    private void set(GuildMessageReceivedEvent event) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "nitrocolor.set")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }

        String[] HEXArray = event.getMessage().getContentRaw().split(" ");

        try {

            HEXArray[2].getBytes(); //Check of crash

        } catch (Exception e) {

            ReadJSON.read("messages", "nitrocolorusage", event);
            return;

        }

        String HEX = HEXArray[2].replaceAll("#", "");

        if (!HEX.matches("[0-9a-fA-F]{6}")) {
            ReadJSON.read("messages", "nitrocolorinvalidhex", event);
            return;
        }

        if (HEX.equalsIgnoreCase("000000")) {
            HEX = "000001"; //For some fucking reason 000000 gives none
        }

        updateJSON(); //Starts parsing file
        Map<String, String> colorMap = new HashMap() {
        };

        String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

        System.out.println("-" + json + "-");

        if (!json.equals("{}")) {

            json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
            json = json.replaceAll(":", " ");
            json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be
            String[] filteredjson = json.split(" ");

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                colorMap.put(filteredjson[i], (filteredjson[i + 1]));

            }

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                if (HEX.equalsIgnoreCase(filteredjson[i + 1]) && String.valueOf(event.getAuthor().getIdLong()).equalsIgnoreCase(filteredjson[i])) {

                    ReadJSON.read("messages", "nitrocolorduplicate", event);
                    return;
                }

            }

        }

        colorMap.put(event.getAuthor().getId(), HEX);

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            ReadJSON.read("messages", "nitrocolorsuccess", event);
            update(event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }
    }

    private void set(GuildMessageReceivedEvent event, String User) {

        ManageJSON manageJson = new ManageJSON();

        try {
            if (!manageJson.userIsPermitted(event, event.getAuthor().getId(), "nitrocolor.adminset")) { //Check for permissions
                ReadJSON.read("messages", "lackofpermissions", event);
                return;
            }
        } catch (Exception e) {
            event.getChannel().sendMessage(e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ VerifyCommand/Permissioncheck" + "\n**Check the config**");
            return;
        }

        updateJSON();

        String[] HEXArray = event.getMessage().getContentRaw().split(" ");

        try {

            HEXArray[3].getBytes(); //Check of crash, make sure that user have written a third arg

        } catch (Exception e) {
            ReadJSON.read("messages", "nitrocolorusage", event);
            return;
        }

        String HEX = HEXArray[3].replaceAll("#", "");

        if (!HEX.matches("[0-9a-fA-F]{6}")) {
            ReadJSON.read("messages", "nitrocolorinvalidhex", event);
            return;
        }

        if (HEX.equalsIgnoreCase("000000")) {
            HEX = "000001"; //For some fucking reason 000000 gives none
        }

        User = User.substring(3, 21); //Converts the userid into valid snowflake

        Map<String, String> colorMap = new HashMap() {
        };

        String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

        if (!json.equals("{}")) {

            json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
            json = json.replaceAll(":", " ");
            json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be
            String[] filteredjson = json.split(" ");

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                colorMap.put(filteredjson[i], (filteredjson[i + 1]));
            }

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                if (HEX.equalsIgnoreCase(filteredjson[i + 1]) && String.valueOf(User).equalsIgnoreCase(filteredjson[i])) {

                    ReadJSON.read("messages", "nitrocolorduplicate", event);
                    return;
                }

            }

        }

        colorMap.put(User, HEX);

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            ReadJSON.read("messages", "nitrocolorsuccess", event);

            update(event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }
    }

}
