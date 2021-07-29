/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author Stryk3r
 *
 */
public class ManageJSON {

    private static JsonElement JSONObject;

    public void writeDefault() throws IOException {

        Map<String, String> configMap = new HashMap() {
            {
                put("verifiedchannel", "verified");
                put("defaultrole", "member");
                put("botid", "ODU0NDk2MTgwNjY1NTgxNTk4.YMkxlQ.DA6CekO5JJVLOisxQCbeGKtPplM");
            }
        };

        Map<String, String> messageMap = new HashMap() {
            {
                put("lackofpermissions", "**You don't have the permission to execute that command**\nRequired: {{permittedroles}}");
                put("verified", "{{affecteduserbymention}} has been verified by {{sender}}");
                put("verifiedinverifiedchannel", "{{affecteduserbymention}} has been verified by {{sender}} \n At: {{currenttime}} , Reason: {{genericreason}}");
                put("verifiedusage", "**Usage:** .verify [user] [reason] \n**Required:** {{permittedroles}}");
                put("nitrocolorusage", "**Usage:** .nitrocolor ``set`` ``<colour in hex>`` OR ``remove`` \n**Required:** {{permittedroles}}");
                put("nitrocolorinvalidhex", "**Invalid HEX Code**\n**Example format:** .nitrocolor ``set`` FFFFFF");
                put("nitrocolorsuccess", "**Nitro color changed!**");
                put("nitrocolornewrolesuccess", "**NitroColor role granted!**");
                put("nitrocolorremoved", "**Nitro color successfully removed!**");
                put("nitrocolornotfound", "**You don't have a nitro color!**");
                put("nitrocolorduplicate", "**You already have that nitro color!**");
            }
        };

        Map<String, String[]> permissionsMap = new HashMap() {
            {
                put("verify", new String[]{"Administrator", "Moderator", "Owner", "Nebula"});
                put("reloadjson", new String[]{"Administrator", "Owner"});
                put("defaultjson", new String[]{"Administrator", "Owner"});
                put("nitrocolor.set", new String[]{"Nitro Booster"});
                put("nitrocolor.remove", new String[]{"Nitro Booster"});
                put("nitrocolor.adminset", new String[]{"Admin", "Owner"});
                put("nitrocolor.adminremove", new String[]{"Administrator", "Owner"});
                put("nitrocolor.forceupdate", new String[]{"Administrator", "Owner"});
            }
        };

        Map<String, String> helpMap = new HashMap() {
            {
                put("help", "**__Sweplox Utilities Help__**\n"
                        + "\n"
                        + "SweploxUtilities ver. 1.0, written by Dextnt\n"
                        + "https://github.com/asdpoafomkasd\n"
                        + "\n"
                        + "**Commands list:**\n"
                        + "\n"
                        + "> `.verify` - Used by staff to verify a new member into the server\n"
                        + "__Usage:__  ``.verify``  ``@User``\n"
                        + "\n"
                        + "\n"
                        + "> `.nitrocolor` - Used by members with the Nitro Booster role to claim or remove their color\n"
                        + "__Usage:__  ``.nitrocolor / .nc`` ``remove`` OR ``set`` ``color in hex``\n"
                        + "\n"
                        + "\n"
                        + "> `.help` - The command you're using right now\n"
                        + "__Usage:__  ``.help`` \n"
                        + "\n"
                        + "\n"
                        + "> `.adminnitrocolor` - Used by admin to change and remove other members nitro colors \n"
                        + "__Usage:__  ``.adminnitrocolor / .nc`` ``remove`` ``@User``  OR  ``set`` ``@User`` ``color in hex``\n"
                        + "\n"
                        + "\n"
                        + "> `.move` - Used to move one or multiple users to another voice channel \n"
                        + "__Usage:__  ``.move / .mv`` ``channel`` ``@User`` ``@User`` (Up to 5 users)\n"
                        + "\n"
                        + "\n"
                        + "> `.movemyself` - Used to move yourself + one or multiple users to another voice channel \n"
                        + "__Usage:__  ``.movemyself / .mvm`` ``channel`` ``@User`` ``@User`` (Up to 5 users)\n"
                        + "\n"
                        + "**Upcoming features**\n"
                        + "\n"
                        + "``.ChangeBackground``\n"
                        + "\n"
                        + "``\"Active channels\"``\n"
                        + "\n"
                        + "``.AutoResponder``");
            }
        };

        ConfigJSON config = new ConfigJSON(configMap, messageMap, permissionsMap, helpMap);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Writer writer = Files.newBufferedWriter(Paths.get("config.json"));

        gson.toJson(config, writer);

        writer.close();
    }

    public void startUpCheck() {

        try {
            System.out.println("<SWEPLOX UTILITIES> Searching for config.json");
            String failCheck = new FileReader("config.json").toString();
        } catch (FileNotFoundException e) {
            System.out.println("<SWEPLOX UTILITIES> config.json not found, generating new");

            try {
                writeDefault();
                return;
            } catch (Exception ex) {
                System.out.println("<SWEPLOX UTILITIES> ERROR: Failed to create JSON");
                return;
            }
        }

        System.out.println("<SWEPLOX UTILITIES> config.json Successfully found");

    }

    public static JsonObject getJSON() throws FileNotFoundException {

        if (JSONObject != null) {
            return JSONObject.getAsJsonObject();
        } else {
            System.out.println("<SWEPLOX UTILITIES> Creating new JSON instance");
            JSONObject = new JsonParser().parse(new FileReader("config.json"));
            return JSONObject.getAsJsonObject();
        }

    }

    public static void reloadJSON() throws FileNotFoundException {
        JSONObject = new JsonParser().parse(new FileReader("config.json")).getAsJsonObject();
    }

    public String[] readPermission(String JSONvalue) throws NullPointerException {

        JsonArray pageName;

        try {

            pageName = getJSON().getAsJsonObject("permissions").get(JSONvalue).getAsJsonArray();

            String[] permissions = new String[pageName.size()];

            for (int i = 0; i < pageName.size(); i++) {
                permissions[i] = pageName.get(i).getAsString();
            }

            return permissions;

        } catch (FileNotFoundException e) {
            System.out.println("JSON EXCEPTION: " + e);

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;

    }

    public boolean userIsPermitted(GuildMessageReceivedEvent event, String UserID, String JSONValue) {

        String[] permissionsFromJSON = readPermission(JSONValue);
        String User = UserID.replaceAll("\\D+", "");

        try {
            for (int i = 0; i < permissionsFromJSON.length; i++) {

                for (int x = 0; x < event.getGuild().getMemberById(User).getRoles().size(); x++) {

                    if (permissionsFromJSON[i].equalsIgnoreCase(event.getGuild().getMemberById(UserID).getRoles().get(x).getName())) {
                        return true;
                    }

                }

            }
        } catch (Exception e) {
            System.out.println("<SWEPLOX UTILITIES> ERROR @ ManageJSON/userispermitted" + e + "\nTry to delete the config file and restart the bot");
        }

        return false;
    }

    /**
     * Used to get a string ready to be sent in a discord chat, reads from
     * config
     *
     * @param JSONobject Object in ConfigJSON, example "Person"
     * @param JSONvalue Value in said object, example "Age"
     * @param event GuildMessageRecivedEvent, used to calculate tags
     * @return String ready to be sent in rawtext
     * @throws java.io.FileNotFoundException
     */
    public String readLineString(String JSONobject, String JSONvalue, GuildMessageReceivedEvent event) throws FileNotFoundException, NullPointerException {

        String pageName;

        try {

            pageName = getJSON().getAsJsonObject(JSONobject).get(JSONvalue).getAsString();

            ConvertJSON convert = new ConvertJSON();

            return convert.toString(pageName, event);

        } catch (FileNotFoundException e) {
            System.out.println("JSON EXCEPTION: " + e);

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;

    }

    public String readLineString(String JSONobject, String JSONvalue) throws NullPointerException {

        String pageName;

        try {

            pageName = getJSON().getAsJsonObject(JSONobject).get(JSONvalue).getAsString();

            return pageName;

        } catch (FileNotFoundException e) {
            System.out.println("JSON EXCEPTION @ readLineString: " + e);

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;

    }

}
