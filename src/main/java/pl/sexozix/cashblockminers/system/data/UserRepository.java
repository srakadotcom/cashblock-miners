package pl.sexozix.cashblockminers.system.data;

import java.util.*;

import pl.memexurer.srakadb.sql.*;
import pl.sexozix.cashblockminers.system.data.UserDataModel.Deserializer;

public class UserRepository {

  private final Map<UUID, UserDataModel> dataModelMap = new HashMap<>();
  private final DatabaseTable<UserDataModel> databaseTable = new DatabaseTable.Builder<>(
      "cashblock_players", new Deserializer())
      .addPrimaryColumn("PlayerUniqueId", DatabaseDatatype.varCharacter(36))
      .addColumn("PlayerName", DatabaseDatatype.varCharacter(16))
      .addColumn("PlayerMoney", DatabaseDatatype.decimal(15, 2))
      .addColumn("BoostExpire", DatabaseDatatype.integer(8))
      .build();

  public void initialize(DatabaseManager manager) {
    manager.createTable(databaseTable);
    try (DatabaseQueryTransaction<UserDataModel> transaction = databaseTable
        .createQueryAllRowsTransaction()) {
      UserDataModel dataModel;
      while ((dataModel = transaction.readNextResult()) != null) {
        dataModelMap.put(dataModel.uuid(), dataModel);
      }
    }
  }

  public void save() throws DatabaseTransactionError {
    Collection<UserDataModel> dataModelList = new ArrayList<>(dataModelMap.values());

    try(DatabasePreparedTransaction transaction = databaseTable.createUpdateAllColumnsTransaction()) {
      transaction.setBatch();

      for(UserDataModel dataModel: dataModelList) {
        if(!dataModel.update()) continue;

          transaction.set("PlayerUniqueId", dataModel.uuid().toString());
          transaction.set("PlayerName", dataModel.name());
          transaction.set("PlayerMoney", dataModel.money());
          transaction.set("BoostExpire", dataModel.boostExpire());
          transaction.addBatch();
      }
    }
  }

  public List<UserDataModel> createTops() {
    List<UserDataModel> dataModelList = new ArrayList<>(dataModelMap.values());
    dataModelList.sort(Comparator.comparingDouble(UserDataModel::money).reversed());
    return dataModelList.subList(0, Math.min(10, dataModelList.size()));
  }

  public UserDataModel getOrCreateUser(String name, UUID uuid) {
    UserDataModel dataModel = dataModelMap.get(uuid);
    if(dataModel == null) {
      dataModelMap.put(uuid, dataModel = new UserDataModel(uuid, name, 0, 0));
    }

    return dataModel;
  }

  public UserDataModel getExistingUser(UUID uuid) {
    return dataModelMap.get(uuid);
  }

  public UserDataModel findUserByName(String name) {
    for (UserDataModel dataModel : dataModelMap.values()) {
      if (dataModel.name().equals(name)) {
        return dataModel;
      }
    }
    return null;
  }
}
