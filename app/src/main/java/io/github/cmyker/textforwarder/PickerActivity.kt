package io.github.cmyker.textforwarder

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PickerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        val rememberChoiceCheckbox: CheckBox = findViewById(R.id.remember_choice_checkbox)

        val sendIntent = Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(sendIntent, 0)
        val sortedActivities = activities.sortedBy { it.loadLabel(packageManager).toString() }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AppAdapter(this, sortedActivities) { resolveInfo ->
            if (rememberChoiceCheckbox.isChecked) {
                val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                prefs.edit().putString("target_pkg", resolveInfo.activityInfo.packageName).apply()
            }

            if (!text.isNullOrEmpty()) {
                val targetIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                    setPackage(resolveInfo.activityInfo.packageName)
                }
                startActivity(targetIntent)
            }
            finish()
        }
    }
}
