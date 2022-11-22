/*
 * @file SkyBox.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.util.SkyFactory;
/**
 *
 */
public class SkyBox extends SceneComposition {
    public void setTexturePath(String tp) {
        _texturePath=tp;
    }
    @Override public void build(View view) {
        view.getRootNode().attachChild(SkyFactory.createSky(
                        view.getAssetManager(),
                        _texturePath,
                        SkyFactory.EnvMapType.EquirectMap));
    }
    private String _texturePath="res/textures/sky/skysphere1.jpg"; 
}
