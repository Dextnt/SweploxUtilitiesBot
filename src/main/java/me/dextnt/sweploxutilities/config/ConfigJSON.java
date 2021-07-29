/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities.config;

import java.util.Map;

/**
 *
 * @author Stryk3r
 */
public class ConfigJSON {
    
    
    public Map<String, String> config;
    public Map<String, String> messages;
    public Map<String, String[]> permissions;
    public Map<String, String> help;
    
    public ConfigJSON(Map<String, String> config, Map<String, String> messages, Map<String, String[]> permissions, Map<String, String> help) {
        this.config = config;
        this.messages = messages;
        this.permissions = permissions;
        this.help = help;
    }
    
}
