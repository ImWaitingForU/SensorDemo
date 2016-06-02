package com.chan.sensordemo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * 查看录音文件
 */
public class FileActivity extends AppCompatActivity {

	private ListView lv;
	private String fileList[];
	private MyAdapter adapter;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		initLv();
	}

	@Override
	protected void onResume() {
		mediaPlayer = new MediaPlayer();

		super.onResume();
	}

    @Override
    protected void onStop () {
        mediaPlayer.release ();
        super.onStop ();
    }

    private void playRecorder(String fileName) {
        try {
            mediaPlayer.reset ();
            mediaPlayer.setDataSource (MainActivity.SDCARD_PATH+"/"+fileName);
            mediaPlayer.prepare ();
            mediaPlayer.start ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

	private void initLv() {
		lv = (ListView) findViewById(R.id.lv);
		adapter = new MyAdapter();
		File file = new File(MainActivity.SDCARD_PATH);
		// 获取目录下所有文件名
		fileList = file.list();
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                playRecorder (fileList[position]);
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return fileList.length;
		}

		@Override
		public Object getItem(int position) {
			return fileList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = LayoutInflater.from(FileActivity.this).inflate(
					R.layout.file_item_list, parent, false);
			TextView tv = (TextView) v.findViewById(R.id.tv_item);
			tv.setText(fileList[position]);

			return v;
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
