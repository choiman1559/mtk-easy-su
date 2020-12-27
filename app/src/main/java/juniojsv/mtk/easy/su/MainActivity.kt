package juniojsv.mtk.easy.su

import android.content.*
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0.0f
        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        if (!preferences.getBoolean("startup_warning", false))
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.warning_word))
                setMessage(getString(R.string.startup_warning))
                setPositiveButton(getString(R.string.accept)) { _, _ ->
                    preferences.edit(true) {
                        putBoolean("startup_warning", true)
                    }
                }
                create().apply { setCanceledOnTouchOutside(false) }
            }.show()

        mRunAs64.apply {
            isChecked = preferences.getBoolean("run_as_64", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit(true) {
                    putBoolean("run_as_64", isChecked)
                }
            }
        }

        mApplyAfterBoot.apply {
            isChecked = preferences.getBoolean("apply_after_boot", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit(true) {
                    putBoolean("apply_after_boot", isChecked)
                }
            }
        }

        mVersion.text =
            String.format("%s %s", getString(R.string.version), BuildConfig.VERSION_NAME)

        mButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.github_url))
            })
        }

        mButtonXda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.xda_url))
            })
        }

        mButtonTryRoot.setOnClickListener { button ->
            button.isEnabled = false
            ExploitHandler(this) { result, log ->
                mLog.text = log
                mButtonCopy.isEnabled = true
                button.isEnabled = true
                if (result)
                    getString(R.string.success).toast(this, true)
                else
                    getString(R.string.fail).toast(this, false)

            }.execute()
        }

        mButtonCopy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText(getString(R.string.log), mLog.text))
        }
    }

}
