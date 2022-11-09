/*
 * @file BasisLLAtoJME.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 */
public class BasisLLAtoJME implements IChangeOfBasis {
    @Override public Vector3f v(double t,double x, double y, double z) {
        return new Vector3f((float)-y,(float)z,(float)-x);
    }
    @Override public Quaternion q(double t,double x, double y, double z, double w) {
        return new Quaternion((float)-y,(float)z,(float)-x,(float)w);
    }
}
