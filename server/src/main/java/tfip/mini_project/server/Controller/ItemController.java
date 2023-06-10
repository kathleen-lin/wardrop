package tfip.mini_project.server.Controller;

import java.sql.Date;
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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import tfip.mini_project.server.Model.Item;
import tfip.mini_project.server.Payload.reasonPayload;
import tfip.mini_project.server.Repository.itemRepo;
import tfip.mini_project.server.Repository.s3Repo;
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

    // @PostMapping(path="/upload/details", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<String> postItem(@RequestPart MultipartFile photo, @RequestPart String description, @RequestPart String price, @RequestPart String purchaseOn, @RequestPart String timeWorn, @RequestPart String category, @RequestPart String userName) {

    //     System.out.println(photo.getOriginalFilename());
    //     System.out.println(description);
    //     System.out.println(price);
    //     System.out.println(purchaseOn);
    //     System.out.println(timeWorn);
    //     System.out.println(category);

    //     Float fPrice = Float.parseFloat(price);
    //     Date dPurchaseOn = Date.valueOf(purchaseOn);
    //     int iTimeWorn = Integer.parseInt(timeWorn);

    //     String photoName = description.replace(" ", "_").toLowerCase();

    //     if (photoName.length() > 20) {
    //         photoName = photoName.substring(0, 20);
    //     }

    //     try {
    //         if (s3Repo.upload(photo, description, category, photoName))
    //         {
    //             // https://waredrop.sgp1.digitaloceanspaces.com/sport_tights
    //             String photoUrl = "https://waredrop.sgp1.digitaloceanspaces.com/" + photoName;
    //             itemRepo.upload(photoUrl, description, fPrice, dPurchaseOn, iTimeWorn, category, userName);
    //         }
    //         else {
    //             System.out.println("did not upload to s3");
    //         }
    //     } catch (Exception e) {

    //         e.printStackTrace();
    //         return new ResponseEntity<String>("Failed, go troubleshoot zzz", HttpStatus.BAD_GATEWAY);

    //     }
    //     JsonObject jo = Json.createObjectBuilder()
    //                     .add("message", "succeed!")
    //                     .build();
    //     return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);


    // }     
    @CrossOrigin(origins = "*")
    @PostMapping(path="/uploadImage", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhotoS3(@RequestPart MultipartFile photo) {

        // JsonObject jo = Json.createObjectBuilder()
        //                 .add("name", photo.getOriginalFilename())
        //                 .add("type", photo.getContentType())
        //                 .build();
        // return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
        String photoUrl = "";
        try {
            if (s3Repo.uploadImage(photo))
            {
                // https://waredrop.sgp1.digitaloceanspaces.com/sport_tights
                photoUrl = "https://waredrop.sgp1.digitaloceanspaces.com/" + photo.getOriginalFilename();
            }
            else {
                System.out.println("did not upload to s3");
            }
        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<String>("Failed, go troubleshoot zzz", HttpStatus.BAD_GATEWAY);

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

    // GET http://localhost:4200/localhost8080/api/analyse?image=tights.jpg
    @CrossOrigin(origins = "*")
    @GetMapping("analyse")
    public void callApi (@RequestParam("image") String filename){

        String imageUrl = "https://waredrop.sgp1.digitaloceanspaces.com/" + filename;
        imaggaSvc.tagEndpoint(imageUrl);
        
    }

    
}
