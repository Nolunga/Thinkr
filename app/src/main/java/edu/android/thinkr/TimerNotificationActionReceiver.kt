package edu.android.thinkr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import edu.android.thinkr.utils.NotificationUtil
import edu.android.thinkr.utils.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when(intent.action){
            TimerConstants.ACTION_STOP->{
                TimerMain.removeAlarm(context)
                PrefUtil.setTimerState(TimerMain.TimerState.STOPPED, context)
                NotificationUtil.hideTimerNotification(context)
            }
            TimerConstants.ACTION_PAUSE->{
                var secondsRemaining=PrefUtil.getTimerSecondsRemaining(context)
                val alarmSetTime=PrefUtil.getAlarmSetTime(context)
                val nowSeconds=TimerMain.nowSeconds

                secondsRemaining-=nowSeconds-alarmSetTime
                PrefUtil.setTimerSecondsRemaining(secondsRemaining, context)
                TimerMain.removeAlarm(context)

                PrefUtil.setTimerState(TimerMain.TimerState.PAUSED, context)
                NotificationUtil.showTimerPaused(context)
            }
            TimerConstants.ACTION_RESUME->{
                var secondsRemaining=PrefUtil.getTimerSecondsRemaining(context)
                val wakeUpTime=TimerMain.setAlarm(context, TimerMain.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TimerMain.TimerState.RUNNING, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
            TimerConstants.ACTION_START->{
                val minutesRemaining=PrefUtil.getTimerLength(context)
                val secondsRemaining=minutesRemaining*60L
                val wakeUpTime=TimerMain.setAlarm(context, TimerMain.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TimerMain.TimerState.RUNNING, context)
                PrefUtil.setTimerSecondsRemaining(secondsRemaining, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
