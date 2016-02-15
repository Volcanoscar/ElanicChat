package in.elanic.elanicchatdemo.utils;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by elanicdroid on 22/10/15.
 */
public class CustomAnimationUtils {

    public static void animateIn(View view, int visibility) {
        if (visibility != -1) {
            view.setVisibility(visibility);
        }

        ViewCompat.animate(view).scaleX(1f).scaleY(1f).alpha(1f)
                .setInterpolator(new FastOutLinearInInterpolator())
                .withLayer()
                .setListener(null)
                .start();
    }

    public static void animateOut(View view, final int visibility) {
        ViewCompat.animate(view).scaleX(0f).scaleY(0f).alpha(0f)
                .setInterpolator(new FastOutLinearInInterpolator()).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (visibility != -1) {
                            view.setVisibility(visibility);
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
    }

    public static void animateOutDown(View view, final int visibility) {
        ViewCompat.animate(view).translationYBy(view.getHeight())
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (visibility != -1) {
                            view.setVisibility(visibility);
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
    }

    public static void animateOutDown(View view, final int visibility, int translationY) {
        ViewCompat.animate(view).translationYBy(translationY)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (visibility != -1) {
                            view.setVisibility(visibility);
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).start();
    }

    public static void animateInUp(View view, int visibility, int translationY) {
        if (visibility != -1) {
            view.setVisibility(visibility);
        }

        ViewCompat.animate(view).translationYBy(-translationY)
                .setInterpolator(new FastOutLinearInInterpolator())
                .withLayer()
                .setListener(null)
                .start();
    }

    public static void animateInUp(View view, int visibility) {
        if (visibility != -1) {
            view.setVisibility(visibility);
        }

        ViewCompat.animate(view).translationYBy(-view.getHeight())
                .setInterpolator(new FastOutLinearInInterpolator())
                .withLayer()
                .setListener(null)
                .start();
    }
}
