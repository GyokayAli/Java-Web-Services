package info.gyokit.files.servlet;

import info.gyokit.files.FileHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Gali
 */
@WebServlet(name = "SendFilesServlet", urlPatterns = {"/SendFilesServlet/send/file"})
public class SendFilesServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext servletContext = getServletContext();
        //gets the directory to keep the files received by the Web Service
        String destinationDir = servletContext.getInitParameter("FILEDIR");

        //check if the directory given in the context exists, else throws exception
        if (destinationDir == null || destinationDir.isEmpty()) {
            throw new java.lang.IllegalArgumentException("FILEDIR Initialization Parameter is empty. Please configure it in web.xml!");
        } else {
            ServletFileUpload uploader = new ServletFileUpload();

            UUID fileNameUUID = null;
            try {
                // Parse the request
                FileItemIterator iterator = uploader.getItemIterator(request);

                while (iterator.hasNext()) {
                    //generates unique file name
                    fileNameUUID = UUID.randomUUID();
                    //save each file in the specified directory
                    FileHelper.saveFilesToDir(iterator, destinationDir, fileNameUUID.toString());
                }
            } catch (FileUploadException e) {
                System.out.println("File upload exception: " + e.getMessage());
            }

            //send response to client
            response.setContentType(MediaType.TEXT_PLAIN);
            try (PrintWriter pw = response.getWriter()) {
                pw.append(fileNameUUID.toString());
            }
        }
    }
}
