package com.leyo;

import android.opengl.EGLContext;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.leyo.exception.BuildException;
import com.leyo.type.RTCEngineVideoProfileType;

import org.webrtc.MediaStream;
import org.webrtc.VideoRendererGui;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by haizhu on 16/5/21.
 */

public class RTCNngine implements Ngine {


    private static RTCNngine rtcNngine;
    private WebRtcClient mWebRtcClient;

    private RTCNngine() {

    }

    public static synchronized RTCNngine getInstance() {
        if (rtcNngine == null) {
            rtcNngine = new RTCNngine();
        }
        return rtcNngine;

    }

    private static RtcListener mRtcListener = new RtcListener() {
        @Override
        public void onCallReady(String callId) {

        }

        @Override
        public void onStatusChanged(String newStatus) {

        }

        @Override
        public void onLocalStream(MediaStream localStream) {

        }

        @Override
        public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

        }

        @Override
        public void onRemoveRemoteStream(int endPoint) {

        }
    };

    public void init() {


    }


    @Override
    public void joinRoom(String userId, String token) {

    }

    @Override
    public void leaveRoom() {

    }

    @Override
    public boolean startPreview() {
        return false;
    }

    @Override
    public boolean stopPreview() {
        return false;
    }


    public void init(SurfaceView video, RTCEngineVideoProfileType type) throws BuildException {
        WebRtcClient build = RTCNngineBuilder.newBuilder().setApiAddress("10.10.150.177:8000").setRTCEngineVideoProfileType(type).setVideoView(video).build();
        this.mWebRtcClient = build;

    }

    /**
     * 初始化参数相关的
     */
    public static class RTCNngineBuilder {

        private static RTCNngineBuilder builder;
        private String address;
        private RTCEngineVideoProfileType type;
        private boolean videoCodecHwAcceleration;

        private EGLContext mEGLcontext;
        private SurfaceView videoView;

        private List<String> iceServers = new ArrayList<>();

        private RTCNngineBuilder() {
            iceServers.add("stun:stun.l.google.com:19302");
            iceServers.add("stun:23.21.150.121");

        }

        public static RTCNngineBuilder newBuilder() {
            builder = new RTCNngineBuilder();
            return builder;
        }

        public RTCNngineBuilder setApiAddress(String address) {
            this.address = address;
            return this;
        }


        public RTCNngineBuilder setRTCEngineVideoProfileType(RTCEngineVideoProfileType type) {
            this.type = type;
            return this;
        }


        public RTCNngineBuilder setVideoCodecHwAcceleration(boolean videoCodecHwAcceleration) {
            this.videoCodecHwAcceleration = videoCodecHwAcceleration;
            return this;
        }

        public RTCNngineBuilder setEGLcontext(EGLContext mEGLcontext) {
            this.mEGLcontext = mEGLcontext;
            return this;

        }


        public RTCNngineBuilder setVideoView(SurfaceView videoView) {
            this.videoView = videoView;
            return this;

        }

        public RTCNngineBuilder addIceServer(String iceServer) {
            iceServers.add(iceServer);
            return this;

        }


        public WebRtcClient build() throws BuildException {

            if (TextUtils.isEmpty(address)) {
                throw new BuildException("host address must not be null!!!");
            }
            if (type == null) {
                throw new BuildException("RTCEngineVideoProfileType must not be null!!!");
            }
            if (videoView == null) {
                throw new BuildException("videoView must not be null!!!");
            }
            if (iceServers.isEmpty()) {
                throw new BuildException("iceServers must not be null!!!");

            }

            if (mEGLcontext == null) {
                mEGLcontext = VideoRendererGui.getEGLContext();
            }


            WebRtcClient webRtcClient = new WebRtcClient(mRtcListener, type, mEGLcontext);
            RTCPeerManager.getInstance().setListener(mRtcListener);
            RTCPeerManager.getInstance().setIceServer(iceServers);
            RTCPeerManager.getInstance().setWebRtcClient(webRtcClient);

            RTCMessageManger.getInstance().setHost(address);


            return webRtcClient;

        }

    }
}
