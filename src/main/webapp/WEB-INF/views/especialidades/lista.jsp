<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titulo" value="Especialidades"/>
  <jsp:param name="menu" value="especialidades"/>
</jsp:include>

<div class="barra-titulo">
  <h1>Especialidades</h1>
  <a class="btn btn-primario" href="${ctx}/especialidades?accion=nuevo">+ Nueva</a>
</div>

<c:choose>
  <c:when test="${empty lista}">
    <p class="vacio">Todavía no hay especialidades registradas.</p>
  </c:when>
  <c:otherwise>
    <div class="tabla-scroll">
      <table class="tabla">
        <thead>
          <tr><th>#</th><th>Nombre</th><th>Descripción</th><th>Acciones</th></tr>
        </thead>
        <tbody>
          <c:forEach var="e" items="${lista}" varStatus="st">
            <tr>
              <td>${st.count}</td>
              <td><c:out value="${e.nombre}"/></td>
              <td><c:out value="${e.descripcion}"/></td>
              <td class="acciones">
                <a class="btn btn-chico" href="${ctx}/especialidades?accion=editar&id=${e.id}">Editar</a>
                <form action="${ctx}/especialidades" method="post" class="en-linea"
                      onsubmit="return confirm('¿Seguro que quieres eliminar esta especialidad?');">
                  <input type="hidden" name="accion" value="eliminar">
                  <input type="hidden" name="id" value="${e.id}">
                  <button type="submit" class="btn btn-chico btn-peligro">Eliminar</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
