<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Header comun. Se usa asi en cada pagina:
  <jsp:include page="/WEB-INF/views/layout/header.jsp">
    <jsp:param name="titulo" value="Pacientes"/>
    <jsp:param name="menu" value="pacientes"/>   (inicio, pacientes, medicos, especialidades, citas, reportes)
  </jsp:include>
  y al final el footer.jsp
  Para mostrar un mensaje despues de un redirect: session flashMsg + flashTipo ("ok" o "error")
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${param.titulo}"/> | Citas Médicas</title>
  <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<header class="topbar">
  <a class="marca" href="${ctx}/inicio.jsp">+ Citas Médicas</a>
  <input type="checkbox" id="chk-menu" class="chk-menu">
  <label for="chk-menu" class="burger">&#9776;</label>
  <nav class="menu">
    <a href="${ctx}/inicio.jsp" class="${param.menu == 'inicio' ? 'activo' : ''}">Inicio</a>
    <a href="${ctx}/pacientes" class="${param.menu == 'pacientes' ? 'activo' : ''}">Pacientes</a>
    <a href="${ctx}/medicos" class="${param.menu == 'medicos' ? 'activo' : ''}">Médicos</a>
    <a href="${ctx}/especialidades" class="${param.menu == 'especialidades' ? 'activo' : ''}">Especialidades</a>
    <a href="${ctx}/citas" class="${param.menu == 'citas' ? 'activo' : ''}">Citas</a>
    <a href="${ctx}/reportes" class="${param.menu == 'reportes' ? 'activo' : ''}">Reporte</a>
    <a href="${ctx}/logout" class="salir">Salir (<c:out value="${sessionScope.usuario.username}"/>)</a>
  </nav>
</header>

<main class="contenedor">
<c:if test="${not empty sessionScope.flashMsg}">
  <div class="alerta ${sessionScope.flashTipo == 'error' ? 'alerta-error' : 'alerta-ok'}">
    <c:out value="${sessionScope.flashMsg}"/>
  </div>
  <c:remove var="flashMsg" scope="session"/>
  <c:remove var="flashTipo" scope="session"/>
</c:if>
