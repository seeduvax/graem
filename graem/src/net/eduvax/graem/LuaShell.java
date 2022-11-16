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

import java.lang.reflect.Method;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
/**
 *
 */
public class LuaShell implements Runnable, IGraemHandler {
    public LuaShell() {
    }

    @Override public void setGraem(Graem graem) {
        _graem=graem;
    }

    public void setPrompt(String promptMsg) {
        _promptMsg=promptMsg;
    }

    public void setHistoryFile(String filePath) {
        _historyFile=filePath;
    }

    public void runFile(String filePath) {
        LuaValue l=_globals.loadfile(filePath);
        l.call();
    }

    public void run() {
        _globals = JsePlatform.standardGlobals();
        if (_graem!=null) {
            _globals.set("graem",CoerceJavaToLua.coerce(_graem));
        }
        try {
            Terminal terminal=TerminalBuilder.builder()
                                    .system(true)
                                    .build();
            LineReader reader=LineReaderBuilder.builder()
                                    .terminal(terminal)
                                    .completer(new LuaCompleter())
                                    .variable(LineReader.HISTORY_FILE, _historyFile)
                                    .history(new DefaultHistory())
                                    .build();
            boolean running=true;
            while(running) {
                try {
                    String line=reader.readLine(_promptMsg).trim();
                    if (line.startsWith("{")) {
                        line="graem:set("+line+")";
                    } 
                    else if (line.startsWith("=")) {
                        line="print("+line.substring(1)+")";
                    }
                    LuaValue statement=_globals.load(line);
                    statement.call();
                }
                catch (EndOfFileException ctrlD) {
                    running=false;
                }
                catch (UserInterruptException ctrlC) {
                    running=false;
                }
                catch (LuaError luaE) {
                    System.err.println(""+luaE.getMessage());
                }
            }
        }
        catch (Exception ex) {
ex.printStackTrace();
        }
    }

    private class LuaCompleter implements Completer {
        private int lastIndexOf(String from, String chars) {
            int res=-1;
            for (int i=0; i<chars.length();i++) {
                int pos=from.lastIndexOf(chars.charAt(i));
                if (pos>res) {
                    res=pos;
                }
            }
            return res;
        }
        @Override public void complete(LineReader reader,
                            ParsedLine line,
                            List<Candidate> candidates) {
            String w=line.word();
            int dot=lastIndexOf(w,".:");
            String prefix="";
            LuaValue g=_globals.get("_G");
            if (dot>0) {
                String parent=w.substring(0,dot);
                g=_globals.get(parent);
                prefix=parent+w.charAt(dot);
                w=w.substring(dot+1);
            }
            if (!g.isnil()) {
                if (g.isuserdata()) {
                    Method[] methods=g.touserdata().getClass().getMethods();
                    for (Method m: methods) {
                        String mName=m.getName();
                        if (mName.startsWith(w)) {
                            String candidate=prefix+mName+"(";
                            candidates.add(new Candidate(
                                    candidate,
                                    candidate,
                                    null,
                                    null,
                                    null,
                                    null,
                                    false
                                ));
                        }
                    }
                }
                else if (g.istable()) {
                    LuaValueIterator it=new LuaValueIterator(g);
                    while (it.hasNext()) {
                        it.next();
                        String k=it.key().toString();
                        if (k.startsWith(w)) {
                            String candidate=prefix+k;
                            if (it.value().isfunction()) {
                                candidate=candidate+"(";
                            }
                            candidates.add(new Candidate(
                                    candidate,
                                    candidate,
                                    null,
                                    null,
                                    null,
                                    null,
                                    false
                                ));
                        }
                    } 
                }
            }
        }
    }

    private String _promptMsg="> ";
    private Graem _graem;
    private Globals _globals;
    private String _historyFile;
}
