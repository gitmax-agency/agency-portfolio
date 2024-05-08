
const models = require('../models/index.js');
const {Sequelize, DataTypes} = require("sequelize");
const User = require('../models/db/user.js')(models.sequelize, DataTypes);
const Op = models.Sequelize.Op;

module.exports = {

    list(req, res) {
        return User.findAll({
            attributes: {exclude: ['password', 'token']},
            order: [['id','ASC']]})
            .then(users => res.status(200).send(users));
    },

    add(req, res) {
        return User
            .create(req.body)
            .then(user => res.status(201).send(user))
            .catch(error => res.status(400).send(error));
    },

    delete (req, res) {
        return User
            .findOne({
                where: req.body
            }).then(user => {
                if (!user) {
                    return res.status(404).send({
                        message: 'User Not Found',
                    });
                }
                return user
                    .destroy()
                    .then(() => res.status(204).send())
                    .catch(error => res.status(400).send(error));
            })
            .catch(error => res.status(400).send(error));
    },

    find(req, res) {
        return User.findOne({
            where: req.body
        }).then(_user => res.send(_user));
    },

    async update(req, res) {
        let user;
        await User.findOne({
            where: {
                id: req.body.id
            }
        }).then(_user => user = _user);

        user.set(req.body.change);
        user.save().then(res.status(200).send());
    }
};