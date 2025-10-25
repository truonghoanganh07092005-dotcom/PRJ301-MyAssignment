<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  java.util.List<String> errors = (java.util.List<String>) request.getAttribute("errors");
  String fTitle  = (String) request.getAttribute("form_title");
  String fFrom   = (String) request.getAttribute("form_from");
  String fTo     = (String) request.getAttribute("form_to");
  String fReason = (String) request.getAttribute("form_reason");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Tạo đơn nghỉ</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  :root{ --primary:#1a73e8; --bg:#f6f8fc; --card:#fff; --border:#e6e8ef; --muted:#6b7280; }
  *{box-sizing:border-box}
  body{ margin:0; font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif; background:var(--bg); color:#111 }
  .wrap{ max-width:800px; margin:28px auto; padding:0 16px }
  .card{ background:var(--card); border:1px solid var(--border); border-radius:14px; box-shadow:0 10px 24px rgba(16,24,40,.08); padding:18px }
  h2{ margin:0 0 12px; font-size:22px; }
  label{ display:block; margin-top:12px; font-weight:700 }
  input[type="text"], input[type="date"], textarea{
    margin-top:6px; width:100%; padding:12px 12px; border:1px solid var(--border); border-radius:10px; font-size:15px; background:#fff;
  }
  textarea{ min-height:110px; resize:vertical }
  .row{ display:grid; grid-template-columns:1fr 1fr; gap:12px }
  .btns{ display:flex; gap:12px; margin-top:18px }
  .btn{ padding:12px 16px; border:0; border-radius:10px; font-weight:800; cursor:pointer }
  .primary{ background:var(--primary); color:#fff }
  .ghost{ background:#fff; color:#111; border:1px solid var(--border) }
  .err{ background:#fef2f2; border:1px solid #fee2e2; color:#991b1b; padding:10px 12px; border-radius:10px; margin:10px 0 }
  .hint{ color:var(--muted); font-size:13px; margin-top:2px }
</style>
</head>
<body>
  <div class="wrap">
    <div class="card">
      <h2>Tạo đơn nghỉ</h2>

      <% if (errors != null && !errors.isEmpty()) { %>
        <div class="err">
          <ul style="margin:0 0 0 18px; padding:0">
          <% for (String e : errors) { %>
            <li><%= e %></li>
          <% } %>
          </ul>
        </div>
      <% } %>

      <form method="post" action="<%=ctx%>/request/create">
        <label>Tiêu đề (tuỳ chọn)</label>
        <input type="text" name="title" placeholder="Ví dụ: Đơn nghỉ phép"
               value="<%= fTitle==null? "" : fTitle %>">
        <div class="hint">Nếu để trống, hệ thống sẽ hiển thị “Nghỉ từ–đến”.</div>

        <div class="row">
          <div>
            <label>Từ ngày</label>
            <input type="date" name="from" required value="<%= fFrom==null? "" : fFrom %>">
          </div>
          <div>
            <label>Đến ngày</label>
            <input type="date" name="to" required value="<%= fTo==null? "" : fTo %>">
          </div>
        </div>

        <label>Lý do</label>
        <textarea name="reason" required placeholder="Nêu rõ lý do xin nghỉ..."><%= fReason==null? "" : fReason %></textarea>

        <div class="btns">
          <button class="btn primary" type="submit">Gửi đơn</button>
          <a class="btn ghost" href="<%=ctx%>/home">Huỷ</a>
        </div>
      </form>
    </div>
  </div>
</body>
</html>
