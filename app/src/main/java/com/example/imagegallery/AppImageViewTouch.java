package com.example.imagegallery;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class AppImageViewTouch extends ImageViewTouch {

    protected static final int SWIPE_MIN_DISTANCE = 30;

    protected static final int SWIPE_MAX_OFF_PATH = 500;

    protected static final int VELOCITY = 200;

    private static final int UNDEFINED_DIRECTION = -1;

    private static final int LEFT_DIRECTION = 0;

    private static final int RIGHT_DIRECTION = 1;

    private static final int TOP_DIRECTION = 2;

    private static final int BOTTOM_DIRECTION = 3;

    private static final float SCROLL_DELTA_THRESHOLD = 1.0f;

    private NextImageListener swipeImageListener;

    public AppImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
        swipeImageListener = (NextImageListener) context;
    }


    public boolean onLeftFling(double dx, double dy) {
        if (Math.abs(dy) > SWIPE_MAX_OFF_PATH) {
            return false;
        }
        return dx > SWIPE_MIN_DISTANCE;
    }

    public boolean onRightFling(double dx, double dy) {
        if (Math.abs(dy) > SWIPE_MAX_OFF_PATH) {
            return false;
        }
        return -dx > SWIPE_MIN_DISTANCE;
    }

    public boolean onUpFling(double dx, double dy) {
        if (Math.abs(dx) > SWIPE_MAX_OFF_PATH) {
            return false;
        }
        return dy > SWIPE_MIN_DISTANCE;
    }

    public boolean onDownFling(double dx, double dy) {
        if (Math.abs(dx) > SWIPE_MAX_OFF_PATH) {
            return false;
        }
        return -dy > SWIPE_MIN_DISTANCE;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int direction = UNDEFINED_DIRECTION;

        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        if (Math.abs(velocityX) >= VELOCITY || Math.abs(velocityY) >= VELOCITY) {
            if (onLeftFling(diffX, diffY)) {
                direction = LEFT_DIRECTION;
            } else if (onRightFling(diffX, diffY)) {
                direction = RIGHT_DIRECTION;
            } else if (onUpFling(diffX, diffY)) {
                direction = TOP_DIRECTION;
            } else if (onDownFling(diffX, diffY)) {
                direction = BOTTOM_DIRECTION;
            }
        }

        if (direction != UNDEFINED_DIRECTION) {
            if (!canScroll(direction)) {
                switch (direction) {
                    case LEFT_DIRECTION:
                        swipeImageListener.nextImage(-1, 0);
                        break;
                    case RIGHT_DIRECTION:
                        swipeImageListener.nextImage(1, 0);
                        break;
                    case TOP_DIRECTION:
                        swipeImageListener.nextImage(0, -1);
                        break;
                    case BOTTOM_DIRECTION:
                        swipeImageListener.nextImage(0, 1);
                        break;
                }
                return true;
            }
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);
        if(bitmapRect != null) {
            if (direction == RIGHT_DIRECTION) {
                if (bitmapRect.right > imageViewRect.right) {
                    return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
                }
            } else if (direction == LEFT_DIRECTION) {
                if (bitmapRect.left < mScrollRect.left) {
                    return Math.abs(bitmapRect.left - mScrollRect.left) > SCROLL_DELTA_THRESHOLD;
                }
            } else if (direction == BOTTOM_DIRECTION) {
                if (bitmapRect.bottom > imageViewRect.bottom) {
                    return Math.abs(bitmapRect.bottom - imageViewRect.bottom) > SCROLL_DELTA_THRESHOLD;
                }
            } else if (direction == TOP_DIRECTION) {
                if (bitmapRect.top < mScrollRect.top) {
                    return Math.abs(bitmapRect.top - mScrollRect.top) > SCROLL_DELTA_THRESHOLD;
                }
            }
        }
        return false;

    }
}
