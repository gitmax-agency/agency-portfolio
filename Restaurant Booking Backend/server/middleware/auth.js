const jwt = require("jsonwebtoken");

const config = process.env;

const verifyToken = (req, res, next) => {

    const token =
        req.body.token || req.query.token || req.headers["x-access-token"] || req.headers["authorization"];

    // Check for token presence
    if (!token) {

        return res.status(403).send("A token is required for authentication");
    }
    try {

        // Verifying if token is valid, on success - user is returned
        const decoded = jwt.verify(token, config.TOKEN_KEY);
        req.user = decoded;

    } catch (err) {

        // Report errors in case library misfunctioning or invalidity of token
        return res.status(401).send("Invalid Token");

    }

    return next();
};


module.exports = verifyToken;