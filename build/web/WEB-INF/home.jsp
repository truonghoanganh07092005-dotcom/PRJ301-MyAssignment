<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String displayName = (String) request.getAttribute("displayName");
  String roleName = (String) request.getAttribute("roleName");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Home | Leave Management</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<style>
  :root{
    --bg:#f6f8fc; --bg2:#eef3ff;
    --card:#fff; --border:#e6e8ef; --muted:#6b7280; --text:#111827;
    --primary:#1a73e8; --menu:#12161b; --menuHover:#1f2530;
    --ok:#10b981; --warn:#f59e0b; --err:#ef4444;
  }
  *{box-sizing:border-box}
  body{
    margin:0; color:var(--text);
    font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;
    background:
      radial-gradient(1200px 600px at 10% -10%, var(--bg2), transparent),
      radial-gradient(900px 500px at 90% 110%, #e9f2ff, transparent),
      var(--bg);
  }
  a{text-decoration:none;color:inherit}

  /* Topbar */
  .topbar{position:sticky; top:0; z-index:20; backdrop-filter:saturate(160%) blur(6px);
          background:rgba(255,255,255,.7); border-bottom:1px solid var(--border)}
  .topbar-in{max-width:1100px;margin:auto;display:flex;align-items:center;gap:16px;padding:12px 16px}
  .brand{font-weight:800; letter-spacing:.4px; font-size:18px}
  .search{flex:1; position:relative}
  .search input{width:100%; padding:12px 44px 12px 14px; border:1px solid var(--border);
                border-radius:999px; background:#fff; outline:none; transition:box-shadow .15s,border-color .15s}
  .search input:focus{border-color:#c5d7fe; box-shadow:0 0 0 3px #e8f0ff}
  .search .go{position:absolute; right:6px; top:50%; transform:translateY(-50%);
              width:36px; height:36px; border:1px solid var(--border); border-radius:50%;
              display:flex; align-items:center; justify-content:center; background:#fff}
  .actions{display:flex; align-items:center; gap:10px}
  .pillIcon{width:34px;height:34px;border:1px solid var(--border);border-radius:50%;
            display:flex;align-items:center;justify-content:center;background:#fff}

  /* Avatar dropdown */
  .avatar-wrap{ position:relative }
  .avatar-btn{
    width:34px;height:34px;border-radius:50%;border:1px solid var(--border);
    background:#fff;display:flex;align-items:center;justify-content:center;cursor:pointer
  }
  .avatar-ico{font-size:18px}
  .dropdown{
    position:absolute; right:0; top:42px; width:240px; background:#fff; border:1px solid var(--border);
    border-radius:12px; box-shadow:0 16px 30px rgba(16,24,40,.10); display:none; overflow:hidden
  }
  .avatar-wrap.open .dropdown{display:block}
  .dropdown a, .dropdown .dd-hd{display:block; padding:12px 14px; border-bottom:1px solid #f2f2f2; font-size:14px}
  .dropdown a:hover{background:#f8fafc}
  .dropdown .dd-hd{background:#f9fafb}
  .dd-hd .name{font-weight:800}
  .dd-hd .role{font-size:12px; color:#6b7280; margin-top:2px}
  .dropdown a.danger{color:#ef4444}

  /* Menubar */
  .menubar{background:var(--menu); color:#fff; border-bottom:1px solid #00000014}
  .menubar-in{max-width:1100px; margin:auto; padding:0 16px}
  .tabs{display:flex; gap:12px; align-items:center; min-height:46px}
  .tab{color:#cfd6e1; display:flex; gap:8px; align-items:center; padding:9px 12px; border-radius:10px}
  .tab:hover{background:var(--menuHover)} .tab.active{background:#0d1117; color:#fff}

  /* Layout */
  .wrap{max-width:1100px; margin:22px auto; padding:0 16px}
  .section-hd{font-size:22px; font-weight:800; margin:12px 0 14px}
  .card{background:var(--card); border:1px solid var(--border); border-radius:14px;
        box-shadow:0 8px 22px rgba(16,24,40,.06); padding:14px}
  .subpill{background:#f7f8fb; border:1px dashed var(--border); border-radius:10px;
           color:var(--muted); font-size:13px; padding:8px 10px}

  /* Recent list */
  .list{display:flex; flex-direction:column; gap:12px; margin-top:12px}
  .item{background:#fff; border:1px solid #eef0f4; border-radius:12px; padding:14px;
        display:flex; gap:12px; align-items:center; transition:transform .06s, box-shadow .06s}
  .item:hover{transform:translateY(-1px); box-shadow:0 10px 20px rgba(16,24,40,.04)}
  .item .title{flex:1; white-space:nowrap; overflow:hidden; text-overflow:ellipsis}
  .more{border:1px solid var(--border); background:#fff; border-radius:10px; padding:6px 8px; cursor:pointer}

  .badge{font-size:12px; font-weight:700; border-radius:999px; padding:6px 10px}
  .approved{background:#ecfdf5; color:#065f46; border:1px solid #d1fae5}
  .inprogress{background:#fff7ed; color:#9a3412; border:1px solid #ffedd5}
  .rejected{background:#fef2f2; color:#991b1b; border:1px solid #fee2e2}

  /* Quick */
  .quick{display:grid; grid-template-columns:repeat(4, minmax(180px,1fr)); gap:14px; margin-top:22px}
  @media (max-width: 900px){ .quick{grid-template-columns:repeat(2,1fr)} }
  .qbtn{display:flex; align-items:center; justify-content:center; gap:10px; height:56px;
        border-radius:12px; font-weight:800; color:#fff; box-shadow:0 8px 18px rgba(26,115,232,.18)}
  .qbtn:hover{transform:translateY(-1px)}
  .q-blue{background:var(--primary)} .q-teal{background:#0ea5e9}
  .q-orange{background:#f59e0b} .q-gray{background:#6b7280; box-shadow:none}
</style>
</head>
<body>

  <!-- Topbar -->
  <div class="topbar">
    <div class="topbar-in">
   
      <div class="search">
        <input placeholder="Tìm kiếm (đơn, nhân viên, phòng ban) — demo UI">
        <div class="go">➜</div>
      </div>
      <div class="actions">
        <div class="pillIcon">🔔</div>

        <!-- Avatar + dropdown -->
        <div class="avatar-wrap" id="userMenu">
          <button class="avatar-btn" onclick="toggleUserMenu()" aria-label="Tài khoản">
            <span class="avatar-ico">👤</span>
          </button>
          <div class="dropdown">
            <div class="dd-hd">
              <div class="name"><%= displayName==null ? "User" : displayName %></div>
              <div class="role"><%= roleName==null ? "Member" : roleName %></div>
            </div>
            <a href="<%=ctx%>/profile">Thông tin tài khoản</a>
            <a href="<%=ctx%>/request/list?scope=my">Đơn của tôi</a>
            <a class="danger" href="<%=ctx%>/logout">Đăng xuất</a>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Menubar -->
  <div class="menubar">
    <div class="menubar-in">
      <div class="tabs">
        <a class="tab active" href="<%=ctx%>/home">🏠 Trang chủ</a>
        <a class="tab" href="<%=ctx%>/request/create">➕ Tạo đơn</a>
        <a class="tab" href="<%=ctx%>/request/list?scope=my">📄 Đơn của tôi</a>
        <a class="tab" href="<%=ctx%>/request/list?scope=team">👥 Duyệt đơn</a>
        <a class="tab" href="<%=ctx%>/division/agenda">📅 Agenda</a>
      </div>
    </div>
  </div>

  <!-- Content -->
  <div class="wrap">

    <!-- Đơn gần đây -->
    <div class="section-hd">Đơn gần đây</div>
    <div class="card">
      <div class="subpill">Các đơn bạn thao tác gần đây (tối đa 5 đơn)</div>

      <div class="list">
<%
  java.util.List<model.RequestForLeave> recent =
      (java.util.List<model.RequestForLeave>) request.getAttribute("recent");

  if (recent != null && !recent.isEmpty()) {
      for (model.RequestForLeave r : recent) {
          String title = null;
          try { title = (String) r.getClass().getMethod("getTitle").invoke(r); } catch(Exception ignore){}
          String label = (title != null && !title.trim().isEmpty())
                         ? title
                         : ("Nghỉ " + (r.getFrom()==null? "" : r.getFrom()) + " – " + (r.getTo()==null? "" : r.getTo()));

          String badgeText, badgeClass;
          switch (r.getStatus()) {
              case 1:  badgeText = "Approved";    badgeClass = "approved";   break;
              case 2:  badgeText = "Rejected";    badgeClass = "rejected";   break;
              default: badgeText = "In Progress"; badgeClass = "inprogress"; break;
          }
%>
        <div class="item">
          <div class="title"><%= label %></div>
          <span class="badge <%= badgeClass %>"><%= badgeText %></span>
          <button class="more" title="Chi tiết"
                  onclick="location.href='<%=ctx%>/request/detail?rid=<%= r.getId() /* nếu dùng rid riêng: r.getRid() */ %>'">⋯</button>
        </div>
<%
      }
  } else {
%>
        <div class="item" style="justify-content:center; color:#6b7280">
          Chưa có đơn nào gần đây.
        </div>
<%
  }
%>
      </div>
    </div>

    <!-- Lối tắt nhanh -->
    <div class="section-hd" style="margin-top:22px">Lối tắt nhanh</div>
    <div class="quick">
      <a class="qbtn q-blue"   href="<%=ctx%>/request/create">➕ Tạo đơn nghỉ</a>
      <a class="qbtn q-teal"   href="<%=ctx%>/request/list?scope=my">📄 Xem đơn của tôi</a>
      <a class="qbtn q-orange" href="<%=ctx%>/request/list?scope=team">✅ Duyệt đơn cấp dưới</a>
      <a class="qbtn q-gray"   href="<%=ctx%>/division/agenda">📅 Agenda phòng ban</a>
    </div>

  </div>

<script>
  function toggleUserMenu(){
    const box = document.getElementById('userMenu');
    box.classList.toggle('open');
    const close = (e)=>{
      if(!box.contains(e.target)){ box.classList.remove('open'); document.removeEventListener('click', close); }
    };
    document.addEventListener('click', close);
  }
</script>
</body>
</html>
