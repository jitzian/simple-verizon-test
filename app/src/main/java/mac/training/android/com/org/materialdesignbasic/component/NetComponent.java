package mac.training.android.com.org.materialdesignbasic.component;

import dagger.Component;
import mac.training.android.com.org.materialdesignbasic.fragments.PhotoFragment;
import mac.training.android.com.org.materialdesignbasic.module.NetModule;

/**
 * Created by raian on 1/9/17.
 */

@Component(modules = {NetModule.class})
public interface NetComponent {
    void inject(PhotoFragment photoFragment);
}
