package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.Random;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Utils;

/**
 * Created by noyan on 27.06.2016.
 * Nitra Games Ltd.
 */

public class MenuCanvas extends BaseCanvas {

    Canvas canvas;

    private Bitmap arkaplan, oynabuton;
    private int oynabutonx, oynabutony;

    //geçici
    Bitmap gemiresmi;
    int gemix, gemiy;
    int gemihizx, gemihizy;
    int aramakurtarmax, aramakurtarmay;
    Paint sonucrengi;
    int sonucx, sonucy, sonuc;
    double baslangic, suan;
    double titrebaslangic, titresuan;
    Random random;
    //Titreyen gemi
    int titreyenx, titreyeny, titrememiktari, kackeretitredi;
    //Titreyen gemi2
    Bitmap gemiresmi2;
    int gemi2x, gemi2y, gemi2hizx, gemi2hizy,
            kackeretitredi2, gemi2titremehizi;
    float gemi2aci;
    double titre2baslangic, titre2suan;


    public MenuCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        random = new Random();
        baslangic = System.currentTimeMillis();
        sonucrengi = new Paint();
        sonuc = 0;
        sonucx = 300;
        sonucy = 100;
        sonucrengi.setTextSize(100);//Punto
        sonucrengi.setTextAlign(Paint.Align.CENTER);//Hizala(ortala)
        sonucrengi.setTypeface(Typeface.DEFAULT_BOLD);//Yazı şekli(kalın yazı)
        sonucrengi.setARGB(255, 255, 0, 0);//renk saydam(255=değil(0 saydam)), red:255, green:0, blue:0

        arkaplanYukle();
        oynaButonYukle();
        //geçici
        gemiresmi = Utils.loadImage(root, "animasyon/ship_1_1.png");
        gemix = 300;
        gemiy = 1;
        gemihizx = 20;
        gemihizy = 20;
        //titre init variables
        titrebaslangic = System.currentTimeMillis();
        titreyenx = 1080 / 2 - gemiresmi.getWidth() / 2;
        titreyeny = 20;
        titrememiktari = 50;
        kackeretitredi = 0;

        //titre2
        gemiresmi2 = Utils.loadImage(root, "animasyon/ship_2_1.png");
        gemi2x = 10;
        gemi2y = 1920 / 2 - gemiresmi2.getHeight() / 2;
        gemi2hizx = 10;
        gemi2hizy = -10;
        gemi2titremehizi = 20;
        kackeretitredi2 = 0;
        gemi2aci = 235;
        titre2baslangic = System.currentTimeMillis();
    }

    private void arkaplanYukle() {
       arkaplan = Utils.loadImage(root, "menu_background2.jpg");
    }

    private void oynaButonYukle() {
        oynabuton = Utils.loadImage(root, "play.png");
        oynabutonx = 1080 / 2 - oynabuton.getWidth() / 2;
        oynabutony = 1920 / 2 - oynabuton.getHeight() / 2;
    }

    public void update() {
      suan = System.currentTimeMillis();
      titresuan = System.currentTimeMillis();
      titre2suan = System.currentTimeMillis();

      //Açılı gidiş
        if(gemi2x + gemiresmi2.getWidth() < 1080) {
            if(gemi2aci < 270) {
                gemi2x += gemi2hizx;
                gemi2y += gemi2hizy;
                gemi2aci += 0.75;
            } else {
                gemi2x += gemi2hizx;
                gemi2y -= gemi2hizy;
                gemi2aci += 0.75;
            }
        }

        titreyeny += 20;
        //titreyengemi2();
        titreGemiTitre();
        gizliDosyalar();
        yedek();

    }

    private void titreyengemi2() {
        // 1 sn geçti mi geçmedi mi
        if(Math.abs(titre2suan - titre2baslangic) < 1000) {
            return;
        }
        // titreme işlemine girecek.
        if(kackeretitredi2 < 8) {
            gemi2titremehizi *= (-1);
            gemi2y += gemi2titremehizi;
            kackeretitredi2 += 1;
        } else {
            kackeretitredi2 = 0;
            titre2baslangic = System.currentTimeMillis();
        }

    }

    private void titreGemiTitre() {
        if(Math.abs(titresuan - titrebaslangic) < 1000) {
            return;
        }
        if(kackeretitredi < 6) {
            titreyenx += titrememiktari;
            titrememiktari *= (-1);
            kackeretitredi ++;
        } else {
            titrebaslangic = System.currentTimeMillis();
            kackeretitredi = 0;
        }

    }

    private void yedek() {
        if(suan - baslangic >= 1000) {
            sonuc++;
            baslangic = suan;
            sonucrengi.setARGB(255,
                    random.nextInt(255),
                    0,
                    0);
        }
    }

    private void gizliDosyalar() {
        if((gemix + gemiresmi.getWidth() / 2) - aramakurtarmax < 0) {
            gemihizx = +20;
        } else {
            gemihizx = -20;
        }
        if((gemiy + gemiresmi.getHeight() / 2) - aramakurtarmay < 0) {
            gemihizy = +20;
        } else {
            gemihizy = -20;
        }
        if(Math.abs((gemix + gemiresmi.getWidth() / 2) - aramakurtarmax) > 10)
        gemix += gemihizx;
        if(Math.abs((gemiy + gemiresmi.getHeight() / 2) - aramakurtarmay) > 10)
        gemiy += gemihizy;
    }

    public void draw(Canvas canvas) {
        this.canvas = canvas;
        canvas.scale(getWidth() / 1080.0f, getHeight() / 1920.0f);
        arkaplanCiz();
        oynaButonCiz();
        canvas.drawBitmap(gemiresmi, gemix, gemiy, null);
        canvas.drawBitmap(gemiresmi, titreyenx, titreyeny, null);//bayrak
        canvas.save();
        canvas.rotate(gemi2aci,
                gemi2x + gemiresmi2.getWidth() / 2,
                gemi2y + gemiresmi2.getHeight() / 2);
        canvas.drawBitmap(gemiresmi2, gemi2x, gemi2y, null);//bayrak
        canvas.restore();
        canvas.drawText("" + sonuc, sonucx, sonucy, sonucrengi);
    }

    private void arkaplanCiz() {
        canvas.drawBitmap(arkaplan, 0, 0, null);
    }

    private void oynaButonCiz() {
        canvas.drawBitmap(oynabuton, oynabutonx, oynabutony, null);
    }

    public void keyPressed(int key) {
    }

    public void keyReleased(int key) {
    }

    public boolean backPressed() {
        return false;
    }

    public void touchDown(int x, int y, int id) {
        aramakurtarmax = x;
        aramakurtarmay = y;
        if(x > root.xOranla(oynabutonx)
        && x < root.xOranla(oynabutonx + oynabuton.getWidth())
        && y > root.yOranla(oynabutony)
        && y < root.yOranla(oynabutony + oynabuton.getHeight())) {
            root.sescalar.play(root.sesefektleri[root.SE_BUTON]); //buton ses efekti
            GameCanvas mc = new GameCanvas(root);
            root.canvasManager.setCurrentCanvas(mc);
        }

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
    }

    public void resume() {
    }

    public void reloadTextures() {
    }

    public void showNotify() {
    }

    public void hideNotify() {
    }

}
