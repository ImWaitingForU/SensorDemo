package com.chan.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
		implements
			SensorEventListener {

	private ImageView compass; // 指南针图片
	private TextView tv;
	private SensorManager sensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		compass = (ImageView) findViewById(R.id.compass);
		tv = (TextView) findViewById(R.id.tv);
		// 获取SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	@Override
	protected void onResume() {
		//注册方向传感器监听器
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_STATUS_ACCURACY_LOW);
		super.onResume();
	}

	@Override
	protected void onStop() {
		//界面变为不可见就解除监听
		sensorManager.unregisterListener(this);
		super.onStop();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		/* 监听相对于Z轴0度的偏移量,即手机水平放置情况下，和正北方偏移的角度 */
		final float curZ = event.values[0];
		tv.setText(String.valueOf(curZ));

		/* 一旦监听到偏移量改变，反向旋转图片 */
		compass.setRotation(-Math.abs(curZ));

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
