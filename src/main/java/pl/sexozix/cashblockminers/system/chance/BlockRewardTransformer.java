package pl.sexozix.cashblockminers.system.chance;

import pl.sexozix.cashblockminers.system.blockreward.BlockRewardManager;

import java.util.OptionalDouble;

public class BlockRewardTransformer implements ChanceTransformer {
    private final BlockRewardManager rewardManager;

    public BlockRewardTransformer(BlockRewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @Override
    public boolean applyChance(TransformerContext context) {
        OptionalDouble blockReward = rewardManager.getReward(context.getBlock().getLocation());
        if (blockReward.isPresent()) {
            context.setQuantity(blockReward.getAsDouble());
            return false;
        }
        return true;
    }
}
