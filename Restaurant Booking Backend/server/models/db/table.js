'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class Table extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
      Table.belongsTo(models.Restaurant, {
        foreignKey: 'restaurant_id',
        onDelete: 'LAZY'
      });
      Table.hasMany(models.Reservation, {
        foreignKey: 'table_id',
        onDelete: 'LAZY'
      });
    }
  }
  Table.init({
    restaraunt_id: DataTypes.INTEGER,
    coord_x: DataTypes.STRING,
    coord_y: DataTypes.STRING,
    height: DataTypes.INTEGER,
    width: DataTypes.INTEGER,
    num_of_person: DataTypes.INTEGER,
    floor: DataTypes.INTEGER
  }, {
    sequelize,
    modelName: 'Table',
    timestamps: false,
    createdAt: false,
    updatedAt: false
  });
  return Table;
};