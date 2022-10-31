/*
 * @file Main.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
//import org.luaj.vm2.lib.jse.*;

/**
 *
 */
public class Main {
    public static void main(String[] args) {
        View app = new View();
        AppSettings settings = app.getSettings();
        boolean toon=false;
        int tcpPort=0;
        for(String arg: args) {
            if ("-f".equals(arg)) {
                Toolkit tk=Toolkit.getDefaultToolkit();
                Dimension d=tk.getScreenSize();
                settings.setResolution(d.width,d.height);
                settings.setFullscreen(true);
            }
            else if ("-ts".equals(arg)) {
                toon=true;
            }
            else if (arg.startsWith("-tcp")) {
                tcpPort=10001;
                StringTokenizer st=new StringTokenizer(arg,"=");
                st.nextToken();
                if (st.hasMoreTokens()) {
                    tcpPort=Integer.parseInt(st.nextToken());
                }
            }
        }
        app.add(new DummyAvatar());
/*
        app.add(new DummyAvatar());
        app.add(new Playfield());
*/
        app.add(new DefaultLight());
        if (toon) {
            app.add(new ToonStyle());
        }
        if (tcpPort>0) {
            app.add(new TCPServer(tcpPort));
        }

        Globals globals = JsePlatform.standardGlobals();
        Graem graem=new Graem(app);
        globals.set("graem",CoerceJavaToLua.coerce(graem));

        app.start();
        try {
            BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
            String line=in.readLine();
            while (line!=null) {
                if (line!="") {
                    LuaValue statement=globals.load(line);
                    statement.call();
                }
                line=in.readLine();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
