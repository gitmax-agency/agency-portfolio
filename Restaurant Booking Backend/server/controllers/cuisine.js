
const models = require('../models/index.js');
const {Sequelize, DataTypes} = require("sequelize");
const Cuisine = require('../models/db/cuisine.js')(models.sequelize, DataTypes);
const Op = models.Sequelize.Op;

module.exports = {
    list(req, res) {
        return Cuisine
            .findAll()
            .then(cuisines => res.status(200).send(cuisines))
            .catch(error => res.status(400).send(error));
    },

    add(req, res) {
        return Cuisine
            .create(req.body)
            .then(cuisine => res.status(201).send(cuisine))
            .catch(error => res.status(400).send(error));
    },

    delete (req, res) {
        return Cuisine
            .findOne({
                where: req.body
            }).then(cuisine => {
                if (!cuisine) {
                    return res.status(404).send({
                        message: 'Cuisine Not Found',
                    });
                }
                return cuisine
                    .destroy()
                    .then(() => res.status(204).send())
                    .catch(error => res.status(400).send(error));
            })
            .catch(error => res.status(400).send(error));
    },

    find(req, res) {
        return Cuisine.findOne({
            where: req.body
        }).then(_cuisine => res.send(_cuisine));
    },

    async update(req, res) {
        let cuisine;
        await Cuisine.findOne({
            where: {
                id: req.body.id
            }
        }).then(_cuisine => cuisine = _cuisine);

        cuisine.set(req.body.change);
        cuisine.save().then(res.status(200).send());
    }
};