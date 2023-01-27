/*
 * @file Axes.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.Node;

/**
 *
 */
public class Axes extends Avatar {
    private Node _axes;
    private AssetManager _assetManager;
    private float _width=8;

    public void setWidth(float w) {
        _width=w;
    }

    private float _size=10f;

    public void setSize(double s) {
        _size=(float)s;
    }

    private void addAxe(String name, Node parent, Vector3f dir, ColorRGBA color) {
        Arrow arrow=new Arrow(dir);
        arrow.setLineWidth(_width);
        Geometry g = new Geometry(name, arrow);
        Material mat = new Material(_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        parent.attachChild(g);    
    }

    @Override protected Node build() {
        _assetManager=getView().getAssetManager();
        Node n=super.build();
        addAxe("X",n,new Vector3f(_size,0,0),ColorRGBA.Red);
        addAxe("Y",n,new Vector3f(0,_size,0),ColorRGBA.Green);
        addAxe("Z",n,new Vector3f(0,0,_size),ColorRGBA.Blue);
        return n;
    }

}
