// =======================
// Get the packages we need ============
// =======================
var express     = require('express');
var app         = express();
// var bodyParser  = require('body-parser');
var path        = require('path');

app.use(express.static(path.join(__dirname, 'public')))

// CORS filter
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

// =======================
// start the server ======
// =======================
var port = 9090
app.listen(port);
console.log('Magic happens at http://localhost:' + port);
