package hatfat.com.quest.cameras;

import com.hatfat.agl.AglPerspectiveCamera;
import com.hatfat.agl.util.Vec3;

public class HexPlanetCamera extends AglPerspectiveCamera {

    public HexPlanetCamera() {
        super (new Vec3(0.0f, 0.0f, 2.35f),
                new Vec3(0.0f, 0.0f, 0.0f),
                new Vec3(0.0f, 1.0f, 0.0f),
                60.0f, 1.0f, 0.1f, 100.0f);

        }
}
