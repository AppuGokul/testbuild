<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>QR Code Result</title>
</head>
<body>
<h1>Your QR Code</h1>
<form action="generate" method="post">
    <label for="url">Enter URL:</label>
    <input type="text" id="url" name="url" required>
    <button type="submit">Generate QR Code</button>
</form>
<img src="data:image/png;base64,${qrCodeImage}" alt="QR Code">
<p>Scan the QR code or click <a href="http://yourdomain.com/scan?id=${qrCodeId}">here</a> to track.</p>
</body>
</html>