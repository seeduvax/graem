/*
 * @file IAvatar.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;
import com.jme3.input.ChaseCamera;

/**
 *
 */
public interface IAvatar extends INamedObject {
    void setTime(double time);
    void setLocation(double[] l);
    void setAttitude(double[] a);
    void setAttribute(String attrName,double[] value);
}
