<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  java.util.List<String> errors = (java.util.List<String>) request.getAttribute("errors");

  Boolean edit = (Boolean) request.getAttribute("edit");
  if (edit == null) edit = false;

  String rid    = (String) request.getAttribute("rid");
  String title  = (String) request.getAttribute("form_title");
  String fromS  = (String) request.getAttribute("form_from");
  String toS    = (String) request.getAttribute("form_to");
  String reason = (String) request.getAttribute("form_reason");
  String today  = (String) request.getAttribute("todayIso");
  if (today == null) today = java.time.LocalDate.now().toString();

  String formAction = edit ? (ctx + "/request/edit") : (ctx + "/request/create");
  String pageTitle  = edit ? "✏️ Sửa đơn nghỉ" : "📝 Tạo đơn nghỉ";
  String submitText = edit ? "Lưu thay đổi" : "Tạo đơn";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title><%= pageTitle %></title>
<style>
  :root{
    --bg:#f6f7fb; --ink:#0f172a; --muted:#667085; --border:#e6e9f0; --card:#fff;
    --primary:#2563eb; --primary-ink:#fff;
    --danger:#b91c1c; --danger-bg:#fef2f2; --danger-br:#fecaca;
  }
  *{box-sizing:border-box}
  body{margin:0;background:linear-gradient(180deg,#f3f6ff 0%,#f7f8fc 60%);color:var(--ink);
       font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}
  .page{max-width:760px;margin:24px auto;padding:0 18px}
  h1{font-size:28px;margin:6px 0 18px}
  .card{background:var(--card);border:1px solid var(--border);border-radius:18px;
        box-shadow:0 8px 28px rgba(16,24,40,.06);padding:18px}
  label{display:block;margin:12px 0 6px;font-weight:600}
  input[type=text], input[type=date], textarea{
    width:100%;border:1px solid var(--border);border-radius:12px;background:#fff;
    height:42px;padding:0 12px;font-size:14px;outline:none;
  }
  textarea{height:110px;resize:vertical;padding:10px 12px;line-height:1.4}
  input:focus, textarea:focus{border-color:#c7d3ff;box-shadow:0 0 0 4px rgba(37,99,235,.08)}
  .row{display:grid;grid-template-columns:1fr 1fr;gap:12px}
  .btns{display:flex;gap:10px;margin-top:16px}
  .btn{appearance:none;border:1px solid var(--border);background:#fff;color:var(--ink);
       padding:10px 14px;border-radius:12px;cursor:pointer;text-decoration:none;display:inline-block}
  .btn.primary{background:var(--primary);color:var(--primary-ink);border-color:var(--primary)}
  .err{border:1px solid var(--danger-br);background:var(--danger-bg);color:#7f1d1d;
       padding:10px 12px;border-radius:12px;margin-bottom:12px}
</style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="page">
  <h1><%= pageTitle %></h1>

  <div class="card">
    <% if (errors != null && !errors.isEmpty()) { %>
      <div class="err">
        <ul style="margin:0 0 0 18px">
          <% for (String e : errors) { %><li><%= e %></li><% } %>
        </ul>
      </div>
    <% } %>

    <form id="frmCreate" method="post" action="<%=formAction%>" accept-charset="UTF-8" novalidate>
      <% if (edit && rid != null) { %>
        <input type="hidden" name="rid" value="<%= rid %>">
      <% } %>

      <label for="title">Tiêu đề</label>
      <input type="text" id="title" name="title" maxlength="150"
             placeholder="<%= edit ? "Tiêu đề đơn" : "Ví dụ: Đơn xin nghỉ phép" %>"
             value="<%= title==null? "" : title %>">

      <div class="row">
        <div>
          <label for="from">Từ ngày</label>
          <input type="date" id="from" name="from"
                 min="<%= today %>"
                 value="<%= fromS==null? today : fromS %>" required>
        </div>
        <div>
          <label for="to">Đến ngày</label>
          <input type="date" id="to" name="to"
                 min="<%= fromS==null? today : fromS %>"
                 value="<%= toS==null? (fromS==null? today : fromS) : toS %>" required>
        </div>
      </div>

      <label for="reason">Lý do</label>
      <textarea id="reason" name="reason" required><%= reason==null? "" : reason %></textarea>

      <div class="btns">
        <button class="btn primary" type="submit"><%= submitText %></button>
        <a class="btn" href="<%=ctx%>/home">Huỷ</a>
      </div>
    </form>
  </div>
</div>

<script>
  // Chặn client-side: "to" luôn >= "from", "from" luôn >= today
  (function(){
    const from = document.getElementById('from');
    const to   = document.getElementById('to');
    const today = from.min; // server set

    function clamp(){
      if (!from.value) from.value = today;
      if (from.value < today) from.value = today;
      if (to.value && to.value < from.value) to.value = from.value;
      to.min = from.value;
    }
    from.addEventListener('change', clamp);
    to.addEventListener('change', clamp);
    clamp();
  })();
</script>
</body>
</html>
