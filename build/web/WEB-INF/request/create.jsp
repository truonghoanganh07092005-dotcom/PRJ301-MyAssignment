<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  java.util.List<String> errors = (java.util.List<String>) request.getAttribute("errors");

  String form_type   = (String) request.getAttribute("form_type");
  String form_title  = (String) request.getAttribute("form_title");
  String form_from   = (String) request.getAttribute("form_from");
  String form_to     = (String) request.getAttribute("form_to");
  String form_reason = (String) request.getAttribute("form_reason");

  java.time.LocalDate today = java.time.LocalDate.now();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Tạo đơn nghỉ</title>
<style>
  body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#f6f7fb;margin:0}
  .wrap{max-width:920px;margin:28px auto;padding:24px;background:#fff;border:1px solid #e6e9f0;border-radius:16px;box-shadow:0 8px 28px rgba(16,24,40,.06)}
  .row{margin:14px 0}
  label{display:block;margin-bottom:6px;color:#334155}
  input,select,textarea{width:100%;padding:10px 12px;border:1px solid #e6e9f0;border-radius:12px;background:#fff}
  .grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}
  .btn{padding:10px 14px;border-radius:10px;border:1px solid #2563eb;background:#2563eb;color:#fff;text-decoration:none;cursor:pointer}
  .danger{color:#c1121f}
  .errors{background:#fef2f2;border:1px solid #fecaca;padding:10px 12px;border-radius:10px;color:#991b1b}
</style>
</head>
<body>
<div class="wrap">

  <h1>📝 Tạo đơn nghỉ</h1>

  <% if (errors != null && !errors.isEmpty()) { %>
    <div class="errors">
      <ul>
        <% for (String e : errors) { %>
          <li><%= e %></li>
        <% } %>
      </ul>
    </div>
  <% } %>

  <form method="post" action="<%= ctx %>/request/create" id="createForm">
    <div class="row">
      <label>Loại đơn (tùy chọn)</label>
      <select name="type" id="type">
        <option value="" <%= (form_type==null||form_type.isBlank())?"selected":"" %> >— Chọn loại —</option>
        <option <%= "Đơn xin nghỉ".equals(form_type)?"selected":"" %> >Đơn xin nghỉ</option>
        <option <%= "Nghỉ ốm".equals(form_type)?"selected":"" %> >Nghỉ ốm</option>
        <option <%= "Nghỉ phép năm".equals(form_type)?"selected":"" %> >Nghỉ phép năm</option>
        <option <%= "Nghỉ không lương".equals(form_type)?"selected":"" %> >Nghỉ không lương</option>
        <option <%= "Đi trễ / về sớm".equals(form_type)?"selected":"" %> >Đi trễ / về sớm</option>
      </select>
    </div>

    <div class="row">
      <label>Tiêu đề (tùy chọn)</label>
      <input name="title" value="<%= form_title==null?"":form_title %>" placeholder="Ví dụ: Nghỉ phép năm" />
    </div>

    <div class="row grid">
      <div>
        <label>Từ ngày</label>
        <input type="date" id="from" name="from"
               value="<%= (form_from!=null?form_from:today.toString()) %>"
               min="<%= today.toString() %>">
      </div>
      <div>
        <label>Đến ngày</label>
        <input type="date" id="to" name="to"
               value="<%= form_to==null?"":form_to %>"
               min="<%= today.toString() %>">
      </div>
    </div>

    <div class="row">
      <label>Lý do</label>
      <textarea rows="6" name="reason" placeholder="Nhập lý do..."><%= form_reason==null?"":form_reason %></textarea>
    </div>

    <div class="row">
      <button class="btn" type="submit">Tạo đơn</button>
      <a class="btn" style="background:#fff;color:#0f172a;border-color:#e6e9f0" href="<%= ctx %>/home">Hủy</a>
    </div>
  </form>
</div>

<script>
  // Chặn ngày quá khứ ở client
  const today = new Date().toISOString().slice(0,10);
  const from = document.getElementById('from');
  const to   = document.getElementById('to');
  from.min = today;
  to.min   = today;

  // Tự set min(to) = from khi người dùng đổi ngày bắt đầu
  from.addEventListener('change', () => {
    to.min = from.value || today;
    if (to.value && to.value < to.min) to.value = to.min;
  });

  // Kiểm tra nhanh trước khi submit (phòng người dùng sửa HTML)
  document.getElementById('createForm').addEventListener('submit', (e) => {
    const f = from.value, t = to.value;
    if (!f || !t) return; // để server báo lỗi định dạng
    if (f < today) { alert("Ngày bắt đầu phải ≥ hôm nay."); e.preventDefault(); return; }
    if (t < f)     { alert("Ngày kết thúc phải ≥ ngày bắt đầu."); e.preventDefault(); return; }
  });
</script>
</body>
</html>
