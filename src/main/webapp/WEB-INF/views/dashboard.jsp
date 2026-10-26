<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Inicio"/>
  <jsp:param name="menu" value="inicio"/>
</jsp:include>
<h1>Bienvenido, <c:out value="${sessionScope.usuario.nombreCompleto}"/></h1>
<p>Resumen de los registros del sistema.</p>
<div class="tarjetas-resumen">
  <div class="tarjeta-resumen"><h2>Pacientes</h2><strong><c:out value="${totalPacientes}"/></strong><a href="${ctx}/pacientes">Ver pacientes</a></div>
  <div class="tarjeta-resumen"><h2>Médicos</h2><strong><c:out value="${totalMedicos}"/></strong><a href="${ctx}/medicos">Ver médicos</a></div>
  <div class="tarjeta-resumen"><h2>Citas</h2><strong><c:out value="${totalCitas}"/></strong><a href="${ctx}/citas">Ver citas</a></div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
