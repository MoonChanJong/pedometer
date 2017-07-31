package com.example.mcj.manbogi;

import android.app.Activity;

import android.graphics.Color;

import android.graphics.Typeface;
import android.hardware.Sensor;

import android.hardware.SensorEvent;

import android.hardware.SensorEventListener;

import android.hardware.SensorManager;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.mcj.manbogi.R.id.Set;

public class MainActivity extends Activity implements SensorEventListener {

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float x, y, z;
    //민감도(수치가 낮을수록 민감)
    private static int SHAKE_THRESHOLD = 600;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private TextView t, t2;
    private int count = 0;
    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    int tCount = 0;

    private static Typeface typeface;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        t = (TextView) findViewById(R.id.count);
        t2 = (TextView) findViewById(R.id.tCount);
        tHandler.sendEmptyMessage(0);

    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(this.getAssets(), "hunjum.ttf");
        }
        setGlobalFont(getWindow().getDecorView());
    }

    private void setGlobalFont(View view) {
        if(view != null) {
            if(view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)view;
                int vgCnt = viewGroup.getChildCount();
                for(int i = 0; i<vgCnt; i++) {
                    View v = viewGroup.getChildAt(i);
                    if(v instanceof TextView) {
                        ((TextView) v).setTypeface(typeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);

            //센서 동작이 이전/이후 시간의 차이가 0.1초 이내이면
            if (gabOfTime > 100) {
                lastTime = currentTime;

                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트 발생!!
                    count++;
                    t.setText("" + count + "");
                }
            }

            lastX = event.values[DATA_X];
            lastY = event.values[DATA_Y];
            lastZ = event.values[DATA_Z];


        }
    }


    Handler tHandler = new Handler() {
        public void handleMessage(Message msg) {
            tCount++;
            t2.setText("" + tCount + "");
            tHandler.sendEmptyMessageDelayed(0,1000);
        }
    };

    public void setShake(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item){
                switch (item.getItemId()){
                    case R.id.num1:
                        SHAKE_THRESHOLD=200;
                        break;
                    case R.id.num2:
                        SHAKE_THRESHOLD=400;
                        break;
                    case R.id.num3:
                        SHAKE_THRESHOLD=600;
                        break;
                    case R.id.num4:
                        SHAKE_THRESHOLD=800;
                        break;
                    case R.id.num5:
                        SHAKE_THRESHOLD=1000;
                        break;
                }
                return true;
            }
        });
        popup.show();

    }


    public void ResetCount(View v) {
        count = 0;
        tCount=0;
        t.setText("" + count + "");
        t2.setText("" + tCount + "");
    }


}