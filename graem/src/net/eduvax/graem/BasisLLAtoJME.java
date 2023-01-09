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
public class BasisLLAtoJME extends DataProvider {
    public void setV(double[] v) {
        getConsumer().handleData(_vName,new double[]{-v[1],v[2],-v[0]});
    }
    public void setQ(double[] q) {
        getConsumer().handleData(_qName,new double[]{-q[1],q[2],-q[0],q[3]});
    }
    @Override public void setName(String name) {
        super.setName(name);
        _vName=name+".v";
        _qName=name+".q";
    }
    private String _vName;
    private String _qName;
}
