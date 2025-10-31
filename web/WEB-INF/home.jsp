<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, model.RequestForLeave" %>
<%
  String ctx = request.getContextPath();

  // session flags
  String displayName = (String) session.getAttribute("displayName");
  if (displayName == null || displayName.isBlank()) displayName = "User";
  Boolean canReview = (Boolean) session.getAttribute("canReview");
  if (canReview == null) canReview = false;

  // controller -> view data (giữ cả tên cũ để khỏi phải sửa Controller)
  List<RequestForLeave> recentMine = (List<RequestForLeave>) request.getAttribute("recentMine");
  if (recentMine == null) recentMine = (List<RequestForLeave>) request.getAttribute("recent");

  List<RequestForLeave> recentSubs = (List<RequestForLeave>) request.getAttribute("recentSubs");
  if (recentSubs == null) recentSubs = (List<RequestForLeave>) request.getAttribute("subs");

  // status label (THÊM case 3: Cancelled)
  java.util.function.Function<Integer,String> statusText = (s) -> {
    if (s == null) return "Unknown";
    switch (s.intValue()) {
      case 0: return "In Progress";
      case 1: return "Approved";
      case 2: return "Rejected";
      case 3: return "Cancelled";
      default: return "Unknown";
    }
  };
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8" />
<title>Home | Leave Management</title>
<meta name="viewport" content="width=device-width, initial-scale=1" />
<style>
  :root{
    --bg:#f6f7fb; --ink:#0f172a; --muted:#667085; --border:#e6e9f0; --card:#ffffff;
    --chip:#fff7ed; --chip-b:#fed7aa; --chip-c:#9a3412;
    --ok:#16a34a; --ok-bg:#f0fdf4; --ok-br:#bbf7d0; --ok-ink:#065f46;
    --no:#ef4444; --no-bg:#fef2f2; --no-br:#fecaca; --no-ink:#991b1b;
    --primary:#2563eb;
  }
  *{box-sizing:border-box}
  html,body{margin:0;background:linear-gradient(180deg,#f3f6ff 0%,#f7f8fc 60%); color:var(--ink); font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}

  /* Header strip (navbar đã include) */
  .page{max-width:1120px;margin:0 auto;padding:18px 18px 64px}
  h1{font-size:34px;letter-spacing:.2px;margin:12px 0 18px}
  h2{font-size:22px;margin:26px 0 12px}

  /* Search */
  .search-wrap{display:flex;justify-content:center;margin:8px 0 6px}
  .search{display:flex;gap:10px;width:100%;max-width:820px}
  .search input{
    flex:1; height:44px; padding:0 14px 0 14px; border-radius:14px; border:1px solid var(--border);
    background:var(--card); outline:none; font-size:14px;
  }
  .search input:focus{border-color:#c7d3ff; box-shadow:0 0 0 4px rgba(37,99,235,.08)}
  .search .icon-btn{
    min-width:44px;height:44px;border-radius:14px;border:1px solid var(--border);background:var(--card); cursor:pointer;
  }

  /* Card + list */
  .card{
    background:var(--card); border:1px solid var(--border); border-radius:20px;
    box-shadow:0 8px 28px rgba(16,24,40,.06);
    overflow:hidden;
  }
  .card h3{
    margin:0; padding:14px 16px; font-size:15px; color:var(--muted);
    background:#f8fafc; border-bottom:1px dashed var(--border);
  }
  .list .row{
    display:flex; align-items:center; justify-content:space-between; gap:12px;
    padding:16px 18px; border-top:1px solid var(--border);
    transition:background .15s ease, transform .05s ease;
  }
  .list .row:first-of-type{border-top:none}
  .list .row:hover{background:#fafcff}
  .title{font-weight:800}
  .meta{color:var(--muted); font-size:13px; margin-top:6px}

  /* Chips */
  .chip{
    display:inline-flex; align-items:center; gap:6px;
    font-size:13px; padding:6px 12px; border-radius:999px;
    border:1px solid var(--chip-b); background:var(--chip); color:var(--chip-c); white-space:nowrap
  }
  .chip.approved{border-color:var(--ok-br); background:var(--ok-bg); color:var(--ok-ink)}
  .chip.rejected{border-color:var(--no-br); background:var(--no-bg); color:var(--no-ink)}
  /* THÊM style cho Cancelled */
  .chip.cancelled{border-color:#e5e7eb; background:#f3f4f6; color:#374151}

  .right{display:flex; align-items:center; gap:10px}
  .more-btn{
    width:38px;height:38px;border-radius:12px;border:1px solid var(--border);background:var(--card);
    display:grid; place-items:center; cursor:pointer; transition:transform .12s ease, box-shadow .12s ease;
  }
  .more-btn:hover{box-shadow:0 4px 16px rgba(16,24,40,.08)}
  .more-btn:active{transform:scale(.98)}

  /* Floating context menu: mount to body so it never pushes layout */
  .fly{
    position:fixed; z-index:10000; min-width:220px; background:#fff;
    border:1px solid var(--border); border-radius:14px;
    box-shadow:0 24px 60px rgba(16,24,40,.18);
    display:none; overflow:hidden; animation:show .08s ease-out;
  }
  @keyframes show{from{opacity:.4; transform:translateY(-2px)} to{opacity:1; transform:translateY(0)}}
  .fly a, .fly button{
    display:block; width:100%; padding:12px 14px; text-align:left; border:0; background:#fff;
    color:var(--ink); text-decoration:none; cursor:pointer; font-size:14px;
  }
  .fly a:hover, .fly button:hover{background:#f8fafc}
  .danger{color:#c1121f}

  /* Quick actions */
  .actions{display:grid;grid-template-columns:repeat(4,minmax(220px,1fr)); gap:14px}
  .act{
    padding:16px; border-radius:14px; border:1px solid var(--border); background:#fff;
    display:flex; align-items:center; justify-content:center; gap:10px;
    text-decoration:none; color:var(--ink); font-weight:800;
    transition:transform .12s ease, box-shadow .12s ease;
  }
  .act:hover{transform:translateY(-2px); box-shadow:0 10px 24px rgba(16,24,40,.08)}
  @media (max-width:900px){ .actions{grid-template-columns:1fr 1fr} }
</style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="page">
  <!-- Search -->
  <div class="search-wrap">
    <form action="<%=ctx%>/request/my" method="get" class="search">
      <!-- đổi placeholder: chỉ theo Title -->
      <input name="q" placeholder="Tìm theo tiêu đề (title)..." value="<%= request.getParameter("q")==null? "" : request.getParameter("q") %>">
      <button class="icon-btn" type="submit" title="Tìm kiếm">🔎</button>
    </form>
  </div>

  <!-- ĐƠN CỦA TÔI -->
  <h1>Đơn gần đây</h1>
  <div class="card">
    <h3>Các đơn của <b>bạn</b> thao tác gần đây (tối đa 5 đơn)</h3>
    <div class="list">
      <% if (recentMine == null || recentMine.isEmpty()) { %>
        <div class="row"><div class="title" style="color:var(--muted)">Chưa có dữ liệu</div></div>
      <% } else {
           for (RequestForLeave r : recentMine) {
             String t = (r.getTitle()!=null && !r.getTitle().isBlank()) ? r.getTitle() : ("Nghỉ " + r.getFrom() + " – " + r.getTo());
             int st = r.getStatus();
             String chipCls = "chip";
             if (st==1) chipCls+=" approved";
             else if (st==2) chipCls+=" rejected";
             else if (st==3) chipCls+=" cancelled";  // NEW
      %>
      <div class="row">
        <div>
          <div class="title"><%= t %></div>
          <div class="meta">Tạo lúc: <%= r.getCreated_time() %></div>
        </div>
        <div class="right">
          <span class="<%= chipCls %>"><%= statusText.apply(st) %></span>
          <button class="more-btn"
                  aria-haspopup="menu"
                  data-menu='
                    <a href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Xem chi tiết</a>
                    <a href="<%=ctx%>/request/print?rid=<%=r.getRid()%>">In / xuất PDF</a>
                    <a href="<%=ctx%>/request/cancel?rid=<%=r.getRid()%>">Hủy đơn</a>
                    <a class="danger" href="<%=ctx%>/request/delete?rid=<%=r.getRid()%>">Xóa đơn</a>
                  '>⋯</button>
        </div>
      </div>
      <% } } %>
    </div>
  </div>

  <!-- ĐƠN CẤP DƯỚI -->
  <div class="section">
    <h2>Đơn cấp dưới (gần đây)</h2>
    <div class="card">
      <h3>Những đơn thuộc cấp dưới của bạn (mới nhất trước)</h3>
      <div class="list">
        <% if (recentSubs == null || recentSubs.isEmpty()) { %>
          <div class="row"><div class="title" style="color:var(--muted)">Chưa có dữ liệu</div></div>
        <% } else {
             for (RequestForLeave r : recentSubs) {
               String t = (r.getTitle()!=null && !r.getTitle().isBlank()) ? r.getTitle() : ("Nghỉ " + r.getFrom() + " – " + r.getTo());
               String createdName = (r.getCreated_by()!=null && r.getCreated_by().getName()!=null) ? r.getCreated_by().getName() : "Nhân viên";
               int st = r.getStatus();
               String chipCls = "chip";
               if (st==1) chipCls+=" approved";
               else if (st==2) chipCls+=" rejected";
               else if (st==3) chipCls+=" cancelled";  // NEW
        %>
        <div class="row">
          <div>
            <div class="title"><%= t %></div>
            <div class="meta">Người tạo: <b><%= createdName %></b> • Tạo lúc: <%= r.getCreated_time() %></div>
          </div>
          <div class="right">
            <span class="<%= chipCls %>"><%= statusText.apply(st) %></span>
            <button class="more-btn"
                    aria-haspopup="menu"
                    data-menu='
                      <a href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Xem chi tiết</a>
                      <a href="<%=ctx%>/request/approve?rid=<%=r.getRid()%>">Duyệt</a>
                      <a class="danger" href="<%=ctx%>/request/reject?rid=<%=r.getRid()%>">Từ chối</a>
                    '>⋯</button>
          </div>
        </div>
        <% } } %>
      </div>
    </div>
  </div>

  <!-- Lối tắt -->
  <div class="section">
    <h2>Lối tắt nhanh</h2>
    <div class="actions">
      <a class="act" href="<%=ctx%>/request/create">➕ Tạo đơn nghỉ</a>
      <a class="act" href="<%=ctx%>/request/my">🧾 Xem đơn của tôi</a>
      <% if (canReview) { %>
        <a class="act" href="<%=ctx%>/request/review">✅ Duyệt đơn cấp dưới</a>
      <% } else { %>
        <a class="act" href="<%=ctx%>/request/my?created=1">📄 Đơn vừa tạo</a>
      <% } %>
      <a class="act" href="<%=ctx%>/agenda">📅 Agenda phòng ban</a>
    </div>
  </div>
</div>

<!-- 1 floating context menu duy nhất -->
<div id="ctxMenu" class="fly" role="menu" aria-hidden="true"></div>

<script>
  const menu = document.getElementById('ctxMenu');
  let openedBy = null;

  function positionMenu(btn){
    const r = btn.getBoundingClientRect();
    const gap = 10;
    // đo trước để tránh tràn cạnh phải
    menu.style.display = 'block';
    const mw = Math.max(220, menu.offsetWidth || 220);
    const mh = Math.max(10,  menu.offsetHeight || 10);
    let left = Math.min(window.innerWidth - mw - gap, r.right - mw + 38);
    if (left < gap) left = gap;
    let top  = r.bottom + 8;
    if (top + mh + gap > window.innerHeight) top = r.top - mh - 8;
    menu.style.left = left + 'px';
    menu.style.top  = Math.max(gap, top) + 'px';
  }

  function openMenu(btn){
    // nội dung menu theo từng item
    menu.innerHTML = btn.getAttribute('data-menu');
    positionMenu(btn);
    menu.setAttribute('aria-hidden', 'false');
    openedBy = btn;
  }

  function closeMenu(){
    menu.style.display = 'none';
    menu.setAttribute('aria-hidden', 'true');
    openedBy = null;
  }

  // Toggle khi nhấn “...”
  document.addEventListener('click', (e)=>{
    const b = e.target.closest('.more-btn');
    if (b){
      if (openedBy === b && menu.style.display === 'block') closeMenu();
      else openMenu(b);
      e.stopPropagation();
      return;
    }
    if (!e.target.closest('#ctxMenu')) closeMenu();
  });
  // ESC, scroll, resize -> đóng
  document.addEventListener('keydown', (e)=>{ if (e.key==='Escape') closeMenu(); });
  window.addEventListener('scroll', closeMenu, {passive:true});
  window.addEventListener('resize', closeMenu);
</script>

</body>
</html>
