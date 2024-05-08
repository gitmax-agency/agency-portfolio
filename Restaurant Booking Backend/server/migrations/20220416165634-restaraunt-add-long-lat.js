'use strict';

module.exports = {
  async up (queryInterface, Sequelize) {
    /**
     * Add altering commands here.
     *
     * Example:
     * await queryInterface.createTable('users', { id: Sequelize.INTEGER });
     */

      await queryInterface.addColumn('Restaurants', 'long', {
        type: Sequelize.FLOAT,
        allowNull: true,
      });

      await queryInterface.addColumn('Restaurants', 'lat', {
        type: Sequelize.FLOAT,
        allowNull: true,
      });

  },

  async down (queryInterface, Sequelize) {
    /**
     * Add reverting commands here.
     *
     * Example:
     * await queryInterface.dropTable('users');
     */
    await queryInterface.removeColumn('Restaurants', 'long');
    await queryInterface.removeColumn('Restaurants', 'lat');
  }
};
