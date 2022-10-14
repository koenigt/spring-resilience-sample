package group.msg.playground.resilience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebClient targetServiceClient() {
        HttpClient httpClient = HttpClient.create().doOnConnected(
                conn -> conn.addHandlerLast(new ReadTimeoutHandler(3)).addHandlerLast(new WriteTimeoutHandler(5)));

        return WebClient.builder().baseUrl("http://localhost:8081")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Bean
    public WebClient targetServiceClientPlain() {
        return WebClient.builder().baseUrl("http://localhost:8081")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
    }
}
