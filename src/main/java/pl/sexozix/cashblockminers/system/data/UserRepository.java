package pl.sexozix.cashblockminers.system.data;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.query.DatabaseBulkInsertQuery;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;

public class UserRepository {

    private final Map<UUID, UserDataModel> dataModelMap = new HashMap<>();
    private final DatabaseTable<UserDataModel> databaseTable;


    public UserRepository(HikariDataSource dataSource) {
        this.databaseTable = new DatabaseTable<>("cashblock_players", dataSource, UserDataModel.class);
        this.databaseTable.initializeTable();
    }

    public void save() throws DatabaseTransactionError {
        new DatabaseBulkInsertQuery(DatabaseBulkInsertQuery.UpdateType.REPLACE)
                    .values(dataModelMap.values().stream()
                            .map(databaseTable.getModelMapper()::createQueryPairs)
                            .toList())
                    .execute(databaseTable);
    }

    public List<UserDataModel> createTops() {
        List<UserDataModel> dataModelList = new ArrayList<>(dataModelMap.values());
        dataModelList.sort(Comparator.comparingDouble(UserDataModel::money).reversed());
        return dataModelList;
    }

    public UserDataModel getOrCreateUser(String name, UUID uuid) {
        UserDataModel dataModel = dataModelMap.get(uuid);
        if (dataModel == null) {
            dataModelMap.put(uuid, dataModel = new UserDataModel(uuid, name, 0, 0, 0));
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
