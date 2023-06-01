package tfip.mini_project.server.Repository;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class s3Repo {
    
    @Autowired
    private AmazonS3 s3;

    public boolean upload(MultipartFile photo, String description, String category, String photoUrl) throws IOException {
        
        Map<String, String> userData = new HashMap<>();
        
        userData.put("description", description);
		userData.put("category", category);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setUserMetadata(userData);
        metadata.setContentType(photo.getContentType());
        metadata.setContentLength(photo.getSize());

		
		
		try {
				PutObjectRequest putReq = new PutObjectRequest("waredrop", photoUrl, photo.getInputStream(), metadata);
				putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
                s3.putObject(putReq);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
                return false;
			}
        
            return true;
    }
}
