package com.lumina.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.lumina.ui.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LuminaWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val now = Date()
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
        val date = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now)
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_clock)
            views.setTextViewText(R.id.widget_time, time)
            views.setTextViewText(R.id.widget_date, date)
            manager.updateAppWidget(id, views)
        }
    }
}
