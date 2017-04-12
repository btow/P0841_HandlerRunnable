package com.example.samsung.p0841_handlerrunnable;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    final int MAX = 100;

    private ProgressBar pbCount;
    private TextView tvInfo;
    private CheckBox chbInfo;
    private Button btnReset;
    private int cnt;

    Handler handler;
    //Создание нового потока
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                for (cnt = 1; cnt < MAX; cnt++) {
                    //Эмуляция какого-нибудь действия
                    TimeUnit.MILLISECONDS.sleep(100);
                    //Обновление прогресс-бара
                    handler.post(updateProgress);
                    handler.post(setVisibleButton);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cnt = 0;
            thread.interrupt();
        }
    });
    //Показ кнопки перезагрузки
    private Runnable setVisibleButton = new Runnable() {
        @Override
        public void run() {
            if (!thread.isAlive()) {
                btnReset.setVisibility(View.VISIBLE);
            }
        }
    };
    //Скрытие кнопки перезагрузки
    private Runnable setUnvisibleButton = new Runnable() {
        @Override
        public void run() {
            if (thread.isAlive()) {
                btnReset.setVisibility(View.GONE);
            }
        }
    };
    //Показ информации
    private Runnable showInfo = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "showInfo is " + cnt);
            tvInfo.setText("Count = " + cnt);
            //Исполнение с отсрочкой в 1000 мсек
            handler.postDelayed(showInfo, 1000);
        }
    };
    //обновление програсс-бара
    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            pbCount.setProgress(cnt);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        pbCount = (ProgressBar) findViewById(R.id.pbCount);
        pbCount.setMax(MAX);
        pbCount.setProgress(0);

        tvInfo = (TextView) findViewById(R.id.tvInfo);

        chbInfo = (CheckBox) findViewById(R.id.chbInfo);
        chbInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //Показ информации
                    tvInfo.setVisibility(View.VISIBLE);
                    handler.post(showInfo);

                } else {
                    //Отмена показа информации
                    tvInfo.setVisibility(View.GONE);
                    handler.removeCallbacks(showInfo);
                }

            }
        });
        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setVisibility(View.GONE);

        thread.start();

    }

    public void onClickButton(View view) {
        pbCount.setProgress(cnt);

        if (!thread.isInterrupted()) {
            Log.d(LOG_TAG, "The thread is not Interrupted");
            thread.interrupt();
            handler.post(setUnvisibleButton);
        } else {
            Log.d(LOG_TAG, "The thread is Interrupted");
            thread.start();
            handler.post(setVisibleButton);
        }
    }
}
