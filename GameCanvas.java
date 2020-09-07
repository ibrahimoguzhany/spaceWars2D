package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;//ekle
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Vector;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {

    private int OGEMI_TUR = 0, OGEMI_FRAME = 1, OGEMI_X = 2, OGEMI_Y = 3,
            OGEMI_W = 4, OGEMI_H = 5,OGEMI_SALDIRI = 6, OGEMI_HIZ = 7, OGEMI_YOL = 8,
            OGEMI_MX = 9, OGEMI_MY = 10, OGEMI_MERMI = 11, OGEMI_RELOAD = 12,
            OGEMI_PUAN = 13, OGEMI_CAN = 14;
    private int DGEMI_TUR = 0, DGEMI_FRAME = 1, DGEMI_X = 2, DGEMI_Y = 3,
                DGEMI_W = 4, DGEMI_H = 5,DGEMI_SALDIRI = 6, DGEMI_HIZ = 7, DGEMI_YOL = 8,
                DGEMI_MX = 9, DGEMI_MY = 10, DGEMI_MERMI = 11, DGEMI_RELOAD = 12,
                DGEMI_PUAN = 13, DGEMI_CAN = 14;
    private int OMERMI_TUR = 0, OMERMI_X = 1, OMERMI_Y = 2, OMERMI_W = 3,
            OMERMI_H = 4, OMERMI_HIZ = 5, OMERMI_YOL = 6, OMERMI_HASAR = 7;
    private int DMERMI_TUR = 0, DMERMI_X = 1, DMERMI_Y = 2, DMERMI_W = 3,
            DMERMI_H = 4, DMERMI_HIZ = 5, DMERMI_YOL = 6, DMERMI_HASAR = 7;
    private int PATLAMA_FRAME = 0, PATLAMA_X = 1, PATLAMA_Y = 2;
    //Arkaplan
    private Bitmap oyunarkaplan;
    //Ortak canvas
    private Canvas canvas;

    // Oyun bitti penceresi
    Bitmap yenidenbutonu, menuyedonbutonu, oyunsonuektani[];
    int yenidenbutonux, yenidenbutonuy, menuyedonbutonux, menuyedonbutonuy,
            oyunsonuekranix, oyunsonuekraniy;
    boolean oyunbitti, oyundevamediyor, kazandi, kaybettik;

    // Oyuncu Platformlar
    Bitmap platformresimleri;
    int oyuncuplatformx[], oyuncuplatformy[];
    int secilenplatform;

    //Düşman platformlar
    int dusmanplatformx[], dusmanplatformy[];
    int dusmansecilenplatform;//?
    //Oyuncu Gemi
    Bitmap gemiresimleri[][];
    int gemicesidi, gemianimframesayisi[];
    Vector<Vector<Integer>> oyuncugemilistesi; //gemi listesi
    Vector<Integer> oyuncuyenigemi; //bu artık gemi



    //Düşman Gemi
    Vector<Vector<Integer>> dusmangemilistesi;//dusman gemi listesi
    Vector<Integer> dusmanyenigemi; // dusman gemisi(kendisi)
    private double dusmanuretbaslangic, dusmanuretsuanki;

    //Platform animasyonu(seçilen platformdaki mavi ekran animasyonu)
    Bitmap platanimasyon[];
    int platanimframesayisi;
    int platanimkonumx, platanimkonumy, platanimsuankiframe;
    boolean animasyonciz;

    //Mermiler
    Vector<Vector<Integer>> oyuncumermilistesi;
    Vector<Integer> yenioyuncumermi;
    Bitmap mermiresimleri[];
    int mermicesitleri;
    //Düşman mermi
    Vector<Vector<Integer>> dusmanmermilistesi;
    Vector<Integer> yenidusmanmermi;

    // gemi seçimi
    private int gemisecx[], gemisecy[], secimalanix, secimalaniy;

    //Patlama efekti
    private Bitmap patlamaresmi;
    private Rect patlamakaynak[], patlamahedef;
    private int  patlamaframesayisi;
    Vector<Vector<Integer>> patlamalistesi;
    Vector<Integer> yenipatlama;

    //puan sstemi
    Paint puanrengi;
    int puanx, puany, puan;
    int gemipuanlari[];
    //Can sistemi
    int gemicanpuanlari[];
    //Can barları
    Bitmap canbarresimleri[];
    Rect canbarkaynak, canbarhedef;
    int canbariresimsayisi;



    //Geçici
    Paint yazirengi;
    int sayac = 0;
    Rect kontrol1, kontrol2;


    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        root.oyunmuzik.prepare();
        root.oyunmuzik.start();
        dusmanuretbaslangic = System.currentTimeMillis();
        oyunbitti = false;
        oyundevamediyor = true;
        kontrol1 = new Rect();
        kontrol2 = new Rect();
        yazirengi = new Paint();
        yazirengi.setTextSize(40);//Punto
        yazirengi.setTextAlign(Paint.Align.LEFT);//Hizala(sola)
        yazirengi.setTypeface(Typeface.DEFAULT_BOLD);//Yazı şekli(kalın yazı)
        yazirengi.setARGB(255, 255, 0, 0);//renk saydam(255=değil(0 saydam)), red:255, green:0, blue:0
        LOGI("setup");
        arkaplanYukle();
        platformYukle();
        gemilerYukle();

        mermiYukle();
        gemiSecimAyari();
        patlamaEfektiYukle();
        puanSistemi();
        oyunSonuYukle();
    }

    private void oyunSonuYukle() {
        oyunsonuektani = new Bitmap[2];
        oyunsonuektani[0] = Utils.loadImage(root, "bitti/you_win.png");
        oyunsonuektani[1] = Utils.loadImage(root, "bitti/you_lose.png");
        oyunsonuekranix = 1080 / 2 - oyunsonuektani[0].getWidth() / 2;
        oyunsonuekraniy = 1920 / 2 - oyunsonuektani[0].getHeight() / 2;
        yenidenbutonu = Utils.loadImage(root, "bitti/bitti_yeniden.png");
        yenidenbutonux = oyunsonuekranix - 100;
        yenidenbutonuy = oyunsonuekraniy + oyunsonuektani[0].getHeight();
        menuyedonbutonu = Utils.loadImage(root, "bitti/bitti_geri.png");
        menuyedonbutonux = yenidenbutonux + yenidenbutonu.getWidth();
        menuyedonbutonuy = yenidenbutonuy;
    }

    private void puanSistemi() {
        puan = 0;
        puanrengi = new Paint();
        puanrengi.setTextSize(40);//Punto
        puanrengi.setTextAlign(Paint.Align.RIGHT);//Hizala(sola)
        puanrengi.setTypeface(Typeface.DEFAULT_BOLD);//Yazı şekli(kalın yazı)
        puanrengi.setARGB(255, 200, 200, 0);
        puanx = 900;
        puany = 50;

    }

    private void patlamaEfektiYukle() {
        patlamalistesi = new Vector<Vector<Integer>>();
        patlamaframesayisi = 16;
        patlamakaynak = new Rect[patlamaframesayisi];
        patlamahedef = new Rect();
        patlamaresmi = Utils.loadImage(root, "explosion.png");
        for (int pey_i = 0; pey_i < patlamaframesayisi; pey_i++) {
            patlamakaynak[pey_i] = new Rect((pey_i % 4) * 64,
                    (pey_i / 4) * 64,
                    ((pey_i % 4) + 1) * 64,
                    ((pey_i / 4) + 1) * 64);
        }
    }

    private void gemiSecimAyari() {
        int x = oyuncuplatformx[secilenplatform]
                + ((270 - gemiresimleri[0][0].getWidth()) / 2);
        gemisecx = new int[4];
        gemisecx[0] = 15 + (240 - gemiresimleri[0][0].getWidth()) / 2;
        gemisecx[1] = 285+ (240 - gemiresimleri[1][0].getWidth()) / 2;
        gemisecx[2] = 555+ (240 - gemiresimleri[2][0].getWidth()) / 2;
        gemisecx[3] = 825+ (240 - gemiresimleri[3][0].getWidth()) / 2;
        gemisecy = new int[4];
        gemisecy[0] = 1650 + (190 - gemiresimleri[0][0].getHeight()) / 2;
        gemisecy[1] = 1650 + (190 - gemiresimleri[1][0].getHeight()) / 2;
        gemisecy[2] = 1650 + (190 - gemiresimleri[2][0].getHeight()) / 2;
        gemisecy[3] = 1650 + (190 - gemiresimleri[3][0].getHeight()) / 2;
        secimalanix = 240;
        secimalaniy = 190;
    }

    private void mermiYukle() {
        oyuncumermilistesi = new Vector<Vector<Integer>>();
        dusmanmermilistesi = new Vector<Vector<Integer>>();
        mermicesitleri = 2;
        mermiresimleri = new Bitmap[mermicesitleri];
        mermiresimleri[0] = Utils.loadImage(root, "mavi_ates.png");
        mermiresimleri[1] = Utils.loadImage(root, "kirmizi_ates.png");

    }
    
    private void dMermiUret(int tur, int x, int y, int yol) {
        yenidusmanmermi = new Vector<Integer>();
        yenidusmanmermi.add(tur); //tür
        yenidusmanmermi.add(x - mermiresimleri[tur].getWidth() / 2); //x
        yenidusmanmermi.add(y - mermiresimleri[tur].getHeight() / 2); //y
        yenidusmanmermi.add(mermiresimleri[tur].getWidth()); //w
        yenidusmanmermi.add(mermiresimleri[tur].getHeight()); //h
        yenidusmanmermi.add(20); //hız
        yenidusmanmermi.add(yol); //Yol
        yenidusmanmermi.add(5); //hasar
        dusmanmermilistesi.add(yenidusmanmermi); //dMermi eklendi.
        root.sescalar.play(root.sesefektleri[root.SE_ATES]); //ateş ses efekti
    }

    private void oMermiUret(int tur, int x, int y, int yol) {
        yenioyuncumermi = new Vector<Integer>();
        yenioyuncumermi.add(tur); //tür
        yenioyuncumermi.add(x - mermiresimleri[tur].getWidth() / 2); //x
        yenioyuncumermi.add(y - mermiresimleri[tur].getHeight() / 2); //y
        yenioyuncumermi.add(mermiresimleri[tur].getWidth()); //w
        yenioyuncumermi.add(mermiresimleri[tur].getHeight()); //h
        yenioyuncumermi.add(20); //hız
        yenioyuncumermi.add(yol); //Yol
        yenioyuncumermi.add(5); //Hasar
        oyuncumermilistesi.add(yenioyuncumermi);//mermi eklendi.
        root.sescalar.play(root.sesefektleri[root.SE_ATES]); //ateş ses efekti
    }

    private void oyuncuGemiUret(int gemituru, int secilenplatform) {
        oyuncuyenigemi = new Vector<Integer>();
        int x = oyuncuplatformx[secilenplatform]
                + ((255 - gemiresimleri[gemituru][0].getWidth()) / 2);
        oyuncuyenigemi.add(gemituru); // tür (index:0) 0 = OGEMI_TUR
        oyuncuyenigemi.add(0); // frame (index:1)
        oyuncuyenigemi.add(x); // x (yatay) (index:2)
        oyuncuyenigemi.add(oyuncuplatformy[secilenplatform]); // y (dikey) (index:3)
        oyuncuyenigemi.add(gemiresimleri[gemituru][0].getWidth()); // w (genişlik) (index:4)
        oyuncuyenigemi.add(gemiresimleri[gemituru][0].getHeight()); // h (yükseklik) (index:5)
        oyuncuyenigemi.add(0); //Saldırı modu
        oyuncuyenigemi.add(10); // hız (index:7)
        oyuncuyenigemi.add(secilenplatform);//Seçilen yol (index:8)
        oyuncuyenigemi.add(x + gemiresimleri[gemituru][0].getWidth() / 2);//yatay merkez
        oyuncuyenigemi.add(oyuncuplatformy[secilenplatform] + gemiresimleri[gemituru][0].getHeight() / 2);//dikey merkez
        oyuncuyenigemi.add(0);//mermi çeşidi
        oyuncuyenigemi.add(60);//reload süresi
        oyuncuyenigemi.add(gemipuanlari[gemituru]);//puan sistemi
        oyuncuyenigemi.add(gemicanpuanlari[gemituru]);//can miktarı
        oyuncugemilistesi.add(oyuncuyenigemi); // gemi listeye eklendi.

    }

    private void dusmanGemiUret() {
        if(Math.abs(dusmanuretbaslangic - dusmanuretsuanki) < 2000) {
            return;
        }

        int fikir = getHeight();
        int fikirGemiNo = 0;
        int fikirGemiIndex = 0;
        dusmansecilenplatform = (int)(Math.random() * 2);
        int dusmangemituru = (int)(Math.random() * gemicesidi);
        for (int i = 0; i < oyuncugemilistesi.size(); i++) {
            if(oyuncugemilistesi.get(i).get(OGEMI_Y) < getHeight()){
                if (oyuncugemilistesi.get(i).get(OGEMI_Y) < fikir ){
                    fikir = oyuncugemilistesi.get(i).get(OGEMI_Y);
                    fikirGemiNo = oyuncugemilistesi.get(i).get(OGEMI_TUR);
                    fikirGemiIndex = i;
                }
            }
            dusmansecilenplatform = oyuncugemilistesi.get(fikirGemiIndex).get(OGEMI_YOL);
            dusmangemituru = fikirGemiNo;
        }

        dusmanyenigemi = new Vector<Integer>();
        int x = dusmanplatformx[dusmansecilenplatform]
        + ((255 - gemiresimleri[dusmangemituru][0].getWidth()) / 2);
        dusmanyenigemi.add(dusmangemituru); //tür
        dusmanyenigemi.add(0); //frame
        dusmanyenigemi.add(x); //x
        dusmanyenigemi.add(dusmanplatformy[dusmansecilenplatform]); //y
        dusmanyenigemi.add(gemiresimleri[dusmangemituru][0].getWidth()); //w
        dusmanyenigemi.add(gemiresimleri[dusmangemituru][0].getHeight()); //h
        dusmanyenigemi.add(0); // Saldırı modu.
        dusmanyenigemi.add(10);//hız
        dusmanyenigemi.add(dusmansecilenplatform); //Seçilen yol
        dusmanyenigemi.add(x + gemiresimleri[dusmangemituru][0].getWidth() / 2); //merkez yatay
        dusmanyenigemi.add(dusmanplatformy[dusmansecilenplatform]
                + gemiresimleri[dusmangemituru][0].getHeight() / 2); //merkez dikey
        dusmanyenigemi.add(1);//mermi çeşidi
        dusmanyenigemi.add(60);//reload süresi
        dusmanyenigemi.add(gemipuanlari[dusmangemituru]);//puan değeri
        dusmanyenigemi.add(gemicanpuanlari[dusmangemituru]);//can değeri
        dusmangemilistesi.add(dusmanyenigemi);//Eklendi.
        dusmanuretbaslangic = System.currentTimeMillis();
    }

    private void gemilerYukle() {
        oyuncugemilistesi = new Vector<Vector<Integer>>();
        dusmangemilistesi = new Vector<Vector<Integer>>();
        gemicesidi = 4;

        gemianimframesayisi = new int[gemicesidi];
        gemianimframesayisi[0] = 25;
        gemianimframesayisi[1] = 16;
        gemianimframesayisi[2] = 25;
        gemianimframesayisi[3] = 25;
        gemiresimleri = new Bitmap[gemicesidi][25];
        for (int gemi = 0; gemi < gemicesidi; gemi++) {
            for (int frame = 0; frame < 25; frame++) {
                gemiresimleri[gemi][frame] =
                Utils.loadImage(root, "animasyon/ship_" + (gemi + 1) + "_" + (frame + 1) + ".png");
            }
        }
        //gemilerin puanlari
        gemipuanlari = new int[gemicesidi];
        gemipuanlari[0] = 10;
        gemipuanlari[1] = 20;
        gemipuanlari[2] = 30;
        gemipuanlari[3] = 50;

        //gemi canları
        gemicanpuanlari = new int[gemicesidi];
        gemicanpuanlari[0] = 10;
        gemicanpuanlari[1] = 30;
        gemicanpuanlari[2] = 50;
        gemicanpuanlari[3] = 80;
        //canBariYukle
        canbariresimsayisi = 4;
        canbarresimleri = new Bitmap[canbariresimsayisi];
        canbarresimleri[0] = Utils.loadImage(root, "arkaplan_hp_03.png");
        canbarresimleri[1] = Utils.loadImage(root, "platform_turuncu.png");
        canbarresimleri[2] = Utils.loadImage(root, "platform_sari.png");
        canbarresimleri[3] = Utils.loadImage(root, "platform_kirmizi.png");
        canbarkaynak = new Rect();
        canbarhedef = new Rect();
    }

    private void platformYukle() {
        //Oyuncu platform yüklemesi
        secilenplatform = 0;
        oyuncuplatformx = new int[2];
        oyuncuplatformy = new int[2];
        platformresimleri = Utils.loadImage(root, "silahli_gemi1.png");
        oyuncuplatformx[0] = 145;
        oyuncuplatformy[0] = 1300;
        oyuncuplatformx[1] = 145 + 514;
        oyuncuplatformy[1] = 1300;
        //Düşman platform yüklemesi
        dusmanplatformx = new int[2];
        dusmanplatformy = new int[2];
        dusmanplatformx[0] = 145;
        dusmanplatformx[1] = 145 + 514;
        dusmanplatformy[0] = 80;
        dusmanplatformy[1] = 80;
        platAnimYukle();
    }

    private void platAnimYukle() {
        //frame miktarı
        platanimframesayisi = 25;
        platanimsuankiframe = 0;
        animasyonciz = true;
        //frame yüklemesi
        platanimasyon = new Bitmap[platanimframesayisi];
        for (int frame = 0; frame < platanimframesayisi; frame++) {
            platanimasyon[frame] = Utils.loadImage(root,
                    "animasyon/platform_" + frame + ".png");
        }
        platanimkonumx = oyuncuplatformx[secilenplatform];
        platanimkonumy = oyuncuplatformy[secilenplatform];
    }


    private void arkaplanYukle() {
        oyunarkaplan = Utils.loadImage(root, "map.png");
    }

    public void update() {
        //LOGI("update");
        if(oyunbitti == false && oyundevamediyor == true) {
            dusmanuretsuanki = System.currentTimeMillis();
            dusmanGemiUret();
            oyuncuGemiIlerlet();
            dusmanGemiIlerlet();
            carpismaKontrolleri();
            mermiUretecekMi();
            oMermiIlerlet();
            dMermiIlerlet();
        }

    }

    public void draw(Canvas canvas) {
        //LOGI("draw");
        this.canvas = canvas;
        arkaplanCiz();
        oyuncuPlatformCiz();
        dusmanPlatformCiz();
        secimAlaniCiz();
        oyuncuGemiCiz();
        dusmanGemiCiz();
        oyuncumermiCiz();
        dusmanMermiCiz();
        patlamaCiz();
        puanCiz();
        for (int i = 0; i < oyuncugemilistesi.size(); i++) {
            ayrintiCiz(i);
            canbariCiz(i);
        }
        for (int i = 0; i < dusmangemilistesi.size(); i++) {
            dusmanayrintiCiz(i);
        }
        oyunSonuEkraniCiz();
    }

    private void oyunSonuEkraniCiz() {
        if(!oyunbitti)return;
        if(kazandi) {
            oyunsonuekranix = 1080 / 2 - oyunsonuektani[0].getWidth() / 2;
            oyunsonuekraniy = 1920 / 2 - oyunsonuektani[0].getHeight() / 2;
            yenidenbutonux = oyunsonuekranix + 70;
            yenidenbutonuy = oyunsonuekraniy + oyunsonuektani[0].getHeight();
            menuyedonbutonux = yenidenbutonux + yenidenbutonu.getWidth();
            menuyedonbutonuy = yenidenbutonuy;
            canvas.drawBitmap(oyunsonuektani[0], oyunsonuekranix, oyunsonuekraniy, null);

        }
        if(kaybettik) {
            oyunsonuekranix = 1080 / 2 - oyunsonuektani[1].getWidth() / 2;
            oyunsonuekraniy = 1920 / 2 - oyunsonuektani[1].getHeight() / 2;
            yenidenbutonux = oyunsonuekranix + 180;
            yenidenbutonuy = oyunsonuekraniy + oyunsonuektani[1].getHeight();
            menuyedonbutonux = yenidenbutonux + yenidenbutonu.getWidth();
            menuyedonbutonuy = yenidenbutonuy;
            canvas.drawBitmap(oyunsonuektani[1],
                    oyunsonuekranix, oyunsonuekraniy, null);
        }
        canvas.drawBitmap(yenidenbutonu, yenidenbutonux,
                yenidenbutonuy, null);
        canvas.drawBitmap(menuyedonbutonu, menuyedonbutonux,
                menuyedonbutonuy, null);
    }

    private void canbariCiz(int cbc_i) {

        //Canlar
        int canrengi;
        int canrengigen;
        for (int cbc_o = 0; cbc_o < oyuncugemilistesi.size(); cbc_o++) {
            if (gemicanpuanlari[oyuncugemilistesi.get(cbc_o).get(OGEMI_TUR)] -
                    oyuncugemilistesi.get(cbc_o).get(OGEMI_CAN) < 40) {
                canrengi = 2;
            } else {
                canrengi = 1;
            }
            canrengigen = (oyuncugemilistesi.get(cbc_i).get(OGEMI_X) + 200)
                    / gemicanpuanlari[oyuncugemilistesi.get(cbc_o).get(OGEMI_TUR)]
                    * oyuncugemilistesi.get(cbc_o).get(OGEMI_CAN);
            canbarkaynak.set(0, 0,
                    canbarresimleri[canrengi].getWidth(),
                    canbarresimleri[canrengi].getHeight());
            canbarhedef.set(oyuncugemilistesi.get(cbc_i).get(OGEMI_X) + 10,
                    oyuncugemilistesi.get(cbc_i).get(OGEMI_Y) +
                            gemiresimleri[oyuncugemilistesi.get(cbc_i).get(OGEMI_TUR)][oyuncugemilistesi.get(cbc_i).get(OGEMI_FRAME)].getHeight(),
                    oyuncugemilistesi.get(cbc_i).get(OGEMI_X) + canrengigen,
                    oyuncugemilistesi.get(cbc_i).get(OGEMI_Y) +
                            gemiresimleri[oyuncugemilistesi.get(cbc_i).get(OGEMI_TUR)]
                                    [oyuncugemilistesi.get(cbc_i).get(OGEMI_FRAME)].getHeight() +
                            canbarresimleri[0].getHeight() / 2);
            canvas.drawBitmap(canbarresimleri[canrengi], canbarkaynak, canbarhedef, null);
        }
        //Çerçeve
        canbarkaynak.set(0, 0,
                canbarresimleri[0].getWidth(), canbarresimleri[0].getHeight());
        canbarhedef.set(oyuncugemilistesi.get(cbc_i).get(OGEMI_X) + 10,
                oyuncugemilistesi.get(cbc_i).get(OGEMI_Y) +
                        gemiresimleri[oyuncugemilistesi.get(cbc_i).get(OGEMI_TUR)][oyuncugemilistesi.get(cbc_i).get(OGEMI_FRAME)].getHeight(),
                oyuncugemilistesi.get(cbc_i).get(OGEMI_X) + 200,
                oyuncugemilistesi.get(cbc_i).get(OGEMI_Y) +
                        gemiresimleri[oyuncugemilistesi.get(cbc_i).get(OGEMI_TUR)][oyuncugemilistesi.get(cbc_i).get(OGEMI_FRAME)].getHeight() +
                        canbarresimleri[0].getHeight() / 2);
        canvas.drawBitmap(canbarresimleri[0], canbarkaynak, canbarhedef, null);

    }


    private void dMermiIlerlet() {
        for (int dmi_i = 0; dmi_i < dusmanmermilistesi.size(); dmi_i++) {
            if(dusmanmermilistesi.get(dmi_i).get(DMERMI_Y) < getHeight()) {
                dusmanmermilistesi.get(dmi_i).set(DMERMI_Y,
                        dusmanmermilistesi.get(dmi_i).get(DMERMI_Y) +
                                dusmanmermilistesi.get(dmi_i).get(DMERMI_HIZ));
            } else {
                dusmanmermilistesi.remove(dmi_i);
            }
        }
    }

    private void oMermiIlerlet() {
        for (int omi_i = 0; omi_i < oyuncumermilistesi.size(); omi_i++) {
            if(oyuncumermilistesi.get(omi_i).get(OMERMI_Y) > 0) {
                oyuncumermilistesi.get(omi_i).set(OMERMI_Y,
                        oyuncumermilistesi.get(omi_i).get(OMERMI_Y) -
                                oyuncumermilistesi.get(omi_i).get(OMERMI_HIZ));
            } else {
                oyuncumermilistesi.remove(omi_i);
            }
        }
    }

    private void mermiUretecekMi() {


    }
    private void puanCiz() {
        canvas.drawText("Puan: " + puan, puanx, puany, puanrengi);
    }

    private void patlamaCiz() {
        for (int pc_i = 0; pc_i < patlamalistesi.size(); pc_i++) {
            patlamahedef.set(patlamalistesi.get(pc_i).get(PATLAMA_X),
                    patlamalistesi.get(pc_i).get(PATLAMA_Y),
                    patlamalistesi.get(pc_i).get(PATLAMA_X) + 250,
                    patlamalistesi.get(pc_i).get(PATLAMA_Y) + 250);
            canvas.drawBitmap(patlamaresmi,
                    patlamakaynak[patlamalistesi.get(pc_i).get(PATLAMA_FRAME)],
                    patlamahedef, null);
            //Frame ilerlet!!!
            if(patlamalistesi.get(pc_i).get(PATLAMA_FRAME) < patlamaframesayisi - 1) {
                patlamalistesi.get(pc_i).set(PATLAMA_FRAME,
                        patlamalistesi.get(pc_i).get(PATLAMA_FRAME) + 1);
            } else {
                patlamalistesi.remove(pc_i);
            }
        }

    }

    private void secimAlaniCiz() {
        for (int sac_i = 0; sac_i < 4; sac_i++) {
            canvas.drawBitmap(gemiresimleri[sac_i][0],
                              gemisecx[sac_i],
                              gemisecy[sac_i],
                              null);
        }
    }

    private void dusmanMermiCiz() {
        for (int dmc_i = 0; dmc_i < dusmanmermilistesi.size(); dmc_i++) {
            canvas.drawBitmap(mermiresimleri[dusmanmermilistesi.get(dmc_i).get(DMERMI_TUR)],
                    dusmanmermilistesi.get(dmc_i).get(DMERMI_X),
                    dusmanmermilistesi.get(dmc_i).get(DMERMI_Y), null);
        }
    }

    private void oyuncumermiCiz() {
        for (int omc_i = 0; omc_i < oyuncumermilistesi.size(); omc_i++) {
            canvas.drawBitmap(mermiresimleri[oyuncumermilistesi.get(omc_i).get(OMERMI_TUR)],
                    oyuncumermilistesi.get(omc_i).get(OMERMI_X),
                    oyuncumermilistesi.get(omc_i).get(OMERMI_Y),
                    null);
        }
    }

    private void carpismaKontrolleri() {
        oyuncuGemisiSaldiriKontrolu();
        dusmanGemisiSaldiriKontrolu();
        oyuncuMermiVurduMu();
        dusmanMermiVurduMu();

        /*
        for (int o = 0; o < oyuncugemilistesi.size(); o++) {
            for (int d = 0; d < dusmangemilistesi.size(); d++) {
                moderenCarpisma(o, d);
            }
        }
        */
    }

    private void dusmanMermiVurduMu() {
        for (int dmv_i = 0; dmv_i < dusmanmermilistesi.size(); dmv_i++) {
            for (int dmv_d = 0; dmv_d < oyuncugemilistesi.size(); dmv_d++) {
                        kontrol1.set(dusmanmermilistesi.get(dmv_i).get(DMERMI_X),
                                dusmanmermilistesi.get(dmv_i).get(DMERMI_Y),
                                dusmanmermilistesi.get(dmv_i).get(DMERMI_X)
                               +dusmanmermilistesi.get(dmv_i).get(DMERMI_W),
                                dusmanmermilistesi.get(dmv_i).get(DMERMI_Y)
                               +dusmanmermilistesi.get(dmv_i).get(DMERMI_H));
                        kontrol2.set(oyuncugemilistesi.get(dmv_d).get(OGEMI_X),
                                oyuncugemilistesi.get(dmv_d).get(OGEMI_Y),
                                oyuncugemilistesi.get(dmv_d).get(OGEMI_X)
                               +oyuncugemilistesi.get(dmv_d).get(OGEMI_W),
                                oyuncugemilistesi.get(dmv_d).get(OGEMI_Y)
                               +oyuncugemilistesi.get(dmv_d).get(OGEMI_H));
                        if(kontrol1.intersect(kontrol2)) {
                            oyuncugemilistesi.get(dmv_d).set(OGEMI_CAN,
                                    oyuncugemilistesi.get(dmv_d).get(OGEMI_CAN)
                            - dusmanmermilistesi.get(dmv_i).get(DMERMI_HASAR));
                            dusmanmermilistesi.remove(dmv_i);
                            if(oyuncugemilistesi.get(dmv_d).get(OGEMI_CAN) <= 0) {
                                patlamaUret(oyuncugemilistesi.get(dmv_d).get(OGEMI_X), oyuncugemilistesi.get(dmv_d).get(OGEMI_Y));
                                oyuncugemilistesi.remove(dmv_d);
                            }
                            break;
                        }
            }
        }
    }

    private void oyuncuMermiVurduMu() {
        for (int omv_i = 0; omv_i < oyuncumermilistesi.size(); omv_i++) {
            for (int omv_d = 0; omv_d < dusmangemilistesi.size(); omv_d++) {
                if (oyuncumermilistesi.get(omv_i).get(OMERMI_X)
                <  dusmangemilistesi.get(omv_d).get(DGEMI_X)
                +  dusmangemilistesi.get(omv_d).get(DGEMI_W)
                && oyuncumermilistesi.get(omv_i).get(OMERMI_X)
                +  oyuncumermilistesi.get(omv_i).get(OMERMI_W)
                >  dusmangemilistesi.get(omv_d).get(DGEMI_X)
                && oyuncumermilistesi.get(omv_i).get(OMERMI_Y)
                <  dusmangemilistesi.get(omv_d).get(DGEMI_Y)
                +  dusmangemilistesi.get(omv_d).get(DGEMI_H)
                && oyuncumermilistesi.get(omv_i).get(OMERMI_Y)
                +  oyuncumermilistesi.get(omv_i).get(OMERMI_H)
                >  dusmangemilistesi.get(omv_d).get(DGEMI_Y)) {
                    dusmangemilistesi.get(omv_d).set(DGEMI_CAN,
                            dusmangemilistesi.get(omv_d).get(DGEMI_CAN) - oyuncumermilistesi.get(omv_i).get(OMERMI_HASAR));
                    oyuncumermilistesi.remove(omv_i);
                    if(dusmangemilistesi.get(omv_d).get(DGEMI_CAN) <= 0) {
                        puan += dusmangemilistesi.get(omv_d).get(DGEMI_PUAN);
                        patlamaUret(dusmangemilistesi.get(omv_d).get(DGEMI_X),
                                dusmangemilistesi.get(omv_d).get(DGEMI_Y));
                        dusmangemilistesi.remove(omv_d);
                    }
                    break;
                }
            }
        }
    }

    private void patlamaUret(int x, int y) {
        yenipatlama = new Vector<Integer>();
        yenipatlama.add(0);//frame
        yenipatlama.add(x);//x
        yenipatlama.add(y);//y
        patlamalistesi.add(yenipatlama);

    }


    private void moderenCarpisma(int oyuncugemi, int dusmangemi) {
        //1.GEMİ
        kontrol1.set(oyuncugemilistesi.get(oyuncugemi).get(OGEMI_X), oyuncugemilistesi.get(oyuncugemi).get(OGEMI_Y),
                     oyuncugemilistesi.get(oyuncugemi).get(OGEMI_X) + oyuncugemilistesi.get(oyuncugemi).get(OGEMI_W),
                     oyuncugemilistesi.get(oyuncugemi).get(OGEMI_Y) + oyuncugemilistesi.get(oyuncugemi).get(OGEMI_H));
        //2.GEMİ
        kontrol2.set(dusmangemilistesi.get(dusmangemi).get(DGEMI_X),
                dusmangemilistesi.get(dusmangemi).get(DGEMI_Y),
                dusmangemilistesi.get(dusmangemi).get(DGEMI_X)
                        + dusmangemilistesi.get(dusmangemi).get(DGEMI_W),
                dusmangemilistesi.get(dusmangemi).get(DGEMI_Y) +
                        dusmangemilistesi.get(dusmangemi).get(DGEMI_H));
        if(kontrol1.intersect(kontrol2)) {
            oyuncugemilistesi.get(oyuncugemi).set(OGEMI_SALDIRI, 1);
            dusmangemilistesi.get(dusmangemi).set(DGEMI_SALDIRI, 1);
        } else {
            oyuncugemilistesi.get(oyuncugemi).set(OGEMI_SALDIRI, 0);
            dusmangemilistesi.get(dusmangemi).set(DGEMI_SALDIRI, 0);
        }
    }

    private void dusmanGemisiSaldiriKontrolu() {
        for (int dusmangemi = 0; dusmangemi < dusmangemilistesi.size(); dusmangemi++) {
            dusmangemilistesi.get(dusmangemi).set(DGEMI_SALDIRI, 0);
            for (int oyuncugemi = 0; oyuncugemi < oyuncugemilistesi.size(); oyuncugemi++) {
                if(Math.abs(dusmangemilistesi.get(dusmangemi).get(DGEMI_MY)
                        -  oyuncugemilistesi.get(oyuncugemi).get(OGEMI_MY)) < 400
                        && dusmangemilistesi.get(dusmangemi).get(DGEMI_YOL)
                        == oyuncugemilistesi.get(oyuncugemi).get(OGEMI_YOL)) {
                    dusmangemilistesi.get(dusmangemi).set(DGEMI_SALDIRI, 1);
                    break;
                }
            }
        }
        dusmanMermiUretsinmi();

    }

    private void dusmanMermiUretsinmi() {
        //Düşman
        for (int dmum_i = 0; dmum_i < dusmangemilistesi.size(); dmum_i++) {
            if(dusmangemilistesi.get(dmum_i).get(DGEMI_SALDIRI) == 1
                    && dusmangemilistesi.get(dmum_i).get(DGEMI_RELOAD) >= 20) {
                dMermiUret(dusmangemilistesi.get(dmum_i).get(DGEMI_MERMI),
                        dusmangemilistesi.get(dmum_i).get(DGEMI_MX),
                        dusmangemilistesi.get(dmum_i).get(DGEMI_MY),
                        dusmangemilistesi.get(dmum_i).get(DGEMI_YOL));
                dusmangemilistesi.get(dmum_i).set(DGEMI_RELOAD, 0);
            } else {
                dusmangemilistesi.get(dmum_i).set(DGEMI_RELOAD,
                        dusmangemilistesi.get(dmum_i).get(DGEMI_RELOAD) + 1);
            }
        }
    }

    private void oyuncuGemisiSaldiriKontrolu() {
        for (int oyuncugemi = 0; oyuncugemi < oyuncugemilistesi.size(); oyuncugemi++) {
            for (int dusmangemi = 0; dusmangemi < dusmangemilistesi.size(); dusmangemi++) {
                oyuncugemilistesi.get(oyuncugemi).set(OGEMI_SALDIRI, 0);
                if(Math.abs(oyuncugemilistesi.get(oyuncugemi).get(OGEMI_MY) -
                        dusmangemilistesi.get(dusmangemi).get(DGEMI_MY)) < 400
                        && oyuncugemilistesi.get(oyuncugemi).get(OGEMI_YOL)
                        == dusmangemilistesi.get(dusmangemi).get(DGEMI_YOL)) {
                    oyuncugemilistesi.get(oyuncugemi).set(OGEMI_SALDIRI, 1);
                    Log.d("Saldırı moduna geçti:...", "Evet geçti");
                    break;
                }
                //Log.d("OGEMI_MY:...", "" + oyuncugemilistesi.get(oyuncugemi).get(OGEMI_MY));
                //Log.d("DGEMI_MY:...", "" + dusmangemilistesi.get(dusmangemi).get(DGEMI_MY));
            }
        }
        oyuncuMermiUretecekmi();
    }

    private void oyuncuMermiUretecekmi() {
        for (int mum_i = 0; mum_i < oyuncugemilistesi.size(); mum_i++) {
            if(oyuncugemilistesi.get(mum_i).get(OGEMI_SALDIRI) == 1
                    && oyuncugemilistesi.get(mum_i).get(OGEMI_RELOAD) >= 20) {
                oMermiUret(oyuncugemilistesi.get(mum_i).get(OGEMI_MERMI),
                        oyuncugemilistesi.get(mum_i).get(OGEMI_MX),
                        oyuncugemilistesi.get(mum_i).get(OGEMI_MY),
                        oyuncugemilistesi.get(mum_i).get(OGEMI_YOL));
                oyuncugemilistesi.get(mum_i).set(OGEMI_RELOAD, 0);
            } else {
                oyuncugemilistesi.get(mum_i).set(OGEMI_RELOAD,
                        oyuncugemilistesi.get(mum_i).get(OGEMI_RELOAD) + 1);
            }
        }
    }


    private void dusmanGemiCiz() {
        for (int dgc_i = 0; dgc_i < dusmangemilistesi.size(); dgc_i++) {
            canvas.drawBitmap(gemiresimleri[dusmangemilistesi.get(dgc_i).get(DGEMI_TUR)][dusmangemilistesi.get(dgc_i).get(DGEMI_FRAME)],
                    dusmangemilistesi.get(dgc_i).get(DGEMI_X),
                    dusmangemilistesi.get(dgc_i).get(DGEMI_Y),
                    null);

            //Sonraki frame mekanizması frame = frame + 1 else(sınıra geldi) frame = 0
            if (dusmangemilistesi.get(dgc_i).get(DGEMI_FRAME)
                    < gemianimframesayisi[dusmangemilistesi.get(dgc_i).get(DGEMI_TUR)] - 1) {
                //frame = frame + 1;
                dusmangemilistesi.get(dgc_i).set(DGEMI_FRAME,
                        dusmangemilistesi.get(dgc_i).get(DGEMI_FRAME) + 1);
            } else {
                dusmangemilistesi.get(dgc_i).set(DGEMI_FRAME, 0);
            }
        }
    }

    private void dusmanPlatformCiz() {
        for (int dpc_i = 0; dpc_i < 2; dpc_i++) {
            canvas.save();
            //1.adım platformun orta noktaları gx(yatay), gy(dikey)
            int gx = dusmanplatformx[dpc_i] + platformresimleri.getWidth() / 2;
            int gy = dusmanplatformy[dpc_i] + platformresimleri.getHeight() / 2;
            //2.adım rotate işlemi
            canvas.rotate(180, gx, gy);
            //3.adım çiz
            canvas.drawBitmap(platformresimleri, dusmanplatformx[dpc_i],
                    dusmanplatformy[dpc_i], null);
            canvas.restore();
        }
    }
    private void ayrintiCiz(int gc_i) {
        canvas.drawText("Tür=> " + oyuncugemilistesi.get(gc_i).get(OGEMI_TUR), oyuncugemilistesi.get(gc_i).get(2) + 250, oyuncugemilistesi.get(gc_i).get(3), yazirengi);
        canvas.drawText("Frame=> " + oyuncugemilistesi.get(gc_i).get(OGEMI_FRAME), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 30, yazirengi);
        canvas.drawText("X=> " + oyuncugemilistesi.get(gc_i).get(2), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 60, yazirengi);
        canvas.drawText("Y=> " + oyuncugemilistesi.get(gc_i).get(3), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 90, yazirengi);
        canvas.drawText("W=> " + oyuncugemilistesi.get(gc_i).get(4), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 120, yazirengi);
        canvas.drawText("H=> " + oyuncugemilistesi.get(gc_i).get(5), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 150, yazirengi);
        canvas.drawText("SM=> " + oyuncugemilistesi.get(gc_i).get(6), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 180, yazirengi);
        canvas.drawText("Hız=> " + oyuncugemilistesi.get(gc_i).get(7), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 210, yazirengi);
        canvas.drawText("Yol=> " + oyuncugemilistesi.get(gc_i).get(8), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 240, yazirengi);
        canvas.drawText("MX=> " + oyuncugemilistesi.get(gc_i).get(9), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 270, yazirengi);
        canvas.drawText("MY=> " + oyuncugemilistesi.get(gc_i).get(10), oyuncugemilistesi.get(gc_i).get(2)+ 250, oyuncugemilistesi.get(gc_i).get(3) + 300, yazirengi);
        Log.d("Tür=> ", "" + oyuncugemilistesi.get(gc_i).get(0));
        Log.d("Frame=> ", "" + oyuncugemilistesi.get(gc_i).get(1));
        Log.d("X=> ", "" + oyuncugemilistesi.get(gc_i).get(2));
        Log.d("Y=> ", "" + oyuncugemilistesi.get(gc_i).get(3));
        Log.d("W=> ", "" + oyuncugemilistesi.get(gc_i).get(4));
        Log.d("H=> ", "" + oyuncugemilistesi.get(gc_i).get(5));
        Log.d("SM=> ", "" + oyuncugemilistesi.get(gc_i).get(6));
        Log.d("Hız=> ", "" + oyuncugemilistesi.get(gc_i).get(7));
        Log.d("Yol=> ", "" + oyuncugemilistesi.get(gc_i).get(8));
        Log.d("MX=> ", "" + oyuncugemilistesi.get(gc_i).get(9));
        Log.d("MY=> ", "" + oyuncugemilistesi.get(gc_i).get(10));
    }
    
    private void dusmanayrintiCiz(int gc_i) {
        canvas.drawText("Tür=> " + dusmangemilistesi.get(gc_i).get(DGEMI_TUR), dusmangemilistesi.get(gc_i).get(2) + 250, dusmangemilistesi.get(gc_i).get(3), yazirengi);
        canvas.drawText("Frame=> " + dusmangemilistesi.get(gc_i).get(DGEMI_FRAME), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 30, yazirengi);
        canvas.drawText("X=> " + dusmangemilistesi.get(gc_i).get(2), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 60, yazirengi);
        canvas.drawText("Y=> " + dusmangemilistesi.get(gc_i).get(3), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 90, yazirengi);
        canvas.drawText("W=> " + dusmangemilistesi.get(gc_i).get(4), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 120, yazirengi);
        canvas.drawText("H=> " + dusmangemilistesi.get(gc_i).get(5), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 150, yazirengi);
        canvas.drawText("SM=> " + dusmangemilistesi.get(gc_i).get(6), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 180, yazirengi);
        canvas.drawText("Hız=> " + dusmangemilistesi.get(gc_i).get(7), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 210, yazirengi);
        canvas.drawText("Yol=> " + dusmangemilistesi.get(gc_i).get(8), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 240, yazirengi);
        canvas.drawText("MX=> " + dusmangemilistesi.get(gc_i).get(9), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 270, yazirengi);
        canvas.drawText("MY=> " + dusmangemilistesi.get(gc_i).get(10), dusmangemilistesi.get(gc_i).get(2)+ 250, dusmangemilistesi.get(gc_i).get(3) + 300, yazirengi);
        Log.d("Tür=> ", "" + dusmangemilistesi.get(gc_i).get(0));
        Log.d("Frame=> ", "" + dusmangemilistesi.get(gc_i).get(1));
        Log.d("X=> ", "" + dusmangemilistesi.get(gc_i).get(2));
        Log.d("Y=> ", "" + dusmangemilistesi.get(gc_i).get(3));
        Log.d("W=> ", "" + dusmangemilistesi.get(gc_i).get(4));
        Log.d("H=> ", "" + dusmangemilistesi.get(gc_i).get(5));
        Log.d("SM=> ", "" + dusmangemilistesi.get(gc_i).get(6));
        Log.d("Hız=> ", "" + dusmangemilistesi.get(gc_i).get(7));
        Log.d("Yol=> ", "" + dusmangemilistesi.get(gc_i).get(8));
        Log.d("MX=> ", "" + dusmangemilistesi.get(gc_i).get(9));
        Log.d("MY=> ", "" + dusmangemilistesi.get(gc_i).get(10));
    }

    private void oyuncuGemiIlerlet() {
        //Türk telekom tarafından katledildin :/
        for (int ogi_i = 0; ogi_i < oyuncugemilistesi.size(); ogi_i++) {
            if(oyuncugemilistesi.get(ogi_i).get(OGEMI_Y)
            >  dusmanplatformy[oyuncugemilistesi.get(ogi_i).get(OGEMI_YOL)]
            && oyuncugemilistesi.get(ogi_i).get(OGEMI_SALDIRI) == 0) {
               // y = y  -+ hiz
               oyuncugemilistesi.get(ogi_i).set(OGEMI_Y,
               oyuncugemilistesi.get(ogi_i).get(OGEMI_Y)
               - oyuncugemilistesi.get(ogi_i).get(OGEMI_HIZ));
               //Merkez noktası ilerlet
               oyuncugemilistesi.get(ogi_i).set(OGEMI_MY,
                       oyuncugemilistesi.get(ogi_i).get(OGEMI_MY)
                      -oyuncugemilistesi.get(ogi_i).get(OGEMI_HIZ));
            }
            //oyun bitti mi
            if(oyuncugemilistesi.get(ogi_i).get(OGEMI_Y)
            <=  dusmanplatformy[oyuncugemilistesi.get(ogi_i).get(OGEMI_YOL)]) {
                oyunbitti = true;
                oyundevamediyor = false;
                kazandi = true;
            }
        }
    }

    private void dusmanGemiIlerlet() {
        for (int dgi_i = 0; dgi_i < dusmangemilistesi.size(); dgi_i++) {
            if(dusmangemilistesi.get(dgi_i).get(DGEMI_Y)
               <  oyuncuplatformy[dusmangemilistesi.get(dgi_i).get(DGEMI_YOL)]
            && dusmangemilistesi.get(dgi_i).get(DGEMI_SALDIRI) == 0) {
                dusmangemilistesi.get(dgi_i).set(DGEMI_Y,
                dusmangemilistesi.get(dgi_i).get(DGEMI_Y)
              + dusmangemilistesi.get(dgi_i).get(DGEMI_HIZ));
                //Merkezi hareket
                dusmangemilistesi.get(dgi_i).set(DGEMI_MY,
                dusmangemilistesi.get(dgi_i).get(DGEMI_MY)
              + dusmangemilistesi.get(dgi_i).get(DGEMI_HIZ));
            }
            if(dusmangemilistesi.get(dgi_i).get(DGEMI_Y)
            >=  oyuncuplatformy[dusmangemilistesi.get(dgi_i).get(DGEMI_YOL)]) {
                oyunbitti = true;
                oyundevamediyor = false;
                kaybettik = true;
            }
        }
    }

    private void oyuncuGemiCiz() {
        for (int gc_i = 0; gc_i < oyuncugemilistesi.size(); gc_i++) {
            canvas.save();
            int gx = oyuncugemilistesi.get(gc_i).get(OGEMI_X)
                   + oyuncugemilistesi.get(gc_i).get(OGEMI_W) / 2;
            int gy = oyuncugemilistesi.get(gc_i).get(OGEMI_Y)
                   + oyuncugemilistesi.get(gc_i).get(OGEMI_H) / 2;
            canvas.rotate(180, gx, gy);
            canvas.drawBitmap(gemiresimleri[oyuncugemilistesi.get(gc_i).get(OGEMI_TUR)]
                            [oyuncugemilistesi.get(gc_i).get(OGEMI_FRAME)],
                              oyuncugemilistesi.get(gc_i).get(OGEMI_X),
                              oyuncugemilistesi.get(gc_i).get(OGEMI_Y),
                              null);
            canvas.restore();

            //dusmanayrintiCiz(gc_i);

            //Sonraki frame mekanizması frame = frame + 1 else(sınıra geldi) frame = 0
            if(oyuncugemilistesi.get(gc_i).get(OGEMI_FRAME)
             < gemianimframesayisi[oyuncugemilistesi.get(gc_i).get(OGEMI_TUR)] - 1) {
                //frame = frame + 1;
                oyuncugemilistesi.get(gc_i).set(OGEMI_FRAME,
                        oyuncugemilistesi.get(gc_i).get(OGEMI_FRAME) + 1);
            } else {
                oyuncugemilistesi.get(gc_i).set(OGEMI_FRAME, 0);
            }
        }



    }

    private void oyuncuPlatformCiz() {
        for (int opc_i = 0; opc_i < 2; opc_i++) {
            canvas.drawBitmap(platformresimleri, oyuncuplatformx[opc_i],
                              oyuncuplatformy[opc_i], null);
        }
        if(animasyonciz == true) platanimasyonCiz();
    }

    private void platanimasyonCiz() {
        canvas.drawBitmap(platanimasyon[platanimsuankiframe],
                platanimkonumx + (514 * secilenplatform), platanimkonumy, null);
        //frame mekanizması
        if(platanimsuankiframe < platanimframesayisi - 1) {
            platanimsuankiframe ++;
        } else {
            animasyonciz = false;
        }
    }

    private void arkaplanCiz() {
        canvas.drawBitmap(oyunarkaplan, 0, 0, null);
    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public boolean backPressed() {
        return true;
    }

    public void surfaceChanged(int width, int height) {

    }

    public void surfaceCreated() {

    }

    public void surfaceDestroyed() {

    }

    public void touchDown(int x, int y, int id) {

        if(x > oyuncuplatformx[0] //x
        && x < oyuncuplatformx[0] + platformresimleri.getWidth() //x + w
        && y > oyuncuplatformy[0]  //y
        && y < oyuncuplatformy[0] + platformresimleri.getHeight()) { //y + h
            secilenplatform = 0;
            animasyonciz = true;
            platanimsuankiframe = 0;
        }
        if(x > oyuncuplatformx[1] //x
        && x < oyuncuplatformx[1] + platformresimleri.getWidth() //x + w
        && y > oyuncuplatformy[1]  //y
        && y < oyuncuplatformy[1] + platformresimleri.getHeight()) { //y + h
            secilenplatform = 1;
            animasyonciz = true;
            platanimsuankiframe = 0;
        }
        secilenGemiAlani(x, y);
        oyunSonuButonlari(x, y);
    }

    private void oyunSonuButonlari(int x, int y) {

        if(x > yenidenbutonux
        && y > yenidenbutonuy
        && x < yenidenbutonux + yenidenbutonu.getWidth()
        && y < yenidenbutonuy + yenidenbutonu.getHeight()
        && oyunbitti == true
        && oyundevamediyor == false) {
            if(root.oyunmuzik.isPlaying() == true)root.oyunmuzik.stop();
            root.canvasManager.setCurrentCanvas(new GameCanvas(root));
        }
        if(x > menuyedonbutonux
        && y > menuyedonbutonuy
        && x < menuyedonbutonux + menuyedonbutonu.getWidth()
        && y < menuyedonbutonuy + menuyedonbutonu.getHeight()
        && oyunbitti == true
        && oyundevamediyor == false) {
            if(root.oyunmuzik.isPlaying() == true)root.oyunmuzik.stop();
            root.canvasManager.setCurrentCanvas(new MenuCanvas(root));
        }
    }

    private void secilenGemiAlani(int x, int y) {
        for (int sga_i = 0; sga_i < 4; sga_i++) {
            if(x > gemisecx[sga_i]
            && x < gemisecx[sga_i] + secimalanix
            && y > gemisecy[sga_i]
            && y < gemisecy[sga_i] + secimalaniy) {
                oyuncuGemiUret(sga_i, secilenplatform);
                break;
            }
        }
    }

    public void touchMove(int x, int y, int id) {
    }

    public void touchUp(int x, int y, int id) {
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
