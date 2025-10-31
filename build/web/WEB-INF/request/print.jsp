<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RequestForLeave" %>
<%
  RequestForLeave r = (RequestForLeave) request.getAttribute("requestObj");
  if (r==null){ out.print("No data"); return; }
%>
<!DOCTYPE html>
<html><head>
<meta charset="UTF-8"><title>In đơn #<%=r.getRid()%></title>
<style>
  body{font-family:system-ui,Arial,sans-serif}
  .box{max-width:700px;margin:20px auto;border:1px solid #ddd;padding:16px}
  .row{margin:6px 0}
  .label{color:#555}
</style>
</head>
<body onload="window.print()">
<div class="box">
  <h2>Đơn xin nghỉ #<%=r.getRid()%></h2>
  <div class="row"><span class="label">Tiêu đề:</span> <b><%= (r.getTitle()==null||r.getTitle().isBlank())?("Nghỉ "+r.getFrom()+" – "+r.getTo()):r.getTitle()%></b></div>
  <div class="row"><span class="label">Từ ngày:</span> <%= r.getFrom() %></div>
  <div class="row"><span class="label">Đến ngày:</span> <%= r.getTo() %></div>
  <div class="row"><span class="label">Lý do:</span> <pre style="display:inline"><%= r.getReason() %></pre></div>
</div>
</body></html>
