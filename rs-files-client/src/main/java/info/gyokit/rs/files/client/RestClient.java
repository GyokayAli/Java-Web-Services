package info.gyokit.rs.files.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Gali
 */
public class RestClient {
    
    //for Apache Commons FileUpload
    private static final String targetMultipartURL = "http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile/multipart";
    //for Apache Commons Streaming API
    private static final String targetMultipartURL2 = "http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile/multipart2";
    private static final String targetOctetStreamURL = "http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile/stream";

    public static void main(String[] args) {

        try {
            long startTime = System.currentTimeMillis();

            for (int i = 1; i <= 1; i++) { //test 1000x files
                testWebServiceMultipart("C:\\Users\\gali\\Downloads\\512MB.zip");
                //testWebServiceOctetStream("C:\\Users\\gali\\Downloads\\256kb.pdf");
            }

            long stopTime = System.currentTimeMillis();//end
            double elapsedTime = stopTime - startTime; //total elapsed
            System.out.println("\nTime: " + elapsedTime/1000);
        } catch (NullPointerException ex) {
            System.out.println("A problem has occured while testing in Main: " + ex.getMessage());
        }
    }

    public static void testWebServiceMultipart(String filePath) {
        HttpResponse httpResponse = null;

        try {
            File uploadFile = new File(filePath);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(targetMultipartURL2); //targetMultiparURL2 for Streaming API

            FileBody uploadFilePart = new FileBody(uploadFile);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file", uploadFilePart);

            httpPost.setEntity(reqEntity);
            httpResponse = httpclient.execute(httpPost);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Problem has occured while executing HTTP Post: " + ex.getMessage());
        }

        try {
            HttpEntity responseEntity = httpResponse.getEntity();
            //print response message
            if (responseEntity != null) {
                String responseMessage = EntityUtils.toString(responseEntity);
                System.out.println(responseMessage);
            }
        } catch (IOException ex) {
            System.out.println("Problem has occured while getting response from the server: " + ex.getMessage());
        }
    }

    public static void testWebServiceOctetStream(String filePath) {
        HttpResponse httpResponse = null;

        try {
            File uploadFile = new File(filePath);
            String fileName = uploadFile.getName();
            String mimeType = URLConnection.guessContentTypeFromName(fileName);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(targetOctetStreamURL);

            FileEntity reqEntity = new FileEntity(uploadFile, MediaType.APPLICATION_OCTET_STREAM);
            httpPost.setHeader("fileName", fileName);
            httpPost.setHeader("fileMime", mimeType);

            httpPost.setEntity(reqEntity);

            httpResponse = httpclient.execute(httpPost);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Problem has occured while executing HTTP Post: " + ex.getMessage());
        }

        try {
            HttpEntity responseEntity = httpResponse.getEntity();
            //print response message
            if (responseEntity != null) {
                String responseMessage = EntityUtils.toString(responseEntity);
                System.out.println(responseMessage);
            }
        } catch (IOException ex) {
            System.out.println("Problem has occured while getting response from the server: " + ex.getMessage());
        }
    }
}
