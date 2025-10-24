<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String displayName = (String) request.getAttribute("displayName"); // set từ HomeController
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Home | Leave Management</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<style>
  :root{
    --bg:#f6f7fb; --card:#fff; --border:#e5e7eb; --muted:#6b7280; --dark:#1f2937;
    --primary:#1a73e8; --menu:#2b2f33;
  }
  *{box-sizing:border-box}
  body{margin:0;background:var(--bg);font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;color:var(--dark)}
  a{text-decoration:none;color:inherit}

  /* TOP BAR */
  .topbar{
    background:#f1f3f7; border-bottom:1px solid var(--border);
  }
  .topbar-inner{
    max-width:1200px; margin:auto; display:flex; align-items:center; gap:16px; padding:10px 16px;
  }
  .logo{display:flex; align-items:center; gap:10px}
  .logo img{width:56px; height:28px; object-fit:contain; filter:grayscale(0.1)}
  .search{
    flex:1; position:relative;
  }
  .search input{
    width:100%; padding:12px 44px 12px 14px; border:1px solid var(--border); border-radius:999px; background:#fff;
  }
  .search .go{
    position:absolute; right:6px; top:50%; transform:translateY(-50%);
    width:36px; height:36px; border-radius:50%; display:flex; align-items:center; justify-content:center;
    border:1px solid var(--border); background:#fff; cursor:pointer;
  }
  .icons{display:flex; align-items:center; gap:10px; margin-left:auto}
  .bell{width:34px; height:34px; border-radius:50%; display:flex; align-items:center; justify-content:center; border:1px solid var(--border); background:#fff}
  .avatar-wrap{position:relative}
  .avatar{
    width:38px;height:38px;border-radius:50%;border:1px solid var(--border); background:#fff url('<%=ctx%>/img/sonnt.jpg') center/cover no-repeat;
    cursor:pointer;
  }
  .dropdown{
    position:absolute; right:0; top:46px; width:220px; background:#fff; border:1px solid var(--border); border-radius:10px;
    box-shadow:0 12px 30px rgba(0,0,0,.08); display:none; overflow:hidden;
  }
  .dropdown a, .dropdown div{
    display:block; padding:10px 14px; border-bottom:1px solid #f2f2f2; color:#111; font-size:14px;
  }
  .dropdown a:hover{background:#f8fafc}
  .dropdown .title{font-weight:700; background:#f9fafb}
  .avatar-wrap.open .dropdown{display:block}

  /* MENU BAR */
  .menubar{background:var(--menu); color:#fff; border-bottom:1px solid #00000010}
  .menubar-inner{max-width:1200px; margin:auto; padding:0 16px;}
  .tabs{display:flex; gap:18px; align-items:center; height:46px}
  .tab{display:flex; align-items:center; gap:8px; padding:10px 12px; border-radius:10px; color:#e5e7eb}
  .tab.active{background:#111827; color:#fff}
  .tab:hover{background:#374151}

  /* PAGE BODY */
  .wrap{max-width:1200px; margin:18px auto; padding:0 16px;}
  .grid{display:grid; grid-template-columns:1fr 1fr; gap:20px}
  @media (max-width: 900px){ .grid{grid-template-columns:1fr} }

  .section-title{font-size:20px; font-weight:800; margin:8px 0 10px}
  .card{
    background:var(--card); border:1px solid var(--border); border-radius:12px; padding:14px;
    box-shadow:0 6px 14px rgba(0,0,0,.04);
  }
  .pill{background:#f3f4f6; color:#6b7280; border:1px dashed #e5e7eb; padding:8px 10px; border-radius:10px; font-size:13px}
  .list{display:flex; flex-direction:column; gap:10px; margin-top:12px}
  .item{display:flex; gap:10px; align-items:center}
  .item .title{flex:1; white-space:nowrap; overflow:hidden; text-overflow:ellipsis}
  .soft{background:#f9fafb; border:1px solid #eef0f3; border-radius:10px; padding:10px}
  .xbtn{border:1px solid var(--border); background:#fff; border-radius:8px; padding:6px 8px; cursor:pointer}
  .textarea{width:100%; min-height:120px; border:1px solid var(--border); border-radius:10px; padding:10px; resize:vertical}

  .like-box{background:linear-gradient(180deg, #e9ffe9, #f7fdf7); border:1px solid #dcfce7; border-radius:16px; padding:16px}
  .thumbs{display:grid; grid-template-columns:repeat(5, minmax(120px,1fr)); gap:12px}
  .thumb{background:#fff; border:1px solid #e5e7eb; border-radius:12px; height:110px; overflow:hidden}
  .thumb img{width:100%; height:100%; object-fit:cover}

  .quick{display:grid; grid-template-columns:repeat(4, minmax(160px,1fr)); gap:12px}
  .btn{
    display:flex; align-items:center; justify-content:center; gap:8px; font-weight:700;
    padding:14px; border-radius:12px; color:#fff; background:var(--primary);
    box-shadow:0 6px 16px rgba(26,115,232,.20);
  }
  .btn.gray{background:#6b7280; box-shadow:none}
  .btn.orange{background:#f59e0b}
  .btn.teal{background:#0ea5e9}
  .btn:hover{transform:translateY(-1px)}
</style>
</head>

<body>

  <!-- TOP BAR -->
  <div class="topbar">
    <div class="topbar-inner">
      <div class="logo">
        <img src="<%=ctx%>/img/sonnt.jpg" alt="logo">
        <div style="font-weight:800; letter-spacing:.5px;">STV</div>
      </div>

      <div class="search">
        <input placeholder="Tìm kiếm (đơn, nhân viên, phòng ban) — demo UI" />
        <button class="go" title="Tìm kiếm">➜</button>
      </div>

      <div class="icons">
        <div class="bell">🔔</div>

        <div class="avatar-wrap" id="avtBox">
          <div class="avatar" onclick="toggleMenu()"></div>
          <div class="dropdown">
            <div class="title">THẦN TỘI</div>
            <div style="padding:10px 14px; font-size:13px; color:var(--muted)">Xin chào, <b><%= displayName==null? "User" : displayName %></b></div>
            <a href="<%=ctx%>/profile">Thông tin cá nhân</a>
            <a href="<%=ctx%>/settings">Cài đặt</a>
            <a href="<%=ctx%>/logout" style="color:#ef4444">Đăng xuất</a>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- MENU BAR -->
  <div class="menubar">
    <div class="menubar-inner">
      <div class="tabs">
        <a class="tab active" href="<%=ctx%>/home">🏠 Trang chủ</a>
        <a class="tab" href="<%=ctx%>/request/create">➕ Tạo đơn</a>
        <a class="tab" href="<%=ctx%>/request/list?scope=my">📄 Đơn của tôi</a>
        <a class="tab" href="<%=ctx%>/request/list?scope=team">👥 Duyệt đơn</a>
        <a class="tab" href="<%=ctx%>/division/agenda">📅 Agenda</a>
      </div>
    </div>
  </div>

  <!-- BODY -->
  <div class="wrap">
    <div class="grid">
      <!-- Cột trái -->
      <div>
        <div class="section-title">Truyền vừa đọc</div>
        <div class="card">
          <div class="pill">Đơn gần đây của bạn (demo)</div>
          <div class="list">
            <div class="item soft">
              <div class="title">Nghỉ phép 23–24/10 • Trạng thái: Approved</div>
              <button class="xbtn" onclick="alert('Mở chi tiết đơn #1 (demo)')">...</button>
            </div>
            <div class="item soft">
              <div class="title">Nghỉ ốm 01/11 • Trạng thái: InProgress</div>
              <button class="xbtn">...</button>
            </div>
            <div class="item soft">
              <div class="title">Nghỉ cưới 05–07/12 • Trạng thái: Rejected</div>
              <button class="xbtn">...</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Cột phải -->
      <div>
        <div class="section-title">Dịch nhanh &gt;</div>
        <div class="card">
          <textarea class="textarea" placeholder="Ghi chú/nhắc việc nhanh (demo UI)."></textarea>
          <div style="margin-top:10px; display:flex; gap:10px; justify-content:flex-end">
            <button class="xbtn" onclick="alert('Lưu ghi chú (demo)')">Lưu</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Lối tắt nhanh -->
    <div class="section-title" style="margin-top:20px">Lối tắt nhanh</div>
    <div class="quick">
      <a class="btn" href="<%=ctx%>/request/create">➕ Tạo đơn nghỉ</a>
      <a class="btn teal" href="<%=ctx%>/request/list?scope=my">📄 Xem đơn của tôi</a>
      <a class="btn orange" href="<%=ctx%>/request/list?scope=team">✅ Duyệt đơn cấp dưới</a>
      <a class="btn gray" href="<%=ctx%>/division/agenda">📅 Agenda phòng ban</a>
    </div>

    <!-- Được nhiều Like -->
    <div class="section-title" style="margin-top:24px">Được nhiều Like &gt;</div>
    <div class="like-box">
      <div class="thumbs">
        <div class="thumb"><img src="https://picsum.photos/seed/a/400/300" alt=""></div>
        <div class="thumb"><img src="https://picsum.photos/seed/b/400/300" alt=""></div>
        <div class="thumb"><img src="https://picsum.photos/seed/c/400/300" alt=""></div>
        <div class="thumb"><img src="https://picsum.photos/seed/d/400/300" alt=""></div>
        <div class="thumb"><img src="https://picsum.photos/seed/e/400/300" alt=""></div>
      </div>
    </div>
  </div>

<script>
  function toggleMenu(){
    var box = document.getElementById('avtBox');
    box.classList.toggle('open');
    // click ngoài để đóng
    document.addEventListener('click', function onDoc(e){
      if(!box.contains(e.target)){
        box.classList.remove('open');
        document.removeEventListener('click', onDoc);
      }
    });
  }
</script>
</body>
</html>
