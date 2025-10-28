<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx    = request.getContextPath();
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
<meta name="viewport" content="width=device-width, initial-scale=1" />
<style>
  :root{
    --bg:#f6f7fb; --ink:#0f172a; --muted:#667085; --card:#fff; --border:#e6e9f0;
    --primary:#2563eb; --primary-ink:#fff; --danger:#b91c1c; --danger-bg:#fef2f2; --danger-br:#fecaca;
  }
  *{box-sizing:border-box}
  html,body{margin:0;background:var(--bg);color:var(--ink);font-family:system-ui,-apple-system,"Segoe UI",Roboto,Arial,sans-serif}
  .page{max-width:900px;margin:24px auto;padding:20px;background:var(--card);border:1px solid var(--border);border-radius:16px}
  h1{margin:0 0 14px}
  .row{display:grid;grid-template-columns:1fr 1fr;gap:16px}
  label{display:block;margin:10px 0 6px;color:var(--muted)}
  input[type=text],input[type=date],textarea{
    width:100%;padding:10px 12px;border:1px solid #d0d5dd;border-radius:10px;
    font:inherit;background:#fff;outline:none
  }
  input:focus, textarea:focus{border-color:#c7d3ff;box-shadow:0 0 0 4px rgba(37,99,235,.08)}
  textarea{height:140px;resize:vertical}
  .btn{display:inline-block;margin-top:16px;padding:10px 16px;border:1px solid var(--primary);border-radius:10px;
       color:var(--primary-ink);background:var(--primary);text-decoration:none;cursor:pointer}
  .muted{margin-left:8px;border-color:#d0d5dd;background:#fff;color:var(--ink)}
  .err{background:var(--danger-bg);border:1px solid var(--danger-br);color:var(--danger);padding:10px 12px;border-radius:10px;margin-bottom:12px}
  .help{font-size:12px;color:var(--muted);margin-top:6px}
</style>
</head>
<body>
  <div class="page">
    <h1>📝 Tạo đơn nghỉ</h1>

    <% if (errors != null && !errors.isEmpty()) { %>
      <div class="err" role="alert">
        <b>Không thể tạo đơn:</b>
        <ul style="margin:6px 0 0 18px">
          <% for (String e : errors) { %><li><%= e %></li><% } %>
        </ul>
      </div>
    <% } %>

    <form id="frmCreate" method="post" action="<%=ctx%>/request/create" accept-charset="UTF-8" novalidate>
      <label for="title">Tiêu đề (tuỳ chọn)</label>
      <input type="text" id="title" name="title" maxlength="150"
             placeholder="Ví dụ: Đơn xin nghỉ phép"
             value="<%= title==null? "" : title %>">

      <div class="row">
        <div>
          <label for="from">Từ ngày</label>
          <input type="date" id="from" name="from" value="<%= fromS==null? "" : fromS %>" required>
          <div class="help">Chỉ chọn từ hôm nay trở đi.</div>
        </div>
        <div>
          <label for="to">Đến ngày</label>
          <input type="date" id="to" name="to" value="<%= toS==null? "" : toS %>" required>
        </div>
      </div>

      <label for="reason">Lý do</label>
      <textarea id="reason" name="reason" minlength="3" required
                placeholder="Nhập lý do xin nghỉ..."><%= reason==null? "" : reason %></textarea>

      <button class="btn" type="submit">Tạo đơn</button>
      <a class="btn muted" href="<%=ctx%>/home">Huỷ</a>
    </form>
  </div>

<script>
  (function(){
    // today (YYYY-MM-DD)
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth()+1).padStart(2,'0');
    const dd = String(today.getDate()).padStart(2,'0');
    const iso = `${yyyy}-${mm}-${dd}`;

    const from = document.getElementById('from');
    const to   = document.getElementById('to');
    const frm  = document.getElementById('frmCreate');

    // chặn ngày quá khứ + mặc định "from" = hôm nay nếu rỗng
    if (!from.value) from.value = iso;
    from.min = iso;
    to.min   = from.value || iso;

    // khi đổi from → ép to >= from
    from.addEventListener('change', () => {
      if (!from.value) return;
      to.min = from.value;
      if (to.value && to.value < from.value) to.value = from.value;
    });

    // chặn submit nếu user lách trên client
    frm.addEventListener('submit', (e) => {
      const f = from.value, t = to.value;
      const reason = document.getElementById('reason').value.trim();
      const warn = [];
      if (!f || f < iso) warn.push('Từ ngày phải từ hôm nay trở đi.');
      if (!t || t < f)   warn.push('Đến ngày phải ≥ Từ ngày.');
      if (!reason)      warn.push('Vui lòng nhập lý do.');
      if (warn.length){
        e.preventDefault();
        alert(warn.join('\n'));
      }
    });
  })();
</script>
</body>
</html>
