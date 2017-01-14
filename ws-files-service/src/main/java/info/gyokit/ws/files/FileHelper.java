package info.gyokit.ws.files;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author Gyokay Ali
 */
public class FileHelper {

    public static void saveToDatFile(UUID fileNameUUID, byte[] content, String StorageDirectoryPath) {
        FileOutputStream fos = null;
        try {
            //Convert byte array to a new file and save it
            File file = new File(StorageDirectoryPath + File.separator + fileNameUUID + ".dat");
            file.getParentFile().mkdirs(); //if parent directories does not exist, will be created
            fos = new FileOutputStream(file);
            fos.write(content);
            fos.flush();
        } catch (FileNotFoundException fex) {
            System.out.println("File Not Found: " + fex.getMessage());
        } catch (IOException ex) {
            System.out.println("A problem has occured while I/O. Exception: " + ex.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                System.out.println("A problem has occured while closing stream. Exception: " + ex.getMessage());
            }
        }
    }

    public static void saveToInfoFile(UUID fileNameUUID, String fileName, String contentLength, String mimeType, String StorageDirectoryPath) {
        try {
            //save received info as a list
            List<String> fileInfoList = Arrays.asList("File name:" + fileName, "Content-Length:" + contentLength, "MIME Type:" + mimeType);
            //create a path the file to be saved
            Path filePath = Paths.get(StorageDirectoryPath + File.separator + fileNameUUID + ".info");
            //create the files using the info provided above
            Files.write(filePath, fileInfoList, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            System.out.println("A problem has occured while I/O. Exception: " + ex.getMessage());
        }
    }

    public static void saveDataToDir(UUID fileNameUUID, String fileName, String contentLength, byte[] content, String mimeType, String StorageDirectoryPath) {
        try {
            //creates a .dat file using the content of a received file
            saveToDatFile(fileNameUUID, content, StorageDirectoryPath);
            //creates a .info file using the information about the file
            saveToInfoFile(fileNameUUID, fileName, contentLength, mimeType, StorageDirectoryPath);
        } catch (Exception ex) {
            System.out.println("Problem occured while saving data to directory. Exception: " + ex.getMessage());
        }
    }
}

