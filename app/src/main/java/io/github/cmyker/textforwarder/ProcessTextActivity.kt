package io.github.cmyker.textforwarder

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

class ProcessTextActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()?.trim()
        if (text.isNullOrEmpty()) {
            finish()
            return
        }

        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val targetPackage = prefs.getString(Constants.PREF_TARGET_PACKAGE, null)

        if (targetPackage != null) {
            val sendIntent = createSendIntent(text)
            sendIntent.setPackage(targetPackage)

            if (isIntentResolvable(sendIntent)) {
                startActivity(sendIntent)
                finish()
            } else {
                // Target app is not installed anymore, clear settings and show picker
                prefs.edit().remove(Constants.PREF_TARGET_PACKAGE).apply()
                showPicker(text)
            }
        } else {
            showPicker(text)
        }
    }

    private fun showPicker(text: String) {
        val pickerIntent = Intent(this, PickerActivity::class.java)
        pickerIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(pickerIntent)
        finish()
    }

    private fun createSendIntent(text: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
    }

    private fun isIntentResolvable(intent: Intent): Boolean {
        val resolvable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return resolvable != null
    }
}
