/*
 * @file IDataConsumer.java
 *
 * Copyright 2023 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

/**
 *
 */
public interface IDataConsumer {
    void handleData(String name, double[] values);
}
