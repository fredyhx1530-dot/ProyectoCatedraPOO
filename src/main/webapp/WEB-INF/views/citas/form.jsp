<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Formulario de cita"/>
  <jsp:param name="menu" value="citas"/>
</jsp:include>

<h1>${cita.id > 0 ? 'Editar cita' : 'Nueva cita'}</h1>

<c:if test="${not empty errores}">
  <div class="alerta alerta-error">
    <ul>
      <c:forEach var="er" items="${errores}"><li><c:out value="${er.value}"/></li></c:forEach>
    </ul>
  </div>
</c:if>

<form class="formulario formulario-ancho" method="post" action="${ctx}/citas" id="formCita">
  <input type="hidden" name="accion" value="guardar">
  <input type="hidden" name="id" value="${cita.id}">

  <label for="idPaciente">Paciente *</label>
  <select id="idPaciente" name="idPaciente" required>
    <option value="">-- Selecciona un paciente --</option>
    <c:forEach var="p" items="${pacientes}">
      <option value="${p.id}" ${p.id == cita.idPaciente ? 'selected' : ''}>
        <c:out value="${p.nombre}"/> <c:out value="${p.apellido}"/> (<c:out value="${p.dui}"/>)
      </option>
    </c:forEach>
  </select>

  <label for="idMedico">Médico *</label>
  <select id="idMedico" name="idMedico" required>
    <option value="">-- Selecciona un médico --</option>
    <c:forEach var="m" items="${medicos}">
      <option value="${m.id}" ${m.id == cita.idMedico ? 'selected' : ''}>
        <c:out value="${m.nombre}"/> <c:out value="${m.apellido}"/> &mdash;
        <c:forEach var="esp" items="${m.especialidades}" varStatus="st"><c:out value="${esp.nombre}"/><c:if test="${not st.last}">, </c:if></c:forEach>
        <c:if test="${not m.activo}">(inactivo)</c:if>
      </option>
    </c:forEach>
  </select>

  <label for="fecha">Fecha *</label>
  <input type="date" id="fecha" name="fecha" required
         value="<c:out value='${not empty fechaIngresada ? fechaIngresada : cita.fecha}'/>">

  <label for="hora">Hora * <small>(07:00 a 18:00, cada 30 minutos)</small></label>
  <input type="time" id="hora" name="hora" required min="07:00" max="18:00" step="1800"
         value="<c:out value='${not empty horaIngresada ? horaIngresada : cita.hora}'/>">

  <label for="estado">Estado *</label>
  <select id="estado" name="estado" required>
    <c:forEach var="e" items="${estados}">
      <option value="${e}" ${e == cita.estado ? 'selected' : ''}><c:out value="${e}"/></option>
    </c:forEach>
  </select>

  <label for="motivo">Motivo</label>
  <textarea id="motivo" name="motivo" rows="3" maxlength="255"><c:out value="${cita.motivo}"/></textarea>

  <div class="botones">
    <button type="submit" class="btn btn-primario">Guardar</button>
    <a href="${ctx}/citas" class="btn">Cancelar</a>
  </div>
</form>

<script src="${ctx}/js/validacionesCita.js"></script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
