package info.gyokit.rs.files.service;

import info.gyokit.rs.files.FileHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Gyokay Ali
 */
@Path("SendFiles")
public class SendFilesResource {

    /**
     * Creates a new instance of SendFilesResource
     */
    public SendFilesResource() {
    }

    //An example of Apache Commons FileUpload
    //Accessible at: http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile/multipart
    @POST
    @Path("/sendFile/multipart")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(value = MediaType.MULTIPART_FORM_DATA)
    public String sendFileAsMultipartFormData(@Context HttpServletRequest httpRequest, @Context ServletContext servletContext) {

        //gets the directory to keep the files received by the Web Service
        String destinationDir = getDestinationDir(servletContext);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(1024);
        factory.setRepository(new File(destinationDir));

        ServletFileUpload uploader = new ServletFileUpload(factory);

        UUID fileNameUUID = null;
        try {
            List<FileItem> items = uploader.parseRequest(httpRequest);
            Iterator<FileItem> iterator = items.iterator();

            while (iterator.hasNext()) {
                //generates unique file name
                fileNameUUID = UUID.randomUUID();
                //save each file in the specified directory
                FileHelper.saveFilesToDir(iterator, destinationDir, fileNameUUID.toString());
            }
        } catch (FileUploadException e) {
            System.out.println("File upload exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception was thrown: " + e.getMessage());
        }
        return fileNameUUID.toString();
    }

    //An example of Apache's Streaming API
    //Accessible at: http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile/multipart2
    @POST
    @Path("/sendFile/multipart2")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(value = MediaType.MULTIPART_FORM_DATA)
    public String sendFileAsMultipartFormData2(@Context HttpServletRequest httpRequest, @Context ServletContext servletContext) {

        //gets the directory to keep the files received by the Web Service
        String destinationDir = getDestinationDir(servletContext);

        ServletFileUpload uploader = new ServletFileUpload();

        UUID fileNameUUID = null;
        try {
            // Parse the request
            FileItemIterator iterator = uploader.getItemIterator(httpRequest);

            while (iterator.hasNext()) {
                //generates unique file name
                fileNameUUID = UUID.randomUUID();
                //save each file in the specified directory
                FileHelper.saveFilesToDir(iterator, destinationDir, fileNameUUID.toString());
            }
        } catch (FileUploadException e) {
            System.out.println("File upload exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception was thrown: " + e.getMessage());
        }
        return fileNameUUID.toString();
    }

    //Accessible at: http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile/stream
    @POST
    @Path("/sendFile/stream")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public String sendFileAsOctetStream(@Context HttpServletRequest httpRequest, @Context ServletContext servletContext) {
        UUID fileNameUUID = null;
        try {
            //gets the directory to keep the files received by the Web Service
            String destinationDir = getDestinationDir(servletContext);
            //generates unique file name
            fileNameUUID = UUID.randomUUID();
            InputStream inputStream = httpRequest.getInputStream();

            String fileName = httpRequest.getHeader("fileName");
            String mimeType = httpRequest.getHeader("fileMime");
            String contentLength = String.valueOf(httpRequest.getContentLength());

            FileHelper.saveFilesToDir(inputStream, destinationDir, fileNameUUID.toString(), fileName, contentLength, mimeType);
        } catch (IOException ex) {
            System.out.println("A problem has occured while reading the stream: " + ex.getMessage());
        }
        return fileNameUUID.toString();
    }

    //gets the directory to keep the files received by the Web Service
    public static String getDestinationDir(ServletContext servletContext) {
        //gets the directory to keep the files received by the Web Service
        String destinationDir = servletContext.getInitParameter("FILEDIR");

        //check if the directory given in the context exists, else throws exception
        if (destinationDir == null || destinationDir.isEmpty()) {
            throw new java.lang.IllegalArgumentException("FILEDIR Initialization Parameter is empty. Please configure it in web.xml!");
        }

        return destinationDir;
    }
}
