package tfip.mini_project.server.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class appConfig {

    @Value("${spaces.secret.key}")
	private String spacesSecretKey;
	@Value("${spaces.access.key}")
	private String spacesAccessKey;

	// @Value("${spaces.endpoint.url}")
	// private String spacesEndpointUrl;


    @Bean 
	public AmazonS3 createS3Client() {
		BasicAWSCredentials cred = new BasicAWSCredentials(spacesAccessKey, spacesSecretKey);
		EndpointConfiguration epConfig = new EndpointConfiguration("sgp1.digitaloceanspaces.com", "sgp1");

		return AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(epConfig)
				.withCredentials(new AWSStaticCredentialsProvider(cred))
				.build();
	}
    
}
