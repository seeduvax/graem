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
import java.util.Vector;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 *
 */
public class Main {
    public static void main(String[] args) {
        View app = new View();
        Vector<String> files=new Vector<String>();
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
            else {
                files.addElement(arg);
            }
        }
        app.add(new DefaultLight());
        if (toon) {
            app.add(new ToonStyle());
        }
        Graem graem=new Graem(app);

        TCPServer tcpServer=null;
        if (tcpPort>0) {
            tcpServer=new TCPServer(tcpPort,graem);
        }

        LuaRunner lua=new LuaRunner(graem,System.in);
        for (String file: files) {
            lua.runFile(file);
        }

        app.start();
        lua.setPrompt(System.out,"GraEm> ");
        lua.run();
        if (tcpServer!=null) {
            tcpServer.stop();
        }
    }
}
