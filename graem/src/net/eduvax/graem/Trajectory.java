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
    @Override public void build(View view) {
        _mat=new Material(view.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        _mat.getAdditionalRenderState().setLineWidth(2f);
        _mat.setColor("Color",ColorRGBA.Red);
        _node=new Node(getName());
        view.getRootNode().attachChild(_node);
        _count=0;
    }

    /**
     * Call parent's location and set new point avalaible status.
     */ 
    @Override public void setLocation(double x, double y, double z) {
        super.setLocation(x,y,z);
        synchronized(this) {
            _newPoint=true;
        }
    }

    /**
     * On view update add new segment if location has changed since last update.
     */ 
    @Override public void update(float tpf) {
        boolean newPoint=false;
        synchronized(this) {
            newPoint=_newPoint;
            _newPoint=false;
        }
        if (newPoint) {
            if (_segStart==null) {
                Vector3f loc=getLocation();
                _segStart=new float[]{loc.getX(),loc.getY(),loc.getZ()};
            }
            else {
                _count++;
                Vector3f loc=getLocation();
                Mesh mesh=new Mesh();
                mesh.setMode(Mesh.Mode.Lines);
                Geometry segment = new Geometry("Segment"+_count, mesh);
                segment.setMaterial(_mat);
                mesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{
                            _segStart[0],
                            _segStart[1],
                            _segStart[2],
                            loc.getX(),
                            loc.getY(),
                            loc.getZ()
                        });
                mesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
                mesh.updateBound();
                mesh.updateCounts();
                _node.attachChild(segment);
                _segStart[0]=loc.getX();
                _segStart[1]=loc.getY();
                _segStart[2]=loc.getZ();
            }
        }
    }

    private Material _mat;
    private Node _node;
    private long _count;
    private float[] _segStart;
    private boolean _newPoint=false;
}
