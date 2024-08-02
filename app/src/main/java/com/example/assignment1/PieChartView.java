package com.example.assignment1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PieChartView extends View {
    private Paint paint;
    private float interestPaid = 0f;
    private float principalPaid = 0f;
    private String monthlyRepayment = "";

    private int interestColor, principalColor;
    private float interestStartAngle, interestSweepAngle;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true); // Smooth the edges of the arcs

        // Retrieve colors from resources
        Resources res = context.getResources();
        interestColor = res.getColor(R.color.PieRed);
        principalColor = res.getColor(R.color.PieGreen);
    }

    public void setPieData(float interestPaid, float principalPaid, String monthlyRepayment) {
        this.interestPaid = interestPaid;
        this.principalPaid = principalPaid;
        this.monthlyRepayment = monthlyRepayment;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float total = interestPaid + principalPaid;
        if (total == 0) return;

        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        float startAngle = 0f;

        // For Principal Paid
        paint.setColor(principalColor);
        float principalAngle = (principalPaid / total) * 360f;
        canvas.drawArc(rectF, startAngle, principalAngle, true, paint);

        // For Interest Paid
        paint.setColor(interestColor);
        float interestAngle = (interestPaid / total) * 360f;
        canvas.drawArc(rectF, startAngle, interestAngle, true, paint);
        startAngle += interestAngle;

        // For monthly Repayment
        paint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fm = paint.getFontMetrics();
        float lineHeight = fm.bottom - fm.top;
        float yOffset = (getHeight() / 2) - ((lineHeight * 2) / 2); // Center vertically

        canvas.drawText("Monthly Repayment", getWidth() / 2, yOffset + lineHeight, paint);
        canvas.drawText("RM " + monthlyRepayment, getWidth() / 2, yOffset + lineHeight * 2, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float radius = getWidth() / 2;
            float dx = x - radius;
            float dy = y - radius;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= radius) {
                float touchAngle = (float) Math.toDegrees(Math.atan2(y - radius, x - radius));
                if (touchAngle < 0) {
                    touchAngle += 360;
                }

                // Check if touch is within interest segment
                if (touchAngle >= interestStartAngle && touchAngle <= (interestStartAngle + interestSweepAngle)) {
                    showToast("Interest Paid = RM " + interestPaid);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
