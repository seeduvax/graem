/*
 * @file View.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import java.util.Vector;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.input.InputManager;
import com.jme3.input.FlyByCamera;

/**
 *
 */
public class View extends SimpleApplication {
    public View() {
        setShowSettings(false);
        setDisplayFps(false);
        setDisplayStatView(false);
        setPauseOnLostFocus(false);
        setSettings(_settings);
        _settings.setTitle("GraEm - data projectionist");
        _settings.setVSync(true);
        _settings.setResolution(1280,768);
    }

    public AppSettings getSettings() {
        return _settings;
    }
    public synchronized void add(ISceneComposition comp) {
        _toAdd.add(comp);
    }
    @Override public void simpleInitApp() {
        _hudText=new BitmapText(guiFont);
        _hudText.setColor(ColorRGBA.Yellow);
        _hudText.setSize(guiFont.getCharSet().getRenderedSize());
        _hudText.setLocalTranslation(10f,10f+_hudText.getLineHeight(),0);
        _hudText.setText("Hello Graem");
        guiNode.attachChild(_hudText); 

xxx=new AutoChaseCam();
xxx.build(this);
    }
private AutoChaseCam xxx;


    public Node getRootNode() {
        return rootNode;
    }
    public InputManager getInputManager() {
        return inputManager;
    }
    public FlyByCamera getFlyCam() {
        return flyCam;
    }
    public Camera getCamera() {
        return cam;
    }
    @Override public void simpleUpdate(float tpf) {
        synchronized(this) {
            if (_toAdd.size()>0) {
                for (ISceneComposition comp: _toAdd) {
                    comp.build(this);
                    _sceneElements.add(comp);
                }
                _toAdd.clear();
            }
        }
        for (ISceneComposition c: _sceneElements) {
            c.update(tpf);
        }
    }

    protected BitmapText getHudText() {
        return _hudText;
    }
    
    private Vector<ISceneComposition> _toAdd=new Vector<ISceneComposition>();
    private Vector<ISceneComposition> _sceneElements=
                                           new Vector<ISceneComposition>();
    private AppSettings _settings=new AppSettings(true);
    private BitmapText _hudText;
}
