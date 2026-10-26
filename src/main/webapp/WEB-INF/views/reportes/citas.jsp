<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Reporte de citas"/>
  <jsp:param name="menu" value="reportes"/>
</jsp:include>

<div class="barra-titulo">
  <h1>Reporte de citas</h1>
  <button type="button" class="btn sin-imprimir" onclick="window.print()">Imprimir</button>
</div>
<p class="nota">Cada fila combina la cita con su paciente, su médico y las especialidades del médico.</p>

<c:if test="${not empty errorFiltro}">
  <div class="alerta alerta-error"><c:out value="${errorFiltro}"/></div>
</c:if>

<form method="get" action="${ctx}/reportes" class="filtros sin-imprimir">
  <div>
    <label for="desde">Desde</label>
    <input type="date" id="desde" name="desde" value="<c:out value='${filtroDesde}'/>">
  </div>
  <div>
    <label for="hasta">Hasta</label>
    <input type="date" id="hasta" name="hasta" value="<c:out value='${filtroHasta}'/>">
  </div>
  <div>
    <label for="estado">Estado</label>
    <select id="estado" name="estado">
      <option value="">Todos los estados</option>
      <c:forEach var="e" items="${estados}">
        <option value="${e}" ${e == filtroEstado ? 'selected' : ''}><c:out value="${e}"/></option>
      </c:forEach>
    </select>
  </div>
  <div class="filtros-botones">
    <button type="submit" class="btn btn-primario">Generar</button>
    <a class="btn" href="${ctx}/reportes">Limpiar</a>
  </div>
</form>

<c:choose>
  <c:when test="${empty reporte}">
    <p class="vacio">No hay citas para los filtros elegidos.</p>
  </c:when>
  <c:otherwise>
    <div class="tabla-scroll">
      <table class="tabla">
        <thead>
          <tr><th>#</th><th>Fecha</th><th>Hora</th><th>Paciente</th><th>DUI</th><th>Médico</th><th>Especialidad(es)</th><th>Estado</th><th>Motivo</th></tr>
        </thead>
        <tbody>
          <c:forEach var="r" items="${reporte}" varStatus="st">
            <tr>
              <td>${st.count}</td>
              <td><c:out value="${r.fecha}"/></td>
              <td><c:out value="${r.hora}"/></td>
              <td><c:out value="${r.pacienteNombre}"/></td>
              <td><c:out value="${r.pacienteDui}"/></td>
              <td><c:out value="${r.medicoNombre}"/></td>
              <td><c:out value="${r.especialidades}"/></td>
              <td><span class="estado estado-${r.estado}"><c:out value="${r.estado}"/></span></td>
              <td><c:out value="${r.motivo}"/></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <p class="nota">Total: <c:out value="${reporte.size()}"/> cita(s)</p>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
