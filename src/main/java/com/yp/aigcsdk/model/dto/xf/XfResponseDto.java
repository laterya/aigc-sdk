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
public class XfResponseDto {

    @JsonProperty("header")
    private Header header;
    @JsonProperty("payload")
    private Payload payload;

    @NoArgsConstructor
    @Data
    public static class Header {
        @JsonProperty("code")
        private Integer code;
        @JsonProperty("message")
        private String message;
        @JsonProperty("sid")
        private String sid;
        @JsonProperty("status")
        private Integer status;
    }

    @NoArgsConstructor
    @Data
    public static class Payload {
        @JsonProperty("choices")
        private Choices choices;
        @JsonProperty("usage")
        private Usage usage;

        @NoArgsConstructor
        @Data
        public static class Choices {
            @JsonProperty("status")
            private Integer status;
            @JsonProperty("seq")
            private Integer seq;
            @JsonProperty("text")
            private List<Text> text;

            @NoArgsConstructor
            @Data
            public static class Text {
                @JsonProperty("content")
                private String content;
                @JsonProperty("role")
                private String role;
                @JsonProperty("index")
                private Integer index;
            }
        }

        @NoArgsConstructor
        @Data
        public static class Usage {
            @JsonProperty("text")
            private Text text;

            @NoArgsConstructor
            @Data
            public static class Text {
                @JsonProperty("question_tokens")
                private Integer questionTokens;
                @JsonProperty("prompt_tokens")
                private Integer promptTokens;
                @JsonProperty("completion_tokens")
                private Integer completionTokens;
                @JsonProperty("total_tokens")
                private Integer totalTokens;
            }
        }
    }
}
