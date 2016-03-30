package co.natsuhi.getwild;

import android.app.SearchManager;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
  private int thresholdValue;
  private float tmpValue;
  private TextView currentBrightness;
  private TextView threshold;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
  }

  @Override protected void onResume() {
    super.onResume();
    registLightSensor();
  }

  @Override protected void onPause() {
    unregistLightSensor();
    super.onPause();
  }

  private void initView() {
    currentBrightness = (TextView) findViewById(R.id.current_brightness);
    threshold = (TextView) findViewById(R.id.threshold);

    SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
    updateThreshold(seekBar.getProgress());
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateThreshold(progress);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });
  }

  private void updateThreshold(int progress) {
    thresholdValue = progress * 5;
    threshold.setText(getString(R.string.threshold, thresholdValue));
  }

  private void registLightSensor() {
    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  private void unregistLightSensor() {
    ((SensorManager) getSystemService(SENSOR_SERVICE)).unregisterListener(listener);
  }

  private Intent getWildAndTouch() {
    Intent intent = new Intent();
    intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
    intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio");
    intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, "get wild");
    intent.putExtra(SearchManager.QUERY, "get wild");
    return intent;
  }

  private SensorEventListener listener = new SensorEventListener() {
    @Override public void onSensorChanged(SensorEvent event) {
      float value = event.values[0];
      if (tmpValue >= thresholdValue && value < thresholdValue) {
        startActivity(getWildAndTouch());
      }
      tmpValue = value;
      currentBrightness.setText(getString(R.string.current_Brightness, tmpValue));
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  };
}
