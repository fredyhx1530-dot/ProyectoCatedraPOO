<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Citas"/>
  <jsp:param name="menu" value="citas"/>
</jsp:include>

<div class="barra-titulo">
  <h1>Citas</h1>
  <a class="btn btn-primario" href="${ctx}/citas?accion=nuevo">+ Nueva cita</a>
</div>

<c:if test="${not empty errorFiltro}">
  <div class="alerta alerta-error"><c:out value="${errorFiltro}"/></div>
</c:if>

<%-- Filtro por fecha y/o estado (GET) --%>
<form method="get" action="${ctx}/citas" class="filtros">
  <div>
    <label for="fecha">Fecha</label>
    <input type="date" id="fecha" name="fecha" value="<c:out value='${filtroFecha}'/>">
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
    <button type="submit" class="btn btn-primario">Filtrar</button>
    <a class="btn" href="${ctx}/citas">Limpiar</a>
  </div>
</form>

<c:choose>
  <c:when test="${empty lista}">
    <p class="vacio">No hay citas que coincidan con el filtro.</p>
  </c:when>
  <c:otherwise>
    <div class="tabla-scroll">
      <table class="tabla">
        <thead>
          <tr><th>Fecha</th><th>Hora</th><th>Paciente</th><th>Médico</th><th>Estado</th><th>Motivo</th><th>Acciones</th></tr>
        </thead>
        <tbody>
          <c:forEach var="ci" items="${lista}">
            <tr>
              <td><c:out value="${ci.fecha}"/></td>
              <td><c:out value="${ci.hora}"/></td>
              <td><c:out value="${ci.pacienteNombre}"/></td>
              <td><c:out value="${ci.medicoNombre}"/></td>
              <td><span class="estado estado-${ci.estado}"><c:out value="${ci.estado}"/></span></td>
              <td><c:out value="${ci.motivo}"/></td>
              <td class="acciones">
                <a class="btn btn-chico" href="${ctx}/citas?accion=editar&amp;id=${ci.id}">Editar</a>
                <form action="${ctx}/citas" method="post" class="en-linea"
                      onsubmit="return confirm('¿Seguro que deseas eliminar esta cita?');">
                  <input type="hidden" name="accion" value="eliminar">
                  <input type="hidden" name="id" value="${ci.id}">
                  <button type="submit" class="btn btn-chico btn-peligro">Eliminar</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <p class="nota"><c:out value="${lista.size()}"/> cita(s) mostrada(s)</p>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
