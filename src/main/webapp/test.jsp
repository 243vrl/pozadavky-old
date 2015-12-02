<%-- 
    Document   : test
    Created on : 2.12.2015, 17:48:56
    Author     : vena
--%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<%@ page import='java.sql.*' %>
<%@ page import='javax.sql.*' %>
<%@ page import='javax.naming.*' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Java Application Server Datasource Example</title>
</head>
<body>
<%
Connection result = null;
try {
    Context initialContext = new InitialContext();
    DataSource datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PostgreSQLDS");
    result = datasource.getConnection();
    Statement stmt = result.createStatement() ;
    String query = "select * from typy_sluzeb;" ;
    ResultSet rs = stmt.executeQuery(query) ;
    while (rs.next()) {
        out.println(rs.getString(1) + " " + rs.getString(2)+" "+"<br />");
    }
} catch (Exception ex) {
    out.println("Exception: " + ex + ex.getMessage());
}
%>
</body>
</html>