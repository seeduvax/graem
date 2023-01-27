/*
 * @file DummyAvatar.java
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
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort; 
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

/**
 *
 */
public class DummyAvatar extends Avatar {

    private void addOrientationMarker(String name,Material mat,ColorRGBA c,float x, float z) {
        Material m=mat.clone();
        m.setColor("Ambient",c);
        Geometry g=new Geometry(name,new Box(0.05f,1,0.05f));
        g.setLocalTranslation(x,-0.05f,z);
        g.setMaterial(m);
        _tail.attachChild(g);
    }
    @Override protected Node build() {
        Node node=super.build();
        AssetManager assetManager=getView().getAssetManager();
        Geometry head=new Geometry("head",new Sphere(20,20,0.5f));
        head.setLocalTranslation(0,1,0);
        _tail=new Node("tail");
        Geometry core=new Geometry("tail",new Cylinder(20,20,0.5f,2,true));
        float f[]={(float)Math.PI/2,0,0};
        Quaternion q=new Quaternion(f);
        core.setLocalRotation(q);
        node.attachChild(head);
        node.attachChild(_tail);
        _tail.attachChild(core);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Ambient",new ColorRGBA(1,1,1,0.5f));
        mat.setColor("Diffuse",new ColorRGBA(1,1,1,0.5f));
        mat.setColor("Specular",ColorRGBA.Yellow);
        mat.setFloat("Shininess", 96f);
        node.setMaterial(mat);
       
        addOrientationMarker("A",mat,ColorRGBA.Red,0.5f,0); 
        addOrientationMarker("B",mat,ColorRGBA.Green,0,0.5f); 
        addOrientationMarker("C",mat,ColorRGBA.Blue,-0.5f,0); 
        addOrientationMarker("D",mat,ColorRGBA.Yellow,0,-0.5f); 
        return node;
    }
    @Override public synchronized void update(float tpf) {
        super.update(tpf);
        if (_split) {
            Vector3f tl=_tail.getLocalTranslation();
            tl.setY(tl.getY()-0.1f);
        }
    }
    public void setSplit(double[] v) {
        _split=v[0]!=0;
    }
    private Node _tail;
    private boolean _split=false;
}
