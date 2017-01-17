package info.gyokit.saaj.files.service;

import info.gyokit.saaj.files.FileHelper;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author Gyokay Ali
 */
@WebServiceProvider(serviceName = "SendFilesService")
@ServiceMode(value = Service.Mode.MESSAGE)
public class SendFilesImpl implements Provider<SOAPMessage> {

    @Resource
    private WebServiceContext context;

    @Override
    public SOAPMessage invoke(SOAPMessage soapRequest) {

        // Response message
        SOAPMessage soapResponse = null;
        // Attachment (file)
        AttachmentPart attachment = null;
        try {
            // Data acquisition from the request message
            // Acquire the file name
            SOAPBody soapBody = soapRequest.getSOAPBody();
            SOAPBodyElement requestRoot
                    = (SOAPBodyElement) soapBody.getChildElements().next();
            Iterator fileIterator = requestRoot.getChildElements();
            //get the original file name from the request
            String fileName = ((SOAPElement) fileIterator.next()).getFirstChild().getNodeValue();

            // Acquire the file
            Iterator attachment_iterator = soapRequest.getAttachments();
            while (attachment_iterator.hasNext()) {
                attachment = (AttachmentPart) attachment_iterator.next();
            }

            // Generating a response message
            soapResponse = MessageFactory.newInstance().createMessage();
            SOAPBody responseSoapBody = soapResponse.getSOAPBody();
            SOAPBodyElement responseRoot = responseSoapBody.addBodyElement(
                    new QName("http://service.files.saaj.gyokit.info", "uuid"));
            SOAPElement soapElement = responseRoot.addChildElement(
                    new QName("http://service.files.saaj.gyokit.info", "value"));

            // Set up a basic validation
            if (null == attachment) {
                soapElement.addTextNode("No file attached!");
            } else {
                //generate UUID to be the file name
                UUID fileNameUUID = UUID.randomUUID();
                
                //stream to carry the attachment content
                InputStream inputStream = attachment.getRawContent();
                
                //get MIME from attachment
                String mimeType = attachment.getContentType();

                //initialize and get the context
                ServletContext servletContext
                        = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

                //gets the directory to keep the files received by the Web Service
                String fileDir = servletContext.getInitParameter("FILEDIR");

                //check if the directory given in the context exists, else throws exception
                if (fileDir == null || fileDir.isEmpty()) {
                    throw new java.lang.IllegalArgumentException("FILEDIR Initialization Parameter is empty. Please configure it in web.xml!");
                }

                //save received data into files
                FileHelper.saveDataToDir(fileNameUUID, fileName, inputStream, mimeType, fileDir);
                //set response message for uuid
                soapElement.addTextNode(fileNameUUID.toString());
            }
        } catch (SOAPException e) {
            System.out.println("SOAP Exception: " + e.getMessage());
        }
        return soapResponse;
    }
}
