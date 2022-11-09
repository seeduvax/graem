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
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.ViewPort; 
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
/**
 *
 */
public class DefaultLight extends SceneComposition {
    @Override public void build(View view) {
        Node parent=view.getRootNode();

        AssetManager assetManager=view.getAssetManager();
        ViewPort viewPort=view.getViewPort();

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        parent.addLight(sun);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, _shadowmapSize, 3);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.4f));
        parent.addLight(al);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, _shadowmapSize, 3);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);

    }

    public void setShadowmapSize(int size) {
        _shadowmapSize=size;
    }

    private int _shadowmapSize=1024;
}
