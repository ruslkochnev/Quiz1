package hell.luc1fer.quiz;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

public class AnimatorStars {
    public static final long SHOW_DURATION = 500;
    public static final long TO_NORMAL_DURATION = 500;
    public static final long HIDE_DURATION = 200;
    public static final long HIDE_DELAY = 400;
    private static boolean isAnimate = false;

    public static void likeAnimation(@DrawableRes int icon,
                                     final ImageView view) {
        if (view != null && !isAnimate) {
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(
                    showAnimatorSet(view),
                    toNormalAnimatorSet(view));
            //hideAnimatorSet(view));
            set.addListener(getLikeEndListener(view, icon));
            set.start();
        }
        assert view != null;
        view.animate().alphaBy(0).alpha(1).start();
    }


    private static AnimatorListenerAdapter getLikeEndListener(final ImageView view, final int icon) {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimate = true;
                view.setVisibility(View.VISIBLE);
                view.setImageResource(icon);
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimate = false;
                //view.setVisibility(View.GONE);
                //view.setImageDrawable(null);
                //view.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        };
    }

    private static AnimatorSet showAnimatorSet(View view) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(SHOW_DURATION).playTogether(
                ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(view, View.SCALE_X, 0.2f, 1.2f),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.2f, 1.2f)
        );
        return set;
    }

    private static AnimatorSet toNormalAnimatorSet(View view) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(TO_NORMAL_DURATION).playTogether(
                ObjectAnimator.ofFloat(view, View.SCALE_X, 1.2f, 1f),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.2f, 1f)
        );
        return set;
    }

    private static AnimatorSet hideAnimatorSet(View view) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(HIDE_DURATION).playTogether(
                ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
                ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.2f),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.2f)
        );
        set.setStartDelay(HIDE_DELAY);
        return set;
    }
}
