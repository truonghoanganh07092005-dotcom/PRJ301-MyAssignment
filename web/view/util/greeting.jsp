<%-- 
    Document   : greeting
    Created on : Oct 18, 2025, 11:10:55 AM
    Author     : sonnt
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <c:if test="${sessionScope.auth ne null}">
            Session of: ${sessionScope.auth.displayname}
            <br/>
            employee: ${sessionScope.auth.employee.id} - ${sessionScope.auth.employee.name}
        </c:if>
        <c:if test="${sessionScope.auth eq null}">
            You are not logged in yet!
        </c:if>    
    </body>
</html>
