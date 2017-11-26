package junction.fortumquest;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import junction.fortumquest.utils.DpToPixel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View egg = findViewById(R.id.splash_egg);

        final float eggHeight = DpToPixel.convert(250f, this);

        ObjectAnimator animator = ObjectAnimator.ofFloat(egg, "translationY", 0, eggHeight / 6, 0);
        animator.setDuration(800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        View title = findViewById(R.id.title_container);
        title.setAlpha(0);
        title.animate().setStartDelay(300).setDuration(300).alpha(1.0f).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GameActivity.startActivity(MainActivity.this);
                MainActivity.this.finish();
            }
        }, 4000);
    }
}
