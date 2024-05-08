'use strict';

module.exports = {
  async up (queryInterface, Sequelize) {
    /**
     * Add seed commands here.
     *
     * Example:
     * await queryInterface.bulkInsert('People', [{
     *   name: 'John Doe',
     *   isBetaMember: false
     * }], {});
    */
   await queryInterface.bulkInsert('Restaurants', [{
      name: 'Mado',
      address: '123 Main St',
      rating: 4,
      phone: '123-456-7890',
      info: 'This is a good restaurant',
  },
  {
      name: 'Pizza Hut',
      address: '99 Mangilik St',
      rating: 5,
      phone: '123-456-324890',
      info: 'This is a good restaurant',
  },
  {
      name: 'Panda Express',
      address: '99 Mangilik St',
      rating: 5,
      phone: '342-456-324890',
      info: 'That is a very good restaurant',
  },
], {});

  },

  async down (queryInterface, Sequelize) {
    /**
     * Add commands to revert seed here.
     *
     * Example:
     * await queryInterface.bulkDelete('People', null, {});
     */
    await queryInterface.bulkDelete('Restaurants', null, {});
  }
};
