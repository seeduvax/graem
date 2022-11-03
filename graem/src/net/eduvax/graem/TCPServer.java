/*
 * @file TCPServer.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import com.jme3.math.Quaternion;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
/**
 *
 */
public class TCPServer implements Runnable {
    public TCPServer(int port, Graem graem) {
        _graem=graem;
        try {
            _server=new ServerSocket(port);
            start();
        }
        catch (IOException ex) {
System.err.println("Can't start server on port "+port+": "+ex);
ex.printStackTrace();
        }
    }

    public void start() {
        if (_th==null) {
            _th=new Thread(this);
            _th.start();
        }
    }

    @Override public void run() {
        _run=true;
        while(_run) {
            try {
                Socket socket=_server.accept();
                Thread th=new Thread(new SocketHandler(socket));
                th.start();
            }
            catch (IOException ex) {
System.err.println("Incoming connection error: "+ex);
ex.printStackTrace();
            }
        }
    }

    public void stop() {
        _run=false;
        if (_th!=null) {
            try {
                _th.join();
            }
            catch (InterruptedException ex) {
                // interrupt in join ignored.
            }
            _th=null;
        }
    }

    private Thread _th=null;
    private boolean _run=false;
    ServerSocket _server;
    Graem _graem;
    
    class SocketHandler implements Runnable {
        public SocketHandler(Socket socket) {
            _socket=socket;
        }

        @Override public void run() {
            Globals globals = JsePlatform.standardGlobals();
            globals.set("graem",CoerceJavaToLua.coerce(_graem));
            try {
                BufferedReader in=new BufferedReader(new InputStreamReader(_socket.getInputStream()));
                String line=in.readLine();
                while (line!=null) {
                    if (line.startsWith("{")) {
                        line="graem:set("+line+")";
                    }
                    LuaValue statement=globals.load(line);
                    statement.call();
                    line=in.readLine();
                }
            }
            catch (java.io.IOException ex) {
System.err.println("Read error on socket read: "+ex);
ex.printStackTrace();
            }
        }

        private Socket _socket;
    }
}
