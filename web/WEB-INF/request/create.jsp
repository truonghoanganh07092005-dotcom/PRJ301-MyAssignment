<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  java.util.List<String> errors = (java.util.List<String>) request.getAttribute("errors");

  Boolean edit = (Boolean) request.getAttribute("edit");
  if (edit == null) edit = false;

  String rid    = (String) request.getAttribute("rid");
  String title  = (String) request.getAttribute("form_title");
  String fromS  = (String) request.getAttribute("form_from");
  String toS    = (String) request.getAttribute("form_to");
  String reason = (String) request.getAttribute("form_reason");

  String formAction = edit ? (ctx + "/request/edit") : (ctx + "/request/create");
  String pageTitle  = edit ? "✏️ Sửa đơn nghỉ" : "📝 Tạo đơn nghỉ";
  String submitText = edit ? "Lưu thay đổi" : "Tạo đơn";
%>
<!DOCTYPE html>
<html lang="vi">
<head>…(giữ nguyên CSS cũ)…</head>
<body>
  <div class="page">
    <h1><%= pageTitle %></h1>

    <% if (errors != null && !errors.isEmpty()) { %>
      <div class="err"><ul style="margin:0 0 0 18px"><% for (String e : errors) { %><li><%= e %></li><% } %></ul></div>
    <% } %>

    <form id="frmCreate" method="post" action="<%=formAction%>" accept-charset="UTF-8" novalidate>
      <% if (edit && rid != null) { %>
        <input type="hidden" name="rid" value="<%= rid %>">
      <% } %>

      <label for="title">Tiêu đề</label>
      <input type="text" id="title" name="title" maxlength="150"
             placeholder="<%= edit ? "Tiêu đề đơn" : "Ví dụ: Đơn xin nghỉ phép" %>"
             value="<%= title==null? "" : title %>">

      <div class="row">
        <div>
          <label for="from">Từ ngày</label>
          <input type="date" id="from" name="from" value="<%= fromS==null? "" : fromS %>" required>
        </div>
        <div>
          <label for="to">Đến ngày</label>
          <input type="date" id="to" name="to" value="<%= toS==null? "" : toS %>" required>
        </div>
      </div>

      <label for="reason">Lý do</label>
      <textarea id="reason" name="reason" required><%= reason==null? "" : reason %></textarea>

      <button class="btn" type="submit"><%= submitText %></button>
      <a class="btn muted" href="<%=ctx%>/home">Huỷ</a>
    </form>
  </div>

<script>
  (function(){
    const today = new Date(), yyyy=today.getFullYear(), mm=String(today.getMonth()+1).padStart(2,'0'), dd=String(today.getDate()).padStart(2,'0');
    const iso = `${yyyy}-${mm}-${dd}`;
    const from = document.getElementById('from');
    const to   = document.getElementById('to');
    if (!from.value) from.value = iso;
    from.min = iso;
    to.min   = from.value || iso;
    from.addEventListener('change', () => { if (!from.value) return; to.min = from.value; if (to.value && to.value < from.value) to.value = from.value; });
  })();
</script>
</body>
</html>
