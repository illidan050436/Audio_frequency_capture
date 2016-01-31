package com.test02.illid.audio_capture02;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.TextView;
import org.jtransforms.fft.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int sample_rate = 16000;
    private int fft_size = 4096;
    private int buffer_size = fft_size / 2;
    //private float mf = 16384f;
    //private AudioRecord ard = new AudioRecord(MediaRecorder.AudioSource.MIC, sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
    private AudioRecord ard = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
    private boolean isrecording = false;
    //private ArrayList<Integer> freq_final = new ArrayList<Integer>();
    private boolean isrealtime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final Button button1 = (Button) findViewById(R.id.button1);
        //final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final TextView text1 = (TextView) findViewById(R.id.textView1);
        final Button button4 = (Button) findViewById(R.id.button4);

        //buffer_size = AudioRecord.getMinBufferSize(sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //Log.d("ACCG", "buffer size = " + buffer_size);
        //buffer_size = fft_size / 2;
/*
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isrealtime){
                    if (ard.getState() == AudioRecord.STATE_INITIALIZED) {
                        //Log.d("DebugMainActivity", "start");
                    } else {
                        //ard = new AudioRecord(MediaRecorder.AudioSource.MIC, sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                        ard = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                        //Log.d("DebugMainActivity", "start_no_ard");
                    }
                    isrecording = true;
                    ard.startRecording();
                    new backAStask().execute();
                    //Log.d("DebugMainActivity", "AsyncTask");
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ard.getState() == AudioRecord.STATE_INITIALIZED && isrecording) {
                    //Log.d("DebugMainActivity", "stop");
                    ard.stop();
                    ard.release();
                    //new backAStask().cancel(true);
                    isrecording = false;
                    if (!isrealtime) {
                        Integer sum = 0;
                        if (freq_final.size() >= 10) {
                            for (int i = 0; i < 5; i++) {
                                freq_final.remove(0);
                            }
                        }
                        for (Integer i : freq_final) {
                            sum += i;
                        }
                        //Log.d("DebugMainActivity", "sum : " + sum);
                        sum = sum / freq_final.size();
                        //Log.d("DebugMainActivity", "freq_final size: " + freq_final.size());
                        //Log.d("DebugMainActivity", "stop final freq: " + sum);
                        freq_final.clear();
                        text1.setText(sum.toString());
                    }
                } else {
                    //Log.d("DebugMainActivity", "stop_no_ard");
                    freq_final.clear();
                }
            }
        });
        */
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isrealtime) {
                    text1.setText("Frequency");
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isrealtime) {
                    isrealtime = false;
                    button4.setBackgroundColor(getResources().getColor(R.color.cyan));
                    if (ard.getState() == AudioRecord.STATE_INITIALIZED && isrecording) {
                        ard.stop();
                        ard.release();
                        isrecording = false;
                    }
                } else {
                    isrealtime = true;
                    button4.setBackgroundColor(getResources().getColor(R.color.red));
                    if (ard.getState() == AudioRecord.STATE_INITIALIZED) {
                        //Log.d("DebugMainActivity", "start");
                    } else {
                        //ard = new AudioRecord(MediaRecorder.AudioSource.MIC, sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                        ard = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sample_rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                        //Log.d("DebugMainActivity", "start_no_ard");
                    }
                    isrecording = true;
                    ard.startRecording();
                    new backAStask().execute();
                }
            }
        });
    }


    private class backAStask extends AsyncTask<Void, short[], Void> {
        final TextView text1 = (TextView) findViewById(R.id.textView1);

        @Override
        protected Void doInBackground(Void... params) {
            //Log.d("debugdoInBackground", "beforewhile");
            while (isrecording) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                short[] buffer = new short[buffer_size];
                //Log.d("debugdoInBackground", "insidewhile");
                ard.read(buffer, 0, buffer_size);
                publishProgress(buffer);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(short[]... buffer){
            //System.out.println(Arrays.toString(buffer));
            //Log.d("debugOnProgressUpdate", "audio data: " + buffer.toString());
            super.onProgressUpdate(buffer);
            //for (Short var: buffer[0]){
            //Log.d("debugOnProgressUpdate", "audio data: " + var.toString());
            //}
            double[] trans = new double[fft_size];
            //double temp = mf * mf * fft_size * fft_size / 2d;
            double[] mag = new double[buffer_size / 2];
            for (int i = 0; i < fft_size / 2; i++){
                trans[2*i] = (double)buffer[0][i];
                trans[2*i+1] = 0;
            }
            DoubleFFT_1D fft = new DoubleFFT_1D(fft_size / 2);
            fft.complexForward(trans);
            // power spectrum magnitude
            for (int i = 0; i < buffer_size/2; i++){
                mag[i] = Math.hypot(trans[2*i], trans[2*i+1]);
            }

            double max = -100;
            int max_index = 0;
            //int start = (int)Math.round(200*fft_size/sample_rate);
            for (int i = 0; i < mag.length; i++){
                //trans[i] = Math.abs(trans[i]);
                if (max < mag[i]){
                    max = mag[i];
                    max_index = i;
                }
            }
            int freq = max_index * sample_rate / fft_size * 2;
            /*Log.d("debugOnProgressUpdate", "first freq: " + freq);
            if (sample_rate / fft_size < freq && freq < sample_rate/2 - sample_rate / fft_size) {
                int id = max_index;
                double x1 = trans[id-1];
                double x2 = trans[id];
                double x3 = trans[id+1];
                double c = x2;
                double a = (x3+x1)/2 - x2;
                double b = (x3-x1)/2;
                if (a < 0) {
                    double xPeak = -b/(2*a);
                    if (Math.abs(xPeak) < 1) {
                        freq += xPeak * sample_rate / fft_size;
                        max = (4*a*c - b*b)/(4*a);
                    }
                }
            }
            freq = Math.round(freq);
            Log.d("debugOnProgressUpdate", "maxDB: " + max);*/
            //Log.d("debugOnProgressUpdate", "frequency: " + freq);
            //if (!isrealtime) {
//                freq_final.add(freq);
  //          }else{
                Integer tmp_int = freq;
                text1.setText(tmp_int.toString());
    //        }
        }
    }

}

