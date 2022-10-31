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

    public void bind(String dataName, IAvatar avatar, String attrName) {
        _bindMap.bind(dataName,avatar,attrName);
    }

    public void set(String name,double[] values) {
        _bindMap.handleData(name,values);
    }

    private BindMap _bindMap=new BindMap();
    private View _view;
    private Hashtable<String,IAvatar> _avatars=new Hashtable<String,IAvatar>();
}
