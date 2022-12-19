/*
 * @file SimpleAvatar.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.asset.ModelKey;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 */
public class SimpleAvatar extends Avatar implements ISceneComposition {
    // ISCeneComposition implementation
    @Override public void build(View view) {
        ModelKey key=new ModelKey(_modelPath);
        _node=(Node)view.getAssetManager().loadModel(key);
        _node.setName(getName());
        view.getRootNode().attachChild(_node);
    }
    @Override public synchronized void update(float tpf) {
        double[] l=getLocation();
        Vector3f lv=new Vector3f((float)l[0],(float)l[1],(float)l[2]);
        _node.setLocalTranslation(
                _node.getLocalTranslation().interpolateLocal(lv,0.2f));
        Quaternion q=_node.getLocalRotation();
        q.nlerp(getAttitude(),0.2f);
        _node.setLocalRotation(q);
    }
    public void setModelPath(String mp) {
        _modelPath=mp;
    }

    private String _modelPath;
    private Node _node;
}
