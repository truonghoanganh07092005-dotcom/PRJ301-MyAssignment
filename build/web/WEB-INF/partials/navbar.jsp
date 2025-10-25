<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%-- ===== Helpers (JSP declaration: không tạo biến cục bộ trùng lặp) ===== --%>
<%! 
  String active(String currentUri, String matchPrefix) {
    try { 
      return (currentUri != null && currentUri.startsWith(matchPrefix)) ? "active" : ""; 
    } catch (Exception e) { 
      return ""; 
    }
  }
%>

<%
  // Các biến dùng trong navbar: KHAI BÁO MỘT LẦN
  String ctx = request.getContextPath();
  String uri = request.getRequestURI();

  String displayName = (String) session.getAttribute("displayName");
  if (displayName == null || displayName.isBlank()) displayName = "User";

  Boolean canReview = (Boolean) session.getAttribute("canReview");
  if (canReview == null) canReview = false;
%>

<style>
  .topbar{background:#0f1625;color:#fff;padding:10px 18px;display:flex;align-items:center;gap:16px}
  .brand{font-weight:800;letter-spacing:.5px}
  .navlinks{display:flex;gap:10px}
  .navlink{color:#c7d2fe;text-decoration:none;padding:8px 12px;border-radius:12px;display:inline-flex;align-items:center;gap:6px}
  .navlink.active{background:#1f2937;color:#fff}
  .nav-right{margin-left:auto;display:flex;align-items:center;gap:10px}
  .icon-btn{width:40px;height:40px;border-radius:50%;border:1px solid #e5e7eb;background:#fff;display:grid;place-items:center;cursor:pointer;color:#111}
  .avatar{width:40px;height:40px;border-radius:50%;border:1px solid #e5e7eb;background:linear-gradient(135deg,#9b8cf2,#7c3aed);color:#fff;display:grid;place-items:center;font-weight:700;cursor:pointer}
  .dropdown{position:relative}
  .dropdown-panel{position:absolute;right:0;top:52px;background:#fff;border:1px solid #e5e7eb;border-radius:14px;box-shadow:0 24px 60px rgba(0,0,0,.12);min-width:240px;display:none;overflow:hidden;color:#111}
  .dropdown-panel .head{padding:12px 14px;border-bottom:1px solid #e5e7eb;background:#f8fafc}
  .dropdown-panel a{display:block;padding:12px 14px;color:#111;text-decoration:none}
  .dropdown-panel a:hover{background:#f1f5f9}
</style>

<div class="topbar">
 

  <nav class="navlinks">
    <a class="navlink <%= active(uri, ctx + "/home") %>"             href="<%=ctx%>/home">🏠 Trang chủ</a>
    <a class="navlink <%= active(uri, ctx + "/request/create") %>"   href="<%=ctx%>/request/create">➕ Tạo đơn</a>
    <a class="navlink <%= active(uri, ctx + "/request/my") %>"       href="<%=ctx%>/request/my">📁 Đơn của tôi</a>
    <% if (canReview) { %>
      <a class="navlink <%= active(uri, ctx + "/request/review") %>" href="<%=ctx%>/request/review">👥 Duyệt đơn</a>
    <% } %>
    <a class="navlink <%= active(uri, ctx + "/agenda") %>"           href="<%=ctx%>/agenda">🗓️ Agenda</a>
  </nav>

  <div class="nav-right">
    <div class="icon-btn">➤</div>
    <div class="icon-btn">🔔</div>

    <div class="dropdown" id="navProfile">
      <div class="avatar" id="btnAvatar"><%= displayName.substring(0,1).toUpperCase() %></div>
      <div class="dropdown-panel" id="panelAvatar">
        <div class="head">Xin chào, <b style="color:#2563eb"><%= displayName %></b></div>
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
    if(btn){
      btn.addEventListener('click', ()=> {
        panel.style.display = (panel.style.display === 'block') ? 'none' : 'block';
      });
      document.addEventListener('click', (e)=>{
        if(!e.target.closest('#navProfile')) panel.style.display = 'none';
      });
    }
  })();
</script>
