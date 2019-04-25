package com.tank.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.tank.util.Constants;
import com.tank.util.Util;

public class Tank {
    private int type; //坦克种类 0: 玩家 1-4: 敌人
    private Bitmap[] bitmap; //坦克图片
    private int x, y; //坦克位置
    private int screenW, screenH; //屏幕宽高
    private int eW, eH; //图片宽高
    private int moveSpeed; //移动速度
    private int shootSpeed; //射击速度
    private int direction; //移动方向
    private int hp; //血量
    private boolean isDead; //存活状态
    private int score; //分值

    public Tank(int type, Bitmap[] bitmap, int x, int y, int screenW, int screenH, int direction) {
        this.type = type;
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.screenW = screenW;
        this.screenH = screenH;
        this.direction = direction;
        init();
    }

    private void init() {
        this.eW = bitmap[0].getWidth();
        this.eH = bitmap[0].getHeight();
        this.moveSpeed = Constants.MOVESPEED[type];
        this.shootSpeed = Constants.SHOOTSPEED[type];
        this.hp = Constants.HP[type];
        this.isDead = false;
        this.score = Constants.SCORE[type];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int geteW() {
        return eW;
    }

    public int geteH() {
        return eH;
    }

    public int getScore() {
        return score;
    }

    public boolean isDead() {
        return isDead;
    }

    public int getShootSpeed() {
        return shootSpeed;
    }

    public int getDirection() {
        return direction;
    }

    public int getHp() {
        return hp;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap[direction - 1], x - eW / 2, y - eH / 2, paint);
    }

    public void move() {
        if(!isDead) {
            //碰到边界时随机改变方向
            switch (this.direction) {
                case 1:
                    y -= moveSpeed;
                    if(y <= eH / 2) {
                        y = eH / 2;
                        if(type == 0) {
                            break;
                        }
                        this.direction = Util.randomDirection(this.direction);
                    }
                    break;
                case 2:
                    y += moveSpeed;
                    if(y >= screenH - eH / 2) {
                        y = screenH - eH / 2;
                        if(type == 0) {
                            break;
                        }
                        this.direction = Util.randomDirection(this.direction);
                    }
                    break;
                case 3:
                    x -= moveSpeed;
                    if(x <= eW / 2) {
                        x = eW / 2;
                        if(type == 0) {
                            break;
                        }
                        this.direction = Util.randomDirection(this.direction);
                    }
                    break;
                case 4:
                    x += moveSpeed;
                    if(x >= screenW - eW / 2) {
                        x = screenW - eW / 2;
                        if(type == 0) {
                            break;
                        }
                        this.direction = Util.randomDirection(this.direction);
                    }
                    break;
            }
        }
    }

    public boolean isCollision(Tank tank) {
        if(Math.abs(x - tank.getX()) <= (tank.geteW() + eW) / 2 &&
                Math.abs(y - tank.getY()) <= (tank.geteH() + eH) / 2) {
            return true;
        }
        return false;
    }

    public boolean isCollision(Bullet bullet) {
        if(Math.abs(x - bullet.getX()) <= (bullet.getbW() + eW) / 2 &&
                Math.abs(y - bullet.getY()) <= (bullet.getbH() + eH) / 2) {
            return true;
        }
        return false;
    }
}
