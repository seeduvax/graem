/*
 * @file IChangeOfBasis.java
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
public interface IChangeOfBasis {
    Vector3f v(double x, double y, double z);
    Quaternion q(double w, double x, double y, double z);
}
