
const models = require('../models/index.js');
const {Sequelize, DataTypes} = require("sequelize");
const Table = require('../models/db/table.js')(models.sequelize, DataTypes);
const Op = models.Sequelize.Op;

module.exports = {
    list(req, res) {
        return Table
            .findAll()
            .then(tables => res.status(200).send(tables))
            .catch(error => res.status(400).send(error));
    },

    add(req, res) {
        return Table
            .create(req.body)
            .then(table => res.status(201).send(table))
            .catch(error => res.status(400).send(error));
    },

    delete (req, res) {
        return Table
            .findOne({
                where: req.body
            }).then(table => {
                if (!table) {
                    return res.status(404).send({
                        message: 'Table Not Found',
                    });
                }
                return table
                    .destroy()
                    .then(() => res.status(204).send())
                    .catch(error => res.status(400).send(error));
            })
            .catch(error => res.status(400).send(error));
    },

    find(req, res) {
        return Table.findOne({
            where: req.body
        }).then(_table => res.send(_table));
    },

    async update(req, res) {
        let table;
        await Table.findOne({
            where: {
                id: req.body.id
            }
        }).then(_table => table = _table);

        table.set(req.body.change);
        table.save().then(res.status(200).send());
    }
};