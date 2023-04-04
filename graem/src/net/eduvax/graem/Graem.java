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


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
            Class<?>[] ctrArgs= new Class<?>[]{};
            o=c.getConstructor(ctrArgs).newInstance();
        }
        catch (Exception ex) {
            System.err.println("Can't create object using type "+className);
        }
        return o;
    }

    private Method findMatchingMethod(Object o,String name, Class<?> paramType) {
        for (Method m: o.getClass().getMethods()) {
            if (m.getName().equals(name)) {
                Class<?>[] pc=m.getParameterTypes();
                if (pc.length==1 && pc[0].isAssignableFrom(paramType)) {
                    return m;
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
                    Method m=findMatchingMethod(o,setName,a.getClass());
                    if (m!=null) {
                        m.invoke(o,a);
                        res=true;
                    }
                }
            }
            // TODO shall find a better way to manage all the cases related to
            // the ugly condition here after
            else if (value.istable() && !(o==this && ("components".equals(name) || "import".equals(name))) && !(o!=this&&"bind".equals(name))) {
                double[] values=new double[value.length()];
                for (int i=0;i<value.length();i++) {
                    values[i]=value.get(i+1).todouble();
                }
                Method m=findMatchingMethod(o,setName,values.getClass());
                if (m!=null) {
                    m.invoke(o,values);
                    res=true;
                }
            }

            else if (value.isint()) {
                try {
                    Method m=o.getClass().getMethod(setName,new Class<?>[]{int.class});
                    m.invoke(o,value.toint());
                }
                catch (NoSuchMethodException nsmEx1) {
                    try {
                        Method m=o.getClass().getMethod(setName,new Class<?>[]{long.class});
                        m.invoke(o,value.tolong());
                    }
                    catch (NoSuchMethodException nsmEx2) {
                        Method m=o.getClass().getMethod(setName,new Class<?>[]{double.class});
                        m.invoke(o,value.todouble());
                    }
                }
            }
            else if (value.islong()) {
                try {
                    Method m=o.getClass().getMethod(setName,new Class<?>[]{long.class});
                    m.invoke(o,value.tolong());
                }
                catch (NoSuchMethodException nsmEx) {
                    Method m=o.getClass().getMethod(setName,new Class<?>[]{double.class});
                    m.invoke(o,value.todouble());
                }
            }
            else if (value.isnumber()) {
                Method m=o.getClass().getMethod(setName,new Class<?>[]{double.class});

                m.invoke(o,value.todouble());
                res=true;
            }
            else if (value.isstring()) {
                Method m=o.getClass().getMethod(setName,new Class<?>[]{String.class});
                m.invoke(o,value.toString());
                res=true;
            }
            else if (value.isboolean()) {
                Method m=o.getClass().getMethod(setName,new Class<?>[]{boolean.class});

                m.invoke(o,value.toboolean());
                res=true;
            }
            else {
                Method m=o.getClass().getMethod(setName,new Class<?>[]{LuaValue.class});
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
            System.err.println("Can't set attribute "+name+": "+itex.getCause().getMessage());
itex.getCause().printStackTrace();
        }
        catch (Exception ex) {
            System.err.println("Can't set attribute "+name+": "+ex.getMessage());
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
            if (name!=null && o instanceof INamedObject) {
                _components.put(name,(INamedObject)o);
            }
            if (o instanceof IDataProvider) {
                IDataProvider dp=(IDataProvider)o;
                dp.setConsumer(_bindMap);
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
                    if ("bind".equals(it.key().toString())) {
                        LuaValueIterator bindIt=new LuaValueIterator(it.value());
                        while (bindIt.hasNext()) {
                            bindIt.next();
                            if (bindIt.key().isstring()) {
                                bind(bindIt.value().toString(),o,bindIt.key().toString());
                            }
                            else if (bindIt.key().isfunction()) {
                                bind(bindIt.value().toString(),o,bindIt.key());
                            }
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

    public void bind(String dataName, Object o, String attrName) {
        String setName="set"+attrName.substring(0,1).toUpperCase()+attrName.substring(1);
        double[] param={0.0};
        Method m=findMatchingMethod(o,setName,param.getClass());
        if (m==null) {
            m=findMatchingMethod(o,setName,double.class);
        }
        if (m!=null) {
            _bindMap.bind(dataName,o,m);
        }
        else {
System.err.println("Can't bind " + dataName + " to " + attrName
        +", object "+o+" has no compatible method "+setName);
        }
    }
    public void bind(String dataName, Object o, Method m) {
        _bindMap.bind(dataName,o,m);
    }
    public void bind(String dataName, Object o, LuaValue f) {
        _bindMap.bind(dataName,o,f);
    }

    public void setRelativeframe(boolean rf) {
        _view.setRelativeFrame(rf);
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


    public static void dump(int level, Spatial from) {
        for (int i=0;i<level;i++) {
            System.out.print("  ");
        }
        System.out.println(from.getName());
        if (from instanceof Node) {
            Node n=(Node)from;
            int nl=level+1;
            for (Spatial s: n.getChildren()) {
                dump(nl,s);
            }
        }
    }

    public void dump() {
        dump(0,_view.getRootNode());
    }

    public INamedObject get(String name) {
        return _components.get(name);
    }

    private BindMap _bindMap=new BindMap();
    private View _view;
    private Hashtable<String,INamedObject> _components=new Hashtable<String,INamedObject>();
    private Vector<StopHandler> _toStop=new Vector<StopHandler>();
    private Vector<String> _imports=new Vector<String>();

    public static void TRACE(String msg,int l) {
        StackTraceElement st=(new Throwable()).getStackTrace()[l+1];
        System.out.println("TRACE["+st.getClassName()+"."+st.getMethodName()
            +"():"+st.getLineNumber()+"]: "+msg);
    }
    public static void TRACE(String msg) {
        TRACE(msg,1);
    }
}
