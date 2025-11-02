<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, model.Notification" %>
<%
  String ctx = request.getContextPath();
  @SuppressWarnings("unchecked")
  List<Notification> list = (List<Notification>) request.getAttribute("list");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8"><title>Thông báo</title>
<style>
  body{font-family:system-ui;background:#f6f7fb;margin:0}
  .wrap{max-width:900px;margin:24px auto}
  .card{background:#fff;border:1px solid #e5e7eb;border-radius:14px;overflow:hidden}
  .item{display:flex;justify-content:space-between;gap:10px;padding:14px;border-top:1px solid #eef2f6}
  .item:first-child{border-top:none}
  .title{font-weight:800}
  .meta{color:#64748b;font-size:13px}
  form{margin:0}
  .btn{padding:6px 10px;border:1px solid #e5e7eb;border-radius:10px;background:#fff;text-decoration:none;color:#111}
</style>
</head>
<body>
  <jsp:include page="/WEB-INF/partials/navbar.jsp"/>
  <div class="wrap">
    <h2 style="margin:6px 0 12px">🔔 Thông báo của bạn</h2>
    <div class="card">
      <% if (list==null || list.isEmpty()) { %>
        <div class="item"><div class="meta">Chưa có thông báo.</div></div>
      <% } else { for (Notification n : list) { %>
        <div class="item">
          <div>
            <div class="title"><%= n.getTitle() %></div>
            <div class="meta"><%= n.getCreatedTime() %></div>
            <% if (n.getContent()!=null && !n.getContent().isBlank()) { %>
              <div style="margin-top:6px"><%= n.getContent() %></div>
            <% } %>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <% if (n.getUrl()!=null) { %>
              <a class="btn" href="<%= n.getUrl() %>">Mở</a>
            <% } %>
            <form method="post" action="<%=ctx%>/notify">
              <input type="hidden" name="nid" value="<%= n.getId() %>">
              <button class="btn" type="submit">Đã đọc</button>
            </form>
          </div>
        </div>
      <% } } %>
    </div>
  </div>
</body>
</html>
