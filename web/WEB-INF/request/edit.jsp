<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx    = request.getContextPath();
  String rid    = (String) request.getAttribute("rid");
  String title  = (String) request.getAttribute("form_title");
  String fromS  = (String) request.getAttribute("form_from");
  String toS    = (String) request.getAttribute("form_to");
  String reason = (String) request.getAttribute("form_reason");
  java.util.List<String> errors = (java.util.List<String>) request.getAttribute("errors");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>✏️ Sửa đơn ngha</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  :root{--bg:#f7f9fc;--card:#fff;--border:#e5e7eb;--muted:#6b7280;--ink:#0f172a;--primary:#2563eb}
  *{box-sizing:border-box}
  body{margin:0;background:var(--bg);color:var(--ink);font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}
  .page{max-width:820px;margin:26px auto;padding:0 16px}
  .card{background:var(--card);border:1px solid var(--border);border-radius:18px;box-shadow:0 12px 30px rgba(16,24,40,.06);padding:20px}
  h1{margin:6px 0 18px}
  label{display:block;margin:14px 0 6px;font-weight:700}
  input[type="text"], input[type="date"], textarea{
    width:100%;padding:12px;border:1px solid var(--border);border-radius:12px;font:inherit;background:#fff
  }
  textarea{min-height:120px;resize:vertical}
  .row{display:grid;grid-template-columns:1fr 1fr;gap:12px}
  @media(max-width:680px){.row{grid-template-columns:1fr}}
  .actions{display:flex;gap:10px;margin-top:16px}
  .btn{padding:12px 16px;border-radius:12px;border:1px solid var(--border);background:#fff;text-decoration:none;color:var(--ink);font-weight:800}
  .btn.primary{background:var(--primary);border-color:var(--primary);color:#fff}
  .err{background:#fef2f2;border:1px solid #fecaca;color:#991b1b;padding:10px 12px;border-radius:10px;margin-bottom:12px}
</style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="page">
  <div class="card">
    <h1>✏️ Sửa đơn nghỉ</h1>

    <% if (errors != null && !errors.isEmpty()) { %>
      <div class="err">
        <ul style="margin:0 0 0 18px">
          <% for(String e : errors){ %><li><%= e %></li><% } %>
        </ul>
      </div>
    <% } %>

    <form method="post" action="<%=ctx%>/request/edit" accept-charset="UTF-8" novalidate>
      <input type="hidden" name="rid" value="<%= rid == null ? "" : rid %>">

      <label for="title">Tiêu đề</label>
      <input id="title" type="text" name="title" maxlength="150"
             placeholder="Ví dụ: Đơn xin nghỉ phép" value="<%= title==null? "" : title %>">

      <div class="row">
        <div>
          <label for="from">Từ ngày</label>
          <input id="from" type="date" name="from" value="<%= fromS==null? "" : fromS %>" required>
        </div>
        <div>
          <label for="to">Đến ngày</label>
          <input id="to" type="date" name="to" value="<%= toS==null? "" : toS %>" required>
        </div>
      </div>

      <label for="reason">Lý do</label>
      <textarea id="reason" name="reason" required><%= reason==null? "" : reason %></textarea>

      <div class="actions">
        <button class="btn primary" type="submit">Lưu thay đổi</button>
        <a class="btn" href="<%=ctx%>/request/my">Huỷ</a>
      </div>
    </form>
  </div>
</div>

<script>
  // đảm bảo to >= from
  (function(){
    var f = document.getElementById('from');
    var t = document.getElementById('to');
    if (f && t){
      if (f.value) t.min = f.value;
      f.addEventListener('change', function(){
        if (t.value && t.value < f.value) t.value = f.value;
        t.min = f.value;
      });
    }
  })();
</script>
</body>
</html>
