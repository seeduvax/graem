/*
 * @file HudText.java
 *
 * Copyright 2023 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
/**
 *
 */
public class HudText extends SceneComposition {
    @Override public void build(View view) {
        _hudText=new BitmapText(view.getGuiFont());
        _hudText.setColor(new ColorRGBA(_r,_g,_b,1.0f));
        _hudText.setSize(view.getGuiFont().getCharSet().getRenderedSize());
        _hudText.setLocalTranslation(_x,_y,0);
        _text=getName();
        view.getGuiNode().attachChild(_hudText); 
    }

    @Override public void update(float tpf) {
        synchronized (this) {
            _hudText.setText(_text);
        }
    }

    public synchronized void setText(String text) {
        _text=text;
    }
    public void setR(double r) {
        _r=(float)r;
    }
    public void setG(double g) {
        _g=(float)g;
    }
    public void setB(double b) {
        _b=(float)b;
    }
    public void setX(double x) {
        _x=(float)x;
    }
    public void setY(double y) {
        _y=(float)y;
    }

    private float _r=1.0f;
    private float _g=1.0f;
    private float _b=1.0f;
    private float _x=0.0f;
    private float _y=0.0f;
    private BitmapText _hudText;
    private String _text;
}
