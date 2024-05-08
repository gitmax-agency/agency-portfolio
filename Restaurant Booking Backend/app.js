const cookieParser = require("cookie-parser");
 const logger = require("morgan");
 const express = require('express')
 const body_parser = require('body-parser')
 const app = express();
 var cors = require('cors')
 app.use(logger("dev"));
 app.use(cors());
 app.use(body_parser.json())
 
 module.exports = app;
