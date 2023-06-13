package tfip.mini_project.server.Service;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class imaggaSvc {

    public String tagEndpoint(String imageUrl) {
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

        return payload;
    }

    public String colorEndpoint(String imageUrl) {
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

        return payload;
    }

    public String extractColor(String payload) {

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jo = reader.readObject();

        JsonObject result = jo.getJsonObject("result");
        JsonObject colors = result.getJsonObject("colors");
        JsonArray foregroundColors = colors.getJsonArray("foreground_colors");
        JsonObject firstForegroundColor = foregroundColors.getJsonObject(0);

        String closestPaletteColorParent = firstForegroundColor.getString("closest_palette_color_parent");
        
        return closestPaletteColorParent;
    }
    
    public String extractTag(String payload) {

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject jo = reader.readObject();

        JsonObject result = jo.getJsonObject("result");
        JsonArray tags = result.getJsonArray("tags");
        JsonObject firstTag = tags.getJsonObject(0);
        JsonObject tag = firstTag.getJsonObject("tag");
        String enValue = tag.getString("en");
        
        return enValue;
    }
    


}



