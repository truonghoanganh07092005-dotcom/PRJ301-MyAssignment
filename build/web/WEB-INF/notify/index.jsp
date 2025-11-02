<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, dal.NotificationDBContext.NotifyRow" %>
<%
    List<NotifyRow> unread = (List<NotifyRow>) request.getAttribute("unread");
    List<NotifyRow> recent = (List<NotifyRow>) request.getAttribute("recent");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Thông báo</title>
<style>
  body{font-family:system-ui,Arial,sans-serif;margin:24px}
  h1{margin:0 0 12px}
  a.btn{display:inline-block;padding:8px 12px;border:1px solid #ccc;border-radius:8px;text-decoration:none}
  .section{margin-top:20px}
  .item{padding:12px 0;border-bottom:1px solid #eee;display:flex;gap:12px;align-items:center}
  .badge{font-size:12px;padding:2px 6px;border-radius:6px;background:#ffefc2}
  .time{color:#666;font-size:12px}
  .empty{color:#888}
</style>
</head>
<body>
  <h1>Thông báo</h1>
  <p><a class="btn" href="<%=ctx%>/notify?act=read_all">Đánh dấu đã đọc tất cả</a></p>

  <div class="section">
    <h2>Chưa đọc</h2>
    <% if (unread == null || unread.isEmpty()) { %>
      <p class="empty">Không có thông báo chưa đọc.</p>
    <% } else {
         for (NotifyRow n : unread) { %>
        <div class="item">
          <span class="badge">Mới</span>
          <a href="<%=ctx%>/notify?act=read_one&nid=<%=n.nid%>"><%= n.content %></a>
          <span class="time"><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(n.createdTime) %></span>
        </div>
    <% } } %>
  </div>

  <div class="section">
    <h2>Gần đây</h2>
    <% if (recent == null || recent.isEmpty()) { %>
      <p class="empty">Chưa có thông báo.</p>
    <% } else {
         for (NotifyRow n : recent) { %>
        <div class="item">
          <% if (n.isUnread()) { %><span class="badge">Mới</span><% } %>
          <a href="<%= n.url==null? (ctx+"/notify?act=read_one&nid="+n.nid) : n.url %>"
             onclick="this.href='<%=ctx%>/notify?act=read_one&nid=<%=n.nid%>'">
            <%= n.content %>
          </a>
          <span class="time"><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(n.createdTime) %></span>
        </div>
    <% } } %>
  </div>
</body>
</html>
