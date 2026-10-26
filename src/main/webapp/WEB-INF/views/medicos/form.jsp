<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Formulario de médico"/>
  <jsp:param name="menu" value="medicos"/>
</jsp:include>

<h1>${medico.id > 0 ? 'Editar médico' : 'Nuevo médico'}</h1>

<c:if test="${not empty errores}">
  <div class="alerta alerta-error">
    <ul>
      <c:forEach var="er" items="${errores}"><li><c:out value="${er.value}"/></li></c:forEach>
    </ul>
  </div>
</c:if>

<form class="formulario formulario-ancho" method="post" action="${ctx}/medicos" id="formMedico">
  <input type="hidden" name="accion" value="guardar">
  <input type="hidden" name="id" value="${medico.id}">

  <label for="jvpm">Número de JVPM *</label>
  <input type="text" id="jvpm" name="jvpm" required maxlength="20" placeholder="JVPM-12345"
         pattern="([Jj][Vv][Pp][Mm]-)?[0-9]{3,10}" value="<c:out value='${medico.jvpm}'/>">

  <label for="nombre">Nombre *</label>
  <input type="text" id="nombre" name="nombre" required minlength="2" maxlength="60" value="<c:out value='${medico.nombre}'/>">

  <label for="apellido">Apellido *</label>
  <input type="text" id="apellido" name="apellido" required minlength="2" maxlength="60" value="<c:out value='${medico.apellido}'/>">

  <label for="telefono">Teléfono</label>
  <input type="text" id="telefono" name="telefono" maxlength="9" placeholder="0000-0000"
         pattern="[0-9]{4}-[0-9]{4}" value="<c:out value='${medico.telefono}'/>">

  <label for="email">Correo electrónico</label>
  <input type="email" id="email" name="email" maxlength="100" value="<c:out value='${medico.email}'/>">

  <%-- Relacion N:M: varios checkboxes con el mismo name = varias especialidades --%>
  <label>Especialidades * <small>(marca una o más)</small></label>
  <div class="check-grupo" id="grupoEspecialidades">
    <c:forEach var="esp" items="${especialidades}">
      <label class="check-item" for="esp${esp.id}">
        <input type="checkbox" class="especialidad" name="idEspecialidad" id="esp${esp.id}" value="${esp.id}"
               ${idsSeleccionados.contains(esp.id) ? 'checked' : ''}>
        <c:out value="${esp.nombre}"/>
      </label>
    </c:forEach>
  </div>
  <p class="error-campo" id="errorEspecialidades" style="display:none">Selecciona al menos una especialidad.</p>

  <label class="check-item" for="activo">
    <input type="checkbox" id="activo" name="activo" value="1" ${medico.activo ? 'checked' : ''}>
    Médico activo
  </label>

  <div class="botones">
    <button type="submit" class="btn btn-primario">Guardar</button>
    <a href="${ctx}/medicos" class="btn">Cancelar</a>
  </div>
</form>

<script src="${ctx}/js/validacionesMedico.js"></script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
