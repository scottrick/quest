package hatfat.com.quest.cameras;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.hatfat.agl.AglNode;
import com.hatfat.agl.AglOrthographicCamera;
import com.hatfat.agl.mesh.AglPoint;
import com.hatfat.agl.util.Quat;
import com.hatfat.agl.util.Util;
import com.hatfat.agl.util.Vec3;

import hatfat.com.quest.planet.HexPlanet;
import hatfat.com.quest.planet.HexTile;

public class HexPlanetCamera extends AglOrthographicCamera implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private HexPlanet planet;

    /* used for keeping track of starting values during scale gesture */
    private float startSpan;
    private float startCameraWidth;

    private float maxDistance = 4.0f;
    private float minDistance = 0.1f;

    private static final float eyeDist = 2.0f;

    public HexPlanetCamera() {
        super(new Vec3(0.0f, 0.0f, eyeDist),
                new Vec3(0.0f, 0.0f, 0.0f),
                new Vec3(0.0f, 1.0f, 0.0f),
                2.0f, 0.0f, 4.0f);
    }

    public float getCameraDistance() {
        return getEye().z;
    }

    public float getCameraViewWidth() {
        return getWidth();
    }

    private void setCameraViewWidth(float width) {
        width = Math.min(maxDistance, width);
        width = Math.max(minDistance, width);

        setWidth(width);
    }

    public void setPlanet(HexPlanet planet) {
        this.planet = planet;
    }

    public void setFocusOnTile(HexTile tile) {
        int centerIndex = tile.getShape().getCenterIndex();
        AglPoint centerPoint = planet.getMesh().getPoints().get(centerIndex);
        Vec3 target = new Vec3(centerPoint.p.x, centerPoint.p.y, centerPoint.p.z);

        Vec3 eye = new Vec3(0.0f, 0.0f, 1.0f);
        Vec3 axis = Vec3.crossProduct(eye, target);
        float angleRadians = - Vec3.calculateAngleRadians(eye, target);

        Quat rotation = new Quat();
        if (axis.getMagnitude() > Util.NORMALIZATION_ALLOWABLE_ERROR) {
            rotation.setWithRotationInRadians(angleRadians, axis);
            rotation.normalize();
        }

        if (planet != null) {
            for (AglNode node : planet.getNodes()) {
                node.posQuat.quat = rotation;
            }
        }
    }

    public void handleTouchDrag(float dx, float dy) {
        Quat rotation = new Quat();

        float magnitude = Math.abs(dx) + Math.abs(dy);
        Vec3 axis = new Vec3(dy, dx, 0.0f);
        axis.normalize();

        rotation.setWithRotationInDegrees(magnitude / 20.0f, axis);
        rotation.normalize();

        if (planet != null) {
            for (AglNode node : planet.getNodes()) {
                node.posQuat.quat.rotateBy(rotation);
            }
        }
    }

    @Override public boolean onDown(MotionEvent e) {
        //has to return true otherwise the other methods never get called
        return true;
    }

    @Override public void onShowPress(MotionEvent e) {

    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        handleTouchDrag(-distanceX, -distanceY);
        return true;
    }

    @Override public void onLongPress(MotionEvent e) {

    }

    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        return true;
    }

    @Override public boolean onScale(ScaleGestureDetector detector) {
        float currentSpan = detector.getCurrentSpan();
        float multiplier = startSpan / currentSpan;

        setCameraViewWidth(startCameraWidth * multiplier);

        return true;
    }

    @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
        startSpan = detector.getCurrentSpan();
        startCameraWidth = getCameraViewWidth();
        return true;
    }

    @Override public void onScaleEnd(ScaleGestureDetector detector) {
    }
}
