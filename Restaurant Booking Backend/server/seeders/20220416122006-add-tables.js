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
   // Tables for first restaraunt
    await queryInterface.bulkInsert('Tables', [{
      restaraunt_id:1,
      coord_x:1,
      coord_y:1,
      height:1,
      width:1,
      num_of_person:4,
      floor: 1,
  },
  {
      restaraunt_id:1,
      coord_x:23,
      coord_y:34,
      height:23,
      width:34,
      num_of_person:6,
      floor: 1,
  },], {});
  // Tables for second restaraunt
    await queryInterface.bulkInsert('Tables', [{
      restaraunt_id:2,
      coord_x:3,
      coord_y:7,
      height:8,
      width:9,
      num_of_person:5,
      floor: 1,
  },
  {
      restaraunt_id:2,
      coord_x:23,
      coord_y:34,
      height:23,
      width:34,
      num_of_person:6,
      floor: 1,
  },], {});
  // Tables for third restaraunt

    await queryInterface.bulkInsert('Tables', [{
      restaraunt_id:3,
      coord_x:3,
      coord_y:7,
      height:8,
      width:9,
      num_of_person:5,
      floor: 1,
  },
  {
      restaraunt_id:3,
      coord_x:23,
      coord_y:34,
      height:23,
      width:34,
      num_of_person:6,
      floor: 1,
  },], {});
},


  async down (queryInterface, Sequelize) {
    /**
     * Add commands to revert seed here.
     *
     * Example:
     * await queryInterface.bulkDelete('People', null, {});
     */
    await queryInterface.bulkDelete('Tables', null, {});

  }
};
