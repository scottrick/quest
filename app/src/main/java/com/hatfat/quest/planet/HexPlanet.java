package com.hatfat.quest.planet;

import android.content.Context;
import android.util.Log;

import com.hatfat.agl.app.AglApplication;
import com.hatfat.agl.component.RenderableComponent;
import com.hatfat.agl.component.transform.Transform;
import com.hatfat.agl.entity.AglEntity;
import com.hatfat.agl.mesh.AglBBMesh;
import com.hatfat.agl.mesh.AglMesh;
import com.hatfat.agl.mesh.AglPoint;
import com.hatfat.agl.mesh.AglShape;
import com.hatfat.agl.render.AglColoredGeometry;
import com.hatfat.agl.render.AglWireframe;
import com.hatfat.agl.util.AglRandom;
import com.hatfat.agl.util.Color;
import com.hatfat.agl.util.Vec3;
import com.hatfat.quest.mesh.HexMeshManager;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class HexPlanet extends AglEntity {

    private RenderableComponent meshComponent;
    private RenderableComponent wireframeComponent;
    private RenderableComponent highlightComponent;

    private AglBBMesh shapeMesh = null;

    @Inject HexMeshManager meshManager;
    @Inject Bus            bus;

    private HexTile       highlightTile = null;
    private List<HexTile> tiles         = new ArrayList<>();
    private HashMap<AglShape, HexTile> shapeTileMap;

    private AglRandom random = new AglRandom();

    public HexPlanet(Context context, int planetLevel) {
        super();

        AglApplication app = AglApplication.get(context);
        app.inject(this);

        shapeMesh = meshManager.loadMesh(planetLevel);

        if (shapeMesh != null) {
            shapeTileMap = new HashMap<>();

            //make tiles
            for (AglShape shape : shapeMesh.getShapes()) {
                HexTile tile = new HexTile(shape);
                tiles.add(tile);
                shapeTileMap.put(shape, tile);
            }

            //setup tile neighbors
            for (HexTile tile : tiles) {
                for (Integer shapeIndex : tile.getShape().getNeighborIndices()) {
                    AglShape shape = shapeMesh.getShapes().get(shapeIndex);
                    HexTile neighborTile = shapeTileMap.get(shape);
                    tile.addNeighbor(neighborTile);
                }
            }

            generateTestTerrain(planetLevel);
        }
    }

    public void setupComponents() {
        AglWireframe wireframeRenderable = shapeMesh.toWireframeRenderable();
        wireframeRenderable.setLineWidth(2.0f);
        wireframeRenderable.setWireframeColor(new Color(0.15f, 0.15f, 0.15f, 1.0f));
        AglColoredGeometry coloredRenderable = toColoredGeometryRenderable();

        AglWireframe highlightWireframeRenderable = makeHighlightRenderableWireframe();

        Transform transform = new Transform();
        meshComponent = new RenderableComponent(coloredRenderable);
        wireframeComponent = new RenderableComponent(wireframeRenderable);
        highlightComponent = new RenderableComponent(highlightWireframeRenderable);

        addComponent(transform);
        addComponent(meshComponent);
        addComponent(wireframeComponent);
        addComponent(highlightComponent);
//        highlightNode.setScale(new Vec3(1.002f, 1.002f, 1.002f));
    }

    private void generateTestTerrain(int planetLevel) {
        long generateStartTime = System.currentTimeMillis();

        //set starting seed tiles
        int numToSeed = (int)((float) tiles.size() * 0.015f);
        numToSeed = Math.max(numToSeed, 9); //always seed at least 9

        List<HexTile> workingSet = new ArrayList<>();

        //always at least one land tile
        int startingLandTileIndex = random.get().nextInt(tiles.size());
        HexTile startingLandTile = tiles.get(startingLandTileIndex);
        startingLandTile.setType(HexTile.HexTileType.LAND);
        workingSet.add(startingLandTile);

        for (int i = 1; i < numToSeed; i++) {
            int randomTile = random.get().nextInt(tiles.size());
            HexTile tile = tiles.get(randomTile);
            tile.assignRandomBaseType(random);
//            tile.assignRandomType(random);
            workingSet.add(tile);
        }

        List<HexTile> tilesToChooseFrom = new ArrayList<>();

        while (workingSet.size() > 0) {
            int randomTile = random.get().nextInt(workingSet.size());
            HexTile tile = workingSet.get(randomTile);

            tilesToChooseFrom.clear();

            for (HexTile neighborTile : tile.getNeighbors()) {
                if (!neighborTile.isTypeSet()) {
                    tilesToChooseFrom.add(neighborTile);
                }
            }

            if (tilesToChooseFrom.size() > 0) {
                int randomNeighborIndex = random.get().nextInt(tilesToChooseFrom.size());
                HexTile chosenTile = tilesToChooseFrom.get(randomNeighborIndex);
                chosenTile.setType(tile.getType());
                workingSet.add(chosenTile);
            }
            else {
                //no more to choose from!  so lets remove this one
                workingSet.remove(tile);
            }
        }

        long generateEndTime = System.currentTimeMillis();

        Log.i("HexPlanet", "[" + planetLevel + "] generateTestTerrain took " + (generateEndTime
                - generateStartTime) + " milliseconds.");
    }

    public HexTile findTileClosestToPoint(Vec3 point) {
        HexTile currentBest = tiles.get(0);
        HexTile nextBest = null;

        while (currentBest != nextBest) {
            nextBest = currentBest;

            ArrayList<AglPoint> points = shapeMesh.getPoints();

            float bestDist = Vec3.calculateDistanceSquared(point,
                    points.get(currentBest.getShape().getCenterIndex()).p);

            Set<HexTile> neighbors = currentBest.getNeighbors();

            for (HexTile tile : neighbors) {
                float testDist = Vec3.calculateDistanceSquared(point,
                        points.get(tile.getShape().getCenterIndex()).p);

                if (testDist < bestDist) {
                    currentBest = tile;
                }
            }
        }

        return currentBest;
    }

    private AglColoredGeometry toColoredGeometryRenderable() {
        float[] vertices = shapeMesh.getVertexArray();
        int numVertices = shapeMesh.getNumVertices();

        float[] newVertices = new float[numVertices * 10];

        for (int i = 0; i < numVertices; i++) {
            newVertices[i * 10 + 0] = vertices[i * 3 + 0]; //x
            newVertices[i * 10 + 1] = vertices[i * 3 + 1]; //y
            newVertices[i * 10 + 2] = vertices[i * 3 + 2]; //z
//            newVertices[i * 10 + 3] = 0.9f; //r
//            newVertices[i * 10 + 4] = 0.9f; //g
//            newVertices[i * 10 + 5] = 0.9f; //b
//            newVertices[i * 10 + 6] = 1.0f; //a
            newVertices[i * 10 + 7] = vertices[i * 3 + 0]; //normal x
            newVertices[i * 10 + 8] = vertices[i * 3 + 1]; //normal y
            newVertices[i * 10 + 9] = vertices[i * 3 + 2]; //normal z
        }

        List<Integer> indicesToColor = new LinkedList<>();

        for (HexTile tile : tiles) {
            indicesToColor.clear();
            indicesToColor.addAll(tile.getShape().getOuterPoints());

            Color tileColor = tile.getType().getColor();

            for (Integer index : indicesToColor) {
                newVertices[index * 10 + 3] = tileColor.r;
                newVertices[index * 10 + 4] = tileColor.g;
                newVertices[index * 10 + 5] = tileColor.b;
                newVertices[index * 10 + 6] = tileColor.a;
            }

            //color the center a little brighter for now
            int index = tile.getShape().getCenterIndex();
            newVertices[index * 10 + 3] = tileColor.r * 1.15f;
            newVertices[index * 10 + 4] = tileColor.g * 1.15f;
            newVertices[index * 10 + 5] = tileColor.b * 1.15f;
            newVertices[index * 10 + 6] = tileColor.a;
        }

        int[] elements = new int[AglBBMesh.NUM_PENTAGONS * 15 + (shapeMesh.getNumShapes() - AglBBMesh.NUM_PENTAGONS) * 18];
        int currentOffset = 0;

        for (AglShape shape : shapeMesh.getShapes()) {
            for (int j = 0; j < shape.getOuterPoints().size(); j++) {
                int index1 = shape.getOuterPoint(j);
                int index2 = shape.getOuterPoint((j + 1) % shape.getOuterPoints().size());

                elements[currentOffset + j * 3 + 0] = index2;
                elements[currentOffset + j * 3 + 1] = shape.getCenterIndex();
                elements[currentOffset + j * 3 + 2] = index1;
            }

            currentOffset += shape.getOuterPoints().size() * 3;
        }

        return new AglColoredGeometry(newVertices, numVertices, elements, elements.length);
    }

    public void setHighlightTile(HexTile tile) {
        if (highlightTile == tile) {
            return;
        }

        highlightTile = tile;

        int numVertices = 6;
        float[] vertices = new float[numVertices * 3];

        for (int i = 0; i < numVertices; i++) {
            if (tile != null) {
                int vertexIndex = tile.getShape()
                        .getOuterPoint(i % tile.getShape().getNumberOfSides());
                AglPoint point = shapeMesh.getPoints().get(vertexIndex);

                vertices[(i * 3) + 0] = point.p.x;
                vertices[(i * 3) + 1] = point.p.y;
                vertices[(i * 3) + 2] = point.p.z;
            }
            else {
                vertices[(i * 3) + 0] = 0;
                vertices[(i * 3) + 1] = 0;
                vertices[(i * 3) + 2] = 0;
            }
        }

        AglWireframe wireframe = (AglWireframe) highlightComponent.getRenderable();
        wireframe.updateWithVertices(vertices, numVertices);

        bus.post(new HighlightedHexTileChangedEvent(highlightTile));
    }

    public HexTile getHighlightTile() {
        return highlightTile;
    }

    public AglWireframe makeHighlightRenderableWireframe() {
        int numVertices = 6;

        float[] vertices = new float[numVertices * 3];
        int[] indices = new int[numVertices * 2];

        for (int i = 0; i < numVertices; i++) {
            indices[(i * 2) + 0] = (i + 0) % numVertices;
            indices[(i * 2) + 1] = (i + 1) % numVertices;
        }

        AglWireframe wireframe = new AglWireframe(vertices, numVertices, indices, numVertices * 2);

        wireframe.setLineWidth(4.0f);
        wireframe.setWireframeColor(new Color(0.9f, 0.85f, 0.15f, 1.0f));

        return wireframe;
    }

    public AglMesh getMesh() {
        return shapeMesh;
    }

    public RenderableComponent getMeshComponent() {
        return meshComponent;
    }

    public RenderableComponent getWireframeComponent() {
        return wireframeComponent;
    }

    public List<HexTile> getTiles() {
        return tiles;
    }
}
