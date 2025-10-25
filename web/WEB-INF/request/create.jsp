<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  java.util.List<String> errors = (java.util.List<String>) request.getAttribute("errors");
  String title  = (String) request.getAttribute("form_title");
  String fromS  = (String) request.getAttribute("form_from");
  String toS    = (String) request.getAttribute("form_to");
  String reason = (String) request.getAttribute("form_reason");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Tạo đơn nghỉ</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#f7f9fc;margin:0}
  .wrap{max-width:760px;margin:28px auto;background:#fff;border:1px solid #e5e7eb;border-radius:14px;padding:18px}
  h2{margin:6px 0 12px}
  label{font-weight:700;margin-top:10px;display:block}
  .ip,.ta{width:100%;padding:10px;border:1px solid #e5e7eb;border-radius:10px}
  .row{display:grid;grid-template-columns:1fr 1fr;gap:12px}
  .btn{margin-top:14px;padding:12px 16px;border:none;border-radius:10px;background:#1a73e8;color:#fff;font-weight:800}
  .alert{background:#fde8e8;border:1px solid #f8caca;color:#991b1b;border-radius:10px;padding:10px 12px;margin:0 0 12px}
</style>
</head>
<body>
  <div class="wrap">
    <h2>📝 Tạo đơn nghỉ</h2>

    <% if (errors != null && !errors.isEmpty()) { %>
      <div class="alert">
        <% for(String e: errors){ %>• <%= e %><br><% } %>
      </div>
    <% } %>

    <form method="post" action="<%=ctx%>/request/create">
      <label>Tiêu đề (tuỳ chọn)</label>
      <input class="ip" name="title" value="<%= title==null? "" : title %>">

      <div class="row">
        <div>
          <label>Từ ngày</label>
          <input class="ip" type="date" name="from" value="<%= fromS==null? "" : fromS %>" required>
        </div>
        <div>
          <label>Đến ngày</label>
          <input class="ip" type="date" name="to" value="<%= toS==null? "" : toS %>" required>
        </div>
      </div>

      <label>Lý do</label>
      <textarea class="ta" rows="5" name="reason" required><%= reason==null? "" : reason %></textarea>

      <button class="btn" type="submit">Tạo đơn</button>
      <a href="<%=ctx%>/request/my" style="margin-left:10px">Hủy</a>
    </form>
  </div>
</body>
</html>
