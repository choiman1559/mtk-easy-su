package juniojsv.mtk.easy.su

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import juniojsv.mtk.easy.su.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT > 22 && Build.VERSION.SECURITY_PATCH.replace("-","").toInt() >= 20200301) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.warning_word))
            builder.setMessage(R.string.dialog_security_patch)
            builder.setCancelable(false)
            builder.setPositiveButton(getString(R.string.dialog_close)) { _, _ ->
                finishAndRemoveTask()
            }
            builder.show()
        }

        supportActionBar?.elevation = 0.0f
        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater, findViewById(android.R.id.content))

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

        binding.mRunAs64.apply {
            isChecked = preferences.getBoolean("run_as_64", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit(true) {
                    putBoolean("run_as_64", isChecked)
                }
            }
        }

        binding.mApplyAfterBoot.apply {
            isChecked = preferences.getBoolean("apply_after_boot", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit(true) {
                    putBoolean("apply_after_boot", isChecked)
                }
            }
        }

        binding.mVersion.text =
            String.format("%s %s", getString(R.string.version), BuildConfig.VERSION_NAME)

        binding.mButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.github_url))
            })
        }

        binding.mButtonXda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.xda_url))
            })
        }

        binding.mButtonTryRoot.setOnClickListener { button ->
            button.isEnabled = false
            ExploitHandler(this) { result, log ->
                binding.mLog.text = log
                binding.mButtonCopy.isEnabled = true
                button.isEnabled = true
                if (result)
                    getString(R.string.success).toast(this, true)
                else
                    getString(R.string.fail).toast(this, false)

            }.execute()
        }

        binding.mButtonCopy.setOnClickListener {
            (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(ClipData.newPlainText(getString(R.string.log), binding.mLog.text))
        }
    }

}
