const fs = require('fs');
const path = require('path');
const Sequelize = require('sequelize');
const basename = path.basename(module.filename);
const env = process.env.NODE_ENV || 'development';
const config = require(`${__dirname}/../config/config.js`)[env];  
// const config = require(__dirname + '../config/config.js')[env];
const db = {};

let sequelize;
if (config.use_env_variable) {
  sequelize = new Sequelize(process.env[config.use_env_variable]);
} else {
  sequelize = new Sequelize(
    config.database, config.username, config.password, config
  );
  //  sequelize = new Sequelize(process.env.MYSQL_DB, process.env.MYSQL_USER, process.env.MYSQL_PASSWORD, {
  //   host: process.env.MYSQL_DB_HOST,
  //   port: 5000,
  //   logging: console.log,
  //   maxConcurrentQueries: 100,
  //   dialect: 'mysql',
  //   dialectOptions: {
  //       ssl:'Amazon RDS'
  //   },
  //   pool: { maxConnections: 5, maxIdleTime: 30},
  //   language: 'en'
  // });
}

fs
  .readdirSync(__dirname + '/db')
  .filter(file =>
    (file.indexOf('.') !== 0) &&
    (file !== basename) &&
    (file.slice(-3) === '.js'))
  .forEach(file => {
    const model = sequelize.define(path.join(__dirname + '/db', file));
    db[model.name] = model;
  });



Object.keys(db).forEach(modelName => {
  if (db[modelName].associate) {
    db[modelName].associate(db);
  }
});


db.sequelize = sequelize;
db.Sequelize = Sequelize;

module.exports = db;
