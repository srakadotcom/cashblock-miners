package pl.sexozix.cashblockminers.system.chance;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.system.data.UserDataModel;

public class TransformerContext {
    private double quantity;
    private final Block block;
    private final Player player;
    private final UserDataModel dataModel;

    public TransformerContext(Block block, Player player, UserDataModel dataModel) {
        this.block = block;
        this.player = player;
        this.dataModel = dataModel;
    }

    public Player getPlayer() {
        return player;
    }

    public UserDataModel getDataModel() {
        return dataModel;
    }

    public Block getBlock() {
        return block;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void multiplyQuantity(double multiplier) {
        this.quantity *= multiplier;
    }
}
