<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, model.RequestForLeave" %>

<%-- ===== Helpers (must use JSP declaration <%! ... %>) ===== --%>
<%!
  String statusText(Integer s){
    if (s == null) return "Unknown";
    switch (s.intValue()){
      case 1: return "Approved";
      case 2: return "Rejected";
      case 3: return "Cancelled";
      default: return "In Progress";
    }
  }
  String statusCls(Integer s){
    if (s == null) return "chip";
    switch (s.intValue()){
      case 1: return "chip approved";
      case 2: return "chip rejected";
      case 3: return "chip cancelled";
      default: return "chip";
    }
  }
%>

<%
  String ctx = request.getContextPath();
  List<RequestForLeave> mine = (List<RequestForLeave>) request.getAttribute("mine");
  List<RequestForLeave> subs = (List<RequestForLeave>) request.getAttribute("subs");
%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Agenda | Leave Management</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
  :root{
    --bg:#f6f7fb; --ink:#0f172a; --muted:#667085; --border:#e6e9f0; --card:#fff;
    --chip:#fff7ed; --chip-b:#fed7aa; --chip-c:#9a3412;
    --ok-bg:#f0fdf4; --ok-br:#bbf7d0; --ok-ink:#065f46;
    --no-bg:#fef2f2; --no-br:#fecaca; --no-ink:#991b1b;
    --ca-bg:#f1f5f9; --ca-br:#e2e8f0; --ca-ink:#0f172a;
  }
  *{box-sizing:border-box}
  body{margin:0;background:linear-gradient(180deg,#f3f6ff 0%,#f7f8fc 60%);color:var(--ink);
       font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}
  .page{max-width:1120px;margin:0 auto;padding:18px}
  h1{font-size:32px;margin:12px 0 18px}
  .grid{display:grid;grid-template-columns:1fr 1fr;gap:16px}
  @media(max-width:900px){.grid{grid-template-columns:1fr}}
  .card{background:var(--card);border:1px solid var(--border);border-radius:20px;
        box-shadow:0 8px 28px rgba(16,24,40,.06);overflow:hidden}
  .card h3{margin:0;padding:14px 16px;font-size:15px;color:var(--muted);
           background:#f8fafc;border-bottom:1px dashed var(--border)}
  .list .row{display:flex;justify-content:space-between;align-items:center;gap:12px;
             padding:14px 16px;border-top:1px solid var(--border)}
  .list .row:first-child{border-top:none}
  .title{font-weight:800}
  .meta{color:var(--muted);font-size:13px;margin-top:4px}
  .chip{display:inline-flex;align-items:center;gap:6px;font-size:13px;padding:6px 12px;
        border-radius:999px;border:1px solid var(--chip-b);background:var(--chip);color:var(--chip-c)}
  .approved{border-color:var(--ok-br);background:var(--ok-bg);color:var(--ok-ink)}
  .rejected{border-color:var(--no-br);background:var(--no-bg);color:var(--no-ink)}
  .cancelled{border-color:var(--ca-br);background:var(--ca-bg);color:var(--ca-ink)}
  .right{display:flex;gap:8px}
  .btn{display:inline-block;padding:8px 10px;border:1px solid var(--border);border-radius:10px;
       text-decoration:none;color:var(--ink);background:#fff}
</style>
</head>
<body>
<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="page">
  <h1>Agenda</h1>

  <div class="grid">
    <div class="card">
      <h3>Đơn của bạn (mới nhất)</h3>
      <div class="list">
        <% if (mine == null || mine.isEmpty()) { %>
          <div class="row"><span class="meta">Chưa có dữ liệu</span></div>
        <% } else { for (model.RequestForLeave r : mine) {
             String t = (r.getTitle()!=null && !r.getTitle().trim().isEmpty())
                        ? r.getTitle() : ("Nghỉ " + r.getFrom() + " – " + r.getTo()); %>
          <div class="row">
            <div>
              <div class="title"><%= t %></div>
              <div class="meta">Tạo lúc: <%= r.getCreated_time() %></div>
            </div>
            <div class="right">
              <span class="<%= statusCls(r.getStatus()) %>"><%= statusText(r.getStatus()) %></span>
              <a class="btn" href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Chi tiết</a>
            </div>
          </div>
        <% } } %>
      </div>
    </div>

    <div class="card">
      <h3>Đơn cấp dưới (mới nhất)</h3>
      <div class="list">
        <% if (subs == null || subs.isEmpty()) { %>
          <div class="row"><span class="meta">Chưa có dữ liệu</span></div>
        <% } else { for (model.RequestForLeave r : subs) {
             String createdName = (r.getCreated_by()!=null && r.getCreated_by().getName()!=null)
                                  ? r.getCreated_by().getName() : "Nhân viên";
             String t = (r.getTitle()!=null && !r.getTitle().trim().isEmpty())
                        ? r.getTitle() : ("Nghỉ " + r.getFrom() + " – " + r.getTo()); %>
          <div class="row">
            <div>
              <div class="title"><%= t %></div>
              <div class="meta">Người tạo: <b><%= createdName %></b> • Tạo lúc: <%= r.getCreated_time() %></div>
            </div>
            <div class="right">
              <span class="<%= statusCls(r.getStatus()) %>"><%= statusText(r.getStatus()) %></span>
              <a class="btn" href="<%=ctx%>/request/detail?rid=<%=r.getRid()%>">Chi tiết</a>
            </div>
          </div>
        <% } } %>
      </div>
    </div>
  </div>
</div>
</body>
</html>
