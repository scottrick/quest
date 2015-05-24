package com.hatfat.quest.hex;

import android.app.Application;

import com.hatfat.agl.app.AglModule;
import com.hatfat.quest.activities.HexActivity;
import com.hatfat.quest.mesh.HexMeshManager;
import com.hatfat.quest.planet.HexPlanet;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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


