<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Iniciar sesión | Citas Médicas</title>
  <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body class="fondo-login">
  <div class="login-caja">
    <h1>+ Citas Médicas</h1>
    <p class="sub">Inicia sesión para continuar</p>

    <c:if test="${not empty error}">
      <div class="alerta alerta-error"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${not empty info}">
      <div class="alerta alerta-ok"><c:out value="${info}"/></div>
    </c:if>

    <form action="${ctx}/login" method="post">
      <label for="username">Usuario</label>
      <input type="text" id="username" name="username" value="<c:out value='${username}'/>" required autofocus>

      <label for="password">Contraseña</label>
      <input type="password" id="password" name="password" required>

      <button type="submit" class="btn btn-primario ancho">Entrar</button>
    </form>
  </div>
</body>
</html>
