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

/**
 *
 */
public class Graem {
    public Graem(View v) {
        _view=v;
    }

    public Object create(String name, String className) {
        try {
            Class c=Class.forName(className);
            Object o=c.getConstructor().newInstance();
            if (o instanceof INamedObject) {
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

    public void setup(LuaValue cfgTable) {
        LuaValueIterator it=new LuaValueIterator(cfgTable);
        while (it.hasNext()) {
            it.next();
            Object o=create(it.key().toString(),it.value().get("class").toString());
            if (o instanceof IAvatar) {
                LuaValueIterator bindIt=new LuaValueIterator(it.value().get("bind"));
                while (bindIt.hasNext()) {
                    bindIt.next();
                    bind(bindIt.value().toString(),(IAvatar)o,bindIt.key().toString());
                }
                LuaValue cob=it.value().get("cob");
                if (!cob.isnil()) {
                    try {
                        Class c=Class.forName(cob.toString());
                        IChangeOfBasis b=(IChangeOfBasis)c.getConstructor().newInstance();
                        ((IAvatar)o).setChangeOfBasis(b);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
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

    private BindMap _bindMap=new BindMap();
    private View _view;
    private Hashtable<String,IAvatar> _avatars=new Hashtable<String,IAvatar>();
}
