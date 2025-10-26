<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx         = request.getContextPath();
  String displayName = (String) request.getAttribute("displayName");
  String username    = (String) request.getAttribute("username");
  String email       = (String) request.getAttribute("email");
  String phone       = (String) request.getAttribute("phone");
  String empCode     = (String) request.getAttribute("empCode");
  String department  = (String) request.getAttribute("department");
  String position    = (String) request.getAttribute("position");
  String hireDate    = (String) request.getAttribute("hireDate");  // <-- String
  String manager     = (String) request.getAttribute("manager");
  String roles       = (String) request.getAttribute("roles");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Thông tin cá nhân</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style>
    :root{
      --bg:#f5f7fb; --ink:#0f172a; --muted:#64748b; --card:#fff; --border:#e5e7eb; --accent:#2563eb;
    }
    *{box-sizing:border-box}
    body{margin:0;background:var(--bg);color:var(--ink);font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif}
    .page{max-width:1100px;margin:0 auto;padding:20px}
    h1{font-size:36px;margin:12px 0 18px}
    .card{background:var(--card);border:1px solid var(--border);border-radius:18px;padding:10px 0 6px;
          box-shadow:0 10px 30px rgba(0,0,0,.05)}
    .avatarBox{display:flex;align-items:center;gap:14px;padding:14px 18px}
    .avatar{width:56px;height:56px;border-radius:50%;display:grid;place-items:center;
            background:linear-gradient(135deg,#9b8cf2,#7c3aed);color:#fff;font-size:22px;font-weight:800}
    .name{font-size:20px;font-weight:800}
    .sub{color:var(--muted);font-size:13px}
    .row{display:grid;grid-template-columns:220px 1fr;gap:8px;align-items:center;padding:14px 18px;border-top:1px dashed var(--border)}
    .row:first-of-type{border-top:none}
    .label{color:var(--muted)}
    .value{font-weight:700}
    .stack{display:flex;flex-wrap:wrap;gap:10px}
    .pill{display:inline-block;background:#eef2ff;color:#1e3a8a;border:1px solid #c7d2fe;padding:6px 10px;border-radius:999px}
    @media(max-width:680px){ .row{grid-template-columns:1fr} .row .label{margin-bottom:4px} }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/partials/navbar.jsp"/>

<div class="page">
  <h1>Thông tin cá nhân</h1>

  <div class="card">
    <div class="avatarBox">
      <div class="avatar"><%= (displayName==null||displayName.isBlank()) ? "U" : displayName.substring(0,1).toUpperCase() %></div>
      <div>
        <div class="name"><%= (displayName==null||displayName.isBlank()) ? "User" : displayName %></div>
        <div class="sub">
          <%= (position==null||position.isBlank()) ? "—" : position %>
          <% if (department!=null && !department.isBlank()) { %> • <%= department %><% } %>
        </div>
      </div>
    </div>

    <div class="row"><div class="label">Username</div>        <div class="value"><%= empty(username)   %></div></div>
    <div class="row"><div class="label">Email</div>           <div class="value"><%= empty(email)      %></div></div>
    <div class="row"><div class="label">Số điện thoại</div>   <div class="value"><%= empty(phone)      %></div></div>
    <div class="row"><div class="label">Mã nhân viên</div>    <div class="value"><%= empty(empCode)    %></div></div>
    <div class="row"><div class="label">Phòng ban</div>       <div class="value"><%= empty(department) %></div></div>
    <div class="row"><div class="label">Chức vụ/Vị trí</div>  <div class="value"><%= empty(position)   %></div></div>

    <div class="row">
      <div class="label">Ngày vào làm</div>
      <div class="value"><%= empty(hireDate) %></div>
    </div>

    <div class="row"><div class="label">Quản lý trực tiếp</div> <div class="value"><%= empty(manager) %></div></div>

    <div class="row">
      <div class="label">Quyền</div>
      <div class="value">
        <% if (roles==null || roles.isBlank()) { %>—<% } else { %>
          <div class="stack">
            <% for (String r : roles.split("\\s*,\\s*")) { %>
              <span class="pill"><%= r %></span>
            <% } %>
          </div>
        <% } %>
      </div>
    </div>

  </div>
</div>

<%!  // small helper cho JSP
    private String empty(String s){ return (s==null||s.isBlank()) ? "—" : s; }
%>

</body>
</html>
