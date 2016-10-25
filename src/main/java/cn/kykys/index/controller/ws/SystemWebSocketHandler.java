package cn.kykys.index.controller.ws;

import cn.kykys.index.constants.Constants;
import cn.kykys.index.model.ws.WSMessage;
import com.alibaba.fastjson.JSON;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kuangye on 2016/10/25.
 */
public class SystemWebSocketHandler implements WebSocketHandler {

    private static final ArrayList<WebSocketSession> users;

    private static final ArrayList<WSMessage> messageList;


    static {
        users = new ArrayList<>();
        messageList = new ArrayList<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("connect to the websocket success......");
        users.add(session);

        WSMessage wsMessage = new WSMessage();
        wsMessage.setName(session.getId());
        wsMessage.setContent("当前 " + users.size() + " 人 | 消息数 " + messageList.size() + " 。");
        wsMessage.setCreateTime(new Date());

        TextMessage text = new TextMessage(JSON.toJSONString(wsMessage));
        session.sendMessage(text);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        if (message instanceof TextMessage) {

            String receiveData = (String) message.getPayload();//从客户端接收到的数据

            if (receiveData.equals("count")) {

                WSMessage wsMessage = new WSMessage();
                wsMessage.setName(session.getId());
                wsMessage.setContent("当前 " + users.size() + " 人 | 消息数 " + messageList.size() + " 。");
                wsMessage.setCreateTime(new Date());
                messageList.add(wsMessage);


                TextMessage text = new TextMessage(JSON.toJSONString(wsMessage));

                this.sendMessageToUser(session.getId(), text);

            } else {

                WSMessage wsMessage = new WSMessage();
                wsMessage.setName(session.getId());
                wsMessage.setContent(receiveData);
                wsMessage.setCreateTime(new Date());
                messageList.add(wsMessage);


                TextMessage text = new TextMessage(JSON.toJSONString(wsMessage));
                this.sendMessageToUsers(text);
            }
        }

    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("websocket connection closed......");
        users.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("websocket connection closed......");
        users.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param sessionId
     * @param message
     */
    public void sendMessageToUser(String sessionId, TextMessage message) {
        for (WebSocketSession user : users) {
            if (user.getId().equals(sessionId)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}