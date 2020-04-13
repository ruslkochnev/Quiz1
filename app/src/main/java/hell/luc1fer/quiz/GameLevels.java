package hell.luc1fer.quiz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;


public class GameLevels extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    HomeWatcher mHomeWatcher;
    private Toast continueToast, notAvailable;
    Array array = new Array();
    int level = 0;
    BillingProcessor bp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelevels);

        //music
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

        //in-app purchase

        if(!BillingProcessor.isIabServiceAvailable(this)) {
            Toast.makeText(this, "In-app billing service is unavailable.", Toast.LENGTH_LONG).show();
        }

        bp = new BillingProcessor(this, getString(R.string.license), this);
        bp.initialize();


        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        level = save.getInt("Level", 1);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //возвращаемся в главное меню
        Button button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(GameLevels.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
            }
        });





        for (int i = 1; i < level; i++) {
            TextView tv = findViewById(array.x[i]);
            tv.setText("" + (i + 1));
            tv.setBackgroundResource(R.drawable.button_gamelevels);
        }


    }




    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appSupport50: //нажатие на помощь
                bp.purchase(this, "support1");
                break;
            case R.id.appSupport100: //нажатие на помощь
                bp.purchase(this, "support2");
                break;
            case R.id.appSupport500: //нажатие на помощь
                bp.purchase(this, "support3");
                break;
            case R.id.textView1: //1 уровень
                Intent level1 = new Intent(GameLevels.this, Levels.class);
                level1.putExtra("text_levels", getString(R.string.level1)); //задаем название уровня и создаем ключ "text_levels"
                level1.putExtra("text_color", getColor(R.color.white));//задаем цвет текста и создаем ключ "text_color"
                level1.putExtra("background", R.drawable.level1);//задаем фон уровня и создаем ключ "background"
                level1.putExtra("previewImg", R.drawable.previewimgone);//задаем картинку диалогового окна и создаем ключ "previewImg"
                level1.putExtra("dialogFon", R.drawable.previewbackgroundone);//задаем фон диалогового окна и создаем ключ "dialogFon"
                level1.putExtra("levelDescription", getString(R.string.levelone));//задаем описание уровня и создаем ключ "levelDescription"
                level1.putExtra("dialogFonEnd", R.drawable.previewbackgroundone);//задаем фон даилогового окна в конце уровня и создаем ключ "dialogFonEnd"
                level1.putExtra("levelDescriptionEnd", getString(R.string.leveloneEnd));//задаем текст диалогового окна в конце уровня и создаем ключ "levelDescriptionEnd"
                level1.putExtra("btn_back_style", R.drawable.button_stroke_white_press_blue);//задаем стиль кнопки "назад" и создаем ключ "btn_back_style"
                level1.putExtra("btn_back_text_color", getColor(R.color.white));//задаем цвет текста кнопки "назад" и создаем ключ "btn_back_text_color"
                level1.putExtra("arrayElementsCount", 10);//задаем размер массива и создаем ключ "arrayElementsCount"
                level1.putExtra("img", array.images1);//задаем массив картинок и создаем ключ "img"
                level1.putExtra("text", array.texts1);//задаем массив текста подписей и создаем ключ "text"
                level1.putExtra("L", array.strong1);//задаем массив сравнений и создаем ключ "L"
                level1.putExtra("preferencesDefault", 1); //присваиваем в настройках уровень по умолчанию создаем ключ "preferencesDefault"
                level1.putExtra("preferencesLevel", 1); //сравниваем с текущим уровнем и создаем ключ "preferencesLevel"
                level1.putExtra("preferencesValue", 2); //присваимваем значение [текущий уровень + 1] и создаем ключ "preferencesValue"
                startActivity(level1);
                finish();
                break;
            case R.id.textView2://2 уровень
                try {
                    if (level >= 2) {
                        Intent level2 = new Intent(GameLevels.this, Levels.class);
                        level2.putExtra("text_levels", getString(R.string.level2));
                        level2.putExtra("text_color", getColor(R.color.white));
                        level2.putExtra("background", R.drawable.level1);
                        level2.putExtra("previewImg", R.drawable.previewimgtwo);
                        level2.putExtra("dialogFon", R.drawable.previewbackgroundone);
                        level2.putExtra("levelDescription", getString(R.string.leveltwo));
                        level2.putExtra("dialogFonEnd", R.drawable.previewbackgroundone);
                        level2.putExtra("levelDescriptionEnd", getString(R.string.leveltwoEnd));
                        level2.putExtra("btn_back_style", R.drawable.button_stroke_white_press_blue);
                        level2.putExtra("btn_back_text_color", getColor(R.color.white));
                        level2.putExtra("arrayElementsCount", 10);
                        level2.putExtra("img", array.images2);
                        level2.putExtra("text", array.texts2);
                        level2.putExtra("L", array.strong2);
                        level2.putExtra("preferencesDefault", 1); //присваиваем в настройках уровень по умолчанию
                        level2.putExtra("preferencesLevel", 2); //сравниваем с текущим уровнем
                        level2.putExtra("preferencesValue", 3); //присваимваем значение [текущий уровень + 1]
                        startActivity(level2);
                        finish();
                    } else {
                        continueToast = Toast.makeText(getBaseContext(), R.string.toastMsgNotPassed, Toast.LENGTH_SHORT);
                        continueToast.show();
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.textView3://3 уровень
                try {
                    if (level >= 3) {
                        Intent level3 = new Intent(GameLevels.this, Levels.class);
                        level3.putExtra("text_levels", getString(R.string.level3));
                        level3.putExtra("text_color", getColor(R.color.black95));
                        level3.putExtra("background", R.drawable.level3);
                        level3.putExtra("previewImg", R.drawable.previewimg3);
                        level3.putExtra("dialogFon", R.drawable.previewbackground3);
                        level3.putExtra("levelDescription", getString(R.string.levelthree));
                        level3.putExtra("dialogFonEnd", R.drawable.previewbackground3);
                        level3.putExtra("levelDescriptionEnd", getString(R.string.levelthreeEnd));
                        level3.putExtra("btn_back_style", R.drawable.button_stroke_black95_press_white);
                        level3.putExtra("btn_back_text_color", getColor(R.color.black95));
                        level3.putExtra("arrayElementsCount", 21);
                        level3.putExtra("img", array.images3);
                        level3.putExtra("text", array.texts3);
                        level3.putExtra("L", array.strong3);
                        level3.putExtra("preferencesDefault", 1); //присваиваем в настройках уровень по умолчанию
                        level3.putExtra("preferencesLevel", 3); //текущий уровень
                        level3.putExtra("preferencesValue", 4); //присваимваем значение [текущий уровень + 1]
                        startActivity(level3);
                        finish();

                    } else {
                        continueToast = Toast.makeText(getBaseContext(), R.string.toastMsgNotPassed, Toast.LENGTH_SHORT);
                        continueToast.show();
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.textView4://4 уровень
                try {
                    if (level >= 4) {
                        Intent level4 = new Intent(GameLevels.this, Levels.class);
                        level4.putExtra("text_levels", getString(R.string.level4));
                        level4.putExtra("text_color", getColor(R.color.white));
                        level4.putExtra("background", R.drawable.level4);
                        level4.putExtra("previewImg", R.drawable.previewimg4);
                        level4.putExtra("dialogFon", R.drawable.previewbackground4);
                        level4.putExtra("levelDescription", getString(R.string.levelfour));
                        level4.putExtra("dialogFonEnd", R.drawable.previewbackground4);
                        level4.putExtra("levelDescriptionEnd", getString(R.string.levelfourEnd));
                        level4.putExtra("btn_back_style", R.drawable.button_stroke_white_press_blue);
                        level4.putExtra("btn_back_text_color", getColor(R.color.white));
                        level4.putExtra("arrayElementsCount", 10);
                        level4.putExtra("img", array.images4);
                        level4.putExtra("text", array.texts4);
                        level4.putExtra("L", array.strong4);
                        level4.putExtra("preferencesDefault", 1); //присваиваем в настройках уровень по умолчанию
                        level4.putExtra("preferencesLevel", 4); //сравниваем с текущим уровнем

                        //!!!!!!!!!!!!!!!!!!!!! ИЗМЕНИТЬ ЗНАЧЕНИЕ С 4 НА 5 ПРИ ДОБАВЛЕНИИ 5 УРОВНЯ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        level4.putExtra("preferencesValue", 4); //присваимваем значение [текущий уровень + 1]
                        startActivity(level4);
                        finish();
                    } else {
                        continueToast = Toast.makeText(getBaseContext(), R.string.toastMsgNotPassed, Toast.LENGTH_SHORT);
                        continueToast.show();
                    }
                } catch (Exception e) {
                }
                break;
           case R.id.textView5://5 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView6://6 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView7://7 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView8://8 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView9://9 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView10://10 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView11://11 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView12://12 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView13://13 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView14://14 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView15://15 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView16://16 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView17://17 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView18://18 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView19://19 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView20://20 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView21://21 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView22://22 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView23://23 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView24://24 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView25://25 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView26://26 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView27://27 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView28://28 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView29://29 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;
            case R.id.textView30://30 уровень
                notAvailable = Toast.makeText(getBaseContext(), R.string.level_not_available, Toast.LENGTH_SHORT);
                notAvailable.show();
                break;


        }
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
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

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

    //перепрограмируем софт-кнопку "назад"
    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(GameLevels.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

        Toast.makeText(this, "Thanks for Your donate. "+productId, Toast.LENGTH_LONG).show(); // Благодарим за пожертвование
        bp.consumePurchase(productId); // И сразу после успешного завершения покупки сразу реализуем приобретённый товар, чтобы его можно было купить снова.

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

        Log.d("LOG", "On Billing Error"+Integer.toString(errorCode)); // Пишем ошибку в лог

    }

    @Override
    public void onBillingInitialized() {
        Log.d("LOG", "On Billing Initialaized"); // Просто пишем в лог

    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this, "On Activity Result", Toast.LENGTH_LONG).show();
        Log.d("LOG", "On Activity Result");
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/


}
