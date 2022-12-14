/*
 * @file ISceneComposition.java
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
public interface ISceneComposition extends INamedObject {
    void build(View view);
    void update(float tpf);
}
