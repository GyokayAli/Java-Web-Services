package info.gyokit.ws.files.service;

import info.gyokit.ws.files.FileHelper;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jws.*;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author Gyokay Ali
 */
@WebService
public class SendFilesService {

    @Resource
    private WebServiceContext context;

    @WebMethod
    public String sendFile(String fileName, String contentLength, byte[] content, String mimeType) {

        //generate UUID
        UUID fileNameUUID = UUID.randomUUID();

        //initialize and get the context
        ServletContext servletContext
                = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

        //gets the directory to keep the files received by the Web Service
        String fileDir = servletContext.getInitParameter("FILEDIR");

        //check if the directory given in the context exists, else throws exception
        if (fileDir == null || fileDir.isEmpty()) {
            throw new java.lang.IllegalArgumentException("FILEDIR Initialization Parameter is empty. Please configure it in web.xml!");
        }

        //creates/saves .data and .info files to the given directory       
        FileHelper.saveDataToDir(fileNameUUID, fileName, contentLength, content, mimeType, fileDir);

        //returns the unique file ID as a string to the client
        return fileNameUUID.toString();
    }
}
