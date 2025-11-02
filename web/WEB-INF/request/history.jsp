<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,model.RequestActionHistoryRow" %>
<%
  List<RequestActionHistoryRow> rows = (List<RequestActionHistoryRow>) request.getAttribute("rows");
  String rid = String.valueOf(request.getAttribute("rid"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Lịch sử đơn #<%=rid%></title>
<style>
body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;margin:24px;color:#222}
h1{font-size:32px}
table{border-collapse:collapse;width:100%;margin-top:12px}
th,td{border-bottom:1px solid #eee;padding:10px;text-align:left}
th{background:#fafafa}
.note{opacity:.85}
.time{opacity:.6}
</style>
</head>
<body>
  <h1>Lịch sử đơn #<%=rid%></h1>
  <table>
    <tr>
      <th>Thời gian</th>
      <th>Hành động</th>
      <th>Người thực hiện</th>
      <th>Trạng thái trước → sau</th>
      <th>Ghi chú</th>
    </tr>
    <%
      for (RequestActionHistoryRow r : rows) {
        String actor = r.getActorName()!=null ? r.getActorName() : (r.getActorUid()==null? "-" : ("UID "+r.getActorUid()));
        String trans = (r.getPrevStatus()==null? "-" : String.valueOf(r.getPrevStatus()))
                     + " → "
                     + (r.getNewStatus()==null? "-" : String.valueOf(r.getNewStatus()));
    %>
      <tr>
        <td class="time"><%= r.getCreatedTime() %></td>
        <td><%= r.getAction() %></td>
        <td><%= actor %></td>
        <td><%= trans %></td>
        <td class="note"><%= r.getNote()==null? "" : r.getNote() %></td>
      </tr>
    <%
      }
    %>
  </table>
</body>
</html>
