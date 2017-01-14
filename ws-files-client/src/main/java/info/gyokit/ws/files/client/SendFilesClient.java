package info.gyokit.ws.files.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Gali
 */
public class SendFilesClient {

    public static void main(String[] args) {

        //start
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 1; i <= 1; i++) { //Test 1000/10000 iterations
                doTest("./256kb.pdf");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        long stopTime = System.currentTimeMillis();//end
        double elapsedTime = stopTime - startTime; //total elapsed
        System.out.println("Time: " + elapsedTime/1000);
    }

    public static void doTest(String fileURL) {

        SendFilesServiceService sendFilesService = new SendFilesServiceService();
        SendFilesService port = sendFilesService.getSendFilesServicePort();

        try {
            File file = new File(fileURL); //get file
            String fileName = file.getName(); //get file name
            String contentLength = String.valueOf(file.length()); //get file's content length

            //file should be up to 2GB-1 in size
            byte[] fileBytes = Files.readAllBytes(Paths.get(fileURL));
            
            //get MIME Type of the selected file
            String mimeType = URLConnection.guessContentTypeFromName(fileName);

            //invoke the sendFile web method of the Web Service
            System.out.println(port.sendFile(fileName, contentLength, fileBytes, mimeType));

        } catch (FileNotFoundException fex) {
            System.out.println("File Not Found: " + fex.getMessage());
        } catch (IOException ex) {
            System.out.println("A problem has occured while I/O. Exception: " + ex.getMessage());
        }
    }
}
