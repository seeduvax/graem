/*
 * @file LuaValueIterator.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 *
 */
public class LuaValueIterator {
    public LuaValueIterator(LuaValue lv) {
        _lv=lv;
        _hasNext=!_lv.next(_key).arg1().isnil(); 
    }

    public void next() {
        Varargs va=_lv.next(_key);
        _key=va.arg1();
        _value=va.arg(2);
        _hasNext=!_lv.next(_key).arg1().isnil(); 
    }
    public LuaValue key() {
        return _key;
    }
    public LuaValue value() {
        return _value;
    }
    public boolean hasNext() {
        return _hasNext;
    }
    private LuaValue _key=LuaValue.NIL;
    private LuaValue _value=LuaValue.NIL;
    private LuaValue _lv=LuaValue.NIL;
    private boolean _hasNext=false;
}
