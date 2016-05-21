package com.leyo;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.leyo.RTCConstants.MAX_PEER;

/**
 * Created by haizhu on 16/5/21.
 */

public class RTCPeerManager {

    private MediaConstraints pcConstraints = new MediaConstraints();

    public static RTCPeerManager sRTCPeerManager;

    private HashMap<String, Peer> peers = new HashMap<>();

    private boolean[] endPoints = new boolean[MAX_PEER];

    private RtcListener mListener;
    private MediaStream localMS;
    private List<PeerConnection.IceServer> iceServers = new ArrayList();
    private WebRtcClient mWebRtcClient;

    private RTCPeerManager() {
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

    }

    public MediaConstraints getMediaConstraints() {
        return pcConstraints;
    }


    public void setListener(RtcListener mListener) {
        this.mListener = mListener;
    }

    public Peer addPeer(String id, int endPoint) {
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);

        endPoints[endPoint] = true;
        return peer;
    }

    public void removePeer(String id) {
        Peer peer = peers.get(id);
        mListener.onRemoveRemoteStream(peer.endPoint);
        peer.pc.close();
        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
    }

    public void onDestory() {
        for (Peer peer : peers.values()) {
            peer.pc.dispose();
        }
    }


    private int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    public void setLocalMediaStream(MediaStream localMS) {
        this.localMS = localMS;
    }

    public void setWebRtcClient(WebRtcClient webRtcClient) {
        mWebRtcClient = webRtcClient;
    }


    public static RTCPeerManager getInstance() {
        if (sRTCPeerManager == null) {
            sRTCPeerManager = new RTCPeerManager();
        }
        return sRTCPeerManager;
    }

    public void createOffer() {

    }

    public class Peer implements SdpObserver, PeerConnection.Observer {
        private final String sessionId;
        private PeerConnection pc;
        private String id;
        private int endPoint;

        public String getSessionId() {
            return sessionId;
        }

        public PeerConnection getPc() {
            return pc;
        }

        public String getId() {
            return id;
        }

        public int getEndPoint() {
            return endPoint;
        }

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            RTCMessageManger.getInstance().sendMessage(this, sdp);
            pc.setLocalDescription(Peer.this, sdp);
        }

        @Override
        public void onSetSuccess() {
        }

        @Override
        public void onCreateFailure(String s) {
        }

        @Override
        public void onSetFailure(String s) {
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                RTCPeerManager.getInstance().removePeer(id);
                mListener.onStatusChanged("DISCONNECTED");
            }
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {

            RTCMessageManger.getInstance().sendCandidate(this, candidate);

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d(TAG, "onAddStream " + mediaStream.label());
            // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
            mListener.onAddRemoteStream(mediaStream, endPoint + 1);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "onRemoveStream " + mediaStream.label());
            RTCPeerManager.getInstance().removePeer(id);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
        }

        @Override
        public void onRenegotiationNeeded() {

        }


        public Peer(String id, int endPoint) {
            Log.d(TAG, "new Peer: " + id + " " + endPoint);
            this.pc = mWebRtcClient.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;
            this.sessionId = UUID.randomUUID().toString();

            pc.addStream(localMS); //, new MediaConstraints()

            mListener.onStatusChanged("CONNECTING");
        }
    }

    public void setIceServer(List<String> iceServerHosts) {
        //NAT协议
        for (String h : iceServerHosts) {
            iceServers.add(new PeerConnection.IceServer(h));
        }

    }

}
