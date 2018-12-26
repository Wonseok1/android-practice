package com.example.student.thread2;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tv_time;
    EditText edt_min, edt_sec;
    Button btn_30s, btn_10m, btn_30m, btn_reset, btn_start, btn_stop;
    ProgressBar progressBar;
    int min, sec;
    /*TimeThread timeThread = null;*/
    MyTask myTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_time = findViewById(R.id.tv_time);
        edt_min = findViewById(R.id.edt_min);
        edt_sec = findViewById(R.id.edt_sec);
        progressBar = findViewById(R.id.progressBar);
        btn_10m = findViewById(R.id.btn_10m);
        btn_30m = findViewById(R.id.btn_30m);
        btn_30s = findViewById(R.id.btn_30s);
        btn_reset = findViewById(R.id.btn_reset);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);

        min = Integer.parseInt(edt_min.getText().toString());
        sec = Integer.parseInt(edt_sec.getText().toString());

        btn_start.setOnClickListener(new BtnListener());
        btn_stop.setOnClickListener(new BtnListener());
        btn_30s.setOnClickListener(new BtnListener());
        btn_30m.setOnClickListener(new BtnListener());
        btn_10m.setOnClickListener(new BtnListener());
        btn_reset.setOnClickListener(new BtnListener());
    }


    class MyTask extends AsyncTask<Void, Void, Void> {
        int a = min * 60 + sec;
        int e  = min*60 + sec;

        @Override
        protected Void doInBackground(Void... voids) {

            while (isCancelled() == false) {
                a--;
                if (a > 0) {
                    publishProgress();
                } else {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            progressBar.setMax(e);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tv_time.setText("완료되었습니다.");
            progressBar.setVisibility(View.INVISIBLE);
            myTask.cancel(true);
            myTask = null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            progressBar.setVisibility(View.VISIBLE);
            int b = a / 60;
            int c = a % 60;
            int d = e- a ;
            tv_time.setText(Integer.toString(b) + ":" + Integer.toString(c));
            progressBar.setProgress(d);
        }

        @Override
        protected void onCancelled() {
            tv_time.setText("사용자에 의해 종료되었음");
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
    }


    //아래는 쓰레드와 핸들러를 사용해서 백그라운드에서 초세기 지속
    /*class TimeThread extends Thread {
        @Override
        public void run() {
            try {

                int a = min * 60 + sec;
                while (!Thread.currentThread().isInterrupted()) {
                    if (a > 0) {
                        Thread.sleep(100);
                        a--;
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = a;
                        handler.sendMessage(msg);

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (msg.arg1 == 0) {
                    tv_time.setText("00:00");
                    timeThread.interrupt();
                } else {
                    int b = msg.arg1 / 60;
                    int c = msg.arg1 % 60;
                    tv_time.setText(Integer.toString(b) + ":" + Integer.toString(c));

                }
            }
        }
    };*/

    class BtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_start:
                    if (myTask == null) {
                        myTask = new MyTask();
                        myTask.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "이미동작중", Toast.LENGTH_SHORT).show();
                    }
                    /*if (timeThread == null) {
                        timeThread = new TimeThread();
                        timeThread.start();
                    }*/
                    break;

                case R.id.btn_stop:
                    if (myTask != null) {
                        myTask.cancel(true);
                        myTask = null;
                    }
                    /*if (timeThread != null) {
                        timeThread.interrupt();
                        tv_time.setText("사용자에 의해 종료되었습니다.");
                        timeThread = null;
                    }*/
                    break;

                case R.id.btn_30s:
                   /* sec = sec + 30;
                    if (sec == 60) {
                        sec = 0;
                        min++;
                        int e = Integer.parseInt(edt_min.getText().toString())+1;
                        edt_min.setText(Integer.toString(e));
                        edt_sec.setText(Integer.toString(sec));
                    }
                    edt_sec.setText(Integer.toString(sec));*/
                    break;

                case R.id.btn_30m:
                    /*min = min + 30;
                    int c = Integer.parseInt(edt_min.getText().toString()) + 30;
                    edt_min.setText(Integer.toString(c));*/
                    break;

                case R.id.btn_10m:
                   /* min = min + 10;
                    int d = Integer.parseInt(edt_min.getText().toString()) + 10;
                    edt_min.setText(Integer.toString(d));*/
                    break;

                case R.id.btn_reset:
                    /*min = 0;
                    sec = 0;
                    edt_min.setText(Integer.toString(0));
                    edt_sec.setText(Integer.toString(0));*/
                    break;
            }
        }
    }


}
