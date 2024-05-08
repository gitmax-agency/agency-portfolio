const restaurantController = require('../controllers').restaurant;
const userController = require('../controllers').user;
const reservationController = require('../controllers').reservation;
const cuisineController = require('../controllers').cuisine;
const authController = require("../controllers").auth;
const tableController = require('../controllers').table;
const auth = require('../middleware/auth');

module.exports = (app) => {
  app.get('/api', auth, (req, res) => res.status(200).send({
    message: 'Welcome to the API!',
  }));

  app.post('/api/user/add', auth, userController.add);
  app.post('/api/user/delete', auth, userController.delete);
  app.post('/api/user/update', auth, userController.update);
  app.post('/api/user/find', auth, userController.find);
  app.get('/api/user/list', auth, userController.list);


  app.post('/api/restaurant/add', auth, restaurantController.add);
  app.post('/api/restaurant/delete', auth, restaurantController.delete);
  app.post('/api/restaurant/update', auth, restaurantController.update);
  app.post('/api/restaurant/find', auth, restaurantController.find);
  app.post('/api/restaurant/findExtended', auth, restaurantController.findExtended);
  app.get('/api/restaurant/list', auth, restaurantController.list);
  app.get('/api/restaurant/listExtended', auth, restaurantController.listExtended);


  app.post('/api/reservation/add', auth, reservationController.add);
  app.post('/api/reservation/delete', auth, reservationController.delete);
  app.post('/api/reservation/update', auth, reservationController.update);
  app.post('/api/reservation/find', auth, reservationController.find);
  app.get('/api/reservation/list', auth, reservationController.list);

  app.post('/api/cuisine/add', auth, cuisineController.add);
  app.post('/api/cuisine/delete', auth, cuisineController.delete);
  app.post('/api/cuisine/update', auth, cuisineController.update);
  app.post('/api/cuisine/find', auth, cuisineController.find);
  app.get('/api/cuisine/list', auth, cuisineController.list);

  app.post('/api/table/add', auth, tableController.add);
  app.post('/api/table/delete', auth, tableController.delete);
  app.post('/api/table/update', auth, tableController.update);
  app.post('/api/table/find', auth, tableController.find);
  app.get('/api/table/list', auth, tableController.list);

  app.post("/register", authController.register);
  app.post("/login", authController.login);
  app.put("/logout", authController.logout);

};
