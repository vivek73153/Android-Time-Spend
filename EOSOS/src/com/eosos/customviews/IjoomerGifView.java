package com.eosos.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class IjoomerGifView extends View {

    public static final int IMAGE_TYPE_UNKNOWN = 0;
    public static final int IMAGE_TYPE_STATIC = 1;
    public static final int IMAGE_TYPE_DYNAMIC = 2;

    public static final int DECODE_STATUS_UNDECODE = 0;
    public static final int DECODE_STATUS_DECODING = 1;
    public static final int DECODE_STATUS_DECODED = 2;

    private GifDecoder decoder;
    private Bitmap bitmap;

    public int imageType = IMAGE_TYPE_UNKNOWN;
    public int decodeStatus = DECODE_STATUS_UNDECODE;

    private int width;
    private int height;

    private long time;
    private int index;

    private int resId;
    private String filePath;

    private boolean playFlag = false;

    int gifImageResourceID;

    public IjoomerGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String namespace = "http://schemas.android.com/apk/res/" + context.getPackageName();
        gifImageResourceID = attrs.getAttributeResourceValue(namespace, "gif_image", 0);

        if (gifImageResourceID != 0) {
            setGifImageResourceID(gifImageResourceID);
        }
    }

    /**
     * Constructor
     */
    public IjoomerGifView(Context context) {
        super(context);

    }

    private InputStream getInputStream() {
        if (filePath != null)
            try {
                return new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
            }
        if (resId > 0)
            return getContext().getResources().openRawResource(resId);
        return null;
    }

    /**
     * set gif file path
     *
     * @param filePath
     */
    public void setGif(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        setGif(filePath, bitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (bitmap != null) {
            setMeasuredDimension(MeasureSpec.getSize(bitmap.getWidth()), MeasureSpec.getSize(bitmap.getHeight()));
        }

    }



    /**
     * set gif file path and cache image
     *
     * @param filePath
     * @param cacheImage
     */
    public void setGif(String filePath, Bitmap cacheImage) {
        this.resId = 0;
        this.filePath = filePath;
        imageType = IMAGE_TYPE_UNKNOWN;
        decodeStatus = DECODE_STATUS_UNDECODE;
        playFlag = false;
        bitmap = cacheImage;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        android.view.ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
        play();
    }

    /**
     * set gif resource id
     *
     * @param resId
     */

    public void setGifImageResourceID(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        setGif(resId, bitmap);
    }

    /**
     * set gif resource id and cache image
     *
     * @param resId
     * @param cacheImage
     */
    public void setGif(int resId, Bitmap cacheImage) {
        this.filePath = null;
        this.resId = resId;
        imageType = IMAGE_TYPE_UNKNOWN;
        decodeStatus = DECODE_STATUS_UNDECODE;
        playFlag = false;
        bitmap = cacheImage;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        setLayoutParams(new LayoutParams(width, height));
        play();
    }

    private void decode() {
        release();
        index = 0;

        decodeStatus = DECODE_STATUS_DECODING;

        new Thread() {
            @Override
            public void run() {
                decoder = new GifDecoder();
                decoder.read(getInputStream());
                if (decoder.width == 0 || decoder.height == 0) {
                    imageType = IMAGE_TYPE_STATIC;
                } else {
                    imageType = IMAGE_TYPE_DYNAMIC;
                }
                postInvalidate();
                time = System.currentTimeMillis();
                decodeStatus = DECODE_STATUS_DECODED;
            }
        }.start();
    }

    public void release() {
        decoder = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (decodeStatus == DECODE_STATUS_UNDECODE) {
            canvas.drawBitmap(bitmap, (getWidth()-width)/2, (getHeight()-height)/2, null);
            if (playFlag) {
                decode();
                invalidate();
            }
        } else if (decodeStatus == DECODE_STATUS_DECODING) {
            canvas.drawBitmap(bitmap, (getWidth()-width)/2, (getHeight()-height)/2, null);
            invalidate();
        } else if (decodeStatus == DECODE_STATUS_DECODED) {
            if (imageType == IMAGE_TYPE_STATIC) {
                canvas.drawBitmap(bitmap, (getWidth()-width)/2, (getHeight()-height)/2, null);
            } else if (imageType == IMAGE_TYPE_DYNAMIC) {
                if (playFlag) {
                    long now = System.currentTimeMillis();

                    if (time + decoder.getDelay(index) < now) {
                        time += decoder.getDelay(index);
                        incrementFrameIndex();
                    }
                    Bitmap bitmap = decoder.getFrame(index);
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, (getWidth()-width)/2, (getHeight()-height)/2, null);
                    }
                    invalidate();
                } else {
                    Bitmap bitmap = decoder.getFrame(index);
                    canvas.drawBitmap(bitmap, (getWidth()-width)/2, (getHeight()-height)/2, null);
                }
            } else {
                canvas.drawBitmap(bitmap, (getWidth()-width)/2, (getHeight()-height)/2, null);
            }
        }
    }

    private void incrementFrameIndex() {
        index++;
        if (index >= decoder.getFrameCount()) {
            index = 0;
        }
    }

    private void decrementFrameIndex() {
        index--;
        if (index < 0) {
            index = decoder.getFrameCount() - 1;
        }
    }

    public void play() {
        time = System.currentTimeMillis();
        playFlag = true;
        invalidate();
    }

    public void pause() {
        playFlag = false;
        invalidate();
    }

    public void stop() {
        playFlag = false;
        index = 0;
        invalidate();
    }

    public void nextFrame() {
        if (decodeStatus == DECODE_STATUS_DECODED) {
            incrementFrameIndex();
            invalidate();
        }
    }

    public void prevFrame() {
        if (decodeStatus == DECODE_STATUS_DECODED) {
            decrementFrameIndex();
            invalidate();
        }
    }
}