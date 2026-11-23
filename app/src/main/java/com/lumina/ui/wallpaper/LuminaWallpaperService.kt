package com.lumina.ui.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlin.math.sin
import kotlin.random.Random

class LuminaWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = FlowEngine()

    private inner class FlowEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var visible = false
        private var frame = 0
        private val particles = List(34) {
            Particle(Random.nextFloat(), Random.nextFloat(), 1.2f + Random.nextFloat() * 3.2f, Random.nextFloat() * 6.28f)
        }

        private val drawFrame = object : Runnable {
            override fun run() {
                if (!visible) return
                val canvas = try { surfaceHolder.lockCanvas() } catch (_: Exception) { null }
                if (canvas != null) {
                    try { drawScene(canvas) } finally { surfaceHolder.unlockCanvasAndPost(canvas) }
                }
                frame++
                handler.postDelayed(this, 42L)
            }
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible
            handler.removeCallbacks(drawFrame)
            if (visible) handler.post(drawFrame)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            if (visible) { handler.removeCallbacks(drawFrame); handler.post(drawFrame) }
        }

        override fun onDestroy() {
            visible = false
            handler.removeCallbacks(drawFrame)
            super.onDestroy()
        }

        private fun drawScene(canvas: Canvas) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            canvas.drawColor(Color.rgb(12, 17, 34))
            val t = frame * 0.012
            glow(canvas, w * (0.28f + 0.12f * sin(t).toFloat()), h * 0.30f, w * 0.78f, intArrayOf(0x884A8DFF.toInt(), 0x004A8DFF))
            glow(canvas, w * (0.72f + 0.10f * sin(t * 0.7 + 2.0).toFloat()), h * 0.62f, w * 0.72f, intArrayOf(0x887C5CFF.toInt(), 0x007C5CFF))
            glow(canvas, w * 0.48f, h * (0.82f + 0.04f * sin(t * 0.5).toFloat()), w * 0.65f, intArrayOf(0x5558D6D0, 0x0058D6D0))
            particles.forEachIndexed { i, p ->
                val x = ((p.x + sin(t + p.phase.toDouble()).toFloat() * 0.025f + 1f) % 1f) * w
                val y = ((p.y - frame * (0.00035f + p.radius * 0.00004f) + 1f) % 1f) * h
                paint.shader = null
                paint.color = Color.WHITE
                paint.alpha = (75 + 70 * (0.5f + 0.5f * sin(t + p.phase.toDouble()).toFloat())).toInt().coerceIn(50, 145)
                canvas.drawCircle(x, y, p.radius, paint)
            }
            paint.alpha = 255
        }

        private fun glow(canvas: Canvas, x: Float, y: Float, radius: Float, colors: IntArray) {
            paint.shader = RadialGradient(x, y, radius, colors, null, Shader.TileMode.CLAMP)
            canvas.drawCircle(x, y, radius, paint)
            paint.shader = null
        }
    }

    private data class Particle(val x: Float, val y: Float, val radius: Float, val phase: Float)
}
