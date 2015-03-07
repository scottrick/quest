package hatfat.com.quest.mesh;

import android.app.Application;
import android.util.Log;

import com.hatfat.agl.mesh.AglBBMesh;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import hatfat.com.quest.R;

public class HexMeshManager {

    private Application application;

    private HashMap<Integer, AglBBMesh> cache = new HashMap<>();

    public HexMeshManager(Application application) {
        this.application = application;
    }

    public AglBBMesh loadMesh(int planetLevel) {
        int planetResourceId = application.getResources().getIdentifier("mesh" + planetLevel, "raw", application.getPackageName());
        if (planetResourceId <= 0) {
            //invalid planet level, use default
            planetResourceId = R.raw.mesh4;
        }

        AglBBMesh shapeMesh = cache.get(planetResourceId);

        if (shapeMesh == null) {
            /* not loaded yet */
            InputStream in = application.getResources().openRawResource(planetResourceId);

            try {
                shapeMesh = AglBBMesh.readFromStreamAsBytes(in);
                cache.put(planetResourceId, shapeMesh);
            }
            catch (IOException e) {
                Log.e("HexPlanet", "Error loading BB mesh resources.");
            }
        }

        return shapeMesh;
    }
}
