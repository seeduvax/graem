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
import com.jme3.renderer.Renderer;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.input.InputManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.font.BitmapFont;
import com.jme3.math.Vector3f;
import com.jme3.environment.LightProbeFactory;
import com.jme3.light.LightProbe;
import com.jme3.environment.EnvironmentCamera;

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
        _settings.setGammaCorrection(true);
    }

    public AppSettings getSettings() {
        return _settings;
    }
    public synchronized void add(ISceneComposition comp) {
        _toAdd.add(comp);
    }
    @Override public void simpleInitApp() {
        inputManager.addMapping("nextCam",new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
                if (pressed) handleEvent("nextCam");
            }
        },"nextCam");
        inputManager.addMapping("prevCam",new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
                if (pressed) handleEvent("prevCam");
            }
        },"prevCam");
        final EnvironmentCamera envCam = new EnvironmentCamera(256, new Vector3f(0, 50f, 0));
        stateManager.attach(envCam);
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public BitmapFont getGuiFont() {
        return guiFont;
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
                if(_lightProbe==null){
                    _lightProbe = LightProbeFactory.makeProbe(stateManager.getState(EnvironmentCamera.class), rootNode);
                    _lightProbe.getArea().setRadius(100000000);
                    rootNode.addLight(_lightProbe);
                }
            }
        }
        boolean translate=_relativeFrame;
        if (translate&&_back!=null) {
            translateNodes(_back);
        }
        for (ISceneComposition c: _sceneElements) {
            c.update(tpf);
        }
        setCenter();
        if (translate&&_center!=null) {
            translateNodes(_center);
        }
    }

    private void translateNodes(Vector3f t) {
        rootNode.setLocalTranslation(rootNode.getLocalTranslation().add(t));
        cam.setLocation(cam.getLocation().add(t));
    }
    private void setCenter() {
        if (_centralNodeRequest!=null) {
            _centralNode=rootNode.getChild(_centralNodeRequest);
            _centralNodeRequest=null;
        }
        if (_centralNode!=null) {
            _back=_centralNode.getLocalTranslation().clone();
            _center=_back.clone().mult(-1.0f);
        }
    }
    public synchronized void setCentralNode(String name) {
        _centralNodeRequest=name;
    }

    public void setRelativeFrame(boolean r) {
        _relativeFrame=r;
    }

    private boolean _relativeFrame=true;
    private Vector3f _back=null;
    private Vector3f _center=null;
    private String _centralNodeRequest=null;
    private Spatial _centralNode=null;
    private LightProbe _lightProbe;

    private Vector<ISceneComposition> _toAdd=new Vector<ISceneComposition>();
    private Vector<ISceneComposition> _sceneElements=
                                           new Vector<ISceneComposition>();
    private AppSettings _settings=new AppSettings(true);
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