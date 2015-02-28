package hatfat.com.quest.hex;

import com.hatfat.agl.app.AglModule;

import dagger.Module;
import hatfat.com.quest.activities.HexActivity;

@Module
(
    library = true,

    includes = {
            AglModule.class,
    },

    injects = {
            HexActivity.class,
            HexApplication.class,
    }
)
public class HexModule {

}


