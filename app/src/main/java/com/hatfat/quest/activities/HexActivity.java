package com.hatfat.quest.activities;

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
import com.hatfat.quest.hex.HexPlanetScene;
import com.hatfat.quest.planet.HexTile;
import com.hatfat.quest.planet.HighlightedHexTileChangedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hatfat.com.quest.R;

public class HexActivity extends AglActivity {

    private static final int DEFAULT_PLANET_LEVEL = 3;

    /* the planet scene we are showing */
    private HexPlanetScene planetScene;

    @Bind(R.id.activity_test_layout_generate_button) Button generateButton;
    @Bind(R.id.activity_test_layout_mesh_button) Button meshButton;
    @Bind(R.id.activity_test_layout_wireframe_button) Button wireframeButton;
    @Bind(R.id.activity_test_layout_focus_button) Button focusButton;
    @Bind(R.id.activity_test_layout_level_textview) TextView levelTextView;
    @Bind(R.id.activity_test_layout_level_seekbar) SeekBar seekBar;

    @Bind(R.id.activity_test_layout_desc1_textview) TextView desc1;
    @Bind(R.id.activity_test_layout_desc2_textview) TextView desc2;
    @Bind(R.id.activity_test_layout_desc3_textview) TextView desc3;

    private boolean isShowingMesh      = true;
    private boolean isShowingWireframe = true;

    @Inject AglRandom random;
    @Inject Bus       bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //put any subviews in this container view
        RelativeLayout container = (RelativeLayout) findViewById(
                com.hatfat.agl.R.id.base_layout_content_view);

        View ourView = getLayoutInflater().inflate(R.layout.activity_test_layout, container, false);
        container.addView(ourView);

        /* inject our views */
        ButterKnife.bind(this);

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

        bus.register(this);
    }

    @OnClick(R.id.activity_test_layout_generate_button)
    public void onGenerateClicked(View view) {
        int newLevel = seekBar.getProgress() + 1;
        levelTextView.setText("level " + newLevel);
        HexPlanetScene newPlanetScene = new HexPlanetScene(getApplicationContext(), newLevel, isShowingMesh, isShowingWireframe);
        setPlanetScene(newPlanetScene);
    }

    @OnClick(R.id.activity_test_layout_mesh_button)
    public void onMeshClicked(View view) {
        toggleMesh();
    }

    @OnClick(R.id.activity_test_layout_wireframe_button)
    public void onWireframeClicked(View view) {
        toggleWireframe();
    }

    @OnClick(R.id.activity_test_layout_focus_button)
    public void onFocusClicked(View view) {
        if (planetScene.getPlanet() == null) {
            return;
        }

        List<HexTile> tiles = planetScene.getPlanet().getTiles();
        int randomTileIndex = random.get().nextInt(tiles.size());
        HexTile randomTile = tiles.get(randomTileIndex);
        planetScene.focusTile(randomTile);
    }

    private void setPlanetScene(HexPlanetScene newPlanetScene) {
        aglSurfaceView.setScene(newPlanetScene);
        planetScene = newPlanetScene;
        updateWireframeButton();
        updateHighlightedTile(null);

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
                if (planetScene.isReadyToRender()) {
                    gestureDetector.onTouchEvent(event);
                    scaleDetector.onTouchEvent(event);
                }

                return true;
            }
        });
    }

    @Subscribe
    public void handleHighlightedHexTileChangedEvent(HighlightedHexTileChangedEvent event) {
        updateHighlightedTile(event.getTile());
    }

    private void updateHighlightedTile(HexTile tile) {
        if (tile != null) {
            desc1.setText(tile.getType().name());
            desc2.setText(String.valueOf(tile.getNeighbors().size()));
            desc3.setText("Unknown");
        }
        else {
            desc1.setText("No Selection");
            desc2.setText("");
            desc3.setText("");
        }
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
