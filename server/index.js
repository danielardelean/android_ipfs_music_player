var express = require("express");
var app = express();
var request = require('request');

app.get("/", (req, res, next) => {
    res.send("<h1>Music server API</h1><p>- getcontent(param hash)</p>");
});

app.get('/getcontent', (req, res) => {
    request('http://localhost:40600/api/v0/file/' + req.query.hash + '/content')
        .pipe(res);
});

app.listen(3000, () => {
    console.log("Server running on port 3000");
});