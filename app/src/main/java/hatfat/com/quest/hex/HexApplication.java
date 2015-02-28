package hatfat.com.quest.hex;

import com.hatfat.agl.app.AglApplication;

public class HexApplication extends AglApplication {

    @Override
    public Object[] getInjectionModules() {
        return new Object[]{
                new HexModule(),
        };
    }
}
