import Sequelize, { Model } from "sequelize";

export default class Post extends Model {
  static init(sequelize) {
    super.init(
      {
        id: {
          type: Sequelize.UUID,
          primaryKey: true,
          allowNull: false,
          defaultValue: Sequelize.UUIDV4,
        },
        userId: {
          type: Sequelize.STRING,
          allowNull: false,
          references: {
            model: "user",
            key: "username",
          },
          onUpdate: "CASCADE",
          onDelete: "CASCADE",
        },
        content: {
          type: Sequelize.STRING(300),
          allowNull: false,
        },
        amountLikes: {
          type: Sequelize.INTEGER,
          defaultValue: 0,
          allowNull: false,
        },
        amountComments: {
          type: Sequelize.INTEGER,
          defaultValue: 0,
          allowNull: false,
        },
      },
      {
        sequelize,
      }
    );

    return this;
  }
}
