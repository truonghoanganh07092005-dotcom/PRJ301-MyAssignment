<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<jsp:include page="/WEB-INF/partials/navbar.jsp"/>
<title>Duyệt đơn cấp dưới</title>
<style>
body{font-family:system-ui;background:#f6f7fb;margin:0;padding:20px}
h2{margin-top:0}
table{width:100%;border-collapse:collapse;background:#fff;border-radius:10px;overflow:hidden}
th,td{padding:10px 12px;border-bottom:1px solid #eee;text-align:left}
th{background:#111827;color:#fff}
a.btn{padding:4px 8px;border-radius:6px;text-decoration:none}
a.approve{background:#16a34a;color:#fff}
a.reject{background:#dc2626;color:#fff}
a.unapprove{background:#f59e0b;color:#fff}
</style>
</head>
<body>
<h2>Danh sách đơn cấp dưới đang chờ duyệt</h2>

<c:if test="${empty waiting}">
  <p>Không có đơn nào cần duyệt.</p>
</c:if>

<c:if test="${not empty waiting}">
  <table>
    <tr><th>ID</th><th>Tiêu đề</th><th>Người tạo</th><th>Từ</th><th>Đến</th><th>Trạng thái</th><th>Hành động</th></tr>
    <c:forEach items="${waiting}" var="r">
      <tr>
        <td>${r.rid}</td>
        <td>${r.title}</td>
        <td>${r.created_by.name}</td>
        <td>${r.from}</td>
        <td>${r.to}</td>
        <td>
          <c:choose>
            <c:when test="${r.status==0}">In Progress</c:when>
            <c:when test="${r.status==1}">Approved</c:when>
            <c:when test="${r.status==2}">Rejected</c:when>
            <c:otherwise>Cancelled</c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${r.status==0}">
              <a href="${ctx}/request/approve?rid=${r.rid}" class="btn approve">Duyệt</a>
              <a href="${ctx}/request/reject?rid=${r.rid}"  class="btn reject">Từ chối</a>
            </c:when>
            <c:when test="${r.status==1 || r.status==2}">
              <a href="${ctx}/request/unapprove?rid=${r.rid}" class="btn unapprove">Hủy duyệt / Hủy từ chối</a>
            </c:when>
          </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:if>
</body>
</html>
