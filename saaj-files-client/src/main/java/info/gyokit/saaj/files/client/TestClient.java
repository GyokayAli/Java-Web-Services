package info.gyokit.saaj.files.client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

/**
 *
 * @author Gyokay Ali
 */
public class TestClient {
    //target service URL
    private static final String SERVICE_URL = "http://localhost:8080/saaj-files-service/SendFilesService";

    public static void main(String[] args) throws MalformedURLException {
        
        File file = new File("C:\\Users\\gali\\Downloads\\512MB.zip");

        //start
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            doTest(file);
        }

        long stopTime = System.currentTimeMillis();//end
        double elapsedTime = stopTime - startTime; //total elapsed
        System.out.println("Time: " + elapsedTime/1000);
    }

    public static void doTest(File file) {
        // Generate service 
        QName port = new QName("http://service.files.saaj.gyokit.info", "SendFilesPort");
        Service service = Service.create(
                new QName("http://service.files.saaj.gyokit.info", "SendFilesService"));

        // Add a port to the service
        service.addPort(port, SOAPBinding.SOAP11HTTP_BINDING, SERVICE_URL);

        // Generate Dispatch object
        Dispatch<SOAPMessage> dispatch = service.createDispatch(
                port, SOAPMessage.class, Service.Mode.MESSAGE);

        // Request message
        SOAPMessage soapRequest = null;

        try {
            // Generate request message
            soapRequest = MessageFactory.newInstance().createMessage();
            SOAPBody soapBody = soapRequest.getSOAPBody();
            // Set up file name
            SOAPBodyElement requestRoot = soapBody.addBodyElement(
                    new QName("http://service.files.saaj.gyokit.info", "file_name"));
            SOAPElement soapElement = requestRoot.addChildElement(
                    new QName("http://service.files.saaj.gyokit.info", "value"));
            soapElement.addTextNode(file.getName());
            
            // Set up attachment (file)
            URL fileURL = file.toURI().toURL();
            DataHandler dataHandler = new DataHandler(fileURL);
            AttachmentPart attachment
                    = soapRequest.createAttachmentPart(dataHandler);
            attachment.setContentId("attached_file");
            soapRequest.addAttachmentPart(attachment);
            
            // Sending and receiving SOAP Messages
            SOAPMessage soapResponse = dispatch.invoke(soapRequest);
            // Acquire data from the response message
            SOAPBody responseSoapBody = soapResponse.getSOAPBody();
            SOAPBodyElement responseRoot
                    = (SOAPBodyElement) responseSoapBody.getChildElements().next();
            Iterator iterator = responseRoot.getChildElements();
            //response content (file UUID)
            String result
                    = ((SOAPElement) iterator.next()).getFirstChild().getNodeValue();

            // Display the response message (file UUID)
            System.out.println(result);
        } catch (SOAPException e) {
            System.out.println("SOAP Exception: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e.getMessage());
        }
    }
}
