/*
 * @file Binding.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import java.util.Hashtable;
import java.util.Vector;
import java.lang.reflect.Method;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 *
 */
public class BindMap {
    private interface IHandler {
        public void handle(double[] values);
    }

    private class MethodCallHandler implements IHandler {
        private Method _m;
        private Object _o;
        private boolean _err=false;
        public MethodCallHandler(Object o, Method m) {
            _o=o;
            _m=m;
        }
        @Override public void handle(double[] values) {
            try {
                _m.invoke(_o,values);
            }
            catch (Exception ex) {
                if (!_err) {
                    System.err.println("Can't handle data with obect"+_o+": "+ex.getMessage());
ex.getCause().printStackTrace();
                }
            }
        }
    }

    private class LuaHandler implements IHandler {
        public LuaHandler(Object target, LuaValue f) {
            _target=CoerceJavaToLua.coerce(target);
            _func=f;
        }
        @Override public void handle(double[] values) {
            LuaValue arg=CoerceJavaToLua.coerce(values);
            _func.invoke(_target,arg);
        }
        private LuaValue _target;
        private LuaValue _func;
    }


    private Vector<IHandler> getVect(String name) {
        Vector<IHandler> v=_map.get(name);
        if (v==null) {
            v=new Vector<IHandler>();
            _map.put(name,v);
        }
        return v;
    }   

    public void bind(String dataName, Object o, Method m) {
        Vector<IHandler> v=getVect(dataName);
        v.add(new MethodCallHandler(o,m));
    }
    public void bind(String dataName, Object o, LuaValue f) {
        Vector<IHandler> v=getVect(dataName);
        v.add(new LuaHandler(o,f));
    }
    public void handleData(String name, double[] values) {
         Vector<IHandler> v=_map.get(name);
         if (v!=null) {
             for (IHandler h: v) {
                 h.handle(values);
             }
        }
    }

    private Hashtable<String,Vector<IHandler>> _map=new Hashtable<String,Vector<IHandler>>();  
}
