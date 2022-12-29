/*
 * @file DefaultLight.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.ViewPort; 
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
/**
 *
 */
public class DirLight extends SceneComposition {
    @Override public void build(View view) {
        Node parent=view.getRootNode();

        AssetManager assetManager=view.getAssetManager();
        ViewPort viewPort=view.getViewPort();

        DirectionalLight light = new DirectionalLight();
        light.setColor(new ColorRGBA(_r,_g,_b,1.0f));
        light.setDirection(new Vector3f(_x,_y,_z).normalizeLocal());
        parent.addLight(light);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, _shadowmapSize, 3);
        dlsr.setLight(light);
        viewPort.addProcessor(dlsr);
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, _shadowmapSize, 3);
        dlsf.setLight(light);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);

    }

    public void setShadowmapSize(int size) {
        _shadowmapSize=size;
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

    public void setX(double x) {
        _x=(float)x;
    }
    public void setY(double y) {
        _y=(float)y;
    }
    public void setZ(double z) {
        _z=(float)z;
    }
    private int _shadowmapSize=1024;
    private float _x=1;
    private float _y=-1;
    private float _z=1;
    private float _r=0.5f;
    private float _g=0.5f;
    private float _b=0.5f;
}
