<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RequestForLeave" %>
<%
  RequestForLeave r = (RequestForLeave) request.getAttribute("requestObj");
  if (r == null) { out.print("No data"); return; }
  String createdBy = r.getCreated_by()!=null ? r.getCreated_by().getName() : "—";
  String title = (r.getTitle()==null || r.getTitle().isBlank())
                 ? ("Nghỉ " + r.getFrom() + " – " + r.getTo())
                 : r.getTitle();

  String status;
  switch (r.getStatus()) {
    case 1: status = "Approved"; break;
    case 2: status = "Rejected"; break;
    case 3: status = "Cancelled"; break;   // << thêm
    default: status = "In Progress";
  }
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Chi tiết đơn #<%=r.getRid()%></title>
  <style>
    body{font-family:system-ui,Segoe UI,Roboto,Arial,sans-serif;background:#f6f7fb;margin:0}
    .page{max-width:900px;margin:24px auto;background:#fff;border:1px solid #e6e9f0;border-radius:14px;padding:16px 20px}
    h1{margin:6px 0 16px}
    .row{display:flex;gap:12px;margin:8px 0}
    .label{width:160px;color:#6b7280}
    .val{flex:1}
    a.btn{display:inline-block;padding:8px 14px;border:1px solid #d1d5db;border-radius:10px;text-decoration:none;color:#111}
  </style>
</head>
<body>
  <div class="page">
    <a class="btn" href="<%=ctx%>/home">← Về Home</a>
    <h1>Đơn nghỉ #<%=r.getRid()%></h1>

    <div class="row"><div class="label">Tiêu đề</div><div class="val"><%=title%></div></div>
    <div class="row"><div class="label">Người tạo</div><div class="val"><%=createdBy%></div></div>
    <div class="row"><div class="label">Từ ngày</div><div class="val"><%=r.getFrom()%></div></div>
    <div class="row"><div class="label">Đến ngày</div><div class="val"><%=r.getTo()%></div></div>
    <div class="row"><div class="label">Lý do</div><div class="val"><pre style="margin:0"><%=r.getReason()%></pre></div></div>
    <div class="row"><div class="label">Trạng thái</div><div class="val"><%=status%></div></div>
    <div class="row"><div class="label">Tạo lúc</div><div class="val"><%=r.getCreated_time()%></div></div>
  </div>
</body>
</html>
