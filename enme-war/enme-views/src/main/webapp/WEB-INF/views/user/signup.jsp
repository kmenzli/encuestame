<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp"%>

  <c:if test="${detectedDevice}">
     <%@ include file="mobile/signup.jsp"%>
  </c:if>

  <c:if test="${!detectedDevice}">
     <%@ include file="web/signup.jsp"%>
  </c:if>