<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Pacientes"/>
  <jsp:param name="menu" value="pacientes"/>
</jsp:include>

<div class="barra-titulo">
  <h1>Pacientes</h1>
  <a class="btn btn-primario" href="${ctx}/pacientes?accion=nuevo">+ Nuevo paciente</a>
</div>
<form method="get" action="${ctx}/pacientes" class="buscador-pacientes">
  <input type="text" name="busqueda" maxlength="100" value="<c:out value='${busqueda}'/>" placeholder="Buscar por nombre o DUI">
  <button class="btn btn-primario" type="submit">Buscar</button>
  <a class="btn" href="${ctx}/pacientes">Limpiar</a>
</form>
<c:choose>
  <c:when test="${empty lista}"><p class="vacio">No se encontraron pacientes.</p></c:when>
  <c:otherwise>
    <div class="tabla-scroll"><table class="tabla">
      <thead><tr><th>Nombre</th><th>DUI</th><th>Teléfono</th><th>Correo</th><th>Acciones</th></tr></thead>
      <tbody>
        <c:forEach var="p" items="${lista}">
          <tr>
            <td><c:out value="${p.nombre}"/> <c:out value="${p.apellido}"/></td>
            <td><c:out value="${p.dui}"/></td>
            <td><c:out value="${p.telefono}"/></td>
            <td><c:out value="${p.email}"/></td>
            <td class="acciones">
              <a class="btn btn-chico" href="${ctx}/pacientes?accion=editar&amp;id=${p.id}">Editar</a>
              <form action="${ctx}/pacientes" method="post" class="en-linea" onsubmit="return confirm('¿Seguro que deseas eliminar este paciente?');">
                <input type="hidden" name="accion" value="eliminar">
                <input type="hidden" name="id" value="${p.id}">
                <button class="btn btn-chico btn-peligro" type="submit">Eliminar</button>
              </form>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table></div>
  </c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
