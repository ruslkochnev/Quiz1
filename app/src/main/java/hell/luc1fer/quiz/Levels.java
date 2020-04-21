package hell.luc1fer.quiz;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Random;

public class Levels extends AppCompatActivity {
    HomeWatcher mHomeWatcher;
    Dialog dialog;
    Dialog dialogEnd;
    public int numLeft;
    public int numRight;
    Random random = new Random();
    public int count = 0;
    int[] compareArray;
    private int seconds = 0;
    private boolean timerIsRunning;
    boolean adTrue;
    TextView timer;
    String timeCounterFormatted;
    int goodLevelLimitMax;
    int badLevelLimitMin;
    int timeComparisonForGoodBadResult;
    private static InterstitialAd mInterstitialAd;
    public static AdRequest adRequest; //ДОБАВЛЕНА ВОТ ЭТА ХУЕРГА ЕСЛИ НЕ БУДЕТ РАБОТАТЬ ВЫПИЛИТЬ ЕЁ НАХУЙ!!!!!!!!!!!
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timer = findViewById(R.id.timer);
            timer.setTextColor(getIntent().getExtras().getInt("text_color"));
            int minute_s_1 = (seconds % 3600) / 60;
            int sec_s_1 = seconds % 60; // Секунды
            timeCounterFormatted = String.format("%02d:%02d", minute_s_1, sec_s_1);
            if ((seconds) > 0) {
                timer.setText(timeCounterFormatted);
            } else {
                timer.setText(R.string.zero);
            }
            if (timerIsRunning) {
                seconds++;
            }
            handler.postDelayed(this, 1000);
        }
    };

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        // test ad
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdUnitId(getString(R.string.ad_id));
        adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                try {
                    Intent intent = new Intent(Levels.this, GameLevels.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
                dialogEnd.dismiss();
                timerIsRunning = false;
            }
        });


        final int[] progressArray = {
                R.id.point1, R.id.point2, R.id.point3, R.id.point4, R.id.point5, R.id.point6,
                R.id.point7, R.id.point8, R.id.point9, R.id.point10, R.id.point11, R.id.point12,
                R.id.point13, R.id.point14, R.id.point15, R.id.point16, R.id.point17, R.id.point18,
                R.id.point19, R.id.point20,
        };
        final ImageView img_left = findViewById(R.id.img_left);
        final ImageView img_right = findViewById(R.id.img_right);
        final TextView text_left = findViewById(R.id.text_left);
        final TextView text_right = findViewById(R.id.text_right);
        adTrue = getIntent().getExtras().getBoolean("adTrue");

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();
        goodLevelLimitMax = getIntent().getExtras().getInt("goodMax");
        badLevelLimitMin = getIntent().getExtras().getInt("badMin");
        TextView text_levels = findViewById(R.id.text_levels);
        text_levels.setText(getIntent().getExtras().getString("text_levels"));//устанавливаем текст уровня; с помощью методов getIntent().getExtras() получаем значение  параметра по ключу "text_levels"
        text_levels.setTextColor(getIntent().getExtras().getInt("text_color"));//устанавливаем цвет текст уровня; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "text_color"
        img_left.setClipToOutline(true);
        img_right.setClipToOutline(true);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ImageView background = findViewById(R.id.background);
        background.setImageResource(getIntent().getExtras().getInt("background"));//устанавливаем фон уровня; с помощью методов getIntent().getExtras() получаем значение по ключу "background"
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.previewdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        ImageView previewimg = dialog.findViewById(R.id.previewImg);
        previewimg.setImageResource(getIntent().getExtras().getInt("previewImg"));//устанавливаем картинку диалогового окна; с помощью методов getIntent().getExtras() получаем значение по ключу "previewImg"
        LinearLayout dialogfon = dialog.findViewById(R.id.dialogFon);
        dialogfon.setBackgroundResource(getIntent().getExtras().getInt("dialogFon"));//устанавливаем фон диалогового окна; с помощью методов getIntent().getExtras() получаем значение по ключу "dialogFon"
        TextView textdecription = dialog.findViewById(R.id.textDescription);
        textdecription.setText(getIntent().getExtras().getString("levelDescription"));//устанавливаем описание уровня; с помощью методов getIntent().getExtras() получаем значение по ключу "levelDescription"
        TextView btnclose = dialog.findViewById(R.id.btnClose);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Levels.this, GameLevels.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
                dialog.dismiss();
            }
        });
        TextView btncontinue = dialog.findViewById(R.id.btnContinue);
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                timerIsRunning = true;
            }
        });
        dialog.show();
        dialogEnd = new Dialog(this);
        dialogEnd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnd.setContentView(R.layout.dialogend);
        dialogEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEnd.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogEnd.setCancelable(false);
        final ImageView star1 = dialogEnd.findViewById(R.id.star1);
        final ImageView star2 = dialogEnd.findViewById(R.id.star2);
        final ImageView star3 = dialogEnd.findViewById(R.id.star3);
        LinearLayout dialogfonEnd = dialogEnd.findViewById(R.id.dialogFon);
        dialogfonEnd.setBackgroundResource(getIntent().getExtras().getInt("dialogFon"));//устанавливаем фон диалогового окна; с помощью методов getIntent().getExtras() получаем значение по ключу "dialogFon"
        TextView textdescriptionEnd = dialogEnd.findViewById(R.id.textdescriptionend);
        textdescriptionEnd.setText(getIntent().getExtras().getString("levelDescriptionEnd"));//устанавливаем описание уровня; с помощью методов getIntent().getExtras() получаем значение по ключу "levelDescriptionEnd"
        TextView btncontinue2 = dialogEnd.findViewById(R.id.btnContinue);
        btncontinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adTrue) {
                    try {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Toast adToast = Toast.makeText(getBaseContext(), "Реклама не загружена", Toast.LENGTH_LONG);
                            adToast.show();
                        }
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        Intent intent = new Intent(Levels.this, GameLevels.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                    }
                    dialogEnd.dismiss();
                    timerIsRunning = false;
                }
            }
        });
        //ratingBar = dialogEnd.findViewById(R.id.ratingBar);
        TextView btn_back = findViewById(R.id.button_back);
        btn_back.setBackgroundResource(getIntent().getExtras().getInt("btn_back_style"));//устанавливаем стиль кнопки "назад"; с помощью методов getIntent().getExtras() получаем значение по ключу "btn_back_style"
        btn_back.setTextColor(getIntent().getExtras().getInt("btn_back_text_color"));//устанавливаем цвет текста кнопки "назад"; с помощью методов getIntent().getExtras() получаем значение по ключу "btn_back_text_color"
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Levels.this, GameLevels.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
                timerIsRunning = false;
            }
        });
        final Animation a = AnimationUtils.loadAnimation(Levels.this, R.anim.alpha);
        numLeft = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
        img_left.setImageResource(getIntent().getExtras().getIntArray("img")[numLeft]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
        text_left.setText(getIntent().getExtras().getIntArray("text")[numLeft]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные
        text_left.setTextColor(getIntent().getExtras().getInt("text_color"));//устанавливаем цвет текст подписей; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "text_color"
        numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
        //для каждого уровня был создан массив strong, потому что иначе реализовать сравнение, подходящее под абсолютно любое задание не получилось
        compareArray = getIntent().getExtras().getIntArray("L"); //устанавливаем массив сравнения; с помощью методов getIntent().getExtras() получаем значение по ключу "L"
        while (compareArray[numLeft] == compareArray[numRight]) { //сравнение происходит путем взятия [numLeft] и [numRight] элементов массива l (он же array.strong) для текущего уровня
            numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
        }
        img_right.setImageResource(getIntent().getExtras().getIntArray("img")[numRight]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
        text_right.setText(getIntent().getExtras().getIntArray("text")[numRight]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные
        text_right.setTextColor(getIntent().getExtras().getInt("text_color"));//устанавливаем цвет текст подписей; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "text_color"
        img_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    img_right.setEnabled(false);
                    if (compareArray[numLeft] > compareArray[numRight]) {
                        img_left.setImageResource(R.drawable.level0_img_true);
                    } else {
                        img_left.setImageResource(R.drawable.level0_img_false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (compareArray[numLeft] > compareArray[numRight]) {
                        if (count < 20) {
                            count++;
                        }
                        for (int i = 0; i < 20; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points_green);
                        }
                    } else {
                        if (count > 0) {
                            if (count == 1) {
                                count = 0;
                            } else {
                                count = count - 2;
                            }
                        }
                        for (int i = 0; i < 19; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points_green);
                        }
                    }
                    if (count == 20) {
                        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
                        final int level = save.getInt("Level", getIntent().getExtras().getInt("preferencesDefault"));//устанавливаем стандартное значение для сохранения прогресса; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesDefault"
                        if (level > getIntent().getExtras().getInt("preferencesLevel")) {//сравниваем прогресс с номером текущего уровня; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesLevel"
                        } else {
                            SharedPreferences.Editor editor = save.edit();
                            editor.putInt("Level", getIntent().getExtras().getInt("preferencesValue"));//устанавливаем новый прогресс игры; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesValue"
                            editor.commit();
                        }
                        timerIsRunning = false;
                        timeComparisonForGoodBadResult = seconds;
                        if (timeComparisonForGoodBadResult < goodLevelLimitMax) {
                            //ratingBar.setRating(good);
                            AnimatorStars.likeAnimation(R.drawable.star3, star3);
                        } else if (timeComparisonForGoodBadResult > goodLevelLimitMax & timeComparisonForGoodBadResult < badLevelLimitMin) {
                            //ratingBar.setRating(passably);
                            AnimatorStars.likeAnimation(R.drawable.star2, star2);
                        } else if (timeComparisonForGoodBadResult > badLevelLimitMin) {
                            AnimatorStars.likeAnimation(R.drawable.star1, star1);
                            //ratingBar.setRating(bad);
                        }
                        //ObjectAnimator anim = ObjectAnimator.ofFloat(ratingBar, "rating", 0f, ratingBar.getRating());
                        //anim.setDuration((long) (ratingBar.getRating()) * 1000);
                        //anim.start();
                        dialogEnd.show();
                    } else {
                        numLeft = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));
                        img_left.setImageResource(getIntent().getExtras().getIntArray("img")[numLeft]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
                        img_left.startAnimation(a);
                        text_left.setText(getIntent().getExtras().getIntArray("text")[numLeft]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные
                        numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
                        while (compareArray[numLeft] == compareArray[numRight]) {
                            numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
                        }
                        img_right.setImageResource(getIntent().getExtras().getIntArray("img")[numRight]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
                        img_right.startAnimation(a);
                        text_right.setText(getIntent().getExtras().getIntArray("text")[numRight]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные
                        img_right.setEnabled(true);
                    }
                }
                return true;
            }
        });
        img_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    img_left.setEnabled(false);
                    if (compareArray[numLeft] < compareArray[numRight]) {
                        img_right.setImageResource(R.drawable.level0_img_true);
                    } else {
                        img_right.setImageResource(R.drawable.level0_img_false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (compareArray[numLeft] < compareArray[numRight]) {
                        if (count < 20) {
                            count++;
                        }
                        for (int i = 0; i < 20; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points_green);
                        }
                    } else {
                        if (count > 0) {
                            if (count == 1) {
                                count = 0;
                            } else {
                                count = count - 2;
                            }
                        }
                        for (int i = 0; i < 19; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progressArray[i]);
                            tv.setBackgroundResource(R.drawable.style_points_green);
                        }
                    }
                    if (count == 20) {
                        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
                        final int level = save.getInt("Level", getIntent().getExtras().getInt("preferencesDefault"));//устанавливаем стандартное значение для сохранения прогресса; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesDefault"
                        if (level > getIntent().getExtras().getInt("preferencesLevel")) {//сравниваем прогресс с номером текущего уровня; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesLevel"
                        } else {
                            SharedPreferences.Editor editor = save.edit();
                            editor.putInt("Level", getIntent().getExtras().getInt("preferencesValue"));//устанавливаем новый прогресс игры; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesValue"
                            editor.commit();
                        }
                        timerIsRunning = false;
                        timeComparisonForGoodBadResult = seconds;
                        if (timeComparisonForGoodBadResult < goodLevelLimitMax) {
                            //ratingBar.setRating(good);
                            AnimatorStars.likeAnimation(R.drawable.star3, star3);
                        } else if (timeComparisonForGoodBadResult > goodLevelLimitMax & timeComparisonForGoodBadResult < badLevelLimitMin) {
                            //ratingBar.setRating(passably);
                            AnimatorStars.likeAnimation(R.drawable.star2, star2);
                        } else if (timeComparisonForGoodBadResult > badLevelLimitMin) {
                            AnimatorStars.likeAnimation(R.drawable.star1, star1);
                            //ratingBar.setRating(bad);
                        }
                        //ObjectAnimator anim = ObjectAnimator.ofFloat(ratingBar, "rating", 0f, ratingBar.getRating());
                        //anim.setDuration((long) (ratingBar.getRating()) * 1000);
                        //anim.start();
                        dialogEnd.show();
                    } else {
                        numLeft = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));
                        img_left.setImageResource(getIntent().getExtras().getIntArray("img")[numLeft]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
                        img_left.startAnimation(a);
                        text_left.setText(getIntent().getExtras().getIntArray("text")[numLeft]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные

                        numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
                        //цикл с предусловием, проверяющий равенство чисел
                        while (compareArray[numLeft] == compareArray[numRight]) {
                            numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
                        }
                        img_right.setImageResource(getIntent().getExtras().getIntArray("img")[numRight]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
                        img_right.startAnimation(a);
                        text_right.setText(getIntent().getExtras().getIntArray("text")[numRight]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные

                        img_left.setEnabled(true);
                    }
                }
                return true;
            }
        });
    }

    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable); // Возобновление работы таймера
        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* Вы забыли написать строчку ниже и в вашем приложении появилась утечка памяти */
        handler.removeCallbacks(runnable);
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }
        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);
    }

    //системная кнопка "назад"
    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(Levels.this, GameLevels.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }
}
