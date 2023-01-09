/*
 * @file IDataFilter.java
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
public interface IDataProvider {
    void setConsumer(IDataConsumer consumer);
    IDataConsumer getConsumer();
}
