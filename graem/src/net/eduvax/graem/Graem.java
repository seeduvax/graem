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

    private Object create(String className) {
        Object o=null;
        try {
            Class<?> c=Class.forName(className);
            Class<?>[] ctrArgs= new Class[]{};
            o=c.getConstructor(ctrArgs).newInstance();
        }
        catch (Exception ex) {
            System.err.println("Can't create object using type "+className);
        }
        return o;
    }

    private boolean setObjectAttribute(Object o, String name, LuaValue value) {
        boolean res=false;
        String setName="set"+name.substring(0,1).toUpperCase()+name.substring(1);
        try {
            if (value.istable() && !value.get("class").isnil()) {
                Object a=create(value);
                if (a!=null) {
                    Method m=o.getClass().getMethod(setName,new Class[]{a.getClass()});
                    m.invoke(o,a);
                    res=true;
                }
            }
            else if (value.isstring()) {
                Method m=o.getClass().getMethod(setName,new Class[]{String.class});
                m.invoke(o,value.tostring());
                res=true;
            }
            else if (value.isnumber()) {
                Method m=o.getClass().getMethod(setName,new Class[]{double.class});
                m.invoke(o,value.todouble());
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

    private Object create(String name, String className) {
        try {
            Object o=create(className);
            if (name!=null && o instanceof INamedObject) {
                ((INamedObject)o).setName(name);
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
                setObjectAttribute(o,it.key().toString(),it.value());
            }
        }
        return o;
    }

    private Object create(String name,LuaValue spec) {
        Object o=create(name,spec.get("class").toString(),spec.get("set"));
        if (o!=null && o instanceof IAvatar) {
            LuaValueIterator bindIt=new LuaValueIterator(spec.get("bind"));
            while (bindIt.hasNext()) {
                bindIt.next();
                bind(bindIt.value().toString(),(IAvatar)o,bindIt.key().toString());
            }
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

    public void setup(LuaValue cfgTable) {
        LuaValueIterator it=new LuaValueIterator(cfgTable);
        while (it.hasNext()) {
            it.next();
            setObjectAttribute(this,it.key().toString(),it.value());
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

    private BindMap _bindMap=new BindMap();
    private View _view;
    private Hashtable<String,IAvatar> _avatars=new Hashtable<String,IAvatar>();
}
