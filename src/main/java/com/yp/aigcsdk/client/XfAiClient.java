package com.yp.aigcsdk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yp.aigcsdk.listener.XfListener;
import com.yp.aigcsdk.model.dto.xf.XfRequestDto;
import com.yp.aigcsdk.model.enums.XfVersionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yp
 * @date: 2024/3/2
 */
@Data
@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class XfAiClient {
    private String appid;

    private String apiSecret;

    private String apiKey;

    private XfVersionEnum xfVersion;

    public WebSocket sendMsg(String question, XfListener xfListener) {
        // 1. 获得鉴权url
        String authUrl = this.getAuthUrl();
        if (authUrl == null) {
            return null;
        }
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient.Builder().build();

        // 2. 建立websocket连接
        WebSocket webSocket = client.newWebSocket(request, xfListener);

        XfRequestDto xfRequestDto = getXfRequestDto(question);

        // 4. 发送请求
        ObjectMapper objectMapper = new ObjectMapper();
        String string = null;
        try {
            string = objectMapper.writeValueAsString(xfRequestDto);
        } catch (JsonProcessingException e) {
            log.error("请求转化json异常");
            throw new RuntimeException(e);
        }
        webSocket.send(string);

        return webSocket;
    }


    /**
     * 鉴权方法，并获得请求的url
     */
    public String getAuthUrl() {
        try {
            String path = xfVersion.getPath();
            String hostUrl = "https://spark-api.xf-yun.com" + path;
            URL url = new URL(hostUrl);
            // 时间
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());
            // 拼接
            String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "GET " + url.getPath() + " HTTP/1.1";
            // System.err.println(preStr);
            // SHA256加密
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
            mac.init(spec);

            byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
            // Base64加密
            String sha = Base64.getEncoder().encodeToString(hexDigits);
            // System.err.println(sha);
            // 拼接
            String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
            // 拼接地址
            HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                    addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                    addQueryParameter("date", date).//
                    addQueryParameter("host", url.getHost()).//
                    build();

            // System.err.println(httpUrl.toString());
            return httpUrl.toString();
        } catch (Exception e) {
            log.info("获取鉴权路径时出错");
            return null;
        }
    }

    @NotNull
    private XfRequestDto getXfRequestDto(String question) {
        // 3. 组装请求参数
        XfRequestDto xfRequestDto = new XfRequestDto();
        // 3.1 header部分参数
        XfRequestDto.Header header = new XfRequestDto.Header();
        header.setAppId(appid);
        header.setUid(UUID.randomUUID().toString().substring(0, 10));
        // 3.2 parameter参数
        XfRequestDto.Parameter parameter = new XfRequestDto.Parameter();
        XfRequestDto.Parameter.Chat chat = new XfRequestDto.Parameter.Chat();
        chat.setDomain(xfVersion.getDomain());
        chat.setTemperature(0.5);       // 决定结果的随机性
        chat.setMaxTokens(4096);        // 回答的最大长度，根据请求模型不同限制不同 todo 根据版本判断来去最大长度值
        parameter.setChat(chat);
        // 3.3 payload参数
        XfRequestDto.Payload payload = new XfRequestDto.Payload();
        XfRequestDto.Payload.Message message = new XfRequestDto.Payload.Message();
        XfRequestDto.Payload.Message.Text text = new XfRequestDto.Payload.Message.Text();
        text.setRole("user");       // 系统预设也在这加
        text.setContent(question);
        message.setText(Collections.singletonList(text));
        payload.setMessage(message);

        xfRequestDto.setHeader(header);
        xfRequestDto.setPayload(payload);
        xfRequestDto.setParameter(parameter);
        return xfRequestDto;
    }
}
