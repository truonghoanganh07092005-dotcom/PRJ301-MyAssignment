<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String error = (String) request.getAttribute("error");
  String username = (String) request.getAttribute("username");
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <title>Đăng nhập hệ thống</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <style>
    :root{ --primary:#1a73e8; --bg1:#f7f9fc; --bg2:#eef3ff; --card:#fff; --border:#e5e7eb; --muted:#6b7280; }
    *{box-sizing:border-box}
    body{
      margin:0; min-height:100vh; display:flex; align-items:center; justify-content:center;
      font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif; color:#111;
      background: radial-gradient(1200px 600px at 10% 0%, var(--bg2), transparent),
                  radial-gradient(1000px 500px at 90% 100%, #eaf2ff, transparent),
                  var(--bg1);
    }
    .box{
      position:relative; width:100%; max-width:400px; padding:56px 48px 40px;
      background:var(--card); border:1px solid var(--border); border-radius:18px;
      box-shadow:0 20px 60px rgba(26,115,232,.08), 0 8px 20px rgba(0,0,0,.05);
      animation:pop .25s ease-out;
    }
    @keyframes pop{from{transform:translateY(8px);opacity:.0}to{transform:none;opacity:1}}
    .logo{
      position:absolute; top:-40px; left:-40px; width:88px; height:88px; border-radius:50%;
      background:#fff url('<%=ctx%>/img/sonnt.jpg') center/cover no-repeat;
      box-shadow:0 0 0 6px #fff, 0 6px 20px rgba(0,0,0,.15);
    }
    h2{margin:0 0 22px; text-align:center; font-size:22px; letter-spacing:.2px}
    label{display:block; margin-top:12px; font-weight:600; font-size:14px}
    .input{
      margin-top:6px; width:100%; padding:12px 12px;
      border:1px solid var(--border); border-radius:10px; font-size:15px; background:#fff;
      transition:border-color .15s, box-shadow .15s;
    }
    .input:focus{outline:none; border-color:#c5d7fe; box-shadow:0 0 0 3px #e8f0ff}
    .pw-wrap{position:relative}
    .toggle{
      position:absolute; right:8px; top:50%; transform:translateY(-50%);
      width:34px; height:34px; padding:0; display:flex; align-items:center; justify-content:center;
      border:1px solid var(--border); background:#fff; border-radius:8px; cursor:pointer;
    }
    .toggle svg{width:18px; height:18px}
    .btn{
      width:100%; margin-top:22px; padding:12px 16px; border:0; border-radius:10px;
      background:var(--primary); color:#fff; font-weight:700; font-size:16px; cursor:pointer;
      transition:transform .06s, box-shadow .06s;
    }
    .btn:hover{transform:translateY(-1px); box-shadow:0 8px 18px rgba(26,115,232,.25)}
    .err{
      margin:-6px 0 12px; padding:10px 12px; border-radius:10px;
      background:#fde8e8; color:#b91c1c; border:1px solid #f8caca; font-size:14px;
    }
    .foot{margin-top:16px; text-align:center; color:var(--muted); font-size:13px}
  </style>
</head>
<body>
  <div class="box">
    <div class="logo" aria-hidden="true"></div>

    <h2>Đăng nhập hệ thống</h2>

    <% if (error != null) { %>
      <div class="err"><%= error %></div>
    <% } %>

    <form method="post" action="<%= ctx %>/login" autocomplete="off">
      <label>Tên đăng nhập</label>
      <input class="input" name="username" value="<%= username==null? "" : username %>" required autofocus />

      <label>Mật khẩu</label>
      <div class="pw-wrap">
        <input id="pw" class="input" type="password" name="password" required />
        <button type="button" class="toggle" aria-label="Hiện/ẩn mật khẩu" onclick="togglePw(this)">
          <!-- icon mắt mặc định -->
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
               stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
        </button>
      </div>

      <button class="btn" type="submit">Đăng nhập</button>
    </form>


  </div>

  <script>
    function togglePw(btn){
      const input = document.getElementById('pw');
      const isShowing = input.type === 'text';
      input.type = isShowing ? 'password' : 'text';
      // đổi icon mắt ↔ mắt gạch
      btn.innerHTML = isShowing
        ? `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
             <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/>
             <circle cx="12" cy="12" r="3"/>
           </svg>`
        : `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
             <path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a21.8 21.8 0 0 1 5.06-5.94"/>
             <path d="M9.88 9.88A3 3 0 0 0 12 15a3 3 0 0 0 2.12-5.12"/>
             <line x1="1" y1="1" x2="23" y2="23"/>
           </svg>`;
    }
  </script>
</body>
</html>
