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
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.math.Vector3f;
import java.util.Vector;

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
                if (pressed) nextCam();
            }
        },"nextCam");
        inputManager.addMapping("prevCam",new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
                if (pressed) prevCam();
            }
        },"prevCam");
        flyCam.setMoveSpeed(100.0f);
    }

    public void nextCam() {
        if (rootNode.getChildren().size()>0) {
            _selAvatar++;
            if (_selAvatar>rootNode.getChildren().size()) {
                _selAvatar=0;
            }
            setChase(_selAvatar);
        }
    }

    public void prevCam() {
        if (rootNode.getChildren().size()>0) {
            _selAvatar--;
            if (_selAvatar<0) {
                _selAvatar=rootNode.getChildren().size();
            }
            setChase(_selAvatar);
        }
    }

    
    private ChaseCamera _chaseCam=null;

    public void setChase(Spatial s) {
        if (s!=null) {
            flyCam.setEnabled(false);
            if (_chaseCam!=null) {
                _chaseCam.setEnabled(false);
                _chaseCam.cleanupWithInput(inputManager);
            }
            cam.setFrustumPerspective(
                    45f,       // fov
                    (float)cam.getWidth()/cam.getHeight(), // aspect
                    1.0f,      // near
                    1000000f); // far
            _chaseCam=new ChaseCamera(cam,s,inputManager);
            _chaseCam.setMinVerticalRotation((float)-Math.PI);
            s.removeControl(ChaseCamera.class);
            s.addControl(_chaseCam);
            _hudText.setText("Chasing "+s.getName());
            _chaseCam.setUpVector(Vector3f.UNIT_Y);
            _chaseCam.setEnabled(true);
        }
        else {
            if (_chaseCam!=null) {
                _chaseCam.setEnabled(false);
            }
            flyCam.setEnabled(true);
            _hudText.setText("Fly cam");
        }
    }
    public void setChase(int i) {
        Spatial s=null;
        try {
            s=rootNode.getChildren().get(i);
        }
        catch (IndexOutOfBoundsException ex) {
            // don't care out of bounds, 
            // it's a kind of reset towards
            // the fly cam
        }
        setChase(s);
    }
    public void setChase(String name) {
        setChase(rootNode.getChild(name));
    }

    public Node getRootNode() {
        return rootNode;
    }
    @Override public void simpleUpdate(float tpf) {
        synchronized(this) {
            if (_toAdd.size()>0) {
                for (ISceneComposition comp: _toAdd) {
                    comp.build(this);
                    _sceneElements.add(comp);
                }
                _toAdd.clear();
                _selAvatar=rootNode.getChildren().size()-1;
                setChase(_selAvatar);
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
    private int _selAvatar=0;
    private BitmapText _hudText;
}
