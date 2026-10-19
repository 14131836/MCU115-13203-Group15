package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var maxProgress: Int = 100
        set(value) {
            field = value.coerceAtLeast(1)
            invalidate()
        }

    var progress: Int = 0
        set(value) {
            field = value.coerceIn(0, maxProgress)
            invalidate()
        }

    var showPercentageText: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var strokeWidthDp: Float = 22f
        set(value) {
            field = value
            updatePaints()
            invalidate()
        }

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#E0E0E0")
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#6750A4")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1C1B1F")
        textAlign = Paint.Align.CENTER
    }

    private val rectF = RectF()

    init {
        updatePaints()
    }

    private fun updatePaints() {
        val density = resources.displayMetrics.density
        val strokeWidthPx = strokeWidthDp * density
        trackPaint.strokeWidth = strokeWidthPx
        progressPaint.strokeWidth = strokeWidthPx
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val strokeWidthPx = trackPaint.strokeWidth
        val padding = strokeWidthPx / 2f + 16f
        rectF.set(padding, padding, w - padding, h - padding)

        val minDimension = Math.min(w, h)
        textPaint.textSize = minDimension * 0.18f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background circle track
        canvas.drawOval(rectF, trackPaint)

        // Draw progress arc
        if (progress > 0) {
            val sweepAngle = 360f * progress / maxProgress
            canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)
        }

        // Draw percentage text in center
        if (showPercentageText) {
            val text = "$progress%"
            val x = width / 2f
            val y = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(text, x, y, textPaint)
        }
    }
}
