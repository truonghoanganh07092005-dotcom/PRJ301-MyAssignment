<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, model.RequestForLeave" %>

<%
  String ctx = request.getContextPath();
  String displayName = (String) session.getAttribute("displayName");
  if (displayName == null || displayName.isBlank()) displayName = "User";
  Boolean canReview = (Boolean) session.getAttribute("canReview");
  if (canReview == null) canReview = false;

  List<RequestForLeave> recent = (List<RequestForLeave>) request.getAttribute("recent"); // có thể null
  List<RequestForLeave> subs   = (List<RequestForLeave>) request.getAttribute("subs");   // có thể null

  java.util.function.Function<Integer,String> statusText = (s) -> {
      if (s == null) return "Unknown";
      switch (s.intValue()) {
        case 0: return "In Progress";
        case 1: return "Approved";
        case 2: return "Rejected";
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
  :root{ --bg:#f5f7fb; --card:#fff; --ink:#0f172a; --muted:#64748b; --border:#e5e7eb; }
  *{box-sizing:border-box}
  body{margin:0;background:var(--bg);color:var(--ink);font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}
  .page{max-width:1120px;margin:0 auto;padding:8px 18px 48px}
  h1{font-size:28px;margin:18px 0 12px}
  .card{background:#fff;border:1px solid var(--border);border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.04)}
  .card h3{font-size:15px;color:var(--muted);margin:0 16px 8px;padding:12px;border-radius:12px;background:#f8fafc;border:1px dashed var(--border)}
  .item{display:flex;align-items:center;gap:12px;justify-content:space-between;padding:16px 18px;border-top:1px solid var(--border)}
  .item:first-of-type{border-top:none}
  .title{font-weight:700}
  .meta{color:var(--muted);font-size:13px;margin-top:4px}
  .chip{font-size:13px;padding:6px 12px;border-radius:999px;border:1px solid #fed7aa;background:#fff7ed;color:#9a3412}
  .chip.approved{border-color:#bbf7d0;background:#f0fdf4;color:#065f46}
  .chip.rejected{border-color:#fecaca;background:#fef2f2;color:#991b1b}
  .more{position:relative}
  .more-btn{width:36px;height:36px;border-radius:10px;border:1px solid var(--border);background:#fff;display:grid;place-items:center;cursor:pointer}
  .menu{position:absolute;right:0;top:42px;background:#fff;border:1px solid var(--border);border-radius:12px;box-shadow:0 20px 40px rgba(0,0,0,.1);display:none;min-width:200px;overflow:hidden}
  .menu a{display:block;padding:10px 12px;color:#111;text-decoration:none}
  .menu a:hover{background:#f8fafc}
  .section{margin-top:26px}
  .actions{display:grid;grid-template-columns:repeat(4,minmax(200px,1fr));gap:14px}
  .act{padding:16px;border-radius:14px;border:1px solid var(--border);background:#fff;display:flex;align-items:center;gap:10px;justify-content:center;text-decoration:none;color:#111;font-weight:700}
  @media(max-width:900px){ .actions{grid-template-columns:1fr 1fr} }
</style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp" />

<div class="page">

  <h1>Đơn gần đây</h1>
  <div class="card">
    <h3>Các đơn bạn thao tác gần đây (tối đa 5 đơn)</h3>

    <% if (recent == null || recent.isEmpty()) { %>
      <div class="item"><div class="title" style="color:var(--muted)">Chưa có dữ liệu</div></div>
    <% } else {
         for (RequestForLeave r : recent) {
           String title = (r.getTitle() != null && !r.getTitle().isBlank())
                        ? r.getTitle()
                        : ("Nghỉ " + r.getFrom() + " – " + r.getTo());
           int st = r.getStatus();
           String chipCls = "chip";
           if (st == 1) chipCls += " approved";
           else if (st == 2) chipCls += " rejected";
    %>
      <div class="item">
        <div class="title"><%= title %></div>
        <div style="display:flex;align-items:center;gap:10px">
          <span class="<%= chipCls %>"><%= statusText.apply(Integer.valueOf(st)) %></span>
          <div class="more">
            <button class="more-btn" onclick="toggleMenu(this)">⋯</button>
            <div class="menu">
              <a href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Xem chi tiết</a>
              <a href="<%=ctx%>/request/print?rid=<%=r.getRid()%>">In / xuất PDF</a>
              <a href="<%=ctx%>/request/cancel?rid=<%=r.getRid()%>">Hủy đơn</a>
              <a href="<%=ctx%>/request/delete?rid=<%=r.getRid()%>">Xóa đơn</a>
            </div>
          </div>
        </div>
      </div>
    <%   } // for
       } // else %>
  </div>

  <% if (subs != null && !subs.isEmpty()) { %>
  <div class="section">
    <h1>Đơn cấp dưới</h1>
    <div class="card">
      <h3>Những đơn thuộc cấp dưới của bạn (mới nhất trước)</h3>
      <% for (RequestForLeave r : subs) {
           String title = (r.getTitle()!=null && !r.getTitle().isBlank())
                        ? r.getTitle()
                        : ("Nghỉ " + r.getFrom() + " – " + r.getTo());
           String createdName = (r.getCreated_by()!=null && r.getCreated_by().getName()!=null)
                        ? r.getCreated_by().getName()
                        : "Nhân viên";
           int st = r.getStatus();
           String chipCls = "chip";
           if (st == 1) chipCls += " approved";
           else if (st == 2) chipCls += " rejected";
      %>
        <div class="item">
          <div>
            <div class="title"><%= title %></div>
            <div class="meta">Người tạo: <%= createdName %></div>
          </div>
          <div style="display:flex;align-items:center;gap:10px">
            <span class="<%= chipCls %>"><%= statusText.apply(Integer.valueOf(st)) %></span>
            <div class="more">
              <button class="more-btn" onclick="toggleMenu(this)">⋯</button>
              <div class="menu">
                <a href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Xem chi tiết</a>
                <a href="<%=ctx%>/request/approve?rid=<%=r.getRid()%>">Duyệt</a>
                <a href="<%=ctx%>/request/reject?rid=<%=r.getRid()%>">Từ chối</a>
              </div>
            </div>
          </div>
        </div>
      <% } %>
    </div>
  </div>
  <% } %>

  <div class="section">
    <h1>Lối tắt nhanh</h1>
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

<script>
  function toggleMenu(btn){
    const menu = btn.nextElementSibling;
    document.querySelectorAll('.menu').forEach(m => { if (m!==menu) m.style.display='none'; });
    menu.style.display = (menu.style.display === 'block') ? 'none':'block';
  }
  document.addEventListener('click', (e)=>{
    if(!e.target.closest('.more')) {
      document.querySelectorAll('.menu').forEach(m => m.style.display='none');
    }
  });
</script>

</body>
</html>
