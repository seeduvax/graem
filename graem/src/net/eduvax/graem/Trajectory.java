/*
 * @file Trajectory.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Trajectory renders the location changes as a polyline.
 */
public class Trajectory extends Avatar implements ISceneComposition {
    /**
     * Build material and root node of the trajectory.
     */  
    @Override protected Node build() {
        Node node=super.build();
        _mat=new Material(getView().getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        _mat.getAdditionalRenderState().setLineWidth(2f);
        _mat.setColor("Color",new ColorRGBA(_r,_g,_b,1.0f));
        _count=0;
        return node;
    }

    /**
     * On view update add new segment if location has changed since last update.
     */ 
    @Override public void update(float tpf) {
        boolean newPoint=false;
        synchronized(this) {
            double[] loc=getLocation();
            newPoint=loc!=_loc;
            if (newPoint) {
                _loc=loc;
            }
        }
        if (newPoint) {
            if (_segStart==null) {
                _segStart=new float[]{(float)_loc[0],(float)_loc[1],(float)_loc[2]};
            }
            else {
                _count++;
                Mesh mesh=new Mesh();
                mesh.setMode(Mesh.Mode.Lines);
                Geometry segment = new Geometry("Segment"+_count, mesh);
                segment.setMaterial(_mat);
                mesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{
                            _segStart[0],
                            _segStart[1],
                            _segStart[2],
                            (float)_loc[0],
                            (float)_loc[1],
                            (float)_loc[2]
                        });
                mesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
                mesh.updateBound();
                mesh.updateCounts();
                getNode().attachChild(segment);
                _segStart[0]=(float)_loc[0];
                _segStart[1]=(float)_loc[1];
                _segStart[2]=(float)_loc[2];
            }
        }
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

    private Material _mat;
    private long _count;
    private float[] _segStart;
    private double[] _loc=null;
    private boolean _newPoint=false;
    private float _r=1.0f;
    private float _g=0.0f;
    private float _b=0.0f;
}
