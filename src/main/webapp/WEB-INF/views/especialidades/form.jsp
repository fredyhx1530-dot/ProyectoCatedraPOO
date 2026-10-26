<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Especialidad"/>
  <jsp:param name="menu" value="especialidades"/>
</jsp:include>

<h1>${esp.id == 0 ? 'Nueva especialidad' : 'Editar especialidad'}</h1>

<c:if test="${not empty errores}">
  <div class="alerta alerta-error">
    <ul>
      <c:forEach var="er" items="${errores}"><li><c:out value="${er}"/></li></c:forEach>
    </ul>
  </div>
</c:if>

<form action="${ctx}/especialidades" method="post" class="formulario" id="formEspecialidad">
  <input type="hidden" name="accion" value="guardar">
  <input type="hidden" name="id" value="${esp.id}">

  <label for="nombre">Nombre *</label>
  <input type="text" id="nombre" name="nombre" maxlength="80" required
         value="<c:out value='${esp.nombre}'/>">

  <label for="descripcion">Descripción</label>
  <textarea id="descripcion" name="descripcion" rows="3" maxlength="255"><c:out value="${esp.descripcion}"/></textarea>

  <div class="botones">
    <button type="submit" class="btn btn-primario">Guardar</button>
    <a class="btn" href="${ctx}/especialidades">Cancelar</a>
  </div>
</form>

<script src="${ctx}/js/validacionesEspecialidad.js"></script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
