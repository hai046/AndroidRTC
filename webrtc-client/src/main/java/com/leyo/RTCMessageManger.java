package com.leyo;

import android.text.TextUtils;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.pchab.webrtcclient.*;
import fr.pchab.webrtcclient.WebRtcClient;


/**
 * Created by haizhu on 16/5/21.
 */

public class RTCMessageManger {


    private static final String TAG = RTCMessageManger.class.getName();
    private static RTCMessageManger sRTCMessageManger;
    private String host;
    private String room;
    private String userId;
    private String token;
    private WebSocket ws;
    private List<String> userList = new ArrayList<>(6);


    private RTCMessageManger() {

    }


    public static RTCMessageManger getInstance() {
        if (sRTCMessageManger == null) {
            sRTCMessageManger = new RTCMessageManger();
        }
        return sRTCMessageManger;
    }

    public void setHost(String host) {
        this.host = host;
    }


    public List<String> getUsers() {
        return userList;
    }

    /**
     * Notice:please use background thread
     *
     * @param room
     * @param userId
     * @return
     * @throws Exception
     */
    public boolean start(String room, String userId) throws Exception {

        String token = getToken(room, userId);
        if (TextUtils.isEmpty(token)) {
            throw new Exception("不能获取token");
        }
        this.token = token;


        return startRtcMessage();

    }

    private boolean startRtcMessage() {
        do {
            if (TextUtils.isEmpty(token)) {
                Log.e(TAG, "startRtcMessage: token==null return ");
                break;
            }
            if (TextUtils.isEmpty(host)) {
                Log.e(TAG, "startRtcMessage: host==null return");
                break;
            }

            String wsHost = String.format("ws://%s/ws/%s", host, token);
            Log.i(TAG, "apiHost: " + wsHost);


            WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);

            try {
                ws = factory.createSocket(wsHost).addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
//                        第一次连接,服务器给我推过来信息
                        Log.i(TAG, "onTextMessage: text=" + text);

                        if (!TextUtils.isEmpty(text)) {
                            handlerWSMessage(text);
                        }
                    }
                });
                ws.connect();


            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (WebSocketException e) {
                e.printStackTrace();
                break;
            }
            return true;
        } while (false);

        return false;


    }


    private void sendMsg(String msg) {

        if (ws != null && ws.isOpen()) {
            ws.sendText(msg);
        } else {
            Log.i(TAG, "sendMsg: failure ws=" + ws);
        }

    }

    private void handlerWSMessage(String text) {

        try {
            JSONObject json = new JSONObject(text);
            String type = json.getString("type");

            if (SessionDescription.Type.ANSWER.name().equalsIgnoreCase(type)) {

                handlerPeers(json);


            } else if ("".equalsIgnoreCase(type)) {

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handlerPeers(JSONObject json) throws JSONException {

        //设计一定有message以及下面的user,不用判断
        JSONObject message = json.getJSONObject("message");
        JSONArray users = message.getJSONArray("users");


        List<String> userList = new ArrayList<>();
        for (int i = 0, lg = users.length(); i < lg; i++) {
            //如果user是对象
            userList.add(parseUser(users.get(i)));

        }

        this.userList = userList;


    }

    /**
     * 这里只是id,如果以后是user对象直接在这里解析UserBean即可
     *
     * @param o
     * @return
     */
    private String parseUser(Object o) {
        return String.valueOf(o);
    }


    private String getToken(String room, String userId) throws IOException {
        this.room = room;
        this.userId = userId;
        String apiHost = String.format("http://%s/token/%s/%s", host, room, userId);
        Log.i(TAG, "apiHost: " + apiHost);


        URL url = new URL(apiHost);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[8192];
        int lg = 0;
        while ((lg = in.read(buffer, 0, buffer.length)) > 0) {

            sb.append(new String(buffer, 0, lg));
        }

        in.close();

        String result = sb.toString();

        Log.i(TAG, "getToken: " + result);

        return result;
    }


    public void sendMessage(RTCPeerManager.Peer peer, SessionDescription sessionDescription) {


        try {
            String type = sessionDescription.type.canonicalForm();

            if (SessionDescription.Type.ANSWER.name().equalsIgnoreCase(type) ||
                    SessionDescription.Type.OFFER.name().equalsIgnoreCase(type)) {
                JSONObject jsonObject = new JSONObject();

                JSONObject jsonData = new JSONObject();

                JSONObject typeJson = new JSONObject();
                typeJson.put("type", type);
                typeJson.put("sdp", sessionDescription.description);

                jsonData.put("connectionId", peer.getSessionId());
                jsonData.put(type, typeJson);


                jsonObject.put("eventName", type);
                jsonObject.put("data", jsonData);

                for (String targetUserId : userList) {

                    if (userId.equals("targetUserId")) {
                        continue;
                    }
                    jsonObject.put("targetUserId", targetUserId);

                    String jsonMsg = jsonObject.toString();
                    Log.i(TAG, "sendMessage  send msg=" + jsonMsg + " targetUserId=" + targetUserId);

                    sendMsg(jsonMsg);
                }

            } else {
                Log.w(TAG, "sendMessage: not impl  sessionDescription=" + sessionDescription);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendCandidate(RTCPeerManager.Peer peer, IceCandidate candidate) {

        JSONObject jsonObject = new JSONObject();

        JSONObject jsonData = new JSONObject();

        JSONObject iceCandidateJson = new JSONObject();
        try {
            iceCandidateJson.put("sdp", candidate.sdp);
            iceCandidateJson.put("sdpMid", candidate.sdpMid);
            iceCandidateJson.put("sdpMLineIndex", candidate.sdpMLineIndex);


            jsonData.put("iceCandidate", iceCandidateJson);
            jsonData.put("connectionId", peer.getSessionId());


            jsonObject.put("eventName", "ice");
            jsonObject.put("data", jsonData);

            for (String targetUserId : userList) {

                if (userId.equals("targetUserId")) {
                    continue;
                }
                jsonObject.put("targetUserId", targetUserId);

                String jsonMsg = jsonObject.toString();
                Log.i(TAG, "sendMessage  ice send msg=" + jsonMsg + " targetUserId=" + targetUserId);

                sendMsg(jsonMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
//
//    private class CreateAnswerCommand implements Command {
//        public void execute(String peerId, JSONObject payload) throws JSONException {
//            Log.d(TAG, "CreateAnswerCommand");
//            RTCPeerManager.Peer peer = peers.get(peerId);
//            SessionDescription sdp = new SessionDescription(
//                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
//                    payload.getString("sdp")
//            );
//            peer.pc.setRemoteDescription(peer, sdp);
//            peer.pc.createAnswer(peer, pcConstraints);
//        }
//    }
//
//    private class SetRemoteSDPCommand implements Command {
//        public void execute(String peerId, JSONObject payload) throws JSONException {
//            Log.d(TAG, "SetRemoteSDPCommand");
//            RTCPeerManager.Peer peer = peers.get(peerId);
//            SessionDescription sdp = new SessionDescription(
//                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
//                    payload.getString("sdp")
//            );
//            peer.pc.setRemoteDescription(peer, sdp);
//        }
//    }
//
//    private class AddIceCandidateCommand implements Command {
//        public void execute(String peerId, JSONObject payload) throws JSONException {
//            Log.d(TAG, "AddIceCandidateCommand");
//            PeerConnection pc = peers.get(peerId).pc;
//            if (pc.getRemoteDescription() != null) {
//                IceCandidate candidate = new IceCandidate(
//                        payload.getString("id"),
//                        payload.getInt("label"),
//                        payload.getString("candidate")
//                );
//                pc.addIceCandidate(candidate);
//            }
//        }
//    }
//
//    /**
//     * Send a message through the signaling server
//     *
//     * @param to      id of recipient
//     * @param type    type of message
//     * @param payload payload of message
//     * @throws JSONException
//     */
//    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
//        JSONObject message = new JSONObject();
//        message.put("to", to);
//        message.put("type", type);
//        message.put("payload", payload);
//    }
//
//    private class MessageHandler {
//        private HashMap<String, Command> commandMap;
//
//        private MessageHandler() {
//            this.commandMap = new HashMap<>();
//            commandMap.put("init", new CreateOfferCommand());
//            commandMap.put("offer", new CreateAnswerCommand());
//            commandMap.put("answer", new SetRemoteSDPCommand());
//            commandMap.put("candidate", new AddIceCandidateCommand());
//        }
//
//        private Emitter.Listener onMessage = new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                JSONObject data = (JSONObject) args[0];
//                try {
//                    String from = data.getString("from");
//                    String type = data.getString("type");
//                    JSONObject payload = null;
//                    if (!type.equals("init")) {
//                        payload = data.getJSONObject("payload");
//                    }
//                    // if peer is unknown, try to add him
//                    if (!peers.containsKey(from)) {
//                        // if MAX_PEER is reach, ignore the call
//                        int endPoint = findEndPoint();
//                        if (endPoint != MAX_PEER) {
//                            RTCPeerManager.Peer peer = addPeer(from, endPoint);
//                            peer.pc.addStream(localMS);
//                            commandMap.get(type).execute(from, payload);
//                        }
//                    } else {
//                        commandMap.get(type).execute(from, payload);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        private Emitter.Listener onId = new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                String id = (String) args[0];
//                mListener.onCallReady(id);
//            }
//        };
//    }

}
