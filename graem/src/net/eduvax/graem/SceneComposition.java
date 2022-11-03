/*
 * @file SceneComposition.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

/**
 *
 */
public class SceneComposition implements ISceneComposition {
    // ------------------------------------------------------------------------
    // default ISceneComposition interface empty implementation
    @Override public void build(View v) {
    }
    @Override public void update(float tpf) {
    }
    // ------------------------------------------------------------------------
    // INamedObject interface implementation
    @Override public void setName(String name) {
        _name=name;
    }
    @Override public String getName() {
        return _name;
    }
    private String _name;
}
