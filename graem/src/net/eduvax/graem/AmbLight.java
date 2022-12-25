/*
 * @file AmbLight.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort; 
import com.jme3.scene.Node;

/**
 *
 */
public class AmbLight extends SceneComposition {
    @Override public void build(View view) {
        Node parent=view.getRootNode();
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(_r,_g,_b,1.0f));
        parent.addLight(al);
    }
    public void setR(double r) {
        _r=(float)r;
    }
    public void setG(double g) {
        _g=(float)g;
    }
    public void setB(double b) {
        _b=(float)b;
    }
    private float _r=0.5f;
    private float _g=0.5f;
    private float _b=0.5f;
}
