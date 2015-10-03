package com.hatfat.quest.cameras;

import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.hatfat.agl.component.ComponentType;
import com.hatfat.agl.component.camera.OrthographicCameraComponent;
import com.hatfat.agl.component.transform.Transform;
import com.hatfat.agl.mesh.AglPoint;
import com.hatfat.agl.util.Quat;
import com.hatfat.agl.util.Vec3;
import com.hatfat.quest.planet.HexPlanet;
import com.hatfat.quest.planet.HexTile;

import java.util.List;

public class HexPlanetCameraComponent
        extends OrthographicCameraComponent
        implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private HexPlanet planet;

    /* used for keeping track of starting values during scale gesture */
    private float startSpan;
    private float startCameraWidth;

    private float maxDistance = 4.0f;
    private float minDistance = 0.1f;

    private static final float eyeDist = 2.0f;

    public HexPlanetCameraComponent() {
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
        axis.normalize();
        float angleRadians = - Vec3.calculateAngleRadians(eye, target);

        Quat rotation = new Quat();
        rotation.setWithRotationInRadians(angleRadians, axis);

        if (planet != null) {
            List<Transform> transforms = planet.getComponentsByType(ComponentType.TRANSFORM);
            for (Transform transform : transforms) {
                transform.posQuat.quat = new Quat(rotation);
            }
        }
    }

    public void handleTouchDrag(float dx, float dy) {
        Quat rotation = new Quat();

        float magnitude = Math.abs(dx) + Math.abs(dy);
        Vec3 axis = new Vec3(dy, dx, 0.0f);
        axis.normalize();

        rotation.setWithRotationInDegrees(magnitude / 20.0f, axis);

        if (planet != null) {
            List<Transform> transforms = planet.getComponentsByType(ComponentType.TRANSFORM);
            for (Transform transform : transforms) {
                transform.posQuat.quat.rotateBy(rotation);
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
        InputDevice.MotionRange rangeX = e.getDevice().getMotionRange(MotionEvent.AXIS_X);
        InputDevice.MotionRange rangeY = e.getDevice().getMotionRange(MotionEvent.AXIS_Y);

        float xRange = rangeX.getRange();
        float yRange = rangeY.getRange();

        float percentX = e.getX() / xRange;
        float percentY = e.getY() / yRange;

        float xValue = getLeft() + getWidth() * percentX;
        float yValue = getTop() - getHeight() * percentY;

        //sphere:   x^2 + y^2 + z^2 = r^2
        //radius is always 1, so:
        //          x^2 + y^2 + z^2 = 1
        //          z^2 = 1 - x^2 - y^2

        float zSquared = 1.0f - xValue * xValue - yValue * yValue;
        float zValue = (float)Math.sqrt(zSquared);

        if (Float.isNaN(zValue) || Float.isInfinite(zValue)) {
            //didn't click on the sphere!
            planet.setHighlightTile(null);
            return true;
        }

        Vec3 point = new Vec3(xValue, yValue, zValue);

        //need to ROTATE point
        Transform transform = planet.getComponentByType(ComponentType.TRANSFORM);
        point.rotateBy(transform.posQuat.quat.getInverse());

        HexTile focusTile = planet.findTileClosestToPoint(point);

        planet.setHighlightTile(focusTile);

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
