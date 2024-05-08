bcrypt = require('bcrypt');
const models = require('../models/index.js');
const {Sequelize, DataTypes} = require("sequelize");
const User = require('../models/db/user.js')(models.sequelize, DataTypes);
const jwt = require('jsonwebtoken');

module.exports = {
    async register(req, res) {

        try {
            // Get user input
            const {username, password} = req.body;

            // Validate user input
            if (!(password && username)) {
                return res.status(400).send("All input is required");
            }

            // check if user already exist
            // Validate if user exist in our database
            const oldUser = await User.findOne({
                where: {
                    username: req.body.username
                }
            });
            if (oldUser) {
                return res.status(409).send("User Already Exist. Please Login");
            }

            //Encrypt user password
            const encryptedPassword = await bcrypt.hash(password, 10);

           

            // Create token
            const token = jwt.sign(
                {
                    user_id: username
                },
                process.env.TOKEN_KEY,
                {
                }
            );
            // save user token
             // Create user in our database
             let user;
             await User.create({
                 username,
                 password: encryptedPassword,
                 token: token
             }).then(_user => user = _user);
            
            // return new user
            return res.status(201).json(user);
        } catch (err) {
            console.log(err);
        }
        // Our register logic ends here
    },

    async login(req, res) {

        try {
            // Get user input
            const { username, password } = req.body;

            // Validate user input
            if (!(username && password)) {
                return res.status(400).send("All input is required");
            }

            // Validate if user exist in our database
            const user = await User.findOne({
                where: {
                    username: req.body.username
                }
            });

            if (user && (await bcrypt.compare(password, user.password))) {
                // Create token
                const token = jwt.sign(
                    { user_id: user.username },
                    process.env.TOKEN_KEY,
                    {
                    }
                );

                // save user token
                await user.update({
                    token: token
                });

                // user
                return res.status(200).json(user);

            }

            return res.status(400).send("Invalid Credentials");

        } catch (err) {

            console.log(err);

        }
        // Our register logic ends here

    },

    async logout(req, res) {

        try {
            let token =
                req.body.token || req.query.token || req.headers["x-access-token"] || req.headers["authorization"];

            if (token == null)
                return res.status(400).send("No token");
            let user;
            try {
                const decoded = jwt.verify(token, process.env.TOKEN_KEY);
                user = decoded;
            } catch (err) {
                return res.status(401).send("Invalid Token");
            }

            token = jwt.sign(
                { user_id: user.user_id },
                process.env.TOKEN_KEY,
                {
                    expiresIn: "1m",
                }
            );

            user = await User.findOne({
                where: {
                    id: user.user_id
                }
            });

            await user.update({
                token: token
            });

            res.status(200).send("You have been logged out!");

        } catch (err) {
            console.log(err);
        }
        // Our register logic ends here

    }
};