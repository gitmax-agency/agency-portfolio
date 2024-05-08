const app = require("./app");
const port = process.env.PORT || 3000;

const db = require("./database-check");

// swagger ui connection 
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./swagger.json');


// swagger routes
app.use('/api-docs', swaggerUi.serve, 
   swaggerUi.setup(swaggerDocument)
 );


app.get("/", async function (req, res) {
   res.send(`Reached home!`);
});
require('./server/routes')(app);
app.get('*', (req, res) => res.status(200).send({
  message: 'Welcome to the beginning of nothingness.',
}));
app.listen( port ,function () {
   console.log("Server started. Go to http://localhost:" + port);
});