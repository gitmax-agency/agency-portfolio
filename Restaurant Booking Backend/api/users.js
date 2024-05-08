const express = require("../app");
const route = require("../routes/users");

express.use("/api/", route);

module.exports = express;

