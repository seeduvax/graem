/*
 * @file LuaRunner.java
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
/**
 *
 */
public class LuaRunner implements Runnable {
    public LuaRunner(Graem graem, InputStream in) {
        _graem=graem;
        _in=in;
    }

    public void setPrompt(PrintStream out, String promptMsg) {
        _out=out;
        _promptMsg=promptMsg;
    }

    public void run() {
        Globals globals = JsePlatform.standardGlobals();
        globals.set("graem",CoerceJavaToLua.coerce(_graem));
        try {
            BufferedReader in=new BufferedReader(new InputStreamReader(_in));
            prompt();
            String line=in.readLine();
            while (line!=null) {
                if (line.startsWith("{")) {
                    line="graem:set("+line+")";
                }
                try {
                    LuaValue statement=globals.load(line);
                    statement.call();
                }
                catch (Exception ex) {
                    System.err.println("Lua statement execution error: "+line
                        +"\n"+ex);
                }
                prompt();
                line=in.readLine();
            }
        }
        catch (java.io.IOException ex) {
System.err.println("Lua Runner input stream read error: "+ex);
ex.printStackTrace();
        }
    }

    private void prompt() {
        if (_out!=null && _promptMsg!=null) {
            _out.print(_promptMsg);
        }
    }

    InputStream _in;
    PrintStream _out=null;
    String _promptMsg=null;
    Graem _graem;
}
