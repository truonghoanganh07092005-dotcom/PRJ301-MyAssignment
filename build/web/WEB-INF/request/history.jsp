<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,model.RequestActionHistory" %>
<%
  String ctx = request.getContextPath();
  int rid = Integer.parseInt(String.valueOf(request.getAttribute("rid")));
  @SuppressWarnings("unchecked")
  List<RequestActionHistory> history = (List<RequestActionHistory>) request.getAttribute("history");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8"><title>Lịch sử đơn #<%=rid%></title>
<style>
  body{font-family:system-ui,Segoe UI,Roboto,Arial;background:#f6f7fb;margin:0}
  .page{max-width:900px;margin:24px auto;background:#fff;border:1px solid #e6e9f0;border-radius:14px;padding:18px}
  h1{margin:6px 0 16px}
  table{width:100%;border-collapse:collapse}
  th,td{padding:10px;border-bottom:1px solid #eef2f6;text-align:left}
  th{background:#111827;color:#fff}
  .muted{color:#64748b}
</style>
</head>
<body>
  <div class="page">
    <a href="<%=ctx%>/request/detail?rid=<%=rid%>">← Chi tiết</a>
    <h1>Lịch sử đơn #<%=rid%></h1>
    <table>
      <tr><th>Thời gian</th><th>Hành động</th><th>Người thực hiện</th><th>Trạng thái</th><th>Ghi chú</th></tr>
      <% if (history==null || history.isEmpty()) { %>
        <tr><td colspan="5" class="muted">Chưa có lịch sử.</td></tr>
      <% } else { for (RequestActionHistory h : history) { %>
        <tr>
          <td><%= h.getCreatedTime() %></td>
          <td><b><%= h.getAction() %></b></td>
          <td><%= (h.getActor()!=null && h.getActor().getName()!=null)? h.getActor().getName() : "—" %></td>
          <td><%= h.getPrevStatus()==null? "—" : h.getPrevStatus() %> → <%= h.getNewStatus()==null? "—" : h.getNewStatus() %></td>
          <td><%= h.getNote()==null? "" : h.getNote() %></td>
        </tr>
      <% } } %>
    </table>
  </div>
</body>
</html>
