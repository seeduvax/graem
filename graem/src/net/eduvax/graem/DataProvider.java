/*
 * @file DataFilter.java
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
public class DataProvider implements IDataProvider, INamedObject {
    @Override public void setConsumer(IDataConsumer consumer) {
        _consumer=consumer;
    }
    @Override public IDataConsumer getConsumer() {
        return _consumer;
    }
    private IDataConsumer _consumer=null;
    
    @Override public String getName() {
        return _name;
    }
    @Override public void setName(String name) {
        _name=name;
    }
    private String _name;
}
