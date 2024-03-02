package com.yp.aigcsdk.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yp.aigcsdk.model.dto.xf.XfResponseDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author yp
 * @date: 2024/3/2
 */
@Slf4j
public class XfListener extends WebSocketListener {
    private StringBuilder answer = new StringBuilder();

    private boolean wsCloseFlag = false;

    public StringBuilder getAnswer() {
        return answer;
    }

    public boolean isWsCloseFlag() {
        return wsCloseFlag;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        log.info("大模型：");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        ObjectMapper objectMapper = new ObjectMapper();

        XfResponseDto xfResponseDto = null;
        try {
            xfResponseDto = objectMapper.readValue(text, XfResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("解析结果失败，大模型返回出错");
            throw new RuntimeException(e);
        }
        if (xfResponseDto.getHeader().getCode() != 0) {
            log.error("发生错误，错误码为：" + xfResponseDto.getHeader().getCode() + "; " + "信息：" + xfResponseDto.getHeader().getMessage());
            this.answer = new StringBuilder("大模型响应错误，请稍后再试");
            wsCloseFlag = true;
            return;
        }
        xfResponseDto.getPayload().getChoices().getText().forEach(texts -> {
            this.answer.append(texts.getContent());
        });
        if (xfResponseDto.getHeader().getStatus() == 2) {
            log.info("本次计费的tokens为 {}", xfResponseDto.getPayload().getUsage());
            wsCloseFlag = true;
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                log.error("onFailure code: {}, onFailure body: {}", code, response.body().string());
                if (101 != code) {
                    log.error("connection failed");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
