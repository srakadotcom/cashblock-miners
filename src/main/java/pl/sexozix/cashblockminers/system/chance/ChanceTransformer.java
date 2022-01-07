package pl.sexozix.cashblockminers.system.chance;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.sexozix.cashblockminers.system.data.UserDataModel;

import java.util.Collection;

public interface ChanceTransformer {
    static double apply(Player player, UserDataModel dataModel, Block block, Collection<ChanceTransformer> transformers) {
        TransformerContext context = new TransformerContext(block, player, dataModel);

        for (ChanceTransformer transformer : transformers)
            if (!transformer.applyChance(context))
                break;

        return context.getQuantity();
    }

    boolean applyChance(TransformerContext context);
}
