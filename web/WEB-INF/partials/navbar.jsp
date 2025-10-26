<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  // --- Khai báo 1 lần các biến dùng trong navbar ---
  String ctx = (String) request.getAttribute("_ctx");
  if (ctx == null) ctx = request.getContextPath();

  String uri = request.getRequestURI();

  String displayName = (String) request.getAttribute("_displayName");
  if (displayName == null || displayName.isBlank()) {
      displayName = (String) session.getAttribute("displayName");
      if (displayName == null || displayName.isBlank()) displayName = "User";
  }

  Boolean canReview = (Boolean) request.getAttribute("_canReview");
  if (canReview == null) canReview = (Boolean) session.getAttribute("canReview");
  if (canReview == null) canReview = false;

  // --- Biến final để dùng trong lambda (tránh lỗi effectively final) ---
  final String BASE = ctx;
  final String CURR_URI = uri;

  java.util.function.Function<String,String> active =
      p -> (CURR_URI.startsWith(BASE + p) ? "active" : "");
%>

<style>
  .navbar{background:#0f1625;color:#fff;padding:10px 18px;display:flex;align-items:center;gap:18px}
  .navbar a{color:#c7d2fe;text-decoration:none;padding:6px 10px;border-radius:10px}
  .navbar a.active{background:#1f2937;color:#fff}
  .nav-right{margin-left:auto;display:flex;align-items:center;gap:10px}
  .avatar{width:40px;height:40px;border-radius:50%;display:grid;place-items:center;
          background:linear-gradient(135deg,#9b8cf2,#7c3aed);font-weight:700}
  .dropdown{position:relative}
  .dropdown-panel{position:absolute;right:0;top:52px;background:#fff;color:#0f172a;
      border:1px solid #e5e7eb;border-radius:12px;min-width:240px;display:none;
      box-shadow:0 24px 60px rgba(0,0,0,.12);overflow:hidden;z-index:9999}
  .dropdown-panel .head{padding:12px 14px;border-bottom:1px solid #e5e7eb;background:#f8fafc}
  .dropdown-panel a{display:block;padding:12px 14px;text-decoration:none;color:#0f172a}
  .dropdown-panel a:hover{background:#f1f5f9}
</style>

<div class="navbar">
  <a class="<%=active.apply("/home")%>"            href="<%=ctx%>/home">🏠 Trang chủ</a>
  <a class="<%=active.apply("/request/create")%>" href="<%=ctx%>/request/create">➕ Tạo đơn</a>
  <a class="<%=active.apply("/request/my")%>"     href="<%=ctx%>/request/my">🗂️ Đơn của tôi</a>
  <% if (canReview) { %>
    <a class="<%=active.apply("/request/review")%>" href="<%=ctx%>/request/review">👥 Duyệt đơn</a>
  <% } %>
  <a class="<%=active.apply("/agenda")%>"         href="<%=ctx%>/agenda">📅 Agenda</a>

  <div class="nav-right">
    <a class="<%=active.apply("/search")%>"  href="<%=ctx%>/search"  title="Tìm kiếm">➤</a>
    <a class="<%=active.apply("/notify")%>"  href="<%=ctx%>/notify"  title="Thông báo">🔔</a>

    <!-- Avatar + menu -->
    <div class="dropdown" id="userDrop">
      <div class="avatar" id="btnAvatar"><%= displayName.substring(0,1).toUpperCase() %></div>
      <div class="dropdown-panel" id="panelAvatar">
        <div class="head">Xin chào, <b style="color:#2563eb"><%=displayName%></b></div>
        <a href="<%=ctx%>/profile">Thông tin cá nhân</a>
        <a href="<%=ctx%>/request/my">Đơn của tôi</a>
        <a href="<%=ctx%>/logout">Đăng xuất</a>
      </div>
    </div>
  </div>
</div>

<script>
  (function(){
    const btn = document.getElementById('btnAvatar');
    const panel = document.getElementById('panelAvatar');
    const drop = document.getElementById('userDrop');
    btn.addEventListener('click', (e)=>{
      panel.style.display = (panel.style.display==='block') ? 'none' : 'block';
      e.stopPropagation();
    });
    document.addEventListener('click', (e)=>{
      if (!e.target.closest('#userDrop')) panel.style.display='none';
    });
    window.addEventListener('resize', ()=> panel.style.display='none');
    window.addEventListener('scroll', ()=> panel.style.display='none', {passive:true});
  })();
</script>
