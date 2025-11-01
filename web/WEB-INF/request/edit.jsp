<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctx    = request.getContextPath();
  String rid    = (String) request.getAttribute("rid");
  String title  = (String) request.getAttribute("form_title");
  String from   = (String) request.getAttribute("form_from");
  String to     = (String) request.getAttribute("form_to");
  String reason = (String) request.getAttribute("form_reason");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Sửa đơn nghỉ</title>
<meta name="viewport" content="width=device-width,initial-scale=1">
<style>
  body{margin:0;background:#f5f7fb;font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial;}
  header{background:#0f1220;color:#fff;padding:14px 22px}
  .wrap{max-width:720px;margin:24px auto;background:#fff;border-radius:18px;box-shadow:0 12px 34px rgba(0,0,0,.06);padding:22px}
  h2{margin:0 0 14px}
  .f{display:grid;grid-template-columns:1fr 1fr;gap:14px}
  .f .full{grid-column:1/-1}
  label{display:block;font-size:13px;color:#374151;margin-bottom:6px}
  input,textarea{width:100%;padding:10px 12px;border:1px solid #e5e7eb;border-radius:12px;font:inherit}
  textarea{min-height:120px;resize:vertical}
  .actions{display:flex;gap:10px;margin-top:16px}
  .btn{padding:10px 16px;border-radius:12px;border:1px solid #e5e7eb;background:#111827;color:#fff;text-decoration:none}
  .btn.light{background:#fff;color:#111827}
  .btn:hover{opacity:.92}
</style>
</head>
<body>
<header>
  <a href="<%=ctx%>/home" style="color:#fff;text-decoration:none;">← Trang chủ</a>
</header>

<div class="wrap">
  <h2>Sửa đơn nghỉ</h2>
  <form method="post" action="<%=ctx%>/request/edit">
    <input type="hidden" name="rid" value="<%=rid%>"/>

    <div class="f">
      <div class="full">
        <label>Tiêu đề</label>
        <input type="text" name="title" value="<%= title==null? "" : title %>" required>
      </div>

      <div>
        <label>Từ ngày</label>
        <input type="date" name="from" value="<%= from==null? "" : from %>" required>
      </div>
      <div>
        <label>Đến ngày</label>
        <input type="date" name="to" value="<%= to==null? "" : to %>" required>
      </div>

      <div class="full">
        <label>Lý do</label>
        <textarea name="reason"><%= reason==null? "" : reason %></textarea>
      </div>
    </div>

    <div class="actions">
      <button class="btn" type="submit">Lưu thay đổi</button>
      <a class="btn light" href="<%=ctx%>/request/my">Hủy</a>
    </div>
  </form>
</div>
</body>
</html>
