package com.leyo;

import org.webrtc.MediaStream;

/**
 * Implement this interface to be notified of events.
 */
public interface RtcListener {
    void onCallReady(String callId);

    void onStatusChanged(String newStatus);

    void onLocalStream(MediaStream localStream);

    void onAddRemoteStream(MediaStream remoteStream, int endPoint);

    void onRemoveRemoteStream(int endPoint);
}
