package hatfat.com.quest.hex;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.hatfat.agl.AglScene;
import com.hatfat.agl.util.Vec3;

import hatfat.com.quest.cameras.HexPlanetCamera;
import hatfat.com.quest.planet.HexPlanet;
import hatfat.com.quest.planet.HexTile;

public class HexPlanetScene extends AglScene {

    private HexPlanet planet;

    private int planetLevel;
    private boolean meshInitiallyVisible;
    private boolean wireframeInitiallyVisible;

    public HexPlanetScene(Context context, int planetLevel, boolean meshInitiallyVisible, boolean wireframeInitiallyVisible) {
        super(context);

        this.planetLevel = planetLevel;
        this.meshInitiallyVisible = meshInitiallyVisible;
        this.wireframeInitiallyVisible = wireframeInitiallyVisible;

        HexPlanetCamera camera = new HexPlanetCamera();
        setCamera(camera);

        Vec3 newLightDir = new Vec3(0.1f, 0.1f, 1.0f);
        newLightDir.normalize();
        getGlobalLight().lightDir = newLightDir;
    }

    @Override
    public void setupScene() {
        super.setupScene();

        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);
        GLES20.glPolygonOffset(1.0f, 1.0f);

        long setupStartTime = System.currentTimeMillis();

        planet = new HexPlanet(getContext(), planetLevel);
        getCamera().setPlanet(planet);
        addNodes(planet.getNodes());

        planet.getMeshNode().setShouldRender(meshInitiallyVisible);
        planet.getWireframeNode().setShouldRender(wireframeInitiallyVisible);

        long setupEndTime = System.currentTimeMillis();

        Log.i("HexPlanetScene", " HexPlanetScene setup took " + (setupEndTime - setupStartTime) + " milliseconds.");
    }

    @Override
    public void destroyScene() {
        super.destroyScene();

        GLES20.glPolygonOffset(0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_POLYGON_OFFSET_FILL);
    }

    public void focusTile(HexTile tile) {
        getCamera().setFocusOnTile(tile);
        planet.setHighlightTile(tile);
    }

    public HexPlanetCamera getCamera() {
        return (HexPlanetCamera) super.getCamera();
    }

    public HexPlanet getPlanet() {
        return planet;
    }

    public void toggleMesh() {
        if (planet != null) {
            planet.getMeshNode().setShouldRender(!isMeshVisible());
        }
    }

    public void toggleWireframe() {
        if (planet != null) {
            planet.getWireframeNode().setShouldRender(!isWireframeVisible());
        }
    }

    public boolean isMeshVisible() {
        if (planet != null) {
            return planet.getMeshNode().shouldRender();
        }
        else {
            return meshInitiallyVisible;
        }
    }

    public boolean isWireframeVisible() {
        if (planet != null) {
            return planet.getWireframeNode().shouldRender();
        }
        else {
            return wireframeInitiallyVisible;
        }
    }
}
