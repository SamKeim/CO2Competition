<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
	integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh"
	crossorigin="anonymous">
<link rel="stylesheet" href="/style.css" />
<style>
.dropbtn {
	background-color: #4CAF50;
	color: white;
	padding: 16px;
	font-size: 16px;
	border: none;
}

input[class=name] {
	margin-left: 10px;
}

input[class=last] {
	margin-left: 12px;
}

input[class=user] {
	margin-left: 15px;
}

input[class=pass] {
	margin-left: 20px;
}

input[class=city] {
	margin-left: 59px;
	margin-top: 10px;
}

input[class=street] {
	margin-left: 45px;
}

input[class=zip] {
	margin-left: 25px;
}

.add-div {
	margin-top: 10px;
}

.sel-select {
	margin-top: 15px;
}
</style>
</head>
<body>
	<main class="container">
		<form method="post">
			<section class="jumbotron">

				<div>
					<label>First Name:<input class="name" type="text"
						name="name" required minlength="3"></label>
				</div>
				<div>
					<label>Last Name:<input class="last" type="text"
						name="lastName" required minlength="4"></label>
				</div>
				<div>
					<label>Username:<input class="user" type="text"
						name="username" required minlength="4"></label>
				</div>
				<div>
					<label>Password:<input class="pass" type="password"
						name="password" required minlength="4"></label>
				</div>
				<div class="add-div">Address:</div>
				<div>
					<label>City:<input class="city" type="text" name="city"
						required minlength="4"></label>
				</div>
				<div>
					<label>Street:<input class="street" type="text"
						name="streetAddress" required minlength="4"></label>
				</div>
				<div>
					<label>Zip code:<input class="zip" type="text" name="zipCode"
						required minlength="4"></label>
				</div>

				<select name="vehicleType" class="ve-select" required>
				<c:forEach var="v" items="${employee }">
				<option value="${v }">${v }</option>
				</c:forEach>
				
				
				</select>
				<select name="company" class="sel-select" required>

					<c:forEach var="co" items="${company }">
						<option value="${co.companyId }">${co.name }</option>
					</c:forEach>
				</select>
			</section>

			<button type="submit" class="btn btn-primary">Submit</button>
		</form>


	</main>
</body>
</html>