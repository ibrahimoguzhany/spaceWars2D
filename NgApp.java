package com.ngdroidapp;

import android.graphics.Canvas;

import istanbul.gamelab.ngdroid.base.BaseActivity;
import istanbul.gamelab.ngdroid.core.AppManager;
import istanbul.gamelab.ngdroid.base.BaseApp;
import istanbul.gamelab.ngdroid.core.NgMediaPlayer;
import istanbul.gamelab.ngdroid.core.SoundManager;
import istanbul.gamelab.ngdroid.util.Log;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */

public class NgApp extends BaseApp {

    public NgApp(BaseActivity nitraBaseActivity, AppManager appManager) {
        super(nitraBaseActivity, appManager);
    }
    //3-) Ses efektleri indexlerine etiket
    int SE_BUTON = 0, SE_ATES = 1;

    NgMediaPlayer oyunmuzik;

    //1-) Ses efekti create
    SoundManager sescalar;
    int sesefektleri[]; // ses havuzu

    public void setup() {
        appManager.setUnitResolution(AppManager.RESOLUTION_FULLHD);
        appManager.setFrameRate(20);

        oyunmuzik = new NgMediaPlayer(this);
        oyunmuzik.load("sounds/game_music.mp3");
        //2-) Sesleri yükle ve aktifet.
        sescalar = new SoundManager(this);
        sesefektleri = new int[2];
        //4-) Ses efektlerini yükle
        try {
            sesefektleri[SE_BUTON] = sescalar.load("sounds/click.wav");
            sesefektleri[SE_ATES] = sescalar.load("sounds/dusmanates.wav");
        }catch (Exception e) {

        }

        MenuCanvas mc = new MenuCanvas(this);
        canvasManager.setCurrentCanvas(mc);
    }


    public void update() {

    }

    public void draw(Canvas canvas) {

    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public float xOranla(int x) {
        return x * (getWidth() / 1080.0f);
    }

    public float yOranla(int y) {
        return y * (getHeight() / 1920.0f);
    }

    public boolean backPressed() {
        return true;
    }

    public void touchDown(int x, int y, int id) {

    }

    public void touchMove(int x, int y, int id) {

    }

    public void touchUp(int x, int y, int id) {

    }

    public void surfaceChanged(int width, int height) {

    }

    public void surfaceCreated() {

    }

    public void surfaceDestroyed() {

    }

    public void pause() {
        Log.i("NGAPP", "pause");
    }

    public void resume() {
        Log.i("NGAPP", "resume");
    }

    public void reloadTextures() {
        Log.i("NGAPP", "reloadTextures");
    }
}
