package com.devpupu.notifgrouper.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

/**
 * Extracts installed application icons (APK drawables) via PackageManager and
 * scales them to the sizes needed by notification large icons and in-app cards.
 */
object AppIconManager {

    private const val NOTIFICATION_ICON_DP = 64
    private const val CARD_ICON_DP = 40

    fun getApplicationIcon(context: Context, packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    fun scaledIconBitmap(
        context: Context,
        packageName: String,
        sizePx: Int = dpToPx(context, NOTIFICATION_ICON_DP)
    ): Bitmap? {
        val drawable = getApplicationIcon(context, packageName) ?: return null
        return drawableToBitmap(drawable, sizePx)
    }

    fun notificationIconBitmap(context: Context, packageName: String): Bitmap? =
        scaledIconBitmap(context, packageName, dpToPx(context, NOTIFICATION_ICON_DP))

    fun cardIconBitmap(context: Context, packageName: String): Bitmap? =
        scaledIconBitmap(context, packageName, dpToPx(context, CARD_ICON_DP))

    private fun drawableToBitmap(drawable: Drawable, sizePx: Int): Bitmap {
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else sizePx
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else sizePx
        val scale = sizePx.toFloat() / maxOf(width, height).coerceAtLeast(1)
        val w = (width * scale).toInt().coerceAtLeast(1)
        val h = (height * scale).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt().coerceAtLeast(1)
    }
}
