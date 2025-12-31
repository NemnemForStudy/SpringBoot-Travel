package travel.travel_Spring.Controller.board;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/naver")
public class BoardNaverController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/direction")
    public ResponseEntity<?> getRoute(
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam double goalLat,
            @RequestParam double goalLng
    ) {
        String clientId = "tqts1rasw6";
        String clientSecret = "m6RLgw5stLLkLKTC6VUJgCPXMiBC3gQnIROu07ut";

        String url = String.format(java.util.Locale.ROOT,
                "https://maps.apigw.ntruss.com/map-direction-15/v1/driving?start=%f,%f&goal=%f,%f&option=trafast",
                startLng, startLat, goalLng, goalLat
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-api-key-id", clientId);
        headers.set("x-ncp-apigw-api-key", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
        );

        return response; // JSON 그대로 프론트로
    }
}
