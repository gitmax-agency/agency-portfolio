var mysql = require('mysql');
require('dotenv').config({ path: `${process.cwd()}/.env`})

console.log('Get connection ...');

var conn = mysql.createConnection({
   host: process.env.MYSQL_DB_HOST,
   user: process.env.MYSQL_DB_USER,
   database: process.env.MYSQL_DB,
   password: process.env.MYSQL_DB_PASSWORD,
   port: 3306
});

conn.connect(function(err) {
   if (err) throw err;
   console.log("Connected!");
});

module.exports = conn;