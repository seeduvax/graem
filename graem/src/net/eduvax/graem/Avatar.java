/*
 * @file Avatar.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.LinkedList;

/**
 *
 */
public abstract class Avatar implements IAvatar {
    public class AttrChange {
        public String _name;
        public double[] _values;
        public AttrChange(String name, double[] values) {
            _name=name;
            _values=values;
        }
    }
    @Override public void setTime(double time) { 
        _time=time;
    }
    
    @Override public void setLocation(double[] l) {
        _location=l;
    }

    @Override public void setAttitude(double[] a) {
        if (a.length>=4) {
            _attitude=new Quaternion((float)a[0],(float)a[1],(float)a[2],(float)a[3]);
        }
        else if (a.length==3) {
            float[] f={(float)a[0],(float)a[1],(float)a[2]};
            _attitude=new Quaternion(f);
        }
    }

    @Override public synchronized void setAttribute(String attrName,double[] values) {
        _attrChangesQueue.add(new AttrChange(attrName,values));
    }

    protected synchronized double[] getLocation() {
        return _location;
    } 
    protected synchronized Quaternion getAttitude() {
        return _attitude;
    }

    protected synchronized AttrChange pollAttrChange() {
        return _attrChangesQueue.poll();
    }

    // ------------------------------------------------------------------------
    // INamed Object implementation
    @Override public void setName(String name) {
        _name=name;
    }
    @Override public String getName() {
        return _name;
    }
    
    private String _name;
    private double _time;
    private double[] _location=new double[3];
    private Quaternion _attitude=new Quaternion();
    private LinkedList<AttrChange> _attrChangesQueue=new LinkedList<AttrChange>();
}
