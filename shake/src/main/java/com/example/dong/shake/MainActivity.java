package com.example.dong.shake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.iv_shake)
    ImageView ivShake;

    private static final String TAG = "TestSensorActivity";
    private static final int SENSOR_SHAKE = 10;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private boolean isShaking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        initActionBar();//可以设置状态栏的按钮和内容(返回键)
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("摇一摇");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            // 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
    }


    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
            int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                vibrator.vibrate(200);
                Message msg = new Message();
                msg.what = SENSOR_SHAKE;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 动作执行
     */

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENSOR_SHAKE:

                    if (!isShaking) {
                        startAnim();
                        isShaking = true;
                    }
                    break;
            }
        }
    };

    //定义摇一摇动画动画

    private void startAnim() {
        AnimationSet animup = new AnimationSet(true);
        TranslateAnimation animation0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF, -0.5f,Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,0.f);
        animation0.setDuration(300);

        TranslateAnimation animation1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,0.f);
        animation1.setDuration(300);
        animation1.setStartOffset(300);
        TranslateAnimation animation2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF, 0f,Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,0.f);
        animation2.setDuration(300);
        animation2.setStartOffset(600);

        animup.addAnimation(animation0);
        animup.addAnimation(animation1);
        animup.addAnimation(animation2);
        animup.setRepeatCount(Animation.RESTART);
        ivShake.startAnimation(animup);

        ivShake.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShaking = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //回退
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
