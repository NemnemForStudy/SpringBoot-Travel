package travel.travel_Spring.Controller.Config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    
    // 사진 크기 20MB까지
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(20 * 1024 * 1024);
        });
    }
}
