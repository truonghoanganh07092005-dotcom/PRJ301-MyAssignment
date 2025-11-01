<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, model.RequestForLeave" %>
<%
  String ctx = request.getContextPath();
  @SuppressWarnings("unchecked")
  List<RequestForLeave> list = (List<RequestForLeave>) request.getAttribute("list");
  String q = request.getParameter("q");
  String flash = (String) request.getAttribute("flash");

  java.util.function.Function<Integer,String> statusText = (s)->{
    if (s==null) return "Unknown";
    switch (s) { case 0:return "In Progress"; case 1:return "Approved"; case 2:return "Rejected"; case 3:return "Cancelled"; default:return "Unknown"; }
  };
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Đơn của tôi</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#f7f9fc;margin:0}
  .wrap{max-width:960px;margin:28px auto;padding:0 14px}
  .search{display:flex;gap:10px;margin-bottom:12px}
  .search input{flex:1;height:44px;padding:0 14px;border-radius:14px;border:1px solid #e5e7eb;background:#fff}
  .icon-btn{min-width:44px;height:44px;border-radius:14px;border:1px solid #e5e7eb;background:#fff;cursor:pointer}
  .card{background:#fff;border:1px solid #e5e7eb;border-radius:14px;padding:10px}
  .item{display:flex;justify-content:space-between;align-items:center;border-top:1px solid #e5e7eb;padding:14px 10px}
  .item:first-of-type{border-top:none}
  .title{font-weight:800}
  .badge{padding:6px 10px;border-radius:999px;font-size:13px;border:1px solid #fed7aa;background:#fff7ed;color:#9a3412}
  .approved{border-color:#bbf7d0;background:#f0fdf4;color:#065f46}
  .rejected{border-color:#fecaca;background:#fef2f2;color:#991b1b}
  .cancelled{border-color:#e5e7eb;background:#f8fafc;color:#475569}
  .more-btn{width:38px;height:38px;border-radius:12px;border:1px solid #e5e7eb;background:#fff;display:grid;place-items:center;cursor:pointer}
  .fly{position:fixed;z-index:10000;min-width:220px;background:#fff;border:1px solid #e5e7eb;border-radius:14px;display:none;box-shadow:0 24px 60px rgba(16,24,40,.18)}
  .fly a{display:block;padding:12px 14px;text-decoration:none;color:#0f172a}
  .fly a:hover{background:#f8fafc}
  .danger{color:#c1121f}
  .flash{background:#e0f2fe;border:1px solid #bae6fd;color:#0c4a6e;border-radius:10px;padding:10px 12px;margin-bottom:12px}
</style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="wrap">
  <h2>📄 Đơn của tôi</h2>

  <form class="search" method="get" action="<%=ctx%>/request/my">
    <input name="q" placeholder="Tìm theo tiêu đề (title)..." value="<%= q==null? "" : q %>">
    <button class="icon-btn" type="submit">🔎</button>
  </form>

  <% if (flash != null) { %><div class="flash"><%= flash %></div><% } %>

  <div class="card">
    <% if (list==null || list.isEmpty()) { %>
      <div style="color:#6b7280;text-align:center;padding:18px">Chưa có dữ liệu</div>
    <% } else { for (RequestForLeave r : list) {
         String t = (r.getTitle()!=null && !r.getTitle().isBlank()) ? r.getTitle() : ("Nghỉ "+r.getFrom()+" – "+r.getTo());
         int st = r.getStatus();
         String cls="badge"; if (st==1) cls+=" approved"; else if (st==2) cls+=" rejected"; else if (st==3) cls+=" cancelled";
         boolean isCancelled = (st==3);
    %>
      <div class="item">
        <div class="title"><%= t %></div>
        <div style="display:flex;gap:10px;align-items:center">
          <span class="<%=cls%>"><%= statusText.apply(st) %></span>
          <button class="more-btn" data-menu='
              <a href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Xem chi tiết</a>
              <a href="<%=ctx%>/request/print?rid=<%=r.getRid()%>">In / xuất PDF</a>
              <a href="<%=ctx%>/request/edit?rid=<%=r.getRid()%>">Sửa đơn</a>
              <% if (isCancelled) { %>
                <a href="<%=ctx%>/request/uncancel?rid=<%=r.getRid()%>">Khôi phục hủy</a>
              <% } else { %>
                <a href="<%=ctx%>/request/cancel?rid=<%=r.getRid()%>">Hủy đơn</a>
              <% } %>
              <a class="danger" href="<%=ctx%>/request/delete?rid=<%=r.getRid()%>">Xóa đơn</a>
          '>⋯</button>
        </div>
      </div>
    <% } } %>
  </div>
</div>

<div id="ctxMenu" class="fly"></div>
<script>
  const menu = document.getElementById('ctxMenu'); let opened=null;
  function openMenu(btn){ menu.innerHTML = btn.getAttribute('data-menu'); const r=btn.getBoundingClientRect();
    menu.style.display='block'; const mw=Math.max(220,menu.offsetWidth||220);
    let left=Math.min(window.innerWidth-mw-10, r.right-mw+38); if(left<10) left=10;
    let top=r.bottom+8; menu.style.left=left+'px'; menu.style.top=Math.max(10,top)+'px'; opened=btn; }
  function closeMenu(){ menu.style.display='none'; opened=null; }
  document.addEventListener('click',e=>{
    const b=e.target.closest('.more-btn'); if(b){ if(opened===b && menu.style.display==='block') closeMenu(); else openMenu(b); e.stopPropagation(); return; }
    if(!e.target.closest('#ctxMenu')) closeMenu();
  });
  document.addEventListener('keydown',e=>{ if(e.key==='Escape') closeMenu(); });
  window.addEventListener('scroll',closeMenu,{passive:true}); window.addEventListener('resize',closeMenu);
</script>
</body>
</html>
