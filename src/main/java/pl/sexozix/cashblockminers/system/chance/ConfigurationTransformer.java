package pl.sexozix.cashblockminers.system.chance;

import pl.sexozix.cashblockminers.CashBlockConfiguration;
import pl.sexozix.cashblockminers.system.reward.Reward;
import pl.sexozix.cashblockminers.utils.RandomUtil;

public class ConfigurationTransformer implements ChanceTransformer {
    @Override
    public boolean applyChance(TransformerContext context) {
        for (Reward reward : CashBlockConfiguration.getConfiguration().rewardList)
            if (RandomUtil.chance(reward.chance())) {
                context.setQuantity(reward.quantity());
                break;
            }

        return true;
    }
}
