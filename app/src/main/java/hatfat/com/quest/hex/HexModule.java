package hatfat.com.quest.hex;

import android.app.Application;

import com.hatfat.agl.app.AglModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hatfat.com.quest.activities.HexActivity;
import hatfat.com.quest.mesh.HexMeshManager;
import hatfat.com.quest.planet.HexPlanet;

@Module
(
    library = true,

    includes = {
            AglModule.class,
    },

    injects = {
            HexActivity.class,
            HexApplication.class,
            HexPlanet.class,
    }
)
public class HexModule {

    private final HexApplication app;

    public HexModule(HexApplication app) {
        this.app = app;
    }

    @Provides @Singleton Application provideApplication() {
        return app;
    }

    @Provides @Singleton HexMeshManager provideHexMeshManager(Application app) {
        return new HexMeshManager(app);
    }
}


