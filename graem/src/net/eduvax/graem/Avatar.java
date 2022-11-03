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
    class AttrChange {
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
    
    @Override public void setLocation(double x, double y, double z) {
        _location=_cb.v(_time,x,y,z);
    }

    @Override public void setAttitude(double w,double x, double y, double z) {
        _attitude=_cb.q(_time,w,x,y,z);
    }

    @Override public synchronized void setAttribute(String attrName,double[] values) {
        if ("location".equals(attrName)) {
            if (values.length==3) {
                setLocation(values[0],values[1],values[2]);
            }
        }
        else if ("attitude".equals(attrName)) {
            if (values.length==3) {
                float[] f={(float)values[0],(float)values[1],(float)values[2]};
                Quaternion q=new Quaternion(f);
                setAttitude(q.getW(),q.getX(),q.getY(),q.getZ());
            }
            else if (values.length==4) {
                // For most quaternion libraries, the serialisation order is:
                // X, Y, Z, W.
                setAttitude(values[3],values[0],values[1],values[2]);
            }
        }
        else if ("time".equals(attrName)) {
            setTime(values[0]);
        }
        else {
            _attrChangesQueue.add(new AttrChange(attrName,values));
        }
    }

    @Override public synchronized void setChangeOfBasis(IChangeOfBasis cb) {
        _cb=cb;
    }
  
     
    protected synchronized Vector3f getLocation() {
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
    private Vector3f _location=new Vector3f();
    private Quaternion _attitude=new Quaternion();
    private LinkedList<AttrChange> _attrChangesQueue=new LinkedList<AttrChange>();
    private IChangeOfBasis _cb=new BasisIdentity();
}
