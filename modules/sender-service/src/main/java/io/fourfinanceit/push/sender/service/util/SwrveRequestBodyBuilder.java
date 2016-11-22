package io.fourfinanceit.push.sender.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.common.base.Charsets;

public class SwrveRequestBodyBuilder {

    private StringBuilder sb = new StringBuilder();

    public SwrveRequestBodyBuilder add(String key, String value) {
        String encodedValue = encode(value);
        if (sb.length() > 0) {
            sb.append('&');
        }
        sb.append(key)
                .append('=')
                .append(encodedValue);
        return this;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String build() {
        return sb.toString();
    }
}
