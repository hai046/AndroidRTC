package com.leyo;

/**
 * Created by haizhu on 16/5/21.
 */

public interface Ngine {

    void joinRoom(String userId, String token);

    void leaveRoom();

    boolean startPreview();

    boolean stopPreview();

//    /**
//     * 返回
//     *
//     * @return creameId
//     */
//    int switchCamera();
//
//    void setupLocalView(SurfaceView view);
//
//    void setupVideoProfile(RTCEngineVideoProfileType type);

}
