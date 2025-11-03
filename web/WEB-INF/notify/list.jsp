<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, model.RequestHistory" %>
<%
  List<RequestHistory> list = (List<RequestHistory>) request.getAttribute("list");
  if (list == null) list = Collections.emptyList();
  int rid = (request.getAttribute("rid")==null) ? -1 : (Integer)request.getAttribute("rid");
  String ctx = request.getContextPath();
%>
<!DOCTYPE html><html lang="vi"><head>
<meta charset="UTF-8"><title>Lịch sử đơn #<%=rid%></title>
<style>
  body{font-family:system-ui,Segoe UI,Roboto,Arial,sans-serif;margin:24px}
  table{border-collapse:collapse;width:100%}
  th,td{border:1px solid #e5e7eb;padding:10px 12px}
  th{background:#f8fafc;text-align:left}
  .muted{color:#6b7280}
</style></head><body>

<h1>Lịch sử đơn #<%=rid%></h1>
<p><a href="<%=ctx%>/request/detail?rid=<%=rid%>">← Quay về chi tiết đơn</a></p>

<table>
  <thead>
    <tr>
      <th>Thời gian</th>
      <th>Hành động</th>
      <th>Actor (uid)</th>
      <th>Ghi chú</th>
    </tr>
  </thead>
  <tbody>
  <% if (list.isEmpty()) { %>
    <tr><td colspan="4" class="muted">Chưa có lịch sử.</td></tr>
  <% } else { for (RequestHistory h : list) { %>
    <tr>
      <td><%= h.getCreated_time() %></td>
      <td><%= h.getAction() %></td>
      <td><%= h.getActorUid()==null?"—":h.getActorUid() %></td>
      <td><%= h.getNote()==null?"":h.getNote() %></td>
    </tr>
  <% } } %>
  </tbody>
</table>

</body></html>
