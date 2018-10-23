package dragosholban.com.androidpuzzlegame;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.concurrent.Callable;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;

public class SinglePuzzlePieceTouchListener implements View.OnTouchListener {
    private float xDelta;
    private float yDelta;
    float initialX;
    float initialY;
    private PuzzleActivity activity;

    public SinglePuzzlePieceTouchListener(PuzzleActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getRawX();
        float y = motionEvent.getRawY();
        final double tolerance = sqrt(pow(view.getWidth(), 2) + pow(view.getHeight(), 2)) / 10;

        PuzzlePiece piece = (PuzzlePiece) view;
        if (!piece.canMove) {
            return true;
        }

        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - lParams.leftMargin;
                yDelta = y - lParams.topMargin;
                initialX = piece.getX();
                initialY = piece.getY();
                piece.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                lParams.leftMargin = (int) (x - xDelta);
                lParams.topMargin = (int) (y - yDelta);
                view.setLayoutParams(lParams);
                break;
            case MotionEvent.ACTION_UP:
                float xDiff = abs(piece.xCoord - piece.getX());
                float yDiff = abs(piece.yCoord - piece.getY());
                if (xDiff <= tolerance && yDiff <= tolerance) {
                    piece.setX(piece.xCoord);
                    piece.setY(piece.yCoord);
                    piece.canMove = false;
                    sendViewToBack(piece);
                    activity.checkGameOver();
                } else {
                    // not in the right place
                    moveX(piece, piece.getX(), initialX);
                    moveY(piece, piece.getY(), initialY);
                }
                break;
        }

        return true;
    }

    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public static void moveX(final View view, float startPos, float endPos){
        ValueAnimator va = ValueAnimator.ofFloat(startPos, endPos);
        int mDuration = 1000; //in millis
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setX((float)animation.getAnimatedValue());
            }
        });
        va.start();
    }

    public static void moveY(final View view, float startPos, float endPos){
        ValueAnimator va = ValueAnimator.ofFloat(startPos, endPos);
        int mDuration = 1000; //in millis
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setY((float)animation.getAnimatedValue());
            }
        });
        va.start();
    }
}