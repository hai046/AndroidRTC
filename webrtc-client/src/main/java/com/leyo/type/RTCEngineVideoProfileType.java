package com.leyo.type;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haizhu on 16/5/21.
 * <p>
 * 这里是设置分辨率, 比特率 帧率什么的
 */

public enum RTCEngineVideoProfileType {
//                                                    w h     fps  bits


    //    RTCEngine_VideoProfile_120P_3 = 2,        // 120x120   15   60
    RTCEngine_VideoProfile_120P_3(2, 120, 120, 15, 60),//


    //    RTCEngine_VideoProfile_180P = 10,        // 320x180   15   160
    RTCEngine_VideoProfile_180P(10, 320, 180, 15, 160),//

    //    RTCEngine_VideoProfile_180P_2 = 11,        // 180x320   15   160
    RTCEngine_VideoProfile_180P_2(11, 180, 320, 15, 160),//


    //    RTCEngine_VideoProfile_180P_3 = 12,        // 180x180   15   120
    RTCEngine_VideoProfile_180P_3(12, 180, 180, 15, 120),//

    //    RTCEngine_VideoProfile_240P = 20,        // 320x240   15   200
    RTCEngine_VideoProfile_240P(20, 320, 240, 15, 200),//

    //    RTCEngine_VideoProfile_240P_2 = 21,        // 240x320   15   200
    RTCEngine_VideoProfile_240P_2(21, 240, 320, 15, 200),//

    //    RTCEngine_VideoProfile_240P_3 = 22,        // 240x240   15   160
    RTCEngine_VideoProfile_240P_3(22, 240, 240, 15, 160),//

    //    RTCEngine_VideoProfile_360P = 30,        // 640x360   15   400
    RTCEngine_VideoProfile_360P(30, 640, 360, 15, 400),//

    //    RTCEngine_VideoProfile_360P_2 = 31,        // 360x640   15   400
    RTCEngine_VideoProfile_360P_2(31, 360, 640, 15, 400),//

    //    RTCEngine_VideoProfile_360P_3 = 32,        // 360x360   15   300
    RTCEngine_VideoProfile_360P_3(32, 360, 360, 15, 300),//

    //    RTCEngine_VideoProfile_360P_4 = 33,        // 640x360   30   680
    RTCEngine_VideoProfile_360P_4(33, 640, 360, 30, 680),//

    //    RTCEngine_VideoProfile_360P_5 = 34,        // 360x640   30   680
    RTCEngine_VideoProfile_360P_5(34, 360, 640, 30, 680),//

    //    RTCEngine_VideoProfile_360P_6 = 35,        // 360x360   30   500
    RTCEngine_VideoProfile_360P_6(680, 360, 360, 30, 500),//

    //    RTCEngine_VideoProfile_480P = 40,        // 640x480   15   500
    RTCEngine_VideoProfile_480P(40, 640, 480, 15, 500),//

    //    RTCEngine_VideoProfile_480P_2 = 41,        // 480x640   15   500
    RTCEngine_VideoProfile_480P_2(41, 480, 640, 15, 500),//

    //    RTCEngine_VideoProfile_480P_3 = 42,        // 480x480   15   400
    RTCEngine_VideoProfile_480P_3(42, 480, 480, 15, 400),//


    ;


    public static Map<Integer, RTCEngineVideoProfileType> values = new HashMap<>();
    private final int type;
    private int width, height, fps, bits;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getBits() {
        return bits;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    public int getType() {
        return type;
    }

    RTCEngineVideoProfileType(int type, int width, int height, int fps, int bits) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.bits = bits;

    }

    static {
        for (RTCEngineVideoProfileType t : RTCEngineVideoProfileType.values()) {
            values.put(t.getType(), t);
        }

    }

    public static RTCEngineVideoProfileType valueOf(int type) {
        return values.get(type);
    }
}
