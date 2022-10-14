package group.msg.playground.resilience;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

/**
 * This Controller is simply returning an echo string and should be unsecured so it helps debugging environment or
 * connectivity issues which might be hard when user/pwd are needed everywhere.
 */
@RestController
@Slf4j
public class EchoController {

    @GetMapping(produces = "application/json")
    public String getEcho(@RequestHeader Map<String, String> headers,
            @RequestParam(defaultValue = "100") long duration) {
        log.info("called with duration: " + duration);
        headers.forEach((key, value) -> {
            log.trace(String.format("Header '%s' = %s", key, value));
        });
        try {
            log.debug("start sleeping: " + duration);
            long start = System.currentTimeMillis();
            Thread.sleep(duration);
            log.debug("finished sleeping: " + (System.currentTimeMillis() - start));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "{\"echo\":\"target server\"}";
    }

    @GetMapping(produces = "application/json", path = "/echoError")
    public String getEchoFailure(@RequestHeader Map<String, String> headers,
            @RequestParam(defaultValue = "404") int resCode) {
        log.info("called with response code: " + resCode);
        headers.forEach((key, value) -> {
            log.trace(String.format("Header '%s' = %s", key, value));
        });
        throw new ResponseStatusException(HttpStatus.valueOf(resCode));
    }
}
