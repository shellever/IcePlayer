
package com.shellever.iceplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

//
// 只使用Handler来实现闪屏页界面
//
// 全屏模式：继承自Activity并设置主题为android:theme="@android:style/Theme.NoTitleBar.Fullscreen"即可
//
// 在闪屏页中先启动MyMusicService后台服务，后面就可以多次绑定服务
//
public class SplashActivity extends Activity {

    private static final int MSG_TIMER_ONE_SECOND = 1;
    private int count = 5;

    private TextView mTimerValueTv;
    private Animation animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        // 在安装完程序后，只显示一次，但是因为是使用Activity来显示界面的，会出现闪现，
        // 正确方式时使用Fragment，然后用Activity来进行控制闪屏页的显示与替换
//        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
//        String isExist = sp.getString("isExist", "false");
//        if ("true".equals(isExist)) {
//            Intent intent = new Intent(Splash2Activity.this, MainActivity.class);
//            startActivity(intent);
//            return;
//        }
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("isExist", "true");
//        editor.apply();

        mTimerValueTv = (TextView) findViewById(R.id.tv_timer_value);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_text_timer);
        handler.sendEmptyMessageDelayed(MSG_TIMER_ONE_SECOND, 1000);

        // 在闪屏页中启动MyMusicService后台服务
        startService(new Intent(this, MyMusicService.class));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMER_ONE_SECOND) {
                handler.sendEmptyMessageDelayed(MSG_TIMER_ONE_SECOND, 1000);    // first
                mTimerValueTv.setText(" " + String.valueOf(count));
                mTimerValueTv.startAnimation(animation);              // second
                if (count <= 0) {
                    handler.removeMessages(MSG_TIMER_ONE_SECOND);
                    startMainActivity();
                }
                count--;
            }
        }
    };

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
