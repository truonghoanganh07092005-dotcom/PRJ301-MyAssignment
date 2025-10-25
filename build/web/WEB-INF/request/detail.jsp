<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  model.RequestForLeave r = (model.RequestForLeave) request.getAttribute("req");
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Chi tiết đơn</title></head>
<body style="font-family:system-ui">
  <h2>Chi tiết đơn #<%= r.getRid() %></h2>
  <div>Người tạo: <b><%= r.getCreated_by().getName() %></b></div>
  <div>Khoảng: <b><%= r.getFrom() %></b> – <b><%= r.getTo() %></b></div>
  <div>Trạng thái: <b><%= r.getStatus() %></b></div>
  <div>Lý do:</div>
  <pre><%= r.getReason() %></pre>
  <p><a href="<%=ctx%>/home">Về trang chủ</a></p>
</body>
</html>
