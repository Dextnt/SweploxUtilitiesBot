/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.dextnt.sweploxutilities;

import javax.security.auth.login.LoginException;
import me.dextnt.sweploxutilities.config.ManageJSON;

/**
 *
 * @author Stryk3r
 */
public class Main {
    
    public static void main(String[] args) {
        
        try {
        ManageJSON json = new ManageJSON();
        json.startUpCheck();
        Listener.run(json.readLineString("config", "botid"));
        } catch (LoginException e) {
            System.out.println("<SWEPLOX UTILITIES> Error at STARTUP: " + e);
        } 
        
    }
    
}
