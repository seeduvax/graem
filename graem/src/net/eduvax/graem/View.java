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
import java.util.Hashtable;

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
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

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
        inputManager.addMapping("nextCam",new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
System.out.println("nextCam");
                if (pressed) handleEvent("nextCam");
            }
        },"nextCam");
        inputManager.addMapping("prevCam",new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
System.out.println("prevCam");
                if (pressed) handleEvent("prevCam");
            }
        },"prevCam");
    }


    public void registerEventHandler(String evName,Runnable handler) {
        Vector<Runnable> vh=_eventHandlers.get(evName);
        if (vh==null) {
            vh=new Vector<Runnable>();
            _eventHandlers.put(evName,vh);
        }
        vh.add(handler);
    }

    public Node getRootNode() {
        return rootNode;
    }
    public FlyByCamera getFlyCam() {
        return flyCam;
    }
    public Camera getCamera() {
        return cam;
    }
    public InputManager getInputManager() {
        return inputManager;
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
    private Hashtable<String,Vector<Runnable>> _eventHandlers=new Hashtable<String,Vector<Runnable>>();
    private void handleEvent(String name) {
        Vector<Runnable> toRun=_eventHandlers.get(name);
        if (toRun!=null) {
            for (Runnable r: toRun) {
                r.run();
            }
        }
    }
    
}
