package info.gyokit.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Gyokay Ali
 */
public class FileHelper {

    //uses Apache's Streaming API
    public static void saveFilesToDir(FileItemIterator iterator, String StorageDirectoryPath, String fileNameUUID) {

        try {
            FileItemStream item = iterator.next();
            String fileName = item.getName();//get current file's original name
            String mimeType = URLConnection.guessContentTypeFromName(item.getName()); //get current file's mime type

            InputStream inputStream = item.openStream();

            File file = new File(StorageDirectoryPath + File.separator + fileNameUUID + ".dat");

            //CREATE .dat FILE
            if (!item.isFormField()) {
                saveToDatFileFromStream(file, inputStream);
            }

            //get current file's content length
            String contentLength = String.valueOf(file.length());

            //CREATE .info FILE
            saveToInfoFile(fileNameUUID, StorageDirectoryPath, fileName, contentLength, mimeType);

        } catch (IOException ex) {
            System.out.println("A problem has occured while I/O. Exception: " + ex.getMessage());
        } catch (FileUploadException ex) {
            System.out.println("A problem has occured in FileItemStream: " + ex.getMessage());
        }
    }

    public static void saveToInfoFile(String fileNameUUID, String StorageDirectoryPath, String fileName, String contentLength, String mimeType) {
        try {
            //save received info as a list
            List<String> fileInfoList = Arrays.asList("File name:" + fileName,
                    "Content-Length:" + contentLength,
                    "MIME Type:" + mimeType);
            //create a path the file to be saved
            Path filePath = Paths.get(StorageDirectoryPath + File.separator + fileNameUUID + ".info");

            //create the files using the info provided above
            Files.write(filePath, fileInfoList, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            System.out.println("A problem has occured while writing to INFO file. Exception: " + ex.getMessage());
        }
    }

    public static void saveToDatFileFromStream(File file, InputStream inputStream) {
        OutputStream outputStream = null;
        try {
            file.getParentFile().mkdirs(); //if parent directories does not exist, will be created
            //create file from incoming stream's content
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException fex) {
            System.out.println("File Not Found: " + fex.getMessage());
        } catch (IOException ex) {
            System.out.println("A problem has occured while writing to DAT file. Exception: " + ex.getMessage());
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                System.out.println("A problem has occured while closing the output stream. Exception: " + ex.getMessage());
            }
        }
    }
}
