/*
 * @file AutoChaseCam.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.input.InputManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.font.BitmapText;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.renderer.Camera;
import com.jme3.math.Vector3f;
/**
 *
 */
public class AutoChaseCam extends SceneComposition {
    @Override public void build(View view) {
        _view=view;
        _inputManager=view.getInputManager();
        _root=view.getRootNode();
        _flyCam=view.getFlyCam();
        _flyCam.setMoveSpeed(100.0f);
        _hudText=view.getHudText();

        _inputManager.addMapping("nextCam",new KeyTrigger(KeyInput.KEY_PGUP));
        _inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
                if (pressed) nextCam();
            }
        },"nextCam");
        _inputManager.addMapping("prevCam",new KeyTrigger(KeyInput.KEY_PGDN));
        _inputManager.addListener(new ActionListener() {
            @Override 
            public void onAction(String name, boolean pressed, float tpf) {
                if (pressed) prevCam();
            }
        },"prevCam");
    }

    public void nextCam() {
        if (_root.getChildren().size()>0) {
            _selAvatar++;
            if (_selAvatar>_root.getChildren().size()) {
                _selAvatar=0;
            }
            setChase(_selAvatar);
        }
    }

    public void prevCam() {
        if (_root.getChildren().size()>0) {
            _selAvatar--;
            if (_selAvatar<0) {
                _selAvatar=_root.getChildren().size();
            }
            setChase(_selAvatar);
        }
    }

    
    private ChaseCamera _chaseCam=null;
    private FlyByCamera _flyCam=null;

    public void setChase(Spatial s) {
        if (s!=null) {
            _flyCam.setEnabled(false);
            if (_chaseCam!=null) {
                _chaseCam.setEnabled(false);
                _chaseCam.cleanupWithInput(_inputManager);
            }
            Camera cam=_view.getCamera();
            cam.setFrustumPerspective(
                    45f,       // fov
                    (float)cam.getWidth()/cam.getHeight(), // aspect
                    1.0f,      // near
                    10000000f); // far
            _chaseCam=new ChaseCamera(cam,s,_inputManager);
            _chaseCam.setMinVerticalRotation((float)-Math.PI);
            float size=10f;
            BoundingVolume bound =s.getWorldBound();
            if(bound instanceof BoundingSphere){
                size = ((BoundingSphere)bound).getRadius();
            }
            if(bound instanceof BoundingBox){
                size = Math.max(Math.max(((BoundingBox)bound).getXExtent(),((BoundingBox)bound).getYExtent()),((BoundingBox)bound).getZExtent());
            }
            size = Math.min(size,100000f);
            _chaseCam.setMaxDistance(size*5f);
            _chaseCam.setDefaultDistance(size);
            _chaseCam.setZoomSensitivity(size/5f);

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
            _flyCam.setEnabled(true);
            _hudText.setText("Fly cam");
        }
    }
    public void setChase(int i) {
        Spatial s=null;
        try {
            s=_root.getChildren().get(i);
        }
        catch (IndexOutOfBoundsException ex) {
            // don't care out of bounds, 
            // it's a kind of reset towards
            // the fly cam
        }
        setChase(s);
    }
    public void setChase(String name) {
        setChase(_root.getChild(name));
    }

    @Override public void update(float tpf) {
        if (_root.getChildren().size()!=_prevSize) {
            // something has been added since last update
            // switch cam on it.
            _selAvatar=_root.getChildren().size()-1;
            setChase(_selAvatar);
        }
    }
    
    private View _view;
    private Node _root;
    private int _prevSize;
    private int _selAvatar=0;
    private BitmapText _hudText;
    private InputManager _inputManager;
}
