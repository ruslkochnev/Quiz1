package hell.luc1fer.quiz;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Levels extends AppCompatActivity {
    HomeWatcher mHomeWatcher;

    Dialog dialog;
    Dialog dialogEnd;
    public int numLeft;
    public int numRight;
    Array array = new Array(); //создали новый объект из класса Array
    Random random = new Random(); //Для генерации случайных чисел
    public int count = 0;
    int[] l;

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal);
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

        //создаем переменную text_levels
        TextView text_levels = findViewById(R.id.text_levels);
        text_levels.setText(getIntent().getExtras().getString("text_levels"));//устанавливаем текст уровня; с помощью методов getIntent().getExtras() получаем значение  параметра по ключу "text_levels"
        text_levels.setTextColor(getIntent().getExtras().getInt("text_color"));//устанавливаем цвет текст уровня; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "text_color"

        final ImageView img_left = findViewById(R.id.img_left);
        img_left.setClipToOutline(true);
        final ImageView img_right = findViewById(R.id.img_right);
        img_right.setClipToOutline(true);

        final TextView text_left = findViewById(R.id.text_left);
        final TextView text_right = findViewById(R.id.text_right);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ImageView background = findViewById(R.id.background);
        background.setImageResource(getIntent().getExtras().getInt("background"));//устанавливаем фон уровня; с помощью методов getIntent().getExtras() получаем значение по ключу "background"


        //вызов диалогового окна
        dialog = new Dialog(this); //создаем новое диалоговое окно
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//скрываем заголовок
        dialog.setContentView(R.layout.previewdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//прозрачный фон окна
        dialog.setCancelable(false);


        //устанавливаем картинку в диалоговое окно
        ImageView previewimg = dialog.findViewById(R.id.previewimg);
        previewimg.setImageResource(getIntent().getExtras().getInt("previewImg"));//устанавливаем картинку диалогового окна; с помощью методов getIntent().getExtras() получаем значение по ключу "previewImg"

        //фон диалогового окна
        LinearLayout dialogfon = dialog.findViewById(R.id.dialogfon);
        dialogfon.setBackgroundResource(getIntent().getExtras().getInt("dialogFon"));//устанавливаем фон диалогового окна; с помощью методов getIntent().getExtras() получаем значение по ключу "dialogFon"

        //устанавливаем описание задания
        TextView textdecription = dialog.findViewById(R.id.textdescription);
        textdecription.setText(getIntent().getExtras().getString("levelDescription"));//устанавливаем описание уровня; с помощью методов getIntent().getExtras() получаем значение по ключу "levelDescription"


        //кнопка закрытия диалогового окна
        TextView btnclose = dialog.findViewById(R.id.btnclose);
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
        Button btncontinue = dialog.findViewById(R.id.btncontiue);
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();
        //_____________________________________
        //вызов диалогового окна в конце игры
        dialogEnd = new Dialog(this); //создаем новое диалоговое окно
        dialogEnd.requestWindowFeature(Window.FEATURE_NO_TITLE);//скрываем заголовок
        dialogEnd.setContentView(R.layout.dialogend);
        dialogEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//прозрачный фон окна
        dialogEnd.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialogEnd.setCancelable(false);

        LinearLayout dialogfonEnd = dialogEnd.findViewById(R.id.dialogfon);
        dialogfonEnd.setBackgroundResource(getIntent().getExtras().getInt("dialogFon"));//устанавливаем фон диалогового окна; с помощью методов getIntent().getExtras() получаем значение по ключу "dialogFon"

        //факт
        TextView textdescriptionEnd = dialogEnd.findViewById(R.id.textdescriptionend);
        textdescriptionEnd.setText(getIntent().getExtras().getString("levelDescriptionEnd"));//устанавливаем описание уровня; с помощью методов getIntent().getExtras() получаем значение по ключу "levelDescriptionEnd"


        Button btncontinue2 = dialogEnd.findViewById(R.id.btncontiue);
        btncontinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    Intent intent = new Intent(Levels.this, GameLevels.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                }

                dialogEnd.dismiss();

            }
        });

        //_____________________________________

        Button btn_back = findViewById(R.id.button_back);
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
            }
        });

        //массив для прогресса
        final int[] progress = {
                R.id.point1, R.id.point2, R.id.point3, R.id.point4, R.id.point5, R.id.point6,
                R.id.point7, R.id.point8, R.id.point9, R.id.point10, R.id.point11, R.id.point12,
                R.id.point13, R.id.point14, R.id.point15, R.id.point16, R.id.point17, R.id.point18,
                R.id.point19, R.id.point20,
        };

        //подключаем анимацию
        final Animation a = AnimationUtils.loadAnimation(Levels.this, R.anim.alpha);

        //генерируем числа и вытаскиваем картинки


        numLeft = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
        img_left.setImageResource(getIntent().getExtras().getIntArray("img")[numLeft]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
        text_left.setText(getIntent().getExtras().getIntArray("text")[numLeft]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные
        text_left.setTextColor(getIntent().getExtras().getInt("text_color"));//устанавливаем цвет текст подписей; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "text_color"

        numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"

        //для каждого уровня был создан массив strong, потому что иначе реализовать сравнение, подходящее под абсолютно любое задание не получилось
        l = getIntent().getExtras().getIntArray("L"); //устанавливаем массив сравнения; с помощью методов getIntent().getExtras() получаем значение по ключу "L"

        //цикл с предусловием, проверяющий равенство чисел

        while (l[numLeft] == l[numRight]) { //сравнение происходит путем взятия [numLeft] и [numRight] элементов массива l (он же array.strong) для текущего уровня
            numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
        }

        img_right.setImageResource(getIntent().getExtras().getIntArray("img")[numRight]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
        text_right.setText(getIntent().getExtras().getIntArray("text")[numRight]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные
        text_right.setTextColor(getIntent().getExtras().getInt("text_color"));//устанавливаем цвет текст подписей; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "text_color"

        //обрабатываем нажатие на левую картинку
        img_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //коснулся
                    img_right.setEnabled(false); //блокируем от нажатия вторую картинку
                    if (l[numLeft] > l[numRight]) {
                        img_left.setImageResource(R.drawable.img_true);
                    } else {
                        img_left.setImageResource(R.drawable.img_false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //отпустил
                    if (l[numLeft] > l[numRight]) {
                        if (count < 20) {
                            count++;
                        }

                        //закрашиваем прогресс серым цветом
                        for (int i = 0; i < 20; i++) {
                            TextView tv = findViewById(progress[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }

                        //определяем правильные и закрашиваем зеленым
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progress[i]);
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
                        //закрашиваем прогресс серым цветом
                        for (int i = 0; i < 19; i++) {
                            TextView tv = findViewById(progress[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }

                        //определяем правильные и закрашиваем зеленым
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progress[i]);
                            tv.setBackgroundResource(R.drawable.style_points_green);
                        }

                    }
                    if (count == 20) {
                        MainActivity.showInterstitial();
                        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
                        final int level = save.getInt("Level", getIntent().getExtras().getInt("preferencesDefault"));//устанавливаем стандартное значение для сохранения прогресса; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesDefault"
                        if (level > getIntent().getExtras().getInt("preferencesLevel")) {//сравниваем прогресс с номером текущего уровня; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesLevel"
                        } else {
                            SharedPreferences.Editor editor = save.edit();
                            editor.putInt("Level", getIntent().getExtras().getInt("preferencesValue"));//устанавливаем новый прогресс игры; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesValue"
                            editor.commit();
                        }
                        dialogEnd.show();

                    } else {
                        //генерируем числа и вытаскиваем картинки
                        numLeft = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));
                        img_left.setImageResource(getIntent().getExtras().getIntArray("img")[numLeft]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
                        img_left.startAnimation(a);
                        text_left.setText(getIntent().getExtras().getIntArray("text")[numLeft]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные

                        numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
                        //цикл с предусловием, проверяющий равенство чисел
                        while (l[numLeft] == l[numRight]) {
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


        //обрабатываем нажатие на правую картинку
        img_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //коснулся
                    img_left.setEnabled(false); //блокируем от нажатия вторую картинку
                    if (l[numLeft] < l[numRight]) {
                        img_right.setImageResource(R.drawable.img_true);
                    } else {
                        img_right.setImageResource(R.drawable.img_false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //отпустил
                    if (l[numLeft] < l[numRight]) {
                        if (count < 20) {
                            count++;
                        }

                        //закрашиваем прогресс серым цветом
                        for (int i = 0; i < 20; i++) {
                            TextView tv = findViewById(progress[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }

                        //определяем правильные и закрашиваем зеленым
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progress[i]);
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
                        //закрашиваем прогресс серым цветом
                        for (int i = 0; i < 19; i++) {
                            TextView tv = findViewById(progress[i]);
                            tv.setBackgroundResource(R.drawable.style_points);
                        }

                        //определяем правильные и закрашиваем зеленым
                        for (int i = 0; i < count; i++) {
                            TextView tv = findViewById(progress[i]);
                            tv.setBackgroundResource(R.drawable.style_points_green);
                        }

                    }
                    if (count == 20) {
                        MainActivity.showInterstitial();
                        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
                        final int level = save.getInt("Level", getIntent().getExtras().getInt("preferencesDefault"));//устанавливаем стандартное значение для сохранения прогресса; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesDefault"
                        if (level > getIntent().getExtras().getInt("preferencesLevel")) {//сравниваем прогресс с номером текущего уровня; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesLevel"
                        } else {
                            SharedPreferences.Editor editor = save.edit();
                            editor.putInt("Level", getIntent().getExtras().getInt("preferencesValue"));//устанавливаем новый прогресс игры; с помощью методов getIntent().getExtras() получаем значение параметра по ключу "preferencesValue"
                            editor.commit();
                        }
                        dialogEnd.show();

                    } else {
                        //генерируем числа и вытаскиваем картинки
                        numLeft = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));
                        img_left.setImageResource(getIntent().getExtras().getIntArray("img")[numLeft]);//устанавливаем массив картинок; с помощью методов getIntent().getExtras() получаем значение по ключу "img", т.к. массивы для каждого уровня разные
                        img_left.startAnimation(a);
                        text_left.setText(getIntent().getExtras().getIntArray("text")[numLeft]);//устанавливаем массив текста подписей; с помощью методов getIntent().getExtras() получаем значение по ключу "text", т.к. массивы для каждого уровня разные

                        numRight = random.nextInt(getIntent().getExtras().getInt("arrayElementsCount"));//устанавливаем размер массива; с помощью методов getIntent().getExtras() получаем значение по ключу "arrayElementsCount"
                        //цикл с предусловием, проверяющий равенство чисел
                        while (l[numLeft] == l[numRight]) {
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
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

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
        music.setClass(this,MusicService.class);
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
