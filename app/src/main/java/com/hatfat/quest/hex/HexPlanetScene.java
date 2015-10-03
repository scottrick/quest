package com.hatfat.quest.hex;

import android.content.Context;
import android.opengl.GLES20;

import com.hatfat.agl.AglScene;
import com.hatfat.agl.app.AglRenderer;
import com.hatfat.agl.component.ComponentType;
import com.hatfat.agl.component.transform.Transform;
import com.hatfat.agl.entity.AglEntity;
import com.hatfat.agl.util.Vec3;
import com.hatfat.quest.cameras.HexPlanetCameraComponent;
import com.hatfat.quest.planet.HexPlanet;
import com.hatfat.quest.planet.HexTile;

public class HexPlanetScene extends AglScene {

    private HexPlanet planet;

    private int     planetLevel;
    private boolean meshInitiallyVisible;
    private boolean wireframeInitiallyVisible;

    public HexPlanetScene(Context context, int planetLevel, boolean meshInitiallyVisible,
            boolean wireframeInitiallyVisible) {
        super(context, false);

        this.planetLevel = planetLevel;
        this.meshInitiallyVisible = meshInitiallyVisible;
        this.wireframeInitiallyVisible = wireframeInitiallyVisible;

        /* setup the camera */
        AglEntity cameraEntity = new AglEntity("HexPlanet Camera Entity");
        cameraEntity.addComponent(new HexPlanetCameraComponent());
        addCameraEntity(cameraEntity);

        /* update the light position */
        Vec3 newLightDir = new Vec3(0.25f, 0.35f, 1.0f);
        newLightDir.normalize();

        Transform lightTransform = getGlobalLight().getComponentByType(ComponentType.TRANSFORM);
        lightTransform.posQuat.pos = newLightDir;
    }

    @Override
    protected void setupSceneBackgroundWork() {
        super.setupSceneBackgroundWork();

        planet = new HexPlanet(getContext(), planetLevel);
        getCamera().setPlanet(planet);
    }

    @Override
    protected void setupSceneGLWork(AglRenderer renderer) {
        super.setupSceneGLWork(renderer);

        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);
        GLES20.glPolygonOffset(1.0f, 1.0f);

        planet.setupComponents();
        addEntity(planet);

        planet.getMeshComponent().setShouldRender(meshInitiallyVisible);
        planet.getWireframeComponent().setShouldRender(wireframeInitiallyVisible);
    }

    @Override
    public void destroyScene(AglRenderer renderer) {
        super.destroyScene(renderer);

        GLES20.glPolygonOffset(0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_POLYGON_OFFSET_FILL);
    }

    public void focusTile(HexTile tile) {
        getCamera().setFocusOnTile(tile);
        planet.setHighlightTile(tile);
    }

    public HexPlanetCameraComponent getCamera() {
        return (HexPlanetCameraComponent) super.getCamera();
    }

    public HexPlanet getPlanet() {
        return planet;
    }

    public void toggleMesh() {
        if (planet != null) {
            planet.getMeshComponent().setShouldRender(!isMeshVisible());
        }
    }

    public void toggleWireframe() {
        if (planet != null) {
            planet.getWireframeComponent().setShouldRender(!isWireframeVisible());
        }
    }

    public boolean isMeshVisible() {
        if (planet != null) {
            return planet.getMeshComponent().shouldRender();
        }
        else {
            return meshInitiallyVisible;
        }
    }

    public boolean isWireframeVisible() {
        if (planet != null) {
            return planet.getWireframeComponent().shouldRender();
        }
        else {
            return wireframeInitiallyVisible;
        }
    }
}
