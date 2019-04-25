package com.tank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tank.db.DBManager;
import com.tank.entity.Boom;
import com.tank.entity.Bullet;
import com.tank.entity.Tank;
import com.tank.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MyView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private DBManager db;
    private SurfaceHolder holder;
    private Context context;
    private int screenW, screenH;
    private List<Bitmap[]> enemiesBg; //坦克图片
    private Bitmap[] playerBg; //玩家坦克图片
    private Bitmap[] bulletBg; //子弹图片
    private Bitmap[] boomBg; //爆炸图片
    private Bitmap up, down, left, right, attack; //上, 下, 左, 右, 攻击图片
    private int[] attPosition; //攻击按钮的位置
    private int[] upPosition; //向上按钮的位置
    private int[] downPosition; //向下按钮的位置
    private int[] leftPosition; //向左按钮的位置
    private int[] rightPosition; //向右按钮的位置
    private Canvas canvas;
    private Paint paint;
    private Thread thread, tankThread, bulletThread, moveThread, bulletMoveThread, boomThread;
    private boolean flag;
    private Tank player;
    private Vector<Tank> enemies;
    private Vector<Bullet> bullets, eBullets;
    private Vector<Boom> booms;
    private Random random;
    private int goal;
    private SoundPool sp;
    private int shoot, boom;
    private MediaPlayer media;
    private Typeface tf;
    private Handler handler;
    private SharedPreferences preferences;
    private int audio;

    public MyView(Context context) {
        super(context);
        this.context = context;
        preferences = context.getSharedPreferences("option.txt", Context.MODE_PRIVATE);
        audio = preferences.getInt("audio", 1);
        db = new DBManager(context);
        holder = getHolder();
        holder.addCallback(this);
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
        shoot = sp.load(context, R.raw.shot, 1);
        boom = sp.load(context, R.raw.explode, 1);
        media = MediaPlayer.create(context, R.raw.game);
        media.setLooping(true);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x101) {
                    popupWindow(MyView.this);
                    storeScore(goal);
                }
            }
        };
    }

    @Override
    public void run() {
        while(flag) {
            myDraw();
            logic();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/xk.ttf");
        screenW = this.getWidth() / 10 * 9;
        screenH = this.getHeight();
        paint = new Paint();
        enemiesBg = new Vector<>();
        enemies = new Vector<>();
        bullets = new Vector<>();
        eBullets = new Vector<>();
        booms = new Vector<>();
        random = new Random();
        flag = true;
        goal = 0;
        if(audio == 1) {
            media.start();
        }
        loadImg();
        initPosition();
        thread = new Thread(this);
        thread.start();
        tankThread = new TankThread();
        bulletThread = new BulletThread();
        moveThread = new MoveThread();
        bulletMoveThread = new BulletMoveThread();
        boomThread = new BoomThread();
        tankThread.start();
        bulletThread.start();
        moveThread.start();
        bulletMoveThread.start();
        boomThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        flag = false;
        if(audio == 1) {
            media.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_MOVE :
                switch (touchType(x, y)) {
//                    case 0:
//                        playerShoot();
//                        break;
                    case 1:
                        player.setDirection(1);
                        player.move();
                        break;
                    case 2:
                        player.setDirection(2);
                        player.move();
                        break;
                    case 3:
                        player.setDirection(3);
                        player.move();
                        break;
                    case 4:
                        player.setDirection(4);
                        player.move();
                        break;
                }
                break;
            case MotionEvent.ACTION_DOWN :
                switch (touchType(x, y)) {
                    case 0:
                        playerShoot();
                        break;
                    case 1:
                        player.setDirection(1);
                        player.move();
                        break;
                    case 2:
                        player.setDirection(2);
                        player.move();
                        break;
                    case 3:
                        player.setDirection(3);
                        player.move();
                        break;
                    case 4:
                        player.setDirection(4);
                        player.move();
                        break;
                }
                break;
        }
        return true;
    }

    /**
     * 加载图片资源
     */
    private void loadImg() {
        enemiesBg.add(new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_1_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_1_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_1_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_1_4)
        });
        enemiesBg.add(new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_2_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_2_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_2_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_2_4)
        });
        enemiesBg.add(new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_3_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_3_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_3_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_3_4)
        });
        enemiesBg.add(new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_4_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_4_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_4_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.enemy_4_4)
        });
        bulletBg = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.mipmap.bullet_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.bullet_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.bullet_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.bullet_4)
        };
        playerBg = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.mipmap.player_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.player_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.player_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.player_4)
        };
        boomBg = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.mipmap.boom_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.boom_2)
        };
        up = BitmapFactory.decodeResource(getResources(), R.mipmap.up);
        down = BitmapFactory.decodeResource(getResources(), R.mipmap.down);
        left = BitmapFactory.decodeResource(getResources(), R.mipmap.left);
        right = BitmapFactory.decodeResource(getResources(), R.mipmap.right);
        attack = BitmapFactory.decodeResource(getResources(), R.mipmap.attack);
        player = new Tank(0, playerBg, screenW / 2,
                screenH - playerBg[0].getHeight() / 2, screenW, screenH, 1);
    }

    private void initPosition() {
        attPosition = new int[]{screenW / 8 * 7, screenH / 4 * 3};
        upPosition = new int[]{screenW / 8,  screenH / 4 * 3 - left.getHeight()};
        downPosition = new int[]{screenW /8,  screenH / 4 * 3 + left.getHeight()};
        leftPosition = new int[]{screenW / 8 - up.getWidth(), screenH / 4 * 3};
        rightPosition = new int[]{screenW / 8 + up.getWidth(), screenH / 4 * 3};
    }

    /**
     * 判断按键类型
     * @param x
     * @param y
     * @return 返回 0-5 分别对应攻击和上下左右方向键
     */
    public int touchType(int x, int y) {
        if(Math.abs(x - attPosition[0]) < attack.getWidth() / 2 &&
                Math.abs(y - attPosition[1]) < attack.getHeight() / 2) {
            return 0;
        }else if(Math.abs(x - upPosition[0]) < up.getWidth() / 2 &&
                Math.abs(y - upPosition[1]) < up.getHeight() / 2) {
            return 1;
        }else if(Math.abs(x - downPosition[0]) < down.getWidth() / 2 &&
                Math.abs(y - downPosition[1]) < down.getHeight() / 2) {
            return 2;
        }else if(Math.abs(x - leftPosition[0]) < left.getWidth() / 2 &&
                Math.abs(y - leftPosition[1]) < left.getHeight() / 2) {
            return 3;
        }else if(Math.abs(x - rightPosition[0]) < right.getWidth() / 2 &&
                Math.abs(y - rightPosition[1]) < right.getHeight() / 2) {
            return 4;
        }
        return -1;
    }

    //绘制屏幕
    public void myDraw() {
        try {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.rgb(255, 97, 0));
            canvas.drawRect(new Rect(screenW, 0, screenW + 300, screenH), paint);
            drawGoal(canvas, paint);
//            drawPause(canvas, paint);
            player.draw(canvas, paint);
            for(int i = 0; i < enemies.size(); i++) {
                enemies.elementAt(i).draw(canvas, paint);
            }
            for(int i = 0; i < bullets.size(); i++) {
                bullets.elementAt(i).draw(canvas, paint);
            }
            for(int i = 0; i < eBullets.size(); i++) {
                eBullets.elementAt(i).draw(canvas, paint);
            }
            for(int i = 0; i < booms.size(); i++) {
                booms.elementAt(i).draw(canvas, paint);
            }
            drawKeywords(canvas, paint);
        }catch(Exception e) {

        }finally {
            if(canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    //游戏逻辑
    public void logic() {
        int x = -1;
        for(int i = 0; i < enemies.size(); i++) {
            Tank en = enemies.elementAt(i);
            if(en.isDead()) {
                enemies.removeElementAt(i);
            }
            if(player.isCollision(en)) {
                booms.addElement(new Boom(boomBg[1], player.getX(), player.getY()));
                flag = false;
                myDraw();
                gaveover();
                if(audio == 1) {
                    sp.play(boom, 1f, 1f, 0, 0, 1);
                }
                handler.sendEmptyMessage(0x101);
            }
            if(i == x) {
                continue;
            }
            for(int j = 0; j < enemies.size(); j++) {
                if(j == i) {
                    continue;
                }
                if(en.isCollision(enemies.get(j))) {
                    Tank tk = enemies.get(j);
                    if(tk.getDirection() == en.getDirection()) {
                        if(tk.getMoveSpeed() > en.getMoveSpeed()) {
                            tk.setDirection(Util.difDirection(tk.getDirection()));
                        }else {
                            en.setDirection(Util.difDirection(en.getDirection()));
                        }
                    } else {
                        tk.setDirection(Util.difDirection(tk.getDirection()));
                        en.setDirection(Util.difDirection(en.getDirection()));
                    }
                    en.setDirection(en.getDirection() + 2  > 4 ? 4 : en.getDirection() + 2);
//                    x = j;
//                    break;
                    enemies.get(j).setDirection(enemies.get(j).getDirection() + 1  > 4 ? 4 : enemies.get(j).getDirection() + 1);
                }
            }
        }
        for(int i = 0; i < eBullets.size(); i++) {
            Bullet b = eBullets.elementAt(i);
            if(b.isDead()) {
                eBullets.removeElement(b);
            }
        }
        for(int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.elementAt(i);
            if(b.isDead()) {
                bullets.removeElement(b);
            }else {
                b.move();
            }
        }
        for(int i = 0; i < eBullets.size(); i++) {
            if(player.isCollision(eBullets.elementAt(i))) {
                booms.addElement(new Boom(boomBg[1], player.getX(), player.getY()));
                flag = false;
                myDraw();
                if(audio == 1) {
                    sp.play(boom, 1f, 1f, 0, 0, 1);
                }
                handler.sendEmptyMessage(0x101);
            }
        }
        for(int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            for(int j = 0; j < enemies.size(); j++) {
                if(enemies.elementAt(j).isCollision(b)) {
                    Tank en = enemies.elementAt(j);
                    en.setHp(en.getHp() - 1);
                    if(en.getHp() == 0) {
                        en.setDead(true);
                        goal += en.getScore();
                        booms.add(new Boom(boomBg[1], en.getX(), en.getY()));
                        if(audio == 1) {
                            sp.play(boom, 1f, 1f, 0, 0, 1);
                        }
                    }
                    b.setDead(true);
                }
            }
        }
        for(int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            for(int j = 0; j < eBullets.size(); j++) {
                if(eBullets.get(j).isCollision(b)) {
                    b.setDead(true);
                    eBullets.get(j).setDead(true);
                }
            }
        }
        for(int i = 0; i < booms.size(); i++) {
            Boom b = booms.elementAt(i);
            if(b.isDead()) {
                booms.removeElement(b);
            }
        }
    }

    private void playerShoot() {
        switch (player.getDirection()) {
            case 1:
                bullets.add(new Bullet(bulletBg, player.getX(),
                        player.getY() - player.geteH() / 2 - 15, screenW, screenH,
                        player.getDirection()));
                if(audio == 1) {
                    sp.play(shoot, 1f, 1f, 0, 0, 1);
                }
                break;
            case 2:
                bullets.add(new Bullet(bulletBg, player.getX(),
                        player.getY() + player.geteH() / 2 + 15, screenW, screenH,
                        player.getDirection()));
                if(audio == 1) {
                    sp.play(shoot, 1f, 1f, 0, 0, 1);
                }
                break;
            case 3:
                bullets.add(new Bullet(bulletBg, player.getX() - player.geteW() / 2 - 15,
                        player.getY(), screenW, screenH, player.getDirection()));
                if(audio == 1) {
                    sp.play(shoot, 1f, 1f, 0, 0, 1);
                }
                break;
            case 4:
                bullets.add(new Bullet(bulletBg, player.getX() + player.geteW() / 2 + 15,
                        player.getY(), screenW, screenH, player.getDirection()));
                if(audio == 1) {
                    sp.play(shoot, 1f, 1f, 0, 0, 1);
                }
                break;
        }
    }

    private void drawGoal(Canvas canvas, Paint paint) {
        String s = "分数: " ;
        paint.setTypeface(tf);
        paint.setTextSize(60);
        paint.setColor(Color.BLACK);
        canvas.drawText(s, screenW + 10, 50, paint);
        canvas.drawText(goal + "", screenW + 50, 150, paint);
    }

    private void drawKeywords(Canvas canvas, Paint paint) {
        canvas.drawBitmap(up, upPosition[0] - up.getWidth() / 2, upPosition[1] - up.getHeight() / 2, paint);
        canvas.drawBitmap(down, downPosition[0] - down.getWidth() / 2, downPosition[1] - down.getHeight() / 2, paint);
        canvas.drawBitmap(left, leftPosition[0] - left.getWidth() / 2, leftPosition[1] - left.getHeight() / 2, paint);
        canvas.drawBitmap(right, rightPosition[0] - right.getWidth() / 2, rightPosition[1] - right.getHeight() / 2, paint);
        canvas.drawBitmap(attack, attPosition[0] - attack.getWidth() / 2, attPosition[1] - attack.getHeight() / 2, paint);
    }

    private void gaveover() {
        moveThread.interrupt();
        tankThread.interrupt();
        bulletThread.interrupt();
    }

    private void popupWindow(View v) {
        View view = LayoutInflater.from(context).inflate(R.layout.end_pop, null, false);
        Button b1 = view.findViewById(R.id.e_restart);
        Button b2 = view.findViewById(R.id.eb_goal);
        Button b3 = view.findViewById(R.id.e_back);
        TextView score = view.findViewById(R.id.y_goal);
        TextView end = view.findViewById(R.id.end);
        score.setTypeface(tf);
        score.setText("最高分：" + goal);
        end.setTypeface(tf);
        b1.setTypeface(tf);
        b2.setTypeface(tf);
        b3.setTypeface(tf);
        final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        pop.showAtLocation(v, Gravity.CENTER, 0, 0);

        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                enemies.removeAllElements();
                bullets.removeAllElements();
                eBullets.removeAllElements();
                booms.removeAllElements();
                goal = 0;
                player.setX(screenW / 2);
                player.setY(screenH - playerBg[0].getHeight() / 2);
                player.setDirection(1);
                loadImg();
                initPosition();
                thread = new Thread(MyView.this);
                tankThread = new TankThread();
                bulletThread = new BulletThread();
                moveThread = new MoveThread();
                bulletMoveThread = new BulletMoveThread();
                boomThread = new BoomThread();
                thread.start();
                tankThread.start();
                bulletThread.start();
                moveThread.start();
                bulletMoveThread.start();
                boomThread.start();
                pop.dismiss();
            }
        });

        b2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScoreActivity.class);
                context.startActivity(intent);
            }
        });

        b3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void storeScore(int score) {
        db.insert(score);
    }

    private class MoveThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    for(int i = 0; i < enemies.size(); i++) {
                        Tank tank = enemies.get(i);
                        if(!tank.isDead()) {
                            tank.move();
                        }
                    }
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class TankThread extends Thread {
        private List<int[]> positions = new ArrayList<>(); //坦克出现的位置

        public TankThread() {
            positions.add(new int[]{48, 48}); //左上角
            positions.add(new int[]{screenW / 2, 48}); //中上部
            positions.add(new int[]{screenW - 48, 48}); //右上角
        }
        @Override
        public void run() {
            while (true) {
                try {
                    if (enemies.size() >= 5) {
                        return;
                    }
                    int index = random.nextInt(3); //随机位置
                    int type = random.nextInt(4) + 1; //坦克类型
                    int direction = random.nextInt(3) + 2; //方向
                    enemies.add(new Tank(type, enemiesBg.get(type - 1), positions.get(index)[0],
                            positions.get(index)[1], screenW, screenH, direction));
                    Thread.sleep(3000);
                }catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class BulletMoveThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    for(int i = 0; i < eBullets.size(); i++) {
                        Bullet bullet = eBullets.get(i);
                        if(!bullet.isDead()) {
                            bullet.move();
                        }
                    }
                    for(int i = 0; i < bullets.size(); i++) {
                        Bullet bullet = bullets.get(i);
                        if(!bullet.isDead()) {
                            bullet.move();
                        }
                    }
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class BulletThread extends Thread {
        @Override
        public void run() {
            long time = 0;
            while(true) {
                try {
                    for(int i = 0; i < enemies.size(); i++) {
                        Tank enemy = enemies.get(i);
                        if(time % enemy.getShootSpeed() == 0) {
                            switch (enemy.getDirection()) {
                                case 1:
                                    eBullets.add(new Bullet(bulletBg, enemy.getX(),
                                            enemy.getY() - enemy.geteH() / 2 - 12, screenW, screenH,
                                            enemy.getDirection()));
                                    break;
                                case 2:
                                    eBullets.add(new Bullet(bulletBg, enemy.getX(),
                                            enemy.getY() + enemy.geteH() / 2 + 12, screenW, screenH,
                                            enemy.getDirection()));
                                    break;
                                case 3:
                                    eBullets.add(new Bullet(bulletBg, enemy.getX() - enemy.geteW() / 2 - 12,
                                            enemy.getY(), screenW, screenH, enemy.getDirection()));
                                    break;
                                case 4:
                                    eBullets.add(new Bullet(bulletBg, enemy.getX() + enemy.geteW() / 2 + 12,
                                            enemy.getY(), screenW, screenH, enemy.getDirection()));
                                    break;
                            }
                        }
                    }
                    Thread.sleep(1000);
                    time += 1;
                }catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class BoomThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    for(int i = 0; i < booms.size(); i++) {
                        Boom b = booms.get(i);
                        b.setDead(true);
                    }
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
