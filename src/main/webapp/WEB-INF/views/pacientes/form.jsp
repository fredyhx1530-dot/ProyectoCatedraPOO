<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Formulario de paciente"/>
  <jsp:param name="menu" value="pacientes"/>
</jsp:include>
<h1>${paciente.id > 0 ? 'Editar paciente' : 'Nuevo paciente'}</h1>
<c:if test="${not empty errores}">
  <div class="alerta alerta-error"><ul><c:forEach var="error" items="${errores}"><li><c:out value="${error}"/></li></c:forEach></ul></div>
</c:if>
<form class="formulario" method="post" action="${ctx}/pacientes" id="formPaciente">
  <input type="hidden" name="accion" value="guardar">
  <input type="hidden" name="id" value="${paciente.id}">
  <label for="nombre">Nombre *</label>
  <input id="nombre" name="nombre" type="text" required minlength="2" maxlength="60" value="<c:out value='${paciente.nombre}'/>">
  <label for="apellido">Apellido *</label>
  <input id="apellido" name="apellido" type="text" required minlength="2" maxlength="60" value="<c:out value='${paciente.apellido}'/>">
  <label for="dui">DUI *</label>
  <input id="dui" name="dui" type="text" required maxlength="10" placeholder="00000000-0" pattern="[0-9]{8}-[0-9]" value="<c:out value='${paciente.dui}'/>">
  <label for="telefono">Teléfono</label>
  <input id="telefono" name="telefono" type="text" maxlength="9" placeholder="0000-0000" pattern="[0-9]{4}-[0-9]{4}" value="<c:out value='${paciente.telefono}'/>">
  <label for="email">Correo electrónico</label>
  <input id="email" name="email" type="email" maxlength="100" value="<c:out value='${paciente.email}'/>">
  <label for="fechaNacimiento">Fecha de nacimiento</label>
  <input id="fechaNacimiento" name="fechaNacimiento" type="date" value="${not empty fechaIngresada ? fechaIngresada : paciente.fechaNacimiento}">
  <label for="direccion">Dirección</label>
  <textarea id="direccion" name="direccion" rows="3" maxlength="200"><c:out value="${paciente.direccion}"/></textarea>
  <div class="botones">
    <button type="submit" class="btn btn-primario">Guardar</button>
    <a href="${ctx}/pacientes" class="btn">Cancelar</a>
  </div>
</form>
<script src="${ctx}/js/validacionesPaciente.js"></script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
