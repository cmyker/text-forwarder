package io.github.cmyker.textforwarder

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PickerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        val rememberChoiceCheckbox: CheckBox = findViewById(R.id.remember_choice_checkbox)

        val sendIntent = Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
        val activities: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(sendIntent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        val sortedActivities = activities.sortedBy { it.loadLabel(packageManager).toString() }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AppAdapter(this, sortedActivities) { resolveInfo ->
            if (rememberChoiceCheckbox.isChecked) {
                val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                prefs.edit().putString(Constants.PREF_TARGET_PACKAGE, resolveInfo.activityInfo.packageName).apply()
            }

            if (!text.isNullOrEmpty()) {
                val targetIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                    setPackage(resolveInfo.activityInfo.packageName)
                }
                val resolvable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.resolveActivity(targetIntent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.resolveActivity(targetIntent, PackageManager.MATCH_DEFAULT_ONLY)
                }

                if (resolvable != null) {
                    startActivity(targetIntent)
                } else {
                    Toast.makeText(this, "Selected app is no longer available.", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }
    }
}
