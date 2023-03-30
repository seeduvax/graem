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

import com.jme3.input.FlyByCamera;
import com.jme3.input.ChaseCamera;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.font.BitmapText;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.renderer.Camera;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
/**
 *
 */
public class AutoChaseCam extends SceneComposition {
    @Override public void build(View view) {
        _view=view;
        _root=view.getRootNode();
        _flyCam=view.getFlyCam();
        _flyCam.setMoveSpeed(100.0f);
        _view.registerEventHandler("nextCam",new Runnable() {
                    @Override public void run() {
                        nextCam();
                    }
                });
        _view.registerEventHandler("prevCam",new Runnable() {
                    @Override public void run() {
                        prevCam();
                    }
                });
        _hudText=new BitmapText(_view.getGuiFont());
        _hudText.setColor(ColorRGBA.Yellow);
_hudText.setColor(ColorRGBA.Red);
        _hudText.setSize(_view.getGuiFont().getCharSet().getRenderedSize());
        _hudText.setLocalTranslation(10f,10f+_hudText.getLineHeight(),0);
        _hudText.setText("Hello Graem");
        _view.getGuiNode().attachChild(_hudText); 
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
                _chaseCam.cleanupWithInput(_view.getInputManager());
            }
            Camera cam=_view.getCamera();
            cam.setFrustumPerspective(
                    45f,       // fov
                    (float)cam.getWidth()/cam.getHeight(), // aspect
                    1.0f,      // near
                    10000000f); // far
            _chaseCam=new ChaseCamera(cam,s,_view.getInputManager());
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
            _chaseCam.setMaxDistance(size*20f);
            _chaseCam.setDefaultDistance(size*8f);
            _chaseCam.setZoomSensitivity(size/5f);
            Vector3f lookOffset=bound.getCenter().subtract(s.getLocalTranslation()).add(_offset);
            _chaseCam.setLookAtOffset(lookOffset);

            s.removeControl(ChaseCamera.class);
            s.addControl(_chaseCam);
            _hudText.setText("Chasing "+s.getName());
            _chaseCam.setUpVector(Vector3f.UNIT_Y);
            _chaseCam.setEnabled(true);
            _view.setCentralNode(s.getName());
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
        if(_root==null){
            _initialChase=name;
        }
        else{
            setChase(_root.getChild(name));
        }
    }
    public void setOffsetX(double x) {
        _offset.x=(float)x;
    }
    public void setOffsetY(double y) {
        _offset.y=(float)y;
    }
    public void setOffsetZ(double z) {
        _offset.z=(float)z;
    }

    @Override public void update(float tpf) {
        if(_initialChase!=null){
            setChase(_root.getChild(_initialChase));
            _initialChase=null;
        }
    }
    
    private View _view;
    private Node _root;
    private int _prevSize;
    private int _selAvatar=0;
    private String _initialChase;
    private Vector3f _offset= new Vector3f(0f,0f,0f);
    private BitmapText _hudText;
}
