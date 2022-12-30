/*
 * @file DefaultScene.java
 *
 * Copyright 2022 Sebastien Devaux. All rights reserved.
 * Use is subject to license terms.
 *
 * $Id$
 * $Date$
 */
package net.eduvax.graem;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort; 
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
/**
 *
 */
public class Playfield extends SceneComposition {
    private AssetManager _assetManager;

    private void addGrid(String name, Node parent,
                        int xCount, int yCount, float size,
                        Vector3f loc, ColorRGBA color, int lineWidth) {
        Geometry grid=new Geometry(name,new Grid(xCount,yCount,size));
        if (loc!=null) {
            grid.setLocalTransform(new Transform(loc));
        }
        Material mat = new Material(_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(lineWidth);
        mat.setColor("Color", color);
        grid.setMaterial(mat);
        grid.setShadowMode(RenderQueue.ShadowMode.Off);
        parent.attachChild(grid);
    }

    private void addTransparentBox(String name, Node parent, float x, float y, float z,
                        Vector3f loc, ColorRGBA color) {
        Geometry box = new Geometry(name, new Box(x, y, z));
        box.setLocalTransform(new Transform(loc));
        Material mat = new Material(_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Ambient",color);
        mat.setColor("Diffuse",color);
        mat.setColor("Specular",ColorRGBA.White);
        mat.setFloat("Shininess", 64f);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        box.setMaterial(mat);
        box.setQueueBucket(RenderQueue.Bucket.Transparent);
        parent.attachChild(box);
    }


    @Override public void build(View view) {
        Node parent=view.getRootNode();
        _assetManager=view.getAssetManager();
        Node playfield=new Node(getName());
        parent.attachChild(playfield);

        Node ground=new Node("Ground");
        addTransparentBox("NE",ground,500f,0.01f,500f,new Vector3f(-500,-0.005f,-500),new ColorRGBA(0.6f,0.6f,0.8f,0.7f));
        addTransparentBox("NW",ground,500f,0.01f,500f,new Vector3f(-500,-0.005f,500),new ColorRGBA(0.6f,0.8f,0.6f,0.7f));
        addTransparentBox("SE",ground,500f,0.01f,500f,new Vector3f(500,-0.005f,500),new ColorRGBA(0.8f,0.6f,0.6f,0.7f));
        addTransparentBox("sW",ground,500f,0.01f,500f,new Vector3f(500,-0.005f,-500),new ColorRGBA(0.8f,0.8f,0.6f,0.7f));
        playfield.attachChild(ground);

        Node grid=new Node("Grid");
        addGrid("NE",grid,51,51,10f,new Vector3f(-500, 0, -500),ColorRGBA.Black,2);
        addGrid("NW",grid,51,51,10f,new Vector3f(-500, 0, 0),ColorRGBA.Black,2);
        addGrid("SE",grid,51,51,10f,null,ColorRGBA.Black,2);
        addGrid("SW",grid,51,51,10f,new Vector3f(0, 0, -500),ColorRGBA.Black,2);
        playfield.attachChild(grid);


        Material tb = new Material(_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        tb.setBoolean("UseMaterialColors",true);
        ColorRGBA trBlue=new ColorRGBA(0.5f,0.5f,0.9f,0.5f);
        tb.setColor("Ambient",trBlue);
        tb.setColor("Diffuse",trBlue);
        tb.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        
        for (int i=1;i<10;i++) {
            Geometry plate = new Geometry("Plate"+i, new Box(1000, 0.01f, 1000));
            Transform ts=new Transform(new Vector3f(0,1000.0f*i-0.005f,0));
            plate.setLocalTransform(ts);
            plate.setQueueBucket(RenderQueue.Bucket.Transparent);
            plate.setMaterial(tb);
            plate.setQueueBucket(RenderQueue.Bucket.Transparent);
            playfield.attachChild(plate);
            addGrid("Grid"+i,playfield,101,101,10f,new Vector3f(-500, 1000.0f*i, -500),ColorRGBA.Black,2);
        }
    }
}
