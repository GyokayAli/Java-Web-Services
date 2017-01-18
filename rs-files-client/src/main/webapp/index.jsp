<%-- 
    Document   : index
    Created on : Aug 26, 2016, 9:59:09 AM
    Author     : Gyokay Ali
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Send File Page</title>
    </head>
    <body>
        <form action="http://localhost:8080/rs-files-service/webresources/SendFiles/sendFile" method="POST" enctype="multipart/form-data">
            Select a file:<input type="file" name="file" multiple />
            <input type="submit" value="Upload"/>
        </form>
    </body>
</html>