package com.yp.aigcsdk.service.impl;

import com.yp.aigcsdk.client.XfAiClient;
import com.yp.aigcsdk.listener.XfListener;
import com.yp.aigcsdk.service.AiService;
import lombok.Setter;
import okhttp3.WebSocket;

/**
 * @author yp
 * @date: 2024/3/2
 */
public class XfAiServiceImpl implements AiService {

    @Setter
    public XfAiClient xfAiClient;

    @Override
    public String doChat(String question) {
        XfListener xfListener = new XfListener();
        WebSocket webSocket = xfAiClient.sendMsg(question, xfListener);
        try {
            // todo 设置最大超时时间
            while (true) {
                Thread.sleep(200);
                if (xfListener.isWsCloseFlag()) {
                    break;
                }
            }
            return xfListener.getAnswer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webSocket.close(1000, "");
        }
        return "";
    }

}
