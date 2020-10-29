package edu.android.thinkr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import edu.android.thinkr.utils.NotificationUtil
import edu.android.thinkr.utils.PrefUtil


class TimerExpiredReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(TimerMain.TimerState.STOPPED, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
