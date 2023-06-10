package tfip.mini_project.server.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class imaggaSvc {

    public void tagEndpoint(String imageUrl) {
        String credentialsToEncode = "<replace-with-your-api-key>" + ":" + "<replace-with-your-api-secret>";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String endpointUrl = "https://api.imagga.com/v2/tags";

        String url = endpointUrl + "?image_url=" + imageUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        String jsonResponse = response.getBody();

        System.out.println("\nSending 'GET' request to URL: " + url);
        System.out.println(jsonResponse);
    }
    
}
