package com.tank.util;

import java.util.Random;

/**
 * 通用类
 */
public class Util {
    public static Random random = new Random();
    /**
     * 随机改变方向
     * @param oldDirection
     * @return
     */
    public static int randomDirection(int oldDirection) {
        int dr = random.nextInt(4)  + 1;
        while(dr != oldDirection) {
            return dr;
        }
        return oldDirection;
    }

    public static int difDirection(int oldDirection) {
        switch (oldDirection) {
            case 1: case 2:
                return random.nextInt(2) + 2;
            case 3: case 4:
                return random.nextInt(2) + 1;
        }
        return -1;
    }
}
