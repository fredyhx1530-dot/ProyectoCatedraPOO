<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Error"/>
</jsp:include>

<h1>Algo salió mal</h1>
<p>Ocurrió un error o la página no existe. Revisa que la base de datos esté encendida.</p>
<a class="btn" href="${pageContext.request.contextPath}/inicio.jsp">Volver al inicio</a>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
