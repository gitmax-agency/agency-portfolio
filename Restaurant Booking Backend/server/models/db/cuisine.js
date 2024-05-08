'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class Cuisine extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
      Cuisine.belongsToMany(models.Restaurant, {
        foreignKey: 'cuisine_id', 
        through: 'RestarauntCuisine',
        as: 'restaurants' 
      });
    }
  }
  Cuisine.init({
    name: DataTypes.STRING
  }, {
    sequelize,
    modelName: 'Cuisine',
    timestamps: false,
    createdAt: false,
    updatedAt: false
  });
  return Cuisine;
};