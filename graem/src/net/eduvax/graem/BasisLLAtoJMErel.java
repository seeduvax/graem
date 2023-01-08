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
    @Override public void setV(double[] v) {
        if (_origin==null) {
            _origin=new double[]{v[0],v[1],v[2]};
        }
        super.setV(new double[]{v[0]-_origin[0],v[1]-_origin[1],v[2]-_origin[2]});
    }

    double[] _origin=null;
}
