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

/**
 *
 */
public class BindMap {
    private class Entry {
        public IAvatar _avatar;
        public String _attrName;
        Entry(IAvatar a, String aName) {
            _avatar=a;
            _attrName=aName;
        }
    }
    public void bind(String dataName, IAvatar avatar, String attrName) {
        Vector<Entry> v=_map.get(dataName);
        if (v==null) {
            v=new Vector<Entry>();
            _map.put(dataName,v);
        }
        v.add(new Entry(avatar,attrName));
    }
    public void handleData(String name, double[] values) {
        Vector<Entry> v=_map.get(name);
        if (v!=null) {
            for (Entry e: v) {
                e._avatar.setAttribute(name,values);
            }
        }
    }
    private Hashtable<String,Vector<Entry>> _map=new Hashtable<String,Vector<Entry>>();  
}
