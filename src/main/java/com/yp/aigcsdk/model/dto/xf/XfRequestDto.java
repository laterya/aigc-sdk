package com.yp.aigcsdk.model.dto.xf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yp
 * @date: 2024/3/2
 */

@NoArgsConstructor
@Data
public class XfRequestDto {

    @JsonProperty("header")
    private Header header;
    @JsonProperty("parameter")
    private Parameter parameter;
    @JsonProperty("payload")
    private Payload payload;

    @NoArgsConstructor
    @Data
    public static class Header {
        @JsonProperty("app_id")
        private String appId;
        @JsonProperty("uid")
        private String uid;
    }

    @NoArgsConstructor
    @Data
    public static class Parameter {
        @JsonProperty("chat")
        private Chat chat;

        @NoArgsConstructor
        @Data
        public static class Chat {
            @JsonProperty("domain")
            private String domain;
            @JsonProperty("temperature")
            private Double temperature;
            @JsonProperty("max_tokens")
            private Integer maxTokens;
        }
    }

    @NoArgsConstructor
    @Data
    public static class Payload {
        @JsonProperty("message")
        private Message message;

        @NoArgsConstructor
        @Data
        public static class Message {
            @JsonProperty("text")
            private List<Text> text;

            @NoArgsConstructor
            @Data
            public static class Text {
                @JsonProperty("role")
                private String role;
                @JsonProperty("content")
                private String content;
            }
        }
    }
}
