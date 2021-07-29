/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

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
                nc.remove(event);
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
                nc.remove(event, arr[2]);
                break;
            } catch (ArrayIndexOutOfBoundsException e) {
                ReadJSON.read("messages", "nitrocolorusage", event);
                return;
            }
            default:

                ReadJSON.read("messages", "nitrocolorusage", event);
        }

    }

    private void load() {

        try {
            JSONObject = new JsonParser().parse(new FileReader("nitrocolor.json"));
        } catch (FileNotFoundException e) {
            JSONObject = null;
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

    private void remove(GuildMessageReceivedEvent event) {

        load();

        Map<String, String> colorMap = new HashMap() {
        };

        try {
            String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

            json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
            json = json.replaceAll(":", " ");
            json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be
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
            update(event);

            ReadJSON.read("messages", "nitrocolorremoved", event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }

    }

    private void deleterole(GuildMessageReceivedEvent event, String User) {

        List<Role> roles = event.getGuild().getMemberById(User).getRoles();

        System.out.println("");

        for (int i = 0; i < roles.size(); i++) {
            String[] split = roles.get(i).toString().split("[:()]");

            if (split[1].equals("NitroColor")) {

                event.getGuild().getRoleById(split[2]).delete().queue();

            }

        }

    }

    private void remove(GuildMessageReceivedEvent event, String User) {

        load();

        Map<String, String> colorMap = new HashMap() {
        };

        try {

            User = User.substring(3, 21);

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

        } catch (Exception e) {
            System.out.println(e);
        }

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            deleterole(event, User);
            update(event);

            ReadJSON.read("messages", "nitrocolorremoved", event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/remove/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }

    }

    private void update(GuildMessageReceivedEvent event) {

        load();

        String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

        boolean messageSent = false;

        json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
        json = json.replaceAll(":", " ");
        json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be

        if (json.equalsIgnoreCase("")) {
            System.out.println("<SWEPLOX UTILITIES> NitroColor list is empty, won't execute NitroColor.update()");
            return;
        }

        String[] filteredJson = json.split(" ");

        for (int i = 0; i < filteredJson.length; i = i + 2) {

            boolean rolecheck = false; //Check if you need to create the NitroColor role

            try {

                List<Role> roles = event.getGuild().getMemberById(filteredJson[i]).getRoles(); //Gets list of all roles from the current member in the nitrocolor list

                for (int x = 0; x < roles.size(); x++) {

                    String[] split = roles.get(x).toString().split("[:()]"); //Removes splits the roles at : ( and )

                    if (split[1].equalsIgnoreCase("NitroColor")) {

                        rolecheck = true;

                        Color clr = event.getGuild().getRoleById(split[2]).getColor();
                        String hex;

                        if (clr != null) { //Checks that the role has a colour
                            hex = String.format("%02x%02x%02x", clr.getRed(), clr.getGreen(), clr.getBlue()); //Converts color value from decimal to hex
                        } else {
                            hex = "";
                        }

                        if (!hex.equalsIgnoreCase(filteredJson[i + 1])) {

                            int hexindecimal = Integer.parseInt(filteredJson[i + 1], 16); //converts it back
                            event.getGuild().getRoleById(split[2]).getManager().setColor(hexindecimal).queue(); //Sets the colour

                            if (!messageSent) {
                                ReadJSON.read("messages", "nitrocolorsuccess", event);
                                messageSent = true;
                            }

                        }

                    }

                }

            } catch (Exception e) {

                System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/update/changerolecolour" + "\n**Check nitrocolor.json**");
                event.getChannel().sendMessage(e + " @ Nitrocolor/update/changerolecolour" + "\n**Check nitrocolor.json**").queue();

            }

            if (!rolecheck) {

                event.getGuild().modifyRolePositions(true).queue();

                final String filteredJsonButFinal = filteredJson[i];

                event.getGuild().createRole()
                        .setName("NitroColor")
                        .setColor(Integer.parseInt(filteredJson[i + 1], 16))
                        .setMentionable(false)
                        .setPermissions()
                        .queue(Role -> {

                            int startPos = event.getGuild().getRolesByName("NitroColorStart", false).get(0).getPosition(); //Gets the position of "NitroColorStart"

                            event.getGuild().addRoleToMember(filteredJsonButFinal, Role).queue();
                            event.getGuild().modifyRolePositions().selectPosition(Role).moveTo(startPos - 1).queue();

                            ReadJSON.read("messages", "nitrocolornewrolesuccess", event);

                        });

            }

        }
    }

    private void set(GuildMessageReceivedEvent event) {

        load();

        Map<String, String> colorMap = new HashMap() {
        };

        String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

        json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
        json = json.replaceAll(":", " ");
        json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be
        String[] filteredjson = json.split(" ");

        for (String s : filteredjson) {
            System.out.println(s);
        }

        for (int i = 0; i < filteredjson.length; i = i + 2) {

            colorMap.put(filteredjson[i], (filteredjson[i + 1]));
            System.out.println("filteeded: " + filteredjson[i] + (filteredjson[i + 1]));

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

        for (int i = 0; i < filteredjson.length; i = i + 2) {

            if (HEX.equalsIgnoreCase(filteredjson[i + 1]) && String.valueOf(event.getAuthor().getIdLong()).equalsIgnoreCase(filteredjson[i])) {

                ReadJSON.read("messages", "nitrocolorduplicate", event);
                return;
            }

        }

        colorMap.put(event.getAuthor().getId(), HEX);

        System.out.println(colorMap.toString());

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            update(event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }
    }

    private void set(GuildMessageReceivedEvent event, String User) {

        load();

        User = User.substring(3, 21); //Converts the userid into valid snowflake

        Map<String, String> colorMap = new HashMap() {
        };

        try {
            String json = JSONObject.getAsJsonObject().get("nitrocolor").toString();

            json = json.replaceAll("[\\Q][(){}\".;!?<>%\\E]", "");
            json = json.replaceAll(":", " ");
            json = json.replaceAll(",", " "); //Filtering away everything and replaces " with space. i dont know regualar expressions let me be
            String[] filteredjson = json.split(" ");

            for (String s : filteredjson) {
                System.out.println(s);
            }

            for (int i = 0; i < filteredjson.length; i = i + 2) {

                colorMap.put(filteredjson[i], (filteredjson[i + 1]));
                System.out.println("filteeded: " + filteredjson[i] + (filteredjson[i + 1]));
            }

        } catch (Exception e) {
            System.out.println("wherewewe" + e);
        }

        String[] HEXArray = event.getMessage().getContentRaw().split(" ");

        try {

            HEXArray[3].getBytes(); //Check of crash, make sure that user have written a third arg

        } catch (Exception e) {
            ReadJSON.read("messages", "nitrocolorusage", event);
            return;
        }

        String HEX = HEXArray[3].replaceAll("#", "");

        System.out.println("hexehx:" + HEX);

        if (!HEX.matches("[0-9a-fA-F]{6}")) {
            ReadJSON.read("messages", "nitrocolorinvalidhex", event);
            return;
        }

        if (HEX.equalsIgnoreCase("000000")) {
            HEX = "000001"; //For some fucking reason 000000 gives none
        }

        colorMap.put(User, HEX);

        System.out.println(colorMap.toString());

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Writer writer = Files.newBufferedWriter(Paths.get("nitrocolor.json"));

            NitroColorJSON preparedMap = new NitroColorJSON(colorMap);

            gson.toJson(preparedMap, writer);

            writer.close();

            update(event);

        } catch (IOException e) {

            event.getChannel().sendMessage(e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**").queue();
            System.out.println("<SWEPLOX UTILITIES> " + e + " @ Nitrocolor/set/savetojson" + "\n**Make sure the bot can write to nitrocolor.json**");

        }
    }

}
