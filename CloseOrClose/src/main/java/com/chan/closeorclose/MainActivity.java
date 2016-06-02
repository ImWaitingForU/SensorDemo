package com.chan.closeorclose;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 靠近还是待机?
 */
public class MainActivity extends AppCompatActivity
		implements
			SensorEventListener {

	private SensorManager sensorManager;

	/*使用电量管理器控制屏幕的熄灭和唤醒*/
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		pm = (PowerManager) getSystemService(POWER_SERVICE);
		closeOrClose();
	}


	/**
	 * 初始化唤醒锁wakeLock,直接使用内置PROXIMITY_SCREEN_OFF_WAKE_LOCK，
	 * 就可以直接实现靠近熄灭屏幕的功能
	 *
	 * 初始化完要调用acquire()方法启用
	 */
	private void closeOrClose() {
		wakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
				"w1");
		wakeLock.acquire();
	}

	@Override
	protected void onResume() {
		/*注册距离传感器，只有注册了距离传感器唤醒锁才有效果*/
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}

	@Override
	protected void onStop() {
		/*释放传感器和唤醒锁*/
		wakeLock.release();
		sensorManager.unregisterListener(this);
		super.onStop();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
