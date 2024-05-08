'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class Restaurant extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
      Restaurant.belongsToMany(models.Cuisine, {
        foreignKey: 'restaurant_id', 
        through: 'RestarauntCuisine',
        as: 'cuisines' 
      });
    }
  }
  Restaurant.init({
    name: DataTypes.STRING,
    address: DataTypes.STRING,
    rating: DataTypes.STRING,
    phone: DataTypes.STRING,
    info: DataTypes.STRING,
    long: DataTypes.DOUBLE,
    lat: DataTypes.DOUBLE
  }, {
    sequelize,
    modelName: 'Restaurant',
    timestamps: false,
    createdAt: false,
    updatedAt: false
  });
  return Restaurant;
};