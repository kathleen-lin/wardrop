package tfip.mini_project.server.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import tfip.mini_project.server.Model.Item;
import tfip.mini_project.server.Payload.reasonPayload;
import tfip.mini_project.server.Repository.itemRepo;
import tfip.mini_project.server.Repository.s3Repo;
import tfip.mini_project.server.Service.GoogleDriveAuthService;
import tfip.mini_project.server.Service.imaggaSvc;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private itemRepo itemRepo;

    @Autowired
    private s3Repo s3Repo;

    @Autowired
    private imaggaSvc imaggaSvc;

    @Autowired
    private GoogleDriveAuthService authSvc;
  
  
    @CrossOrigin(origins = "*")
    @GetMapping("item/{id}")
    public ResponseEntity<String> getItemById (@PathVariable int id){

        Optional<Item> optItm = itemRepo.getItemById(id);

        if (optItm.isEmpty()){

            return new ResponseEntity<String>("Can't find", HttpStatus.NOT_FOUND);
            
        }

        System.out.println("THere's something");
        Item itm = optItm.get();
        

        return new ResponseEntity<String>(itm.toJson().toString(), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping
    public ResponseEntity<String> getItemsByCategory(@RequestParam("category") String category, @RequestParam("user") String userName){


        try {
            Optional<List<Item>> optListItms = itemRepo.getItemListByCategory(category, userName);

            if (optListItms.isEmpty()){
            
                return new ResponseEntity<String>("There is nothing in this category yet", HttpStatus.OK);
            }
            
            List<Item> itms = optListItms.get();

            JsonArrayBuilder builder = Json.createArrayBuilder();
            for(Item i: itms) {
                builder.add(i.toJson());
            }
            JsonArray jArray = builder.build();

            return new ResponseEntity<String>(jArray.toString(), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<String>("something is wrong", HttpStatus.BAD_REQUEST);
        }
                
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path="/upload/details", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postItem(@RequestPart String photoUrl, @RequestPart String description, @RequestPart String price, @RequestPart String purchaseOn, @RequestPart String timeWorn, @RequestPart String category, @RequestPart String userName) {

        // System.out.println(photo.getOriginalFilename());
        // System.out.println(description);
        // System.out.println(price);
        // System.out.println(purchaseOn);
        // System.out.println(timeWorn);
        // System.out.println(category);

        Float fPrice = Float.parseFloat(price);
        Date dPurchaseOn = Date.valueOf(purchaseOn);
        int iTimeWorn = Integer.parseInt(timeWorn);

        

        try {
            itemRepo.upload(photoUrl, description, fPrice, dPurchaseOn, iTimeWorn, category, userName);

        } catch (Exception e) {

            JsonObject jo = Json.createObjectBuilder()
                        .add("message", "did not upload to MySQL!")
                        .build();
            return new ResponseEntity<String>(jo.toString(), HttpStatus.BAD_GATEWAY);

        }
        JsonObject jo = Json.createObjectBuilder()
                        .add("message", "succeed!")
                        .build();
        return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
    }


    // }     
    @CrossOrigin(origins = "*")
    @PostMapping(path="/uploadImage", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhotoS3(@RequestPart MultipartFile photo) {

        
        String photoUrl = "";
        try {
           s3Repo.uploadImage(photo);
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<String>("Failed to upload to s3", HttpStatus.BAD_GATEWAY);

        }
        JsonObject jo = Json.createObjectBuilder()
                        .add("message", "succeed!")
                        .add("photoUrl", photoUrl)
                        .build();
        return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);


    }     

    @CrossOrigin(origins = "*")
    @PutMapping("item/{id}")
    public ResponseEntity<String> increaseTimeWorn(@PathVariable int id){
        
        System.out.println("Front end called put mapping");
        try {
            itemRepo.increaseTimeWorn(id);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_GATEWAY);
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "item/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> archiveItem(@PathVariable String id, @RequestBody reasonPayload payload){
        
        int userId = Integer.parseInt(id);
        String reason = payload.getReason();
        System.out.println(userId);
        System.out.println(reason);
        
        try {
            Item i = itemRepo.getItemById(userId).get();

            itemRepo.archiveDeleted(i, reason);
            itemRepo.deleteItem(userId);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_GATEWAY);
        }
        JsonObject jo = Json.createObjectBuilder()
                        .add("message", "succefully archived!")
                        .build();

        return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);

    }
    // localhost8080/api/analyse?image=tights.jpg
    @CrossOrigin(origins = "*")
    @GetMapping("/analyse")
    public ResponseEntity<String> callApi (@RequestParam("i") String fileName){
        
        // System.out.println("passed from front end: " + fileName);
        String imageUrl = "https://waredrop.sgp1.digitaloceanspaces.com/" + fileName;
        
        // System.out.println(itemDescription);
        try {
            String tag =imaggaSvc.extractTag(imaggaSvc.tagEndpoint(imageUrl));
            String color = imaggaSvc.extractColor(imaggaSvc.colorEndpoint(imageUrl));
            String itemDescription = color + " " + tag;

            JsonObject respOb = Json.createObjectBuilder()
                                .add("itemDescription", itemDescription)
                                .build();
            return new ResponseEntity<String>(respOb.toString(), HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_GATEWAY);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/drive")
    public ResponseEntity<String> authenticate() throws IOException {
        
        boolean isUserAuthenticated = false;
        GoogleAuthorizationCodeFlow flow = authSvc.getFlow();
        Credential cred = flow.loadCredential("user3");

        if (cred!=null) {
            boolean tokenValid = cred.refreshToken();
            if (tokenValid){
                isUserAuthenticated = true;
            }
        }

        if (isUserAuthenticated){
            JsonObject respOb = Json.createObjectBuilder()
                                .add("nextUrl", "drive/home")
                                .build();
            return new ResponseEntity<String>(respOb.toString(), HttpStatus.OK);
        }

            JsonObject respOb = Json.createObjectBuilder()
                                .add("nextUrl", "drive/signin")
                                .build();
            return new ResponseEntity<String>(respOb.toString(), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/drive/signin")
    public ResponseEntity<String> doSignin() throws IOException {

        GoogleAuthorizationCodeRequestUrl url = authSvc.getFlow().newAuthorizationUrl();
        String redirectUrl = url.setRedirectUri("http://localhost:8080/api/oauth").setAccessType("offline").build();
            JsonObject respOb = Json.createObjectBuilder()
                                .add("redirectUrl", redirectUrl)
                                .build();

        return new ResponseEntity<String>(respOb.toString(), HttpStatus.OK);

    }

    @CrossOrigin(origins = "*")
    @GetMapping("/oauth")
    public ResponseEntity<String> saveAuthCode(@RequestParam("code") String code) throws IOException {
        System.out.println(code);
        if (code != null){
            saveToken(code);
            JsonObject respOb = Json.createObjectBuilder()
                                .add("message", "you can now close the window")
                                .build();
        return new ResponseEntity<String>(respOb.toString(), HttpStatus.OK);
        }

         JsonObject respOb = Json.createObjectBuilder()
                                .add("message","something wrong")
                                .build();
        return new ResponseEntity<String>(respOb.toString(), HttpStatus.BAD_REQUEST);
    }

    private void saveToken(String code) throws IOException{
        GoogleTokenResponse response = authSvc.getFlow().newTokenRequest(code).setRedirectUri("http://localhost:8080/api/oauth").execute();
        
        authSvc.getFlow().createAndStoreCredential(response, "user3");        

    }
    
    @CrossOrigin(origins = "*")
    @GetMapping("/drive/home")
    public void listFilesInFolder() throws IOException, GeneralSecurityException {
        

    // public Drive createDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authSvc.getFlow().loadCredential("user3");
        JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("wardrop")
                .build();


        String folderId = getFolderIdByName("OOTD");

        System.out.println(folderId);

        if (folderId == null) {
        System.out.println("Folder not found: " + "OOTD");
        // return Collections.emptyList();
        }
        
        String query = "'" + folderId + "' in parents";
        FileList result = drive.files().list()
            .setQ(query)
            .setFields("nextPageToken, files(id, name)")
            .execute();

        System.out.println(result.toString());
    //     List<File> files = result.getFiles();
    //     if (files != null && !files.isEmpty()) {
    //     System.out.println("Files in Folder:");
    //     for (File file : files) {
    //         System.out.println("File Name: " + file.getName());
    //     }
    // } else {
    //     System.out.println("No files found in the folder.");
    // }
    }

    private String getFolderIdByName(String folderName) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authSvc.getFlow().loadCredential("user3");
        JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("wardrop")
                .build();

        String query = "mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'";
        FileList result = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id)")
            .execute();
        List<File> files = result.getFiles();
        
        return files.isEmpty() ? null : files.get(0).getId();
    }

    





    
}
