package ca.yorku.cse.mack.searchInterfaceExperiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * This, FastSearchListView, SimpleAdapter classes used from:
 * https://github.com/survivingwithandroid/Surviving-with-android/tree/master/ListView_SectionIndexer
 */

public class FastSearchListView extends ListView {

    private Context ctx;

    private static int indWidth = 40;
    private String[] sections;
    private float scaledWidth;
    private float sx;
    private int indexSize;
    private String section;
    private boolean showLetter = false;
    private long lastTime, elapsedTime, futureTime;
    private Handler listHandler;

    public FastSearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
        lastTime = SystemClock.elapsedRealtime();
        futureTime = SystemClock.elapsedRealtime() + 100;
    }

    public FastSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        lastTime = SystemClock.elapsedRealtime();
        futureTime = SystemClock.elapsedRealtime() + 100;
    }

    public FastSearchListView(Context context, String keyList) {
        super(context);
        ctx = context;
        lastTime = SystemClock.elapsedRealtime();
        futureTime = SystemClock.elapsedRealtime() + 100;
    }

    /**
     * Draws the side index containing letters in alphabetical order.  Allows user to
     * touch to scroll through list by indexing in alphabetical order.
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        long now = SystemClock.elapsedRealtime();
        elapsedTime += now - lastTime;
        lastTime = now;

        if (futureTime > now)
            return;

        double elapsed = (now - futureTime) / 1000.0;
        SIEScroll.timeElapse += elapsed;

        scaledWidth = indWidth * getSizeInPixel(ctx);
        sx = this.getWidth() - this.getPaddingRight() - scaledWidth;

        Paint p = new Paint();
        p.setColor(Color.parseColor("#CDC9C9"));
        p.setAlpha(255);

        canvas.drawRect(sx, this.getPaddingTop(), sx + scaledWidth,
                this.getHeight() - this.getPaddingBottom(), p);

        indexSize = (this.getHeight() - this.getPaddingTop() - getPaddingBottom())
                / sections.length;

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(scaledWidth / 2);

        for (int i = 0; i < sections.length; i++)
            canvas.drawText(sections[i].toUpperCase(),
                    sx + textPaint.getTextSize() / 2, getPaddingTop()
                            + indexSize * (i + 1), textPaint);

        futureTime = now;

        // Draw letter selected from side to middle of screen
        //along with the rectangle behind it
        if (showLetter && section != null && !section.equals("")) {
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.parseColor("#191970"));
            bgPaint.setAlpha(204);

            Paint textPaint2 = new Paint();
            textPaint2.setColor(Color.WHITE);
            textPaint2.setTextSize(4 * indWidth);
            textPaint2.setTextAlign(Paint.Align.CENTER);

            RectF bg = new RectF();
            bg.left = (getWidth()/2) - (textPaint2.getTextSize() / 2);
            bg.top = (getHeight()/2) - (textPaint2.getTextSize() / 2);
            bg.right = (getWidth()/2) + (textPaint2.getTextSize() / 2);
            bg.bottom = (getHeight()/2) + (textPaint2.getTextSize() / 2);

            canvas.drawRoundRect(bg, 20, 20, bgPaint);
            canvas.drawText(section.toUpperCase(), bg.centerX(), bg.centerY() + 50, textPaint2);
        }
    }

    private static float getSizeInPixel(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof SectionIndexer)
            sections = (String[]) ((SectionIndexer) adapter).getSections();
    }

    /* the letter will not show unless the finger is touching the side index */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        showLetter = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (x >= sx) { //we only want to click on the list items;
                // taking out the touch doesn't make the application confused
                    showLetter = true;
                    // We touched the index bar
                    float y = event.getY() - this.getPaddingTop() - getPaddingBottom();
                    int currentPosition = (int) Math.floor(y / indexSize);
                    if (currentPosition > -1 && currentPosition < sections.length) {
                        section = sections[currentPosition];
                        this.setSelection(((SectionIndexer) getAdapter())
                                .getPositionForSection(currentPosition));
                    }
                } else {
                    Log.i("SEARCHDEBUG", "TOUCH DOWN ON LISTVIEW");
                }
                Log.i("SEARCHDEBUG", "TOUCH DOWN");
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (x < sx) {
                    showLetter = false;
                    return super.onTouchEvent(event);
                }
                else
                {
                    showLetter = true;
                    float y = event.getY();
                    int currentPosition = (int) Math.floor(y / indexSize);
                    if (currentPosition > -1 && currentPosition < sections.length) {
                        section = sections[currentPosition];
                        this.setSelection(((SectionIndexer) getAdapter())
                                .getPositionForSection(currentPosition));
                    }

                }
                Log.i("SEARCHDEBUG", "TOUCH MOVE");
                break;

            }
            case MotionEvent.ACTION_UP: {
                if (x >= sx) {
                    Log.i("SEARCHDEBUG", "TOUCH UP");
                    showLetter = false;
                    listHandler = new ListHandler();
                    listHandler.sendEmptyMessage(0);
                } else {
                    Log.i("SEARCHDEBUG", "TOUCH UP ON LISTVIEW");
                }
                break;
            }
        }
        return true;
    }

    private class ListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLetter = false;
            FastSearchListView.this.invalidate();
        }


    }
}
