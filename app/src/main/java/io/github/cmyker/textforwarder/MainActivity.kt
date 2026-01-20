package io.github.cmyker.textforwarder

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var targetPackageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        targetPackageTextView = findViewById(R.id.target_package_text)
        val clearTargetButton: Button = findViewById(R.id.clear_target_button)

        updateTargetPackageText()

        clearTargetButton.setOnClickListener {
            val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
            prefs.edit().remove(Constants.PREF_TARGET_PACKAGE).apply()
            updateTargetPackageText()
        }
    }

    override fun onResume() {
        super.onResume()
        updateTargetPackageText()
    }

    private fun updateTargetPackageText() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val targetPackage = prefs.getString(Constants.PREF_TARGET_PACKAGE, null)
        targetPackageTextView.text = targetPackage ?: "not set"
    }
}
