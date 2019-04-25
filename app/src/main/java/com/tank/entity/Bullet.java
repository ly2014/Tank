package com.tank.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Bullet {
    private Bitmap[] bitmap; //子弹图片
    private int x, y; //子弹位置
    private int screenW, screenH; //屏幕宽高
    private int bW, bH; //图片宽高
    private int speed; //子弹速度
    private boolean isDead; //状态
    private int direction; //方向

    public Bullet(Bitmap[] bitmap, int x, int y, int screenW, int screenH, int direction) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.screenH = screenH;
        this.screenW = screenW;
        this.direction = direction;
        this.isDead = false;
        this.bW = bitmap[0].getWidth();
        this.bH = bitmap[0].getHeight();
        this.speed = 160;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getbW() {
        return bW;
    }

    public int getbH() {
        return bH;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap[direction - 1], x - bW / 2, y - bH / 2, paint);
    }

    public void move() {
        switch (this.direction) {
            case 1:
                y -= speed;
                if(y <= bH / 2) {
                    isDead = true;
                }
                break;
            case 2:
                y += speed;
                if(y >= screenH - bH / 2) {
                    isDead = true;
                }
                break;
            case 3:
                x -= speed;
                if(x <= bW / 2) {
                    isDead = true;
                }
                break;
            case 4:
                x += speed;
                if(x >= screenW - bW / 2) {
                    isDead = true;
                }
                break;
        }
    }

    public boolean isCollision(Bullet bullet) {
        if(Math.abs(x - bullet.getX()) <= (bullet.getbW() + bW) / 2 &&
                Math.abs(y - bullet.getY()) <= (bullet.getbH() + bH) / 2) {
            return true;
        }
        return false;
    }
}
