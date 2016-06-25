package com.example.arvind.spiderapp3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private ImageView Slide;
    private Button slideShow ;
    private int[] imageResources={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5,R.drawable.pic6};
    private int position;
    private TextView timer;
    private int time;
    private Spinner songs;
    private ArrayAdapter<CharSequence> adapter;
    private String songselected;
    private Button start;
    private Button stop;
    private MediaPlayer mediaPlayer;
    private boolean running=false;
    private Button enable;
    private Button disable;
    private int swipe=0;
    SensorManager sm;
    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Slide.setImageResource(imageResources[position]);
        }
    };
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
                  timer.setText("Time elapsed: "+Integer.toString(time)+"sec");
            if(time==17)
            {enable.setEnabled(true);
                slideShow.setEnabled(true);
            }
            else {
                enable.setEnabled(false);
                slideShow.setEnabled(false);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Slide = (ImageView) findViewById(R.id.imageView);
        slideShow = (Button) findViewById(R.id.button);
        timer= (TextView) findViewById(R.id.textView);
        songs = (Spinner) findViewById(R.id.spinner);
        adapter=ArrayAdapter.createFromResource(this,R.array.music,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        songs.setAdapter(adapter);
        start = (Button)findViewById(R.id.Start);
        stop = (Button)findViewById(R.id.Stop);
        enable=(Button) findViewById(R.id.enable);
        disable=(Button)findViewById(R.id.disable);
        disable.setEnabled(false);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sm.getSensorList(Sensor.TYPE_PROXIMITY).size()!=0){
            //Do something if sensor is found
            //Here set the sensot
            Sensor s = sm.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
            //Toast.makeText(MainActivity.this,"Maximum range is "+Float.toString(s.getMaximumRange()),Toast.LENGTH_SHORT).show();
            sm.registerListener(this,s,SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(MainActivity.this,"Sorry, but your phone doesn't have a proximity sensor", Toast.LENGTH_SHORT).show();
        }

        songs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                songselected = (parent.getItemAtPosition(position)).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        slideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable=(Button) findViewById(R.id.enable);
                enable.setEnabled(false);
                slideShow.setEnabled(false);
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        position = 0;
                        try {
                            while (position <= 5) {
                                synchronized (this) {
                                    handler.sendEmptyMessage(0);
                                    // Slide.setImageResource(imageResources[position]);
                                    wait(3000);
                                    position++;

                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                };
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        time = 0;
                        try {
                            while (position < 6) {
                                synchronized (this) {
                                    handler1.sendEmptyMessage(0);
                                    wait(1000);
                                    time++;

                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                };
                if(time>0)
                {enable.setEnabled(true);
                    slideShow.setEnabled(true);
                }

                Thread ppt = new Thread(r);
                Thread time = new Thread(r1);
                ppt.start();
                time.start();
             }
        });



    start.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Runnable song = new Runnable() {
                @Override
                public void run() {
                    switch(songselected)
                    {
                        case "equinox":
                            mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.equinox);
                            break;
                        case "backmusic":
                            mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.backmusic);
                            break;
                        case "lost":
                            mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.lost);
                            break;
                        default:
                            mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.equinox);
                            break;
                    }
                    while(running)
                        mediaPlayer.start();
                    if(running==false)
                    { mediaPlayer.stop();
                        return ;
                    }
                } };
            final Thread play =new Thread(song);
            if(running==false)
            {play.start();running=true;}
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    running = false;
                    play.interrupt();

                }
            });
        }
    });
    enable.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slideShow.setEnabled(false);
            enable.setEnabled(false);
            disable.setEnabled(true);
        }
    });
    disable.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slideShow.setEnabled(true);
            enable.setEnabled(true);
            disable.setEnabled(false);
        }
    });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
           if(enable.isEnabled()==false&&disable.isEnabled()==true)
          {   swipe++;

              if(position==6)
              position=0;

              Slide.setImageResource(imageResources[position]);
              if(swipe%2==0)
              position=position+1;
          }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
