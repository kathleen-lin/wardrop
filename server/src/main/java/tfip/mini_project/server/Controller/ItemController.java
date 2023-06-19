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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.User;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
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

    private String gDriveUser;
  
  
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
            
                JsonArrayBuilder builder = Json.createArrayBuilder();
                JsonArray jArray = builder.build();
            
                
                return new ResponseEntity<String>(jArray.toString(), HttpStatus.OK);
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

        Boolean userExisit = itemRepo.checkIfUserExist(userName);

        try {
            
            itemRepo.upload(photoUrl, description, fPrice, dPurchaseOn, iTimeWorn, category, userName);

            if (!userExisit) {
                // create user
                itemRepo.insertUser(userName);

            }
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
    public ResponseEntity<String> authenticate(@RequestParam("user")String user) throws IOException {
        
        this.gDriveUser = user;
        
        boolean isUserAuthenticated = false;
        GoogleAuthorizationCodeFlow flow = authSvc.getFlow();
        Credential cred = flow.loadCredential(user);

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
        String redirectUrl = url.setRedirectUri("https://instinctive-celery-production.up.railway.app/api/oauth")
        .setAccessType("offline").build();
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
            saveToken(code, this.gDriveUser);
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

    private void saveToken(String code, String user) throws IOException{
        GoogleTokenResponse response = authSvc.getFlow().newTokenRequest(code).setRedirectUri("https://instinctive-celery-production.up.railway.app/api/oauth").execute();
        
        authSvc.getFlow().createAndStoreCredential(response, user);        

    }
    
    @CrossOrigin(origins = "*")
    @GetMapping("/drive/home")
    public ResponseEntity<String> listFilesInFolder(@RequestParam("user") String user) throws IOException, GeneralSecurityException {
        
        // create a drive
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authSvc.getFlow().loadCredential(user);
        JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("wardrop")
                .build();

        // folderId to be returned
        String folderId = createFolderIfNotExists("OOTD", user);

       // search files in the folder
        String query = "'" + folderId + "' in parents";
        FileList result = drive.files().list()
            .setQ(query)
            .setFields("nextPageToken, files(id, name)")
            .execute();
        
        List<File> files = result.getFiles();
        if (files != null && !files.isEmpty()) {
            // create a json array with the id
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();


            for (File file : files) {
            // add the file id to the json array
            jsonArrayBuilder.add(file.getId());
            }
            JsonArray jsonArray = jsonArrayBuilder.build();

            JsonObject jo = Json.createObjectBuilder()
                            .add("folderId", folderId)
                            .add("files", jsonArray)
                            .build();
            
            return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
        } 
        JsonArray jsonArray = Json.createArrayBuilder().build();
        JsonObject jo = Json.createObjectBuilder()
                            .add("folderId", folderId)
                            .add("files", jsonArray)
                            .build();
        
            return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);


    }

    // private String getFolderIdByName(String folderName) throws IOException, GeneralSecurityException {
    //     final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    //     Credential credential = authSvc.getFlow().loadCredential("user3");
    //     JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    //     Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
    //             .setApplicationName("wardrop")
    //             .build();

    //     String query = "mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'";
    //     FileList result = drive.files().list()
    //         .setQ(query)
    //         .setSpaces("drive")
    //         .setFields("files(id)")
    //         .execute();
    //     List<File> files = result.getFiles();
        
    //     return files.isEmpty() ? null : files.get(0).getId();
    // }
    @CrossOrigin(origins = "*")
    @PostMapping(path="/drive/upload", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public File uploadFile(@RequestPart String parentFolderId, @RequestPart String user, @RequestPart MultipartFile image) throws IOException, GeneralSecurityException {
        System.out.println(parentFolderId);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authSvc.getFlow().loadCredential(user);
        JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("wardrop")
                .build();

        File fileMetadata = new File();
        fileMetadata.setName(image.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(parentFolderId));

        java.io.File tempFile = java.io.File.createTempFile("temp", image.getOriginalFilename());
        image.transferTo(tempFile);

        FileContent mediaContent = new FileContent(image.getContentType(), tempFile);
        

        try {
            File file = drive.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();
            System.out.println("File ID: " + file.getId());
            return file;
        } catch (GoogleJsonResponseException e) {
        System.err.println("Unable to upload file: " + e.getDetails());
        throw e;
        } finally {
        if (tempFile != null) {
        tempFile.delete();
    }
}
    }

    
    private String createFolderIfNotExists(String folderName, String user) throws IOException, GeneralSecurityException {

        // create drive
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authSvc.getFlow().loadCredential(user);
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
        if (!files.isEmpty()) {
            return files.get(0).getId();
        }

        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File createdFolder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return createdFolder.getId();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getTop3")
    public ResponseEntity<String> getTop3(@RequestParam("user") String user){

        try {
            Optional<List<Item>> optListItms = itemRepo.getTop3Items(user);

            if (optListItms.isEmpty()){
            
                JsonArrayBuilder builder = Json.createArrayBuilder();
                JsonArray jArray = builder.build();
            
                
                return new ResponseEntity<String>(jArray.toString(), HttpStatus.OK);
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


    
}
