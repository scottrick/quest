package hatfat.com.quest.hex;

import com.hatfat.agl.app.AglApplication;
import com.hatfat.agl.app.AglModule;

public class HexApplication extends AglApplication {

    @Override
    public Object[] getInjectionModules() {
        return new Object[] {
                new AglModule(),
                new HexModule(this),
        };
    }
}
