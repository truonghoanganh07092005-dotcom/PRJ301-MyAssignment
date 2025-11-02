<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.*, dal.NotificationDBContext.NotifyRow"%>
<%
  List<NotifyRow> unread = (List<NotifyRow>) request.getAttribute("unread");
  List<NotifyRow> recent = (List<NotifyRow>) request.getAttribute("recent");
  String ctx = request.getContextPath();
%>
<h2>Thông báo</h2>
<p><a href="<%=ctx%>/notify?a=markall">Đánh dấu đã đọc tất cả</a></p>

<h3>Chưa đọc</h3>
<ul>
<% for (NotifyRow n : unread) { %>
  <li>
    <a href="<%= (n.url==null? "#": (ctx + n.url)) %>"><%= n.content %></a>
    <small>(<%= n.createdTime %>)</small>
    <a href="<%=ctx%>/notify?a=mark&nid=<%=n.nid%>">đã đọc</a>
  </li>
<% } %>
</ul>

<h3>Gần đây</h3>
<ul>
<% for (NotifyRow n : recent) { %>
  <li>
    <a href="<%= (n.url==null? "#": (ctx + n.url)) %>"><%= n.content %></a>
    <small>(<%= n.createdTime %>)</small>
    <% if (n.isUnread()) { %><b>• mới</b><% } %>
  </li>
<% } %>
</ul>
