/*
 * @file Graem.java
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
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class Graem {
    public Graem(View v) {
        _view=v;
    }


    private Class<?> loadClass(String name) {
        Class<?> c=null;
        try {
           c=Class.forName(name);
        }
        catch (ClassNotFoundException ex) {
            // just let metho return null;
        }
        return c;
    }
    private Class<?> findClass(String name) {
        Class<?> c=loadClass(name);
        if (c==null) {
            for (String p: _imports) {
                c=loadClass(p+"."+name);
                if (c!=null) {
                    return c;
                }
            }
        }
        return c;
    }
    private Object create(String className) {
        Object o=null;
        try {
            Class<?> c=findClass(className);
            Class<?>[] ctrArgs= new Class[]{};
            o=c.getConstructor(ctrArgs).newInstance();
        }
        catch (Exception ex) {
            System.err.println("Can't create object using type "+className);
        }
        return o;
    }

    private Method findMatchingMethod(Object o,String name, Object param) {
        for (Method m: o.getClass().getMethods()) {
            if (m.getName().equals(name)) {
                Class[] pc=m.getParameterTypes();
                if (pc.length==1) {
                    try {
                        pc[0].cast(param);
                        return m;
                    }
                    catch (ClassCastException ex) {
                        // type not matching, just keep searching.
                    }
                }
            }
        }
        return null;
    }

    private boolean setObjectAttribute(Object o, String name, LuaValue value) {
        boolean res=false;
        String setName="set"+name.substring(0,1).toUpperCase()+name.substring(1);
        try {
            if (value.istable() && !value.get("class").isnil()) {
                Object a=create(value);
                if (a!=null) {
                    Method m=findMatchingMethod(o,setName,a);
                    if (m!=null) {
                        m.invoke(o,a);
                        res=true;
                    }
                }
            }
            else if (value.isint()) {
                try {
                    Method m=o.getClass().getMethod(setName,new Class[]{int.class});
                    m.invoke(o,value.toint());
                }
                catch (NoSuchMethodException nsmEx1) {
                    try {
                        Method m=o.getClass().getMethod(setName,new Class[]{long.class});
                        m.invoke(o,value.tolong());
                    }
                    catch (NoSuchMethodException nsmEx2) {
                        Method m=o.getClass().getMethod(setName,new Class[]{double.class});
                        m.invoke(o,value.todouble());
                    }
                }
            }
            else if (value.islong()) {
                try {
                    Method m=o.getClass().getMethod(setName,new Class[]{long.class});
                    m.invoke(o,value.tolong());
                }
                catch (NoSuchMethodException nsmEx) {
                    Method m=o.getClass().getMethod(setName,new Class[]{double.class});
                    m.invoke(o,value.todouble());
                }
            }
            else if (value.isnumber()) {
                Method m=o.getClass().getMethod(setName,new Class[]{double.class});

                m.invoke(o,value.todouble());
                res=true;
            }
            else if (value.isstring()) {
                Method m=o.getClass().getMethod(setName,new Class[]{String.class});
                m.invoke(o,value.toString());
                res=true;
            }
            else {
                Method m=o.getClass().getMethod(setName,new Class[]{LuaValue.class});
                m.invoke(o,value);
                res=true;
            }
        }
        catch (NoSuchMethodException nsmEx) {
            // Don't care exactly what setter has not been tried and found, 
            // false shall be returned to notify the caller.
        }
        catch (SecurityException secEx) {
            System.err.println("Can't set attribute "+name
                    +" because of security rules: "+secEx.getMessage());
        }
        catch (InvocationTargetException itex) {
            System.err.println("Can:'t set attribute "+name+": "+itex.getCause().getMessage());
itex.getCause().printStackTrace();
        }
        catch (Exception ex) {
            System.err.println("Can:'t set attribute "+name+": "+ex.getMessage());
ex.printStackTrace();
        }
        return res;
    }

    public Object create(String name, Class<?> c) {
        return create(name,c.getName());
    }

    private Object create(String name, String className) {
        try {
            Object o=create(className);
            if (name!=null && o instanceof INamedObject) {
                ((INamedObject)o).setName(name);
            }
            if (o instanceof IGraemHandler) {
                ((IGraemHandler)o).setGraem(this);
            }
            if (o instanceof ISceneComposition) {
                _view.add((ISceneComposition)o);
            }
            if (o instanceof IAvatar) {
                _avatars.put(name,(IAvatar)o);
            }
            return o;
        }
        catch (Exception ex) {
            System.err.println("Can't create avatar type: "+className);
        }
        return null;
    }

    private Object create(String name, String className, LuaValue cfg) {
        Object o=create(name,className);
        if (o!=null && cfg.istable()) {
            LuaValueIterator it=new LuaValueIterator(cfg);
            while (it.hasNext()) {
                it.next();
                // ignore class attribute since it is already processed
                // when entering this method.
                if (!"class".equals(it.key().toString())) {
                    if (o instanceof IAvatar && "bind".equals(it.key().toString())) {
                        LuaValueIterator bindIt=new LuaValueIterator(it.value());
                        while (bindIt.hasNext()) {
                            bindIt.next();
                            bind(bindIt.value().toString(),(IAvatar)o,bindIt.key().toString());
                        }
                    }
                    setObjectAttribute(o,it.key().toString(),it.value());
                }
            }
        }
        return o;
    }

    private class StopHandler {
        private IStoppable _toStop;
        private Thread _th;
        public StopHandler(IStoppable toStop, Thread th) {
        }
        public void stop() {
            if (_toStop!=null) {
                _toStop.stop();
                if (_th!=null) {
                    try {
                        _th.join();
                    }
                    catch (InterruptedException ex) {
                        // don't care wht join is interrupted, just go on.
                    }
                }
            }
        }
    }

    private Object create(String name,LuaValue spec) {
        Object o=create(name,spec.get("class").toString(),spec);
        if (o instanceof Runnable) {
            Thread th=new Thread((Runnable)o);
            if (o instanceof IStoppable) {
                _toStop.add(new StopHandler((IStoppable)o,th));
            }
            th.start();
        }
        return o;
    }
    public Object create(LuaValue spec) {
        String name=spec.get("name").isnil()?null:spec.get("name").toString();
        return create(name,spec);
    }

    public void setComponents(LuaValue cpTable) {
        LuaValueIterator it=new LuaValueIterator(cpTable);
        while (it.hasNext()) {
            it.next();
            create(it.key().toString(),it.value());
        }
    }

    public void setImport(LuaValue cpTable) {
        LuaValueIterator it=new LuaValueIterator(cpTable);
        while (it.hasNext()) {
            it.next();
            _imports.add(it.value().toString());
        }
    }

    public void setup(LuaValue cfgTable) {
        LuaValueIterator it=new LuaValueIterator(cfgTable);
        if (!cfgTable.get("import").isnil()) {
            // process import entry first to be sure imports are registered
            // before creating components
            setImport(cfgTable.get("import"));
        }
        while (it.hasNext()) {
            it.next();
            if (!"import".equals(it.key().toString())) {
                setObjectAttribute(this,it.key().toString(),it.value());
            }
        }
    }

    public void bind(String dataName, IAvatar avatar, String attrName) {
        _bindMap.bind(dataName,avatar,attrName);
    }

    public void set(LuaValue t) {
        LuaValueIterator it=new LuaValueIterator(t);
        while (it.hasNext()) {
            it.next();
            LuaValue vt=it.value();
            double[] values=new double[vt.length()];
            for (int i=0;i<vt.length();i++) {
                values[i]=vt.get(i+1).todouble();
            }
            _bindMap.handleData(it.key().toString(),values);
        }
    }

    public void shutdown() {
        for (StopHandler h: _toStop) {
            h.stop();
        }
    }

    private BindMap _bindMap=new BindMap();
    private View _view;
    private Hashtable<String,IAvatar> _avatars=new Hashtable<String,IAvatar>();
    private Vector<StopHandler> _toStop=new Vector<StopHandler>();
    private Vector<String> _imports=new Vector<String>();

    public static void TRACE(String msg) {
        StackTraceElement st=(new Throwable()).getStackTrace()[1];
        System.out.println("TRACE["+st.getClassName()+"."+st.getMethodName()
            +"():"+st.getLineNumber()+"]: "+msg);
    }
}
