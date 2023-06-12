// package tfip.mini_project.server.Service;

// import java.io.IOException;
// import java.security.GeneralSecurityException;
// import java.util.Collections;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.google.api.client.auth.oauth2.Credential;
// import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
// import com.google.api.client.http.FileContent;
// import com.google.api.client.http.javanet.NetHttpTransport;
// import com.google.api.client.json.JsonFactory;
// import com.google.api.client.json.gson.GsonFactory;
// import com.google.api.services.drive.Drive;
// import com.google.api.services.drive.model.File;
// import com.google.api.services.drive.model.FileList;

// @Service
// public class FileManager {
  
//   private static final String FOLDER_NAME = "OOTD";

//   // needs a drive

//   private final Drive driveService;

//   public FileManager(Drive driveService) {
//     this.driveService = driveService;
//   }

//   public List<File> listFilesInFolder() throws IOException {
//     String folderId = getFolderIdByName(FOLDER_NAME);
//     if (folderId == null) {
//       System.out.println("Folder not found: " + FOLDER_NAME);
//       return Collections.emptyList();
//     }

//     FileList result = driveService.files().list()
//         .setQ("'" + folderId + "' in parents")
//         .setFields("nextPageToken, files(id, name)")
//         .execute();
//     return result.getFiles();
//   }

//   private String getFolderIdByName(String folderName) throws IOException {
//     String query = "mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'";
//     FileList result = driveService.files().list()
//         .setQ(query)
//         .setSpaces("drive")
//         .setFields("files(id)")
//         .execute();
//     List<File> files = result.getFiles();
    
//     return files.isEmpty() ? null : files.get(0).getId();
//   }
  
//   public String uploadFile(MultipartFile file) throws IOException {
//         String folderId = createFolderIfNotExists(FOLDER_NAME);
//         if (folderId == null) {
//             System.out.println("Failed to create or find folder: " + FOLDER_NAME);
//             return null;
//         }

//         File fileMetadata = new File();
//         fileMetadata.setName(file.getOriginalFilename());
//         fileMetadata.setParents(Collections.singletonList(folderId));

//         java.io.File filePath = new java.io.File("/temp/" + file.getOriginalFilename());
//         file.transferTo(filePath);

//         FileContent mediaContent = new FileContent(file.getContentType(), filePath);
//         File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
//                 .setFields("id")
//                 .execute();

//         return uploadedFile.getId();
//     }

//     private String createFolderIfNotExists(String folderName) throws IOException {
//         String query = "mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'";
//         FileList result = driveService.files().list()
//                 .setQ(query)
//                 .setSpaces("drive")
//                 .setFields("files(id)")
//                 .execute();
//         List<File> files = result.getFiles();
//         if (!files.isEmpty()) {
//             return files.get(0).getId();
//         }

//         File folderMetadata = new File();
//         folderMetadata.setName(folderName);
//         folderMetadata.setMimeType("application/vnd.google-apps.folder");

//         File createdFolder = driveService.files().create(folderMetadata)
//                 .setFields("id")
//                 .execute();

//         return createdFolder.getId();
//     }
// }
