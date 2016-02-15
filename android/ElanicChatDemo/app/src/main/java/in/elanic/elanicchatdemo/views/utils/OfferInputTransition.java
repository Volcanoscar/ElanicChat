package in.elanic.elanicchatdemo.views.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by Jay Rambhia on 2/15/16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class OfferInputTransition extends TransitionSet {

    public OfferInputTransition() {
        setOrdering(ORDERING_TOGETHER);

        if (Build.VERSION.SDK_INT >= 21) {
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform());
        } else {
            addTransition(new AutoTransition());
        }
    }
}
