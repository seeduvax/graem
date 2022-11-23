/*
 * @file BasisIdentity.java
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
public class BasisIdentity implements IChangeOfBasis {
    @Override public double[] v(double t,double x, double y, double z) {
        return new double[]{x,y,z};
    }
    @Override public Quaternion q(double t,double x, double y, double z, double w) {
        return new Quaternion((float)x,(float)y,(float)z,(float)w);
    }
}
