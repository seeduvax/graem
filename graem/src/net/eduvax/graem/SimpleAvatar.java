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
    @Override protected Node build() {
        if (_modelPath!=null) {
            ModelKey key=new ModelKey(_modelPath);
            Node node=(Node)getView().getAssetManager().loadModel(key);
            node.setName(getName());
            return node;
        }
        else {
            System.err.println("No model defined for avatar "+getName());
            return super.build();
        }
    }


    public void setModelPath(String mp) {
        _modelPath=mp;
    }
    private String _modelPath=null;
    private Node _node;
}
