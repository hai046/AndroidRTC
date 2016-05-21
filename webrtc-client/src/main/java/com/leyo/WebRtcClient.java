package com.leyo;

import android.opengl.EGLContext;

import com.leyo.type.RTCEngineVideoProfileType;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;

import java.util.LinkedList;
import java.util.List;

class WebRtcClient {
    private final static String TAG = WebRtcClient.class.getCanonicalName();
    private PeerConnectionFactory factory;
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private RTCEngineVideoProfileType pcParams;
    private MediaStream localMS;
    private VideoSource videoSource;
    private RtcListener mListener;

    //支持video
    private final boolean videoCallEnabled = true;
    //默认开启硬件加速
    private boolean videoCodecHwAcceleration = true;


    public WebRtcClient(RtcListener listener, RTCEngineVideoProfileType type, EGLContext mEGLcontext) {
        mListener = listener;
        pcParams = type;
        PeerConnectionFactory.initializeAndroidGlobals(listener, true, true,
                videoCodecHwAcceleration, mEGLcontext);
        factory = new PeerConnectionFactory();

    }


    public void init() {

    }

    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (videoSource != null) videoSource.stop();
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        RTCPeerManager.getInstance().onDestory();

        videoSource.dispose();
        factory.dispose();
    }


    /**
     * Start the client.
     * <p>
     * Set up the local stream and notify the signaling server.
     * Call this method after onCallReady.
     *
     * @param name client name
     */
    public void start(String name) {
        setCamera();
        try {
            JSONObject message = new JSONObject();
            message.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCamera() {
        localMS = factory.createLocalMediaStream("ARDAMS");
        if (videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.getHeight())));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.getWidth())));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.getFps())));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.getFps())));
            videoSource = factory.createVideoSource(getVideoCapturer(), videoConstraints);
            localMS.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));
        }

        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localMS.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));

        RTCPeerManager.getInstance().setLocalMediaStream(localMS);

        mListener.onLocalStream(localMS);
    }

    private VideoCapturer getVideoCapturer() {
        String frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        return VideoCapturerAndroid.create(frontCameraDeviceName);
    }

    public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, MediaConstraints pcConstraints, PeerConnection.Observer observer) {
        return factory.createPeerConnection(iceServers, pcConstraints, observer);
    }
}
