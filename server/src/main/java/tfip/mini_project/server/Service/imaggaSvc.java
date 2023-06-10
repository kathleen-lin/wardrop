package tfip.mini_project.server.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class imaggaSvc {

    public void tagEndpoint(String imageUrl) {
        String credentialsToEncode = "acc_dad313c7620314f" + ":" + "8046b7e1df8ffa1ea7544f55355d4ec0";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String endpointUrl = "https://api.imagga.com/v2/tags";

        String url = UriComponentsBuilder.fromUriString(endpointUrl)
                                        .queryParam("image_url", imageUrl)
                                        .build().toUriString();


        System.out.println(">>> url (tag): " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> req = RequestEntity.get(url).headers(headers).build();

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = template.exchange(req, String.class);

        String payload = resp.getBody();
        String code = resp.getStatusCode().toString();

        System.out.println(code); 
        System.out.println(">>> tag " + payload);
    }

    public void colorEndpoint(String imageUrl) {
        String credentialsToEncode = "acc_dad313c7620314f" + ":" + "8046b7e1df8ffa1ea7544f55355d4ec0";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String endpointUrl = "https://api.imagga.com/v2/colors";

        String url = UriComponentsBuilder.fromUriString(endpointUrl)
                                        .queryParam("image_url", imageUrl)
                                        .build().toUriString();


        System.out.println(">>> url (color): " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> req = RequestEntity.get(url).headers(headers).build();

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = template.exchange(req, String.class);

        String payload = resp.getBody();
        String code = resp.getStatusCode().toString();

        System.out.println(code); 
        System.out.println(">>> color " + payload);
    }

    
    


}
