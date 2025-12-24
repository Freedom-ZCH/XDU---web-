package org.example.librarysystem.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * 在启用 HTTPS(8443) 的同时，再额外开启 HTTP(8080) 端口
 */
@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // 额外的 HTTP 连接器（8080）
        Connector httpConnector =
                new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        httpConnector.setScheme("http");
        httpConnector.setPort(8081);
        httpConnector.setSecure(false);

        factory.addAdditionalTomcatConnectors(httpConnector);
    }
}
