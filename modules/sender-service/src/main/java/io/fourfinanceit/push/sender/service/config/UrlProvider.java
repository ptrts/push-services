package io.fourfinanceit.push.sender.service.config;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import com.google.common.base.Throwables;

public class UrlProvider {

    private String url;

    public UrlProvider(String serviceUrl, String path) {
        try {
            url = new URIBuilder(serviceUrl)
                    .setPath(path)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    public String getUrl() {
        return url;
    }
}
