
const models = require('../models/index.js');
const {Sequelize, DataTypes} = require("sequelize");
const jwt = require("jsonwebtoken");
const Restaurant = require('../models/db/restaurant.js')(models.sequelize, DataTypes);
const Table = require('../models/db/table.js')(models.sequelize, DataTypes);
const Reservation = require('../models/db/reservation.js')(models.sequelize, DataTypes);
const User = require('../models/db/user.js')(models.sequelize, DataTypes);
const RestarauntCuisines = require('../models/db/restarauntcuisine')(models.sequelize, DataTypes);
const Cuisine = require('../models/db/cuisine')(models.sequelize, DataTypes);
const Op = models.Sequelize.Op;

const fs = require('fs');
const path = require('path');

async function getImages(imageDir) {
    var fileType = '.jpg',
        files = [], i;
    // console.log(imageDir);
    if (!fs.existsSync(imageDir))
        return [];
    await fs.promises.readdir(imageDir).then((list) => files = list);
    // console.log(files);
    return files;
}

async function getRestaurantPictures(id) {
    let pictures;
    pictures = await getImages(path.join(__dirname, "..", "images/restaurants/" + id));
    pictures = pictures.map((val) => path.join(__dirname, "..", "images/restaurants/" + id, val))
    let result = [];
    for (let picture of pictures) {
        result.push(new Buffer(fs.readFileSync(picture)).toString("base64"));
    }

    return result;
}

async function find (restaurant) {
    let response = restaurant.get();

    await Table.findAll({where: {
            restaraunt_id : restaurant.id
        }}).then(tables => response.allTables = tables.length);

    let date = new Date();
    await Reservation.findAll({where: {
            restaurantId: restaurant.id,
            date: date.toISOString().substring(0, 10)
        }}).then(reservations => response.freeTables = response.allTables - reservations.length)

    response.img = await getRestaurantPictures(restaurant.id)[0];

    let restarauntCuisines = [];
    await RestarauntCuisines.findAll({
        where: {
            restaurant_id: restaurant.id
        }
    }).then(cuisines => restarauntCuisines = cuisines);
    response.cuisines = []
    for (const cuisine of restarauntCuisines) {
        await Cuisine.findOne({
            where: {
                id: cuisine.cuisine_id
            }
        }).then(cuisine => response.cuisines.push(cuisine.get()));
    }
    return response;

}

async function findExtended(restaurant, req) {
    let response = restaurant.get();
    await Table.findAll({
        where: {
            restaraunt_id: restaurant.id
        }
    }).then(tables => {
        response.allTables = tables.length;
        response.tables = [];
        for (const table of tables) {
            response.tables.push(table.get());
        }
    });


    let restarauntCuisines = [];
    await RestarauntCuisines.findAll({
        where: {
            restaurant_id: restaurant.id
        }
    }).then(cuisines => restarauntCuisines = cuisines);
    response.cuisines = []
    for (const cuisine of restarauntCuisines) {
        await Cuisine.findOne({
            where: {
                id: cuisine.cuisine_id
            }
        }).then(cuisine => response.cuisines.push(cuisine.get()));
    }

    let date = new Date();
    for (const table of response.tables) {
        await Reservation.findOne({
            where: {
                restaurantId: restaurant.id,
                table_id: table.id,
                date: date.toISOString().substring(0, 10)
            }
        }).then(async reservation => {
            console.log('RESERVATION:   ' + reservation);
            if (reservation != null) {
                const token =
                    req.body.token || req.query.token || req.headers["authorization"] || req.headers["x-access-token"];
                const _user = jwt.verify(token, process.env.TOKEN_KEY);
                let user;
                await User.findOne({
                    where: {
                        username: _user.user_id
                    }
                }).then(_user => user = _user);
                if (reservation.status == "pending")
                    table.status = "pending";
                else if (reservation.status == "active") {
                    if (reservation.userId == user.id)
                        table.status = "yours";
                    else
                        table.status = "occupied";
                } else
                    table.status = "free";
            } else
                table.status = "free";

        });
    }
    await Reservation.findAll({
        where: {
            restaurantId: restaurant.id,
            date: date.toISOString().substring(0, 10)
        }
    }).then(reservations => response.freeTables = response.allTables - reservations.length)

    //Add pictures
    response.pictures = await getRestaurantPictures(restaurant.id);
  
    return response;
}

module.exports = {
    async list(req, res) {
        // console.log(req.headers);

        let restaurants;
        await Restaurant
            .findAll()
            .then(_restaurants => restaurants = _restaurants)
            .catch(error => res.status(400).send(error));

        let responseList = [];
        for (const restaurant of restaurants) {
            responseList.push(await find(restaurant));
        }

        return res.status(200).send(responseList);
    },

    async listExtended(req, res) {

        let restaurants;
        await Restaurant
            .findAll()
            .then(_restaurants => restaurants = _restaurants)
            .catch(error => res.status(400).send(error));

        let responseList = [];
        for (const restaurant of restaurants) {
            responseList.push(await findExtended(restaurant, req));
        }

        return res.status(200).send(responseList);
    },

    async add(req, res) {
        if (req.body.cuisine){
             await RestaurantCuisine.create(
                {
                    restaurantId: restaraunt.id,
                    cuisineId: req.body.cuisine
                }
            ).catch(error => res.status(400).send(error));
        }

        return Restaurant
            .create(req.body)
            .then(restaurant => res.status(201).send(restaurant))
            .catch(error => res.status(400).send(error));
    },

    delete (req, res) {
        return Restaurant
            .findOne({
                where: req.body
            }).then(restaurant => {
                if (!restaurant) {
                    return res.status(404).send({
                        message: 'Restaurant Not Found',
                    });
                }
                return restaurant
                    .destroy()
                    .then(() => res.status(204).send())
                    .catch(error => res.status(400).send(error));
            })
            .catch(error => res.status(400).send(error));
    },

    async find(req, res) {
        let restaurant;
        await Restaurant.findOne({
            where: req.body
        }).then(_restaurant => restaurant = _restaurant);

        return res.status(200).send(await find(restaurant));
    },

    async findExtended(req, res) {
        let restaurant;
        await Restaurant.findOne({
            where: req.body
        }).then(_restaurant => restaurant = _restaurant);

        return res.status(200).send(await findExtended(restaurant, req));
    },

    async update(req, res) {
        let restaurant;
        await Restaurant.findOne({
            where: {
                id: req.body.id
            }
        }).then(_restaurant => restaurant = _restaurant);

        restaurant.set(req.body.change);
        restaurant.save().then(res.status(200).send());
    }
};