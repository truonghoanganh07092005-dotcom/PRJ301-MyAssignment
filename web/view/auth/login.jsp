<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
 
  <style>
    :root{ --primary:#1a73e8; --bg:#f7f9fc; --card:#fff; --border:#e5e7eb; }
    *{box-sizing:border-box}
    body{
      margin:0;background:var(--bg);
      font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;
      color:#111;height:100vh;display:flex;align-items:center;justify-content:center;
    }
    .login-box{
      background:var(--card);border:1px solid var(--border);border-radius:16px;
      padding:40px 48px;width:100%;max-width:380px;box-shadow:0 4px 18px rgba(0,0,0,.06);
      position:relative;
    }
    .logo{
      position:absolute;top:-40px;left:-40px;width:80px;height:80px;border-radius:50%;
      overflow:hidden;box-shadow:0 0 6px rgba(0,0,0,.2);background:#fff;
      background-image:url('<c:url value="/img/sonnt.jpg"/>'); /* ← LOGO ĐÚNG THƯ MỤC */
      background-size:cover;background-position:center;
    }
    h2{margin:0 0 24px 0;text-align:center;font-size:22px;color:#1a1a1a;}
    label{display:block;margin-top:12px;font-weight:600;font-size:14px;}
    input{
      width:100%;padding:12px;border:1px solid var(--border);border-radius:8px;
      font-size:15px;margin-top:6px;
    }
    input:focus{border-color:#c5d7fe;box-shadow:0 0 0 3px #e8f0ff;outline:none;}
    .btn{
      width:100%;margin-top:22px;padding:12px;background:var(--primary);color:#fff;
      border:none;border-radius:8px;font-weight:700;cursor:pointer;font-size:16px;
    }
    .btn:hover{background:#1667d9}
    .error{
      background:#fde8e8;color:#b91c1c;border:1px solid #f8caca;padding:10px 12px;
      border-radius:8px;margin-bottom:12px;font-size:14px;
    }
    footer{text-align:center;color:#6b7280;font-size:13px;margin-top:20px;}
  </style>
</head>
<body>
  <div class="login-box">
    <div class="logo"></div>

    <h2>Đăng nhập hệ thống</h2>

    <c:if test="${not empty error}">
      <div class="error">${error}</div>
    </c:if>

    <form method="post" action="<c:url value='/login'/>">
      <label>Tên đăng nhập</label>
      <input type="text" name="username" value="${username}" required autofocus>

      <label>Mật khẩu</label>
      <input type="password" name="password" required>

      <button type="submit" class="btn">Đăng nhập</button>
    </form>

   
  </div>
</body>
</html>
