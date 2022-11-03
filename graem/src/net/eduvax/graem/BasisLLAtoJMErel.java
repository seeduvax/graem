/*
 * @file BasisLLAtoJMErel.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.math.Vector3f;
/**
 *
 */
public class BasisLLAtoJMErel extends BasisLLAtoJME {
    @Override public Vector3f v(double t, double x, double y, double z) {
        if (_origin==null) {
            _origin=new double[]{x,y,z};
        }
        return super.v(t,x-_origin[0],y-_origin[1],z-_origin[2]);
    }

    double[] _origin=null;
}
