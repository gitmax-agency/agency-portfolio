'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class RestarauntCuisine extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
    }
  }
  RestarauntCuisine.init({
    restaurant_id: DataTypes.INTEGER,
    cuisine_id: DataTypes.INTEGER
  }, {
    sequelize,
    modelName: 'RestarauntCuisine',
      timestamps: false,
      createdAt: false,
      updatedAt: false
  });
  return RestarauntCuisine;
};