package hatfat.com.quest.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hatfat.agl.app.AglActivity;

import hatfat.com.quest.hex.HexPlanetScene;

public class HexActivity extends AglActivity implements View.OnTouchListener {

    private float touchPreviousX;
    private float touchPreviousY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final HexPlanetScene planetScene = new HexPlanetScene(getApplicationContext());
        aglSurfaceView.setScene(planetScene);

        aglSurfaceView.setOnTouchListener(this);

        //put any subviews in this container view
        RelativeLayout container = (RelativeLayout) findViewById(com.hatfat.agl.R.id.base_layout_content_view);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchPreviousX = event.getX();
                touchPreviousY = event.getY();
                Log.e("catfat", "down!");
                break;
            case MotionEvent.ACTION_UP:
                touchPreviousX = 0.0f;
                touchPreviousY = 0.0f;
                Log.e("catfat", "up!");
                break;
            case MotionEvent.ACTION_MOVE: {

                float currentX = event.getX();
                float currentY = event.getY();

                handleTouchDrag(currentX - touchPreviousX, currentY - touchPreviousY);
                touchPreviousX = currentX;
                touchPreviousY = currentY;
            }
            break;
        }

        return true;
    }

    private void handleTouchDrag(float dx, float dy) {
        Log.e("catfat", "drag (" + dx + ", " + dy + ")");
    }
}
