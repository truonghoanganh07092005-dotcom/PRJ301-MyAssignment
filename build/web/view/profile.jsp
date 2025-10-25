<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String displayName = (String) session.getAttribute("displayName");
  if (displayName == null || displayName.isBlank()) {
      Object auth = session.getAttribute("auth");
      if (auth != null) {
          try {
              java.lang.reflect.Method mEmp = auth.getClass().getMethod("getEmployee");
              Object emp = mEmp.invoke(auth);
              if (emp != null) {
                  java.lang.reflect.Method mName = emp.getClass().getMethod("getName");
                  Object v = mName.invoke(emp);
                  if (v != null && !v.toString().isBlank()) displayName = v.toString();
              }
          } catch(Exception ignore){}
          if (displayName == null || displayName.isBlank()) {
              try {
                  java.lang.reflect.Method mUser = auth.getClass().getMethod("getUsername");
                  Object v = mUser.invoke(auth);
                  if (v != null && !v.toString().isBlank()) displayName = v.toString();
              } catch(Exception ignore){}
          }
  }
  if (displayName == null || displayName.isBlank()) displayName = "User";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Thông tin cá nhân</title>
  <style>
    body{margin:0;font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#f5f7fb;color:#0f172a}
    .page{max-width:960px;margin:0 auto;padding:24px}
    .hello{font-size:24px;margin:18px 0}
    .hello b{color:#1d4ed8}
    .card{background:#fff;border:1px solid #e5e7eb;border-radius:16px;padding:16px}
  </style>
</head>
<body>

<%@ include file="/WEB-INF/partials/navbar.jsp" %>

<div class="page">
  <h2 class="hello">Xin chào, <b><%= displayName %></b></h2>

  <div class="card">
    <!-- Tự hiển thị thêm thông tin cá nhân ở đây -->
    <p>Đây là trang thông tin cá nhân (demo).</p>
  </div>
</div>
</body>
</html>
