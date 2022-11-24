/*
 * @file GraemApp.java
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
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 *
 */
public class GraemApp implements Runnable {
    public class Option {
        public Option(String name, String help) {
            _name=name;
            _help=help;
        } 
        public void run(String arg) {
        }
        private String _name;
        private String _help;
        public String getMsg() {
            return _help;
        }
        public String getName() {
            return _name;
        }
    }

    public GraemApp() {
        setOption(new Option("h",
                 "-h: print help message") {
            @Override public void run(String arg) {
                help();
                System.exit(0);
            }
        });
        setOption(new Option("f",
                 "-f: run in fullscreen mode.") {
            @Override public void run(String arg) {
                Toolkit tk=Toolkit.getDefaultToolkit();
                Dimension d=tk.getScreenSize();
                AppSettings settings = _view.getSettings();
                settings.setResolution(d.width,d.height);
                settings.setFullscreen(true);
            }
        });
        setOption(new Option("tcp",
                "-tcp[=<port>]: run tcp server on given port to listening for "
                +"data formatted as lua table.\n"
                +"          Default port is 10001.") {
            @Override public void run(String arg) {
                _tcpPort=10001;
                if (arg!=null) {
                    _tcpPort=Integer.parseInt(arg);
                }
            }
        });
    }

    protected View newView() {
        return new View();
    }
    protected Graem newGraem(View v) {
        return new Graem(v);
    }

    public void init(String[] args) {
        _view = newView();
        _files=new Vector<String>();
        boolean toon=false;
        int tcpPort=0;
        boolean errArg=false;
        for(String arg: args) {
            if (arg.startsWith("-")) {
                StringTokenizer st=new StringTokenizer(arg,"-=");
                Option opt=_options.get(st.hasMoreTokens()?st.nextToken():"");
                if (opt!=null) {
                    opt.run(st.hasMoreTokens()?st.nextToken():null);
                }
                else {
                    System.err.println("Unexpected argument: "+arg);
                    errArg=true;
                }
            }
            else {
                _files.addElement(arg);
            }
        }
        if (errArg) {
            System.err.println("Run with -h option for help.");
            System.exit(1);
        }
        _graem=newGraem(_view);


        _lua=new LuaShell();
        _lua.setGraem(_graem);
        _lua.setHistoryFile(System.getProperty("user.home")+"/.graem/history");
    }

    public void run() {
        for (String file: _files) {
            _lua.runFile(file);
        }
        _view.start();
        TCPServer tcpServer=null;
        Thread tcpTh=null;
        if (_tcpPort>0) {
            tcpServer=new TCPServer();
            tcpServer.setPort(_tcpPort);
            tcpServer.setGraem(_graem);
            tcpTh=new Thread(tcpServer);
            tcpTh.start();
        }
        _lua.setPrompt(_luaPrompt);
        _lua.run();
        _view.stop();
        if (tcpServer!=null) {
            tcpServer.stop();
            try {
                if (tcpTh!=null) {
                    tcpTh.join();
                }
            }
            catch (InterruptedException ex) {
                // Dont't really car why the join is interrupted since it
                // is done only to nicely cleanup thing just before java
                // VM exit that should anyway close everything.
            }
        }
        _graem.shutdown();
    }

    public void setHelpHeader(String hh) {
        _helpHeader=hh;
    }
    public void setLuaPrompt(String lp) {
        _luaPrompt=lp;
    }

    public void setOption(Option opt) {
        _options.put(opt.getName(),opt);
    } 

    public void help() {
        System.out.println(_helpHeader);
        for (Enumeration<Option> e=_options.elements();e.hasMoreElements();) {
             System.out.println("    "+e.nextElement().getMsg());
        }
    }

    private LuaShell _lua;

    protected View getView() {
        return _view;
    }
    protected Graem getGraem() {
        return _graem;
    }
    private View _view;
    private Graem _graem;
    private Vector<String> _files=new Vector<String>();
    private Hashtable<String,Option> _options=new Hashtable<String,Option>();
    private int _tcpPort=0;

    private String _luaPrompt="GraEm> ";
    private String _helpHeader=
"GraEm - time series data visualization inb 3D space\n"
+"Invoke:\n"
+"    java "+GraemApp.class.getName()+" [option]* [<startup script>]*\n"
+"Options:\n";
}
