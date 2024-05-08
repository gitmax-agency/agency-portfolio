'use strict';
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('RestarauntCuisines', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },
      restaurant_id: {
        type: Sequelize.INTEGER,
        references: { model: 'Restaurants', key: 'id' },
        onDelete: 'SET NULL',
      },
      cuisine_id: {
        type: Sequelize.INTEGER,
        references: { model: 'Cuisines', key: 'id' },
        onDelete: 'SET NULL',
      }
    });
  },
  async down(queryInterface, Sequelize) {
    await queryInterface.dropTable('RestarauntCuisines');
  }
};