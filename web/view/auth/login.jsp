<%-- 
    Document   : login
    Created on : Oct 18, 2025, 11:09:21 AM
    Author     : sonnt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
         <form action="login" method="POST">
            Username:<input type="text" name="username" id="txtUsername"/> <br/>
            Password:<input type="password" name="password" id="txtPassword"/><br/>
            <input type="submit" id="btnLogin" value="Login"/>
        </form>
    </body>
</html>
