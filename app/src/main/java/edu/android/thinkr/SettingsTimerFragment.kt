package edu.android.thinkr

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsTimerFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}