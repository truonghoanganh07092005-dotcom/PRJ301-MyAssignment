<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RequestForLeave, model.Employee, model.Department" %>
<%
  RequestForLeave r = (RequestForLeave) request.getAttribute("r");
  String ctx = request.getContextPath();
  if (r == null) { out.print("Không có dữ liệu"); return; }

  Employee e = r.getCreated_by();
  String empName = e!=null && e.getName()!=null ? e.getName() : "N/A";
  String deptName = (e!=null && e.getDept()!=null && e.getDept().getName()!=null) ? e.getDept().getName() : "—";
  String statusText = switch (r.getStatus()) {
    case 0 -> "In Progress";
    case 1 -> "Approved";
    case 2 -> "Rejected";
    default -> "Unknown";
  };
%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Chi tiết đơn nghỉ #<%= r.getRid() %></title>
<style>
  body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#f6f7fb;margin:0}
  .wrap{max-width:920px;margin:28px auto;padding:24px;background:#fff;border:1px solid #e6e9f0;border-radius:16px;box-shadow:0 8px 28px rgba(16,24,40,.06)}
  h1{margin:0 0 16px}
  .grid{display:grid;grid-template-columns:220px 1fr;gap:10px 18px}
  .label{color:#64748b}
  .val{font-weight:600}
  .chip{display:inline-block;padding:6px 12px;border-radius:999px;border:1px solid #fed7aa;background:#fff7ed;color:#9a3412}
  .chip.ok{border-color:#bbf7d0;background:#f0fdf4;color:#065f46}
  .chip.no{border-color:#fecaca;background:#fef2f2;color:#991b1b}
  .toolbar{margin-top:18px;display:flex;gap:10px}
  .btn{padding:10px 14px;border-radius:10px;border:1px solid #e6e9f0;background:#fff;text-decoration:none;color:#0f172a}
  .btn.primary{background:#2563eb;color:#fff;border-color:#2563eb}
</style>
</head>
<body>
<div class="wrap">
  <h1>Chi tiết đơn nghỉ #<%= r.getRid() %></h1>

  <div style="margin:10px 0 18px">
    <span class="chip <%= r.getStatus()==1?"ok":(r.getStatus()==2?"no":"") %>"><%= statusText %></span>
  </div>

  <div class="grid">
    <div class="label">Tiêu đề</div>
    <div class="val"><%= (r.getTitle()==null||r.getTitle().isBlank())?"(Không có)":r.getTitle() %></div>

    <div class="label">Người tạo</div>
    <div class="val"><%= empName %></div>

    <div class="label">Phòng ban</div>
    <div class="val"><%= deptName %></div>

    <div class="label">Từ ngày</div>
    <div class="val"><%= r.getFrom() %></div>

    <div class="label">Đến ngày</div>
    <div class="val"><%= r.getTo() %></div>

    <div class="label">Lý do</div>
    <div class="val"><%= r.getReason()==null?"":r.getReason() %></div>

    <div class="label">Tạo lúc</div>
    <div class="val"><%= r.getCreated_time() %></div>
  </div>

  <div class="toolbar">
    <a class="btn" href="<%= ctx %>/home">Quay lại</a>
    <a class="btn" href="<%= ctx %>/request/print?rid=<%= r.getRid() %>">In / xuất PDF</a>
    <%-- Bạn có thể bật 2 nút dưới khi đã có luồng duyệt --%>
    <%-- <a class="btn" href="<%= ctx %>/request/approve?rid=<%= r.getRid()%>">Duyệt</a> --%>
    <%-- <a class="btn" href="<%= ctx %>/request/reject?rid=<%= r.getRid()%>">Từ chối</a> --%>
    <a class="btn primary" href="<%= ctx %>/request/delete?rid=<%= r.getRid() %>">Xóa đơn</a>
  </div>
</div>
</body>
</html>
