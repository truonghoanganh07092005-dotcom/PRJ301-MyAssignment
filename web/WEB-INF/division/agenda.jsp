<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.time.*" %>
<%
    String fromIso = (String) request.getAttribute("fromIso");
    String toIso   = (String) request.getAttribute("toIso");

    // cols là List<LocalDate> (đúng kiểu controller gửi)
    List<LocalDate> cols = (List<LocalDate>) request.getAttribute("cols");

    // rows: List<Map<String,Object>> với "name" (String) và "cells" (List<Integer>)
    List<Map<String,Object>> rows = (List<Map<String,Object>>) request.getAttribute("rows");

    boolean hasData = cols != null && !cols.isEmpty() && rows != null && !rows.isEmpty();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Agenda (duyệt đơn nghỉ)</title>
    <style>
        :root {
            --border: #e5e7eb;
            --bg: #f8fafc;
            --text: #111827;
            --muted: #6b7280;
            --work: #e8f5e9;   /* xanh: đi làm */
            --leave: #ffebee;  /* đỏ nhạt: nghỉ */
            --sticky: #fafafa;
        }
        * { box-sizing: border-box; }
        body { font-family: system-ui, Arial, sans-serif; margin: 0; color: var(--text); background: #fff; }
        .container { padding: 16px 20px; }
        h1 { font-size: 24px; margin: 0 0 12px 0; }
        .toolbar { margin: 12px 0 16px; display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
        .toolbar label { font-size: 14px; display: inline-flex; gap: 6px; align-items: center; }
        input[type="date"]{ padding: 6px 8px; border: 1px solid #d1d5db; border-radius: 8px; }
        .btn { padding: 8px 14px; border-radius: 8px; border: 1px solid #111827; background: #111827; color: #fff; cursor: pointer; }

        .legend { display:flex; gap:18px; align-items:center; margin: 6px 0 12px;}
        .dot { width:14px; height:14px; border-radius:3px; display:inline-block; border:1px solid var(--border); }
        .dot.work { background: var(--work); }
        .dot.leave{ background: var(--leave); }

        .table-wrap { border: 1px solid var(--border); border-radius: 10px; overflow: auto; }
        table { border-collapse: collapse; width: 100%; min-width: 780px; }
        thead th { position: sticky; top: 0; background: #fff; z-index: 2; }
        th, td { border: 1px solid var(--border); padding: 8px 10px; text-align: center; white-space: nowrap; }
        th:first-child, td:first-child {
            position: sticky; left: 0; z-index: 3;
            background: var(--sticky); text-align: left; min-width: 180px; font-weight: 600;
        }
        td.work { background: var(--work); }
        td.leave{ background: var(--leave); }
        .empty { color: var(--muted); padding: 16px 0; }
        .sub { font-size: 12px; color: var(--muted); }
    </style>
</head>
<body>
<div class="container">
    <h1>Agenda (duyệt đơn nghỉ)</h1>

    <form class="toolbar" method="get" action="<%=request.getContextPath()%>/agenda">
        <label>Từ ngày:
            <input type="date" name="from" value="<%= fromIso %>">
        </label>
        <label>Đến ngày:
            <input type="date" name="to" value="<%= toIso %>">
        </label>
        <button class="btn" type="submit">Xem</button>
        <span class="sub">Hiển thị danh sách cấp dưới; ô <b class="dot leave"></b> là ngày nghỉ đã duyệt, <b class="dot work"></b> là đi làm.</span>
    </form>

    <div class="legend">
        <span><span class="dot work"></span> Đi làm</span>
        <span><span class="dot leave"></span> Nghỉ (đã duyệt)</span>
    </div>

    <% if (!hasData) { %>
        <div class="empty">Không có dữ liệu để hiển thị.</div>
    <% } else { %>
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Nhân sự</th>
                <% for (LocalDate d : cols) { %>
                    <th>
                        <%= String.format("%02d/%02d", d.getDayOfMonth(), d.getMonthValue()) %>
                    </th>
                <% } %>
            </tr>
            </thead>
            <tbody>
            <%
                for (Map<String,Object> r : rows) {
                    String name = String.valueOf(r.get("name"));
                    List<Integer> cells = (List<Integer>) r.get("cells"); // 1 = nghỉ, 0 = đi làm
            %>
                <tr>
                    <td><%= name %></td>
                    <%  for (int i = 0; i < cols.size(); i++) {
                            Integer v = (cells != null && i < cells.size()) ? cells.get(i) : 0; // thiếu dữ liệu -> coi là 0
                    %>
                        <td class="<%= (v != null && v == 1) ? "leave" : "work" %>"></td>
                    <% } %>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <% } %>
</div>
</body>
</html>
