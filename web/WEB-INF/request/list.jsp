<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  java.util.List<model.RequestForLeave> list =
      (java.util.List<model.RequestForLeave>) request.getAttribute("list");
  String flash = (String) request.getAttribute("flash");
  String createdRid = (String) request.getAttribute("createdRid");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Đơn của tôi</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#f7f9fc;margin:0}
  .wrap{max-width:960px;margin:28px auto;padding:0 14px}
  .card{background:#fff;border:1px solid #e5e7eb;border-radius:14px;padding:12px}
  h2{margin:6px 0 16px}
  .list{display:flex;flex-direction:column;gap:10px}
  .item{display:flex;justify-content:space-between;align-items:center;border:1px solid #e5e7eb;border-radius:10px;padding:12px 14px;background:#fff}
  .title{font-weight:800}
  .badge{padding:6px 10px;border-radius:999px;font-size:13px}
  .approved{background:#dcfce7;color:#166534;border:1px solid #bbf7d0}
  .rejected{background:#fee2e2;color:#991b1b;border:1px solid #fecaca}
  .inprogress{background:#ffedd5;color:#9a3412;border:1px solid #fed7aa}
  .flash{background:#e0f2fe;border:1px solid #bae6fd;color:#0c4a6e;border-radius:10px;padding:10px 12px;margin-bottom:12px}
  .hl{box-shadow:0 0 0 3px rgba(26,115,232,.25)}
</style>
</head>
<body>
  <div class="wrap">
    <h2>📄 Đơn của tôi</h2>

    <% if (flash != null) { %>
      <div class="flash"><%= flash %></div>
    <% } %>

    <div class="card">
      <div class="list">
        <% if (list != null && !list.isEmpty()) {
             for (model.RequestForLeave r : list) {
               String label = (r.getTitle()!=null && !r.getTitle().isBlank())
                               ? r.getTitle()
                               : ("Nghỉ " + r.getFrom() + " – " + r.getTo());
               String cls, txt;
               switch (r.getStatus()){
                 case 1: cls="approved"; txt="Approved"; break;
                 case 2: cls="rejected"; txt="Rejected"; break;
                 default: cls="inprogress"; txt="In Progress";
               }
               boolean highlight = createdRid!=null && createdRid.equals(String.valueOf(r.getRid()));
        %>
          <div class="item <%= highlight? "hl": "" %>">
            <div class="title"><%= label %></div>
            <span class="badge <%= cls %>"><%= txt %></span>
          </div>
        <% } } else { %>
          <div style="color:#6b7280;text-align:center;padding:16px">Chưa có đơn nào.</div>
        <% } %>
      </div>
    </div>
  </div>
</body>
</html>
