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
            Object o=c.newInstance();
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
        LuaValue k=LuaValue.NIL;
        boolean completed=false;
        while (!completed) {
            Varargs n=cfgTable.next(k);
            k=n.arg1();
            completed=k.isnil();
            if (!completed) {
                LuaValue def=n.arg(2);
                Object o=create(k.toString(),def.get("class").toString());
                if (o instanceof IAvatar) {
                    LuaValue bind=def.get("bind");
                    LuaValue bk=LuaValue.NIL;
                    boolean bCompleted=false;
                    while (!bCompleted) {
                        Varargs bn=bind.next(bk);
                        bk=bn.arg1();
                        bCompleted=bk.isnil();
                        if (!bCompleted) {
                            bind(bn.arg(2).toString(),(IAvatar)o,bk.toString());
                        } 
                    }
                }
            }
        }
    }

    public void bind(String dataName, IAvatar avatar, String attrName) {
        _bindMap.bind(dataName,avatar,attrName);
    }

    public void set(LuaValue t) {
        LuaValue k=LuaValue.NIL;
        boolean completed=false;
        while (!completed) {
            Varargs n=t.next(k);
            k=n.arg1();
            completed=k.isnil();
            if (!completed) {
                LuaValue vt=n.arg(2);
                double[] values=new double[vt.length()];
                for (int i=0;i<vt.length();i++) {
                    values[i]=vt.get(i+1).todouble();
                }
                _bindMap.handleData(k.toString(),values);
            }
        }
    }

    private BindMap _bindMap=new BindMap();
    private View _view;
    private Hashtable<String,IAvatar> _avatars=new Hashtable<String,IAvatar>();
}
