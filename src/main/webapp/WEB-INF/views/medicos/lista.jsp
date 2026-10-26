<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Médicos"/>
  <jsp:param name="menu" value="medicos"/>
</jsp:include>

<div class="barra-titulo">
  <h1>Médicos</h1>
  <a class="btn btn-primario" href="${ctx}/medicos?accion=nuevo">+ Nuevo médico</a>
</div>

<c:if test="${not empty errorBD}">
  <div class="alerta alerta-error"><c:out value="${errorBD}"/></div>
</c:if>

<%-- Filtro por especialidad y/o texto (GET) --%>
<form method="get" action="${ctx}/medicos" class="filtros">
  <div>
    <label for="idEspecialidad">Especialidad</label>
    <select id="idEspecialidad" name="idEspecialidad">
      <option value="0">Todas las especialidades</option>
      <c:forEach var="esp" items="${especialidades}">
        <option value="${esp.id}" ${esp.id == filtroEspecialidad ? 'selected' : ''}><c:out value="${esp.nombre}"/></option>
      </c:forEach>
    </select>
  </div>
  <div>
    <label for="q">Nombre o JVPM</label>
    <input type="text" id="q" name="q" maxlength="100" value="<c:out value='${filtroTexto}'/>" placeholder="Ej. Patricia o JVPM-10587">
  </div>
  <div class="filtros-botones">
    <button type="submit" class="btn btn-primario">Filtrar</button>
    <a class="btn" href="${ctx}/medicos">Limpiar</a>
  </div>
</form>

<c:choose>
  <c:when test="${empty medicos}">
    <p class="vacio">No hay médicos que coincidan con la búsqueda.</p>
  </c:when>
  <c:otherwise>
    <div class="tabla-scroll">
      <table class="tabla">
        <thead>
          <tr><th>JVPM</th><th>Nombre</th><th>Contacto</th><th>Especialidades</th><th>Estado</th><th>Acciones</th></tr>
        </thead>
        <tbody>
          <c:forEach var="m" items="${medicos}">
            <tr>
              <td><c:out value="${m.jvpm}"/></td>
              <td><c:out value="${m.nombre}"/> <c:out value="${m.apellido}"/></td>
              <td>
                <c:out value="${m.telefono}"/><c:if test="${not empty m.telefono and not empty m.email}"><br></c:if>
                <c:out value="${m.email}"/>
              </td>
              <td>
                <c:forEach var="esp" items="${m.especialidades}">
                  <span class="etiqueta"><c:out value="${esp.nombre}"/></span>
                </c:forEach>
                <c:if test="${empty m.especialidades}">&mdash;</c:if>
              </td>
              <td>${m.activo ? 'Activo' : 'Inactivo'}</td>
              <td class="acciones">
                <a class="btn btn-chico" href="${ctx}/medicos?accion=editar&amp;id=${m.id}">Editar</a>
                <form action="${ctx}/medicos" method="post" class="en-linea"
                      onsubmit="return confirm('¿Seguro que deseas eliminar a este médico?');">
                  <input type="hidden" name="accion" value="eliminar">
                  <input type="hidden" name="id" value="${m.id}">
                  <button type="submit" class="btn btn-chico btn-peligro">Eliminar</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <p class="nota"><c:out value="${medicos.size()}"/> médico(s) mostrado(s)</p>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
