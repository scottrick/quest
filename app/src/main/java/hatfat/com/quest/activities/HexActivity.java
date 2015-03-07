package hatfat.com.quest.activities;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hatfat.agl.app.AglActivity;
import com.hatfat.agl.util.AglRandom;

import java.util.List;

import javax.inject.Inject;

import hatfat.com.quest.R;
import hatfat.com.quest.hex.HexPlanetScene;
import hatfat.com.quest.planet.HexTile;

public class HexActivity extends AglActivity {

    private static final int DEFAULT_PLANET_LEVEL = 3;

    private HexPlanetScene planetScene;

    private Button generateButton;
    private Button meshButton;
    private Button wireframeButton;
    private Button focusButton;
    private TextView levelTextView;
    private SeekBar seekBar;

    private boolean isShowingMesh = true;
    private boolean isShowingWireframe = true;

    @Inject AglRandom random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //put any subviews in this container view
        RelativeLayout container = (RelativeLayout) findViewById(com.hatfat.agl.R.id.base_layout_content_view);

        View ourView = getLayoutInflater().inflate(R.layout.activity_test_layout, container, false);
        container.addView(ourView);

        generateButton = (Button) ourView.findViewById(R.id.activity_test_layout_generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int newLevel = seekBar.getProgress() + 1;
                levelTextView.setText("level " + newLevel);
                HexPlanetScene newPlanetScene = new HexPlanetScene(getApplicationContext(), newLevel, isShowingMesh, isShowingWireframe);
                setPlanetScene(newPlanetScene);
            }
        });

        meshButton = (Button) ourView.findViewById(R.id.activity_test_layout_mesh_button);
        meshButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                toggleMesh();
            }
        });

        wireframeButton = (Button) ourView.findViewById(R.id.activity_test_layout_wireframe_button);
        wireframeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                toggleWireframe();
            }
        });

        focusButton = (Button) ourView.findViewById(R.id.activity_test_layout_focus_button);
        focusButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (planetScene.getPlanet() == null) {
                    return;
                }

                List<HexTile> tiles = planetScene.getPlanet().getTiles();
                int randomTileIndex = random.get().nextInt(tiles.size());
                HexTile randomTile = tiles.get(randomTileIndex);
                planetScene.focusTile(randomTile);
            }
        });

        levelTextView = (TextView) ourView.findViewById(R.id.activity_test_layout_level_textview);

        seekBar = (SeekBar) ourView.findViewById(R.id.activity_test_layout_level_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                int newLevel = progress + 1;
                levelTextView.setText("level " + newLevel);
                HexPlanetScene newPlanetScene = new HexPlanetScene(getApplicationContext(), newLevel, isShowingMesh, isShowingWireframe);
                setPlanetScene(newPlanetScene);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setProgress(DEFAULT_PLANET_LEVEL);
    }

    private void setPlanetScene(HexPlanetScene newPlanetScene) {
        aglSurfaceView.setScene(newPlanetScene);
        planetScene = newPlanetScene;
        updateWireframeButton();

        final GestureDetector gestureDetector = new GestureDetector(this, planetScene.getCamera());
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });

        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(this, planetScene.getCamera());

        aglSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                scaleDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void toggleMesh() {
        planetScene.toggleMesh();
        updateMeshButton();
    }

    private void updateMeshButton() {
        isShowingMesh = planetScene.isMeshVisible();

        if (planetScene.isMeshVisible()) {
            meshButton.setText("mesh on");
        }
        else {
            meshButton.setText("mesh off");
        }
    }

    private void toggleWireframe() {
        planetScene.toggleWireframe();
        updateWireframeButton();
    }

    private void updateWireframeButton() {
        isShowingWireframe = planetScene.isWireframeVisible();

        if (planetScene.isWireframeVisible()) {
            wireframeButton.setText("wire on");
        }
        else {
            wireframeButton.setText("wire off");
        }
    }
}
