package com.example.driftui

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// =============================================================================================
// PUBLIC DSL (Developer API)
// =============================================================================================

object Notification {

    fun send(block: DriftNotificationBuilder.() -> Unit) {
        val cfg = DriftNotificationBuilder().apply(block)
        DriftNotificationEngine.send(cfg)
    }

    fun schedule(afterSeconds: Int, block: DriftNotificationBuilder.() -> Unit) {
        val cfg = DriftNotificationBuilder().apply(block)
        DriftNotificationEngine.schedule(cfg, afterSeconds)
    }

    fun cancel(id: Int) = DriftNotificationEngine.cancel(id)

    fun cancelAll() = DriftNotificationEngine.cancelAll()
}

// =============================================================================================
// CONFIG BUILDER
// =============================================================================================

class DriftNotificationBuilder {
    var title: String = ""
    var body: String = ""
    var isOngoing: Boolean = false
    var onTap: (() -> Unit)? = null
}

// =============================================================================================
// GLOBALS SET BY DRIFTVIEW
// =============================================================================================

object DriftGlobals {
    internal var applicationContext: Context? = null
    internal var currentActivity: Activity? = null
}

// =============================================================================================
// NOTIFICATION ENGINE
// =============================================================================================

object DriftNotificationEngine {

    private const val CHANNEL_ID = "DRIFT_NOTIFICATION_CHANNEL"
    private var initialized = false
    private var nextId = 10000

    private val tapCallbacks = mutableMapOf<Int, (() -> Unit)?>()

    // Called by DriftView after setting context & activity
    fun prepareIfNeeded() {
        if (initialized) return

        val ctx = DriftGlobals.applicationContext ?: return

        createChannel(ctx)
        initialized = true
    }

    // -----------------------------------------------------------------------------------------
    // AUTO PERMISSION (Android 13+)
    // -----------------------------------------------------------------------------------------

    private fun ensurePermission(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 33) return

        val ctx = DriftGlobals.applicationContext ?: return

        val granted = ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (!granted && activity != null) {
            activity.requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                92020
            )
        }
    }

    // -----------------------------------------------------------------------------------------
    // SEND NOW
    // -----------------------------------------------------------------------------------------

    fun send(cfg: DriftNotificationBuilder) {
        prepareIfNeeded()

        val ctx = DriftGlobals.applicationContext ?: return
        ensurePermission(DriftGlobals.currentActivity)

        val id = nextId++

        if (cfg.onTap != null) tapCallbacks[id] = cfg.onTap

        val intent = Intent(ctx, DriftNotificationReceiver::class.java).apply {
            putExtra("id", id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            ctx, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // FINAL builder with all required flags for ongoing notifications
        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setContentTitle(cfg.title)
            .setContentText(cfg.body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(cfg.isOngoing)
            .setAutoCancel(!cfg.isOngoing)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(ctx).notify(id, builder.build())
    }

    // -----------------------------------------------------------------------------------------
    // SCHEDULE
    // -----------------------------------------------------------------------------------------

    fun schedule(cfg: DriftNotificationBuilder, delaySeconds: Int) {
        prepareIfNeeded()

        val ctx = DriftGlobals.applicationContext ?: return
        ensurePermission(DriftGlobals.currentActivity)

        val id = nextId++

        if (cfg.onTap != null) tapCallbacks[id] = cfg.onTap

        val intent = Intent(ctx, DriftNotificationReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", cfg.title)
            putExtra("body", cfg.body)
            putExtra("isOngoing", cfg.isOngoing)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            ctx, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = System.currentTimeMillis() + delaySeconds * 1000L

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        }
    }

    // -----------------------------------------------------------------------------------------
    // TAP HANDLER
    // -----------------------------------------------------------------------------------------

    internal fun handleTap(id: Int) {
        tapCallbacks[id]?.invoke()
    }

    // -----------------------------------------------------------------------------------------
    // CANCEL
    // -----------------------------------------------------------------------------------------

    fun cancel(id: Int) {
        DriftGlobals.applicationContext?.let {
            NotificationManagerCompat.from(it).cancel(id)
        }
    }

    fun cancelAll() {
        DriftGlobals.applicationContext?.let {
            NotificationManagerCompat.from(it).cancelAll()
        }
    }

    // -----------------------------------------------------------------------------------------
    // CHANNEL CREATION
    // -----------------------------------------------------------------------------------------

    private fun createChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // more persistent than HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Drift Notifications",
                importance
            )
            ctx.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }
}

// =============================================================================================
// RECEIVER
// =============================================================================================

class DriftNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", -1)
        if (id != -1) DriftNotificationEngine.handleTap(id)
    }
}
