<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.*, java.util.*" %>
<%
  String ctx = request.getContextPath();

  String fromIso = (String) request.getAttribute("fromIso");
  String toIso   = (String) request.getAttribute("toIso");

  boolean viewAll    = Boolean.TRUE.equals(request.getAttribute("viewAll"));
  boolean canViewAll = Boolean.TRUE.equals(request.getAttribute("canViewAll"));

  @SuppressWarnings("unchecked")
  List<LocalDate> cols = (List<LocalDate>) request.getAttribute("cols");

  @SuppressWarnings("unchecked")
  List<Map<String,Object>> rows = (List<Map<String,Object>>) request.getAttribute("rows");

  if (fromIso == null) fromIso = "";
  if (toIso   == null) toIso   = "";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Agenda (duyệt đơn nghỉ)</title>
<meta name="viewport" content="width=device-width, initial-scale=1" />
<style>
  :root{
    --ink:#0f172a; --muted:#667085; --card:#ffffff; --border:#e5e7eb;
    --work:#eaf7ea;         /* nền ngày đi làm */
    --leave:#fde2e2;        /* nền ngày nghỉ (đã duyệt) – hồng nhạt */
    --primary:#2563eb;
  }
  *{box-sizing:border-box}
  html,body{margin:0; font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif; color:var(--ink); background:#f7f8fc}

  .page{max-width:1200px; margin:0 auto; padding:16px 16px 48px}
  h1{margin:6px 0 16px; font-size:28px}

  .filters{
    display:flex; align-items:center; gap:10px; flex-wrap:wrap; margin:8px 0 14px;
  }
  .filters input[type=date]{height:38px; padding:0 10px; border:1px solid var(--border); border-radius:10px; background:#fff}
  .filters .btn{
    height:38px; padding:0 16px; border-radius:10px; border:1px solid var(--border); background:#fff; cursor:pointer;
  }
  .legend{margin-left:auto; display:flex; gap:18px; color:var(--muted); font-size:14px}
  .legend .dot{display:inline-block; width:14px; height:14px; border-radius:4px; margin-right:6px; border:1px solid var(--border); vertical-align:-2px}
  .legend .leave{background:var(--leave)}
  .legend .work{background:var(--work)}

  .wrap{border:1px solid var(--border); border-radius:14px; overflow:auto; background:var(--card)}
  table{border-collapse:separate; border-spacing:0; width:100%; min-width:900px}
  thead th{position:sticky; top:0; z-index:3; background:#fff; border-bottom:1px solid var(--border); padding:10px 8px; text-align:center}
  tbody td{padding:0}
  .name-cell{
    position:sticky; left:0; z-index:2; background:#fff; border-right:1px solid var(--border);
    padding:10px 12px; white-space:nowrap; max-width:240px;
  }
  .day-cell{
    width:64px; height:40px; border-left:1px solid var(--border); border-bottom:1px solid var(--border);
    text-align:center; vertical-align:middle;
  }
  .work{background:var(--work)}
  .leave{background:var(--leave)}
  .thead-name{position:sticky; left:0; z-index:4; background:#fff; border-right:1px solid var(--border)}
  .date{font-weight:700}
  .dow{color:var(--muted); font-size:12px}
</style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="page">
  <h1>Agenda (duyệt đơn nghỉ)</h1>

  <form class="filters" method="get" action="<%=ctx%>/agenda">
    <label>Từ ngày:
      <input type="date" name="from" value="<%=fromIso%>">
    </label>
    <label>Đến ngày:
      <input type="date" name="to" value="<%=toIso%>">
    </label>

    <% if (canViewAll) { %>
      <label style="display:flex;align-items:center;gap:6px;margin-left:8px;">
        <input type="checkbox" name="all" value="1" <%= viewAll ? "checked" : "" %> />
        Xem tất cả nhân sự
      </label>
    <% } %>

    <button class="btn" type="submit">Xem</button>

    <div class="legend">
      <span><span class="dot leave"></span> Nghỉ (đã duyệt)</span>
      <span><span class="dot work"></span> Đi làm</span>
    </div>
  </form>

  <div class="wrap">
    <table>
      <thead>
      <tr>
        <th class="thead-name">Nhân sự</th>
        <% if (cols != null) {
             for (LocalDate d : cols) {
                String dd = String.format("%02d/%02d", d.getDayOfMonth(), d.getMonthValue());
                String dow = d.getDayOfWeek().toString().substring(0,3); %>
                <th>
                  <div class="date"><%= dd %></div>
                  <div class="dow"><%= dow %></div>
                </th>
        <%   }
           } %>
      </tr>
      </thead>
      <tbody>
      <% if (rows != null) {
           for (Map<String,Object> row : rows) {
              String name = String.valueOf(row.get("name"));
              @SuppressWarnings("unchecked")
              List<Integer> cells = (List<Integer>) row.get("cells");
      %>
        <tr>
          <td class="name-cell"><%= name %></td>
          <% for (int i = 0; i < cols.size(); i++) {
               int v = (cells != null && i < cells.size()) ? cells.get(i) : 0;
               String cls = (v == 1) ? "leave" : "work";
          %>
            <td class="day-cell <%= cls %>"></td>
          <% } %>
        </tr>
      <% } } %>
      </tbody>
    </table>
  </div>
</div>

</body>
</html>
