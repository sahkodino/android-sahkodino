package junction.fortumquest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daasuu.library.DisplayObject;
import com.daasuu.library.FPSTextureView;
import com.daasuu.library.drawer.SpriteSheetDrawer;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.concurrent.atomic.AtomicBoolean;

import junction.fortumquest.utils.DisplaySize;
import junction.fortumquest.utils.DpToPixel;
import pl.droidsonroids.gif.GifImageView;

public class GameActivity extends FragmentActivity
    implements EventListener<DocumentSnapshot> {

    private static final String TAG = GameActivity.class.getSimpleName();

    private ListenerRegistration listenerRegistration;

    private AtomicBoolean graphRunning = new AtomicBoolean(false);

    private AtomicBoolean run = new AtomicBoolean(false);

    private static final float SCALE = 2.0f;
    private Long consumptionValue = 0l;
    private int currentPoints = 0;
    private int nextLevelPoints = Integer.MAX_VALUE;

    private int sleepiness = 50;
    private int hungriness = 50;

    private State state = State.UNKNOWN;

    private final static int DEFAULT_BASELINE_CONSUMPTION = 1500;
    private final static int DEFAULT_VARIANCE_CONSUMPTION = 250;
    private final static int MAX_DATA_POINTS = 20;
    private int dataPointIndex = 0;

    private long baseLineConsumption = DEFAULT_BASELINE_CONSUMPTION;
    private long varianceConsumption = DEFAULT_VARIANCE_CONSUMPTION;

    private FPSTextureView fpsTextureView;

    private String currentDemand = "";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, GameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fpsTextureView != null) {
            fpsTextureView.tickStart();
        }
        run.set(true);
        updater.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fpsTextureView != null) {
            fpsTextureView.tickStop();
        }
        run.set(false);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);

        GifImageView gifGraph = findViewById(R.id.graph_gif);
        gifGraph.setImageResource(R.drawable.normal_graph);

        currentPoints = 0;
        nextLevelPoints = 200;

        fpsTextureView = findViewById(R.id.character);
        updateDinosaur(State.HAPPY);

        DocumentReference consumptionDoc = FirebaseFirestore
            .getInstance()
            .collection("consumption")
            .document("yex2MizqwdXLxpCr4pMy");
        listenerRegistration = consumptionDoc.addSnapshotListener(this);


        DocumentReference gridDoc = FirebaseFirestore
            .getInstance()
            .collection("grid")
            .document("ihoTjUjHjtwNihCkZOHN");
        listenerRegistration = gridDoc.addSnapshotListener(this);

    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

        if (e != null) {
            Log.w(TAG, "Listen failed.", e);
            return;
        }

        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
            ? "Local"
            : "Server";

        if (snapshot != null && snapshot.exists()) {
            Long consumption = (Long) snapshot.getData().get("reading");
            if (consumption != null) {
                consumptionValue = consumption;
                Log.d(TAG, "Consumption" + consumption);
            }

            String demand = (String) snapshot.getData().get("demand");
            if (demand != null) {
                currentDemand = demand;
                Log.d(TAG, "Demand: " + demand);
            }

        } else {
            Log.d(TAG, source + " data: null");
        }

        onUpdate();
    }

    final Runnable updater = new Runnable() {
        @Override
        public void run() {
            if (run.get()) {
                onUpdate();
                new Handler().postDelayed(this, 500);
            }
        }
    };

    private SpriteSheetDrawer createSleepingSprite(boolean loop) {
        final Bitmap baseSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.sleep);

        final float frameWidth = DpToPixel.convert(200.0f, this);
        final float frameHeight = DpToPixel.convert(103.0f, this);

        final Bitmap spriteSheet = Bitmap.createScaledBitmap(
            baseSpriteSheet,
            (int) DpToPixel.convert(210f, this),
            (int) DpToPixel.convert(200f, this),
            false);

        return new SpriteSheetDrawer(
            spriteSheet,
            frameWidth,
            frameHeight,
            2,
            1).dpSize(this).frequency(4).spriteLoop(loop);
    }


    private SpriteSheetDrawer createHungrySpriteSheet(boolean loop) {

        final Bitmap baseSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.hungry);

        final float frameWidth = DpToPixel.convert(215.0f, this);
        final float frameHeight = DpToPixel.convert(200.0f, this);

        final Bitmap spriteSheet = Bitmap.createScaledBitmap(
            baseSpriteSheet,
            (int) DpToPixel.convert(590f, this),
            (int) DpToPixel.convert(200f, this),
            false);

        return new SpriteSheetDrawer(
            spriteSheet,
            frameWidth,
            frameHeight,
            4,
            3).dpSize(this).frequency(10).spriteLoop(loop);

    }

    private SpriteSheetDrawer createWalkingSprite(boolean loop) {

        final Bitmap baseSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.walking);

        final float frameWidth = DpToPixel.convert(270.0f, this);
        final float frameHeight = DpToPixel.convert(138.0f, this);

        final Bitmap spriteSheet = Bitmap.createScaledBitmap(
            baseSpriteSheet,
            (int) DpToPixel.convert(807f, this),
            (int) DpToPixel.convert(272f, this),
            false);

        return new SpriteSheetDrawer(
            spriteSheet,
            frameWidth,
            frameHeight,
            6,
            3).dpSize(this).frequency(3).spriteLoop(loop);

    }

    private DisplayObject createDisplayObject(SpriteSheetDrawer drawer) {
        final DisplayObject displayObject = new DisplayObject();

        displayObject
            .with(drawer)
            .tween()
            .transform((DisplaySize.width(this) - drawer.getWidth() * SCALE)/ 2, DisplaySize.height(this) / 2, 255, SCALE , SCALE, 0.0f)
            .tweenLoop(false)
            .end();

        return displayObject;
    }

    private void onUpdate() {
        Log.d(TAG, "Value changed " + consumptionValue);

        TextView textView = findViewById(R.id.readings_text);

        if (textView != null) {
            textView.setText(String.format(getString(R.string.readings), consumptionValue.toString()));
        }

        State newState;

        boolean weGucci = false;
        switch (currentDemand) {
            case "high":
                weGucci = consumptionValue < baseLineConsumption;
                sleepiness += weGucci
                    ? 2 * -1
                    : 2;

                newState = weGucci && sleepiness < 40 ? State.HAPPY: State.SLEEPING;
                fpsTextureView.tickStart();
                fpsTextureView.setScaleX(1);
                break;
            case "low":
                weGucci = consumptionValue > baseLineConsumption;
                hungriness += weGucci
                    ? 2 * -1
                    : 2;
                newState = weGucci && hungriness < 45 ? State.HAPPY : State.HUNGRY;
                currentPoints++;
                fpsTextureView.tickStart();
                fpsTextureView.setScaleX(-1);
                break;
            default:

                newState = State.UNKNOWN;
                break;
        }

        currentPoints = Math.max(0, currentPoints);
        hungriness = Math.max(20, Math.min(hungriness, 95));
        sleepiness = Math.max(15, Math.min(sleepiness, 90));

        ProgressBar sleepinessBar = findViewById(R.id.speelyness_progress);
        ProgressBar hungrinessBar = findViewById(R.id.hungryness_progress);

        sleepinessBar.setProgressDrawable(ContextCompat.getDrawable(this, sleepiness > 40 ? R.drawable.progress_drawable_blue : R.drawable.progress_drawable_green));
        sleepinessBar.setProgress(sleepiness);
        hungrinessBar.setProgressDrawable(ContextCompat.getDrawable(this, hungriness > 45 ? R.drawable.progress_drawable_red : R.drawable.progress_drawable_green));
        hungrinessBar.setProgress(hungriness);

        updateDinosaur(newState);
    }

    private void updateDinosaur(State newState) {
        if (newState != state) {
            state = newState;
            SpriteSheetDrawer spriteSheetDrawer;
            final ImageView statusText = findViewById(R.id.status_text);
            final GifImageView gifImageView = findViewById(R.id.graph_gif);
            int direction = (Math.random() - 0.5f) >= 0 ? 1 : -1;
            switch (state) {
                case HUNGRY:
                    statusText.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.more_power));
                    gifImageView.setImageResource(R.drawable.hangry_graph);
                    spriteSheetDrawer = createHungrySpriteSheet(true);
                    fpsTextureView.tickStart();
                    break;
                case SLEEPING:
                    statusText.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.less_power));
                    gifImageView.setImageResource(R.drawable.sleepy_graph);
                    spriteSheetDrawer = createSleepingSprite(true);
                    fpsTextureView.tickStart();
                    break;
                default:
                    statusText.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.happy_dino));
                    gifImageView.setImageResource(R.drawable.normal_graph);
                    fpsTextureView.tickStart();
                    spriteSheetDrawer = createWalkingSprite(true);
                    break;
            }

            fpsTextureView.setScaleX(direction);
            fpsTextureView.removeAllChildren();
            fpsTextureView.addChild(createDisplayObject(spriteSheetDrawer));
        }
    }

    private enum State {
        HUNGRY,
        SLEEPING,
        HAPPY,
        UNKNOWN
    }


}
