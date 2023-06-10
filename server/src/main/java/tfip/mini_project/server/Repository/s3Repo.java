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

    public boolean uploadImage(MultipartFile photo) throws IOException {
        
        
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(photo.getContentType());
        metadata.setContentLength(photo.getSize());


		try {
				PutObjectRequest putReq = new PutObjectRequest("waredrop", photo.getOriginalFilename(), photo.getInputStream(), metadata);
				putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
                s3.putObject(putReq);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
                return false;
			}
        
            return true;
    }
}
