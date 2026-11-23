package com.lumina.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.lumina.ui.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LuminaWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGETS) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(android.content.ComponentName(context, LuminaWidgetProvider::class.java))
            onUpdate(context, manager, ids)
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val prefs = context.getSharedPreferences("lumina_preferences", Context.MODE_PRIVATE)
        val now = Date()
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
        val date = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now)
        val alpha = (prefs.getFloat("transparency", 88f).coerceIn(20f, 100f) * 2.55f).toInt()
        val scale = prefs.getFloat("widget_scale", 100f).coerceIn(80f, 130f) / 100f
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_clock)
            views.setTextViewText(R.id.widget_time, time)
            views.setTextViewText(R.id.widget_date, date)
            views.setInt(R.id.widget_root, "setBackgroundColor", Color.argb(alpha, 33, 38, 56))
            views.setTextViewTextSize(R.id.widget_time, 2, 32f * scale)
            views.setTextViewTextSize(R.id.widget_date, 2, 13f * scale)
            manager.updateAppWidget(id, views)
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGETS = "com.lumina.ui.ACTION_REFRESH_WIDGETS"
    }
}
