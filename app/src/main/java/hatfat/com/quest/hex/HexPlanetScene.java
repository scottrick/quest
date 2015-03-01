package hatfat.com.quest.hex;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.hatfat.agl.AglNode;
import com.hatfat.agl.AglRenderable;
import com.hatfat.agl.AglScene;
import com.hatfat.agl.mesh.AglBBMesh;
import com.hatfat.agl.util.Vec3;

import java.io.IOException;
import java.io.InputStream;

import hatfat.com.quest.R;
import hatfat.com.quest.cameras.HexPlanetCamera;
import hatfat.com.quest.planet.HexPlanet;

public class HexPlanetScene extends AglScene {

    private HexPlanet planet;

    public HexPlanetScene(Context context) {
        super(context);

        HexPlanetCamera camera = new HexPlanetCamera();
        setCamera(camera);
    }

    @Override
    public void setupScene() {
        super.setupScene();

        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);
        GLES20.glPolygonOffset(1.0f, 1.0f);

        long setupStartTime = System.currentTimeMillis();

        InputStream in = getContext().getResources().openRawResource(R.raw.mesh7);

        AglBBMesh shapeMesh = null;
        AglNode meshNode = null;
        AglNode wireframeNode = null;

        try {
            shapeMesh = AglBBMesh.readFromStreamAsBytes(in);
        }
        catch (IOException e) {
            Log.e("TestScene", "Error loading BB mesh resources.");
        }

        if (shapeMesh != null) {
            AglRenderable wireframeRenderable = shapeMesh.toWireframeRenderable();
            AglRenderable coloredRenderable = shapeMesh.toColoredGeometryRenderable();

            meshNode = new AglNode(new Vec3(0.0f, 0.0f, 0.0f), coloredRenderable);
            addNode(meshNode);

            wireframeNode = new AglNode(new Vec3(0.0f, 0.0f, 0.0f), wireframeRenderable);
            addNode(wireframeNode);
        }

        long setupEndTime = System.currentTimeMillis();

        Log.i("HexPlanetScene", " TestScene setup took " + (setupEndTime - setupStartTime) + " milliseconds.");
    }

    @Override
    public void destroyScene() {
        super.destroyScene();

        GLES20.glPolygonOffset(0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_POLYGON_OFFSET_FILL);
    }
}
