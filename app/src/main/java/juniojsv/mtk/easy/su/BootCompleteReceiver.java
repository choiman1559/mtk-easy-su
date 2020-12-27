package juniojsv.mtk.easy.su;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
            if (preferences.getBoolean("apply_after_boot", false)) {
                new ExploitHandler(context, (aBoolean, s) -> {
                    ExtensionsKt.toast(context.getString(aBoolean ? R.string.success : R.string.fail), context, aBoolean);
                    return null;
                }).execute();
            }
        }
    }
}
