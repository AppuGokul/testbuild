<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>QR Code Generator</title>
</head>
<body>
<h1>QR Code Generator</h1>
<form action="generate" method="post">
    <label for="url">Enter URL:</label>
    <input type="text" id="url" name="url" required>
    <button type="submit">Generate QR Code</button>
</form>
<c:if test="${not empty param.error}">
    <p style="color:red;">${param.error}</p>
</c:if>
</body>
</html>