<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	<link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">
</head>
	<body>
	
	<div style="margin-left:25%">	
	<img src="/resources/images/access_denied.png" style="height:70%"/>
	
		<c:if test="${not empty error}">
			<div style="color:red"> 
				Caused : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
			</div>
		</c:if>
	
		
		<a href="/login">Go back to login page</a> 
		<h6><a href="javascript:window.history.back()">Back to last page</a></h6>
		</div>
	</body>
</html>