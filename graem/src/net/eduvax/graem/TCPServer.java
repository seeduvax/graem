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
public class TCPServer implements Runnable {
    public TCPServer(int port, Graem graem) {
        _graem=graem;
        try {
            _server=new ServerSocket(port);
            start();
        }
        catch (IOException ex) {
            System.err.println("Can't start server on port "+port+": "+ex);
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
}
