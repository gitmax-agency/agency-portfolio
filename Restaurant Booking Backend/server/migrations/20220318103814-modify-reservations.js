'use strict';
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('Reservations', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },
      userId: {
        type: Sequelize.INTEGER,
        onDelete: 'SET NULL',
        references: {
          model: 'Users',
          key: 'id',
          as: 'userId',
        },
      },
      restaurantId: {
        type: Sequelize.INTEGER,
        onDelete: 'SET NULL',
        references: {
          model: 'Restaurants',
          key: 'id',
          as: 'RestaurantId',
        }},
      date: {
        type: Sequelize.STRING
      },
      status: {
        type: Sequelize.STRING
      },
      table_id: {
        type: Sequelize.INTEGER,
      onDelete: 'SET NULL',
      references: {
        model: 'Tables',
        key: 'id',
        as: 'TableId'
      }},
      extra_info: {
        type: Sequelize.STRING,
        allowNull: true,
      }
    });
  },
  async down(queryInterface, Sequelize) {
    await queryInterface.dropTable('Reservations');
  }
};