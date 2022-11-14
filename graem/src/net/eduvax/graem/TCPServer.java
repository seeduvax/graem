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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class TCPServer implements Runnable, IGraemHandler, IStoppable {
    
    public TCPServer() {
        _graem=null;
        _server=null;
    }

    @Override public void setGraem(Graem g) {
        _graem=g;
    }

    public void setPort(int port) {
        _port=port;
    }

    @Override public void run() {
        try {
            _server=new ServerSocket(_port);
        }
        catch (IOException ex) {
            System.err.println("Can't start server on port "+_port+": "+ex);
            return;
        }
        _run=true;
        while(_run) {
            try {
                Socket socket=_server.accept();
                Thread th=new Thread(new LuaRunner(_graem,socket.getInputStream()));
                th.start();
            }
            catch (IOException ex) {
                if (_run) {
                    System.err.println("Incoming connection error: "+ex);
                }
            }
        }
    }

    @Override public void stop() {
        _run=false;
        if (_server!=null) {
            try {
                _server.close();
            }
            catch (IOException ex) {
                // Don't really care server can't be stopped.
            }
        }
    }

    private Thread _th=null;
    int _port=10000;
    private boolean _run=false;
    ServerSocket _server;
    Graem _graem;
}
