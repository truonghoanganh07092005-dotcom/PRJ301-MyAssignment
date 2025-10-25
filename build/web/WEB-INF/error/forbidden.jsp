<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String msg = (String) request.getAttribute("forbidden_message");
  if (msg == null) msg = "Bạn không có quyền truy cập chức năng này.";
  String back = (String) request.getAttribute("backUrl");
  if (back == null) back = ctx + "/home";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Không có quyền truy cập</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style>
    :root{--bg:#f7f9fc;--card:#fff;--border:#e5e7eb;--muted:#6b7280;--primary:#1a73e8}
    *{box-sizing:border-box} body{margin:0;min-height:100vh;display:flex;align-items:center;justify-content:center;
      font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:var(--bg);color:#111}
    .card{max-width:560px;width:100%;padding:28px;border:1px solid var(--border);border-radius:16px;background:var(--card);
      box-shadow:0 16px 40px rgba(16,24,40,.08)}
    h1{margin:0 0 8px;font-size:22px}
    p{margin:0 0 18px;color:var(--muted)}
    .btn{display:inline-block;padding:12px 16px;border-radius:10px;text-decoration:none;color:#fff;background:var(--primary);
      font-weight:700}
  </style>
</head>
<body>
  <div class="card">
    <h1>🚫 Không có quyền truy cập</h1>
    <p><%= msg %></p>
    <a class="btn" href="<%= back %>">Quay về trang chủ</a>
  </div>
</body>
</html>
