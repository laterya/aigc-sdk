package com.yp.aigcsdk.model.enums;

import lombok.Getter;

/**
 * @author yp
 * @date: 2024/3/2
 */
@Getter
public enum XfVersionEnum {

    VOnePoint("/v1.1/chat", "general"),

    VTwo("/v2.1/chat", "generalv2"),

    VThree("/v3.1/chat", "generalv3"),

    VThreePoint("/v3.5/chat", "generalv3.5");

    private final String path;

    private final String domain;

    XfVersionEnum(String path, String domain) {
        this.path = path;
        this.domain = domain;
    }
}
