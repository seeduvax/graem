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
import com.jme3.scene.Node;
import com.jme3.renderer.queue.RenderQueue;
import java.util.LinkedList;

/**
 *
 */
public abstract class Avatar implements INamedObject, ISceneComposition {
    public class AttrChange {
        public String _name;
        public double[] _values;
        public AttrChange(String name, double[] values) {
            _name=name;
            _values=values;
        }
    }
    public void setLocation(double[] l) {
        _location=l;
    }

    public void setAttitude(double[] a) {
        if (a.length>=4) {
            _attitude=new Quaternion((float)a[0],(float)a[1],(float)a[2],(float)a[3]);
        }
        else if (a.length==3) {
            float[] f={(float)a[0],(float)a[1],(float)a[2]};
            _attitude=new Quaternion(f);
        }
    }

    public void setSmoothFactor(float f) {
    }

    public synchronized void queueAttrChange(String attrName,double[] values) {
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

    protected Node getNode() {
        return _node;
    }
    protected View getView() {
        return _view;
    }
    protected Node build() {
        return new Node(getName());
    }
    protected final void build(View v,Node parent) {
        _view=v;
        _node=build();    
        parent.attachChild(_node);
        if (_shadowCast==true && _shadowReceive==true) {
            _node.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        }
        else if (_shadowCast==true) {
            _node.setShadowMode(RenderQueue.ShadowMode.Cast);
        }
        else if (_shadowCast==false) {
            _node.setShadowMode(RenderQueue.ShadowMode.Receive);
        }
    }
    @Override public final void build(View v) {
        build(v,v.getRootNode());
    }

    // ISceneComposition implementation
    @Override public synchronized void update(float tpf) {
        double[] l=getLocation();
        Vector3f lv=new Vector3f((float)l[0],(float)l[1],(float)l[2]);
        _node.setLocalTranslation(
                _node.getLocalTranslation().interpolateLocal(lv,_smoothFactor));
        Quaternion q=_node.getLocalRotation();
        q.nlerp(getAttitude(),_smoothFactor);
        _node.setLocalRotation(q);
    }
    // ------------------------------------------------------------------------
    // INamed Object implementation
    @Override public void setName(String name) {
        _name=name;
    }
    @Override public String getName() {
        return _name;
    }
   

    public void setShadowCast(boolean cast) {
        _shadowCast=cast;
    }
    public void setShadowReceive(boolean receive) {
        _shadowReceive=receive;
    }

 
    private float _smoothFactor=1.0f;
    private String _name;
    private Node _node;
    private View _view;
    private double[] _location=new double[3];
    private Quaternion _attitude=new Quaternion();
    private LinkedList<AttrChange> _attrChangesQueue=new LinkedList<AttrChange>();
    private boolean _shadowCast=true;
    private boolean _shadowReceive=false;
}
