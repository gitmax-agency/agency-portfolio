require('dotenv').config({ path: `${process.cwd()}/.env`})

module.exports = {
  "development": {
    "username": process.env.MYSQL_DB_USER,
    "password": process.env.MYSQL_DB_PASSWORD,
    "database": process.env.MYSQL_DB,
    "host": "localhost",
    "port": 3306,
    "dialect": "mysql"
  },
  "test": {
    "username": process.env.MYSQL_DB_USER,
    "password": process.env.MYSQL_DB_PASSWORD,
    "database": process.env.MYSQL_DB,
    "host": "bbo-db.cugn8zapjwmd.eu-west-1.rds.amazonaws.com",
    "port": 5000,
    "dialect": "mysql"
  }
  // "development": {
  //   "use_env_variable": 'DATABASE_URL'
  // },
  // "test": {
  //   "username": process.env.MYSQL_DB_USER,
  //   "password": process.env.MYSQL_DB_PASSWORD,
  //   "database": process.env.MYSQL_DB,
  //   "host": "127.0.0.1",
  //   "port": 5000,
  //   "dialect": "mysql"
  // }
}

