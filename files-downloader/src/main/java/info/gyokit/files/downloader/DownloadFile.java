package info.gyokit.files.downloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Gali
 */
@WebServlet(urlPatterns = {"/show/file/*", "/download/file/*"})
public class DownloadFile extends HttpServlet {

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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        String fileName, mimeType;
        int contentLength;
        
        String servletPath = request.getServletPath();

        String uuid = request.getPathInfo().substring(1);//gets the file uuid given by the user

        String fileToSend = "C:\\work\\tmpdata\\" + uuid + ".dat";
        
        try {
            //read file information from .info
            BufferedReader bf = new BufferedReader(new FileReader("C:\\work\\tmpdata\\" + uuid + ".info"));
            String line;
            List<String> list = new ArrayList<String>();
            while ((line = bf.readLine()) != null) {
                list.add(line);
            }

            fileName = list.get(0).substring(list.get(0).indexOf(":") + 1); //File name:fileName (example)
            contentLength = Integer.parseInt(list.get(1).substring(list.get(1).indexOf(":") + 1));//Content-Lenght:2413142 (example)
            mimeType = list.get(2).substring(list.get(2).indexOf(":") + 1);//MIME Type:application/pdf (example)

            //tell the browser the file type you are going to send
            response.setContentType(mimeType);
            //set file's content length
            response.setContentLength(contentLength);

            //http://localhost:8080/files-downloader/show/file/ + uuid
            if ("/show/file".equalsIgnoreCase(servletPath)) {
                //to open the file in the browser directly
                response.setHeader("Content-disposition", "inline; filename=" + fileName);
            }
            //http://localhost:8080/files-downloader/download/file/ + uuid
            if ("/download/file".equalsIgnoreCase(servletPath)) {
                //make sure to show the download dialog
                response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            }

            //send the file
            sendFile(fileToSend, response);

        } catch (IOException ex) {
            System.out.println("A problem has occured while reading the file info: " + ex.getMessage());
        }
    }

    public static void sendFile(String fileToSend, HttpServletResponse response) {
        OutputStream out = null;
        try {
            // Assume file name is retrieved from file system
            File file = new File(fileToSend);
            // This should send the file to browser
            out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            System.out.println("A problem has occured while writing to file: " + ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                System.out.println("A problem has occured while closing the Output Stream: " + ex.getMessage());
            }
        }
    }
}
