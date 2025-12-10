package com.denica.playlistmaker.mediaplayer.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.denica.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    enum class State { PLAY, STOP }

    private val imagePlayBitmap: Bitmap?
    private val imageStopBitmap: Bitmap?
    var onToggle: ((Boolean) -> Unit)? = null
    var imageRect = RectF(0f, 0f, 0f, 0f)
    var state: State = State.PLAY
        private set(value) {
            field = value
            refreshDrawableState()
            updateIcon()
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                imagePlayBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)?.toBitmap()
                imageStopBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_imageStopResId)?.toBitmap()
            } finally {
                recycle()
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateIcon()?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                return true
            }


            MotionEvent.ACTION_MOVE -> {

                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun toggleState() {
        state = when (state) {
            State.PLAY -> State.STOP
            State.STOP -> State.PLAY
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        toggleState()
        onToggle?.invoke(state == State.STOP)
        invalidate()
        return true
    }

    private fun updateIcon(): Bitmap? = when (state) {
        State.PLAY -> imagePlayBitmap
        State.STOP -> imageStopBitmap
    }


    fun setPlayState(isPlaying: Boolean) {
        state = if (isPlaying) State.STOP else State.PLAY
        invalidate()
    }
}