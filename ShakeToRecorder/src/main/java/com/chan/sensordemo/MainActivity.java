package com.chan.sensordemo;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
		implements
			SensorEventListener {

	private Button btn;
	private SensorManager sensorManager;
	private MediaRecorder mediaRecorder;
	private Dialog dialog;

	public static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/ShakeToRecorder";

	public static final String NO_SDCARD_PATH = "/data/data/com.chan.sensordemo/ShakeToRecorder";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initRecoder();
	}

	private void init() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, FileActivity.class));
			}
		});
		dialog = new Dialog(this);
	}

	@Override
	protected void onResume() {
		/* 注册线性加速度传感器的监听器，这里使用线性加速度而不是用加速度传感器，
		加速度传感器包含重力加速度，为了避免重力加速度带来影响 */
		sensorManager
				.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						SensorManager.SENSOR_DELAY_UI);
		super.onResume();
	}

	@Override
	protected void onStop() {
		sensorManager.unregisterListener(this);

		super.onStop();
	}

	private void initRecoder() {
		// 创建文件夹
		makeDir();
		// 1创建MediaRecoder
		mediaRecorder = new MediaRecorder();
		// 2调用setAudioSource设置声音来源
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 3格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		// 4设置录制的编码格式,编码位率，采样率等..
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// mediaRecorder.setAudioEncodingBitRate (10);
		// mediaRecorder.setAudioSamplingRate ();

		// 5设置保存路径
		if (hasSDCard()) {
			mediaRecorder.setOutputFile(SDCARD_PATH + "/"
					+ SystemClock.elapsedRealtime() + ".amr");
		} else {
			mediaRecorder.setOutputFile(NO_SDCARD_PATH + "/"
					+ SystemClock.elapsedRealtime() + ".amr");
		}
		// 6进入准备录制状态
		try {
			mediaRecorder.prepare();
			Log.d("tag", "准备录音");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 开始录音,显示Dialog */
	private void startRecorder() {

		// 防止录音的同时晃动手机再次开启录音;
		if (dialog.isShowing()) {
			return;
		}

		Toast.makeText(MainActivity.this, "约一约", Toast.LENGTH_SHORT).show();
		// 开启录音
		mediaRecorder.start();
		Log.d("tag", "开始录音");
		// 显示一个Dialog

		dialog.setTitle("正在录音");
		dialog.setCanceledOnTouchOutside(false);
		Button stopButton = new Button(this);
		stopButton.setText("停止录音");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		dialog.setContentView(stopButton, params);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				stopRecorder();
				Toast.makeText(MainActivity.this, "停止录音", Toast.LENGTH_SHORT)
						.show();
			}
		});
		dialog.show();
	}

	/* 结束录音 */
	private void stopRecorder() {
		mediaRecorder.stop();
		mediaRecorder.release();
	}

	/* 开启震动 */
	private void startVibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{200, 300, 300, 300}, -1);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		/* 粗略判断在x，y，z三个方向上加速度，达到一定值就认为摇动啦 */
		if (Math.abs(x) > 18 || Math.abs(y) > 18 || Math.abs(z) > 18) {
			startRecorder();
			startVibrate();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	/**
	 * 判断是否有sd卡
	 */
	private boolean hasSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 创建文件,如果有sdcard就在sdcard创建文件夹，否则在data下创建
	 */
	private void makeDir() {
		if (hasSDCard()) {
			File sdFileDir = new File(SDCARD_PATH);
			if (!sdFileDir.exists()) {
				sdFileDir.mkdir();
			}
		} else {
			File noSdFileDir = new File(NO_SDCARD_PATH);
			if (!noSdFileDir.exists()) {
				noSdFileDir.mkdir();
			}
		}

		Log.d("tag", "创建文件夹成功~~~~");
	}
}
