<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,model.Notification" %>
<%
    List<Notification> unread = (List<Notification>) request.getAttribute("unread");
    List<Notification> recent = (List<Notification>) request.getAttribute("recent");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Thông báo</title>
<style>
body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;margin:24px;color:#222}
h1{font-size:40px;margin:0 0 24px}
h2{font-size:28px;margin:24px 0 12px}
.box{padding:12px 16px;border:1px solid #eee;border-radius:10px;margin:8px 0}
.time{opacity:.6;font-size:13px}
a.btn{float:right;border:1px solid #e2e2e2;border-radius:10px;padding:10px 14px;text-decoration:none;color:#111}
.empty{opacity:.65}
</style>
</head>
<body>
  <h1>Thông báo <a class="btn" href="<%=request.getContextPath()%>/notify" onclick="event.preventDefault();document.getElementById('mark').submit();">Đánh dấu đã đọc tất cả</a></h1>
  <form id="mark" method="post" action="<%=request.getContextPath()%>/notify">
    <input type="hidden" name="mark" value="all"/>
  </form>

  <h2>Chưa đọc</h2>
  <%
    if (unread==null || unread.isEmpty()) {
  %>
    <div class="empty">Không có thông báo chưa đọc.</div>
  <%
    } else {
      for (Notification n : unread) {
  %>
    <div class="box">
      <div><%= n.getContent() %></div>
      <div class="time"><%= n.getCreated_time() %></div>
      <% if (n.getUrl()!=null) { %>
      <div><a href="<%= request.getContextPath()+n.getUrl() %>">Xem</a></div>
      <% } %>
    </div>
  <%
      }
    }
  %>

  <h2>Gần đây</h2>
  <%
    if (recent==null || recent.isEmpty()) {
  %>
    <div class="empty">Chưa có thông báo.</div>
  <%
    } else {
      for (Notification n : recent) {
  %>
    <div class="box">
      <div><%= n.getContent() %></div>
      <div class="time"><%= n.getCreated_time() %> <%= n.getRead_time()==null?"· chưa đọc":"" %></div>
      <% if (n.getUrl()!=null) { %>
      <div><a href="<%= request.getContextPath()+n.getUrl() %>">Xem</a></div>
      <% } %>
    </div>
  <%
      }
    }
  %>
</body>
</html>
