package tfip.mini_project.server.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;

@Service
public class GoogleDriveAuthService {

    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private GoogleAuthorizationCodeFlow flow;

    public GoogleDriveAuthService() throws IOException{
        InputStream in = GoogleDriveAuthService.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: credentials.json");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    public GoogleAuthorizationCodeFlow getFlow(){
        return this.flow;
    }
    // public Credential getCredentials(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY) throws IOException {
        

        
    //     LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(9000).build();
        
    //     return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    // }
}
