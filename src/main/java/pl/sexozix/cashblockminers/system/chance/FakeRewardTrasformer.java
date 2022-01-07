package pl.sexozix.cashblockminers.system.chance;

public class FakeRewardTrasformer implements ChanceTransformer {
    @Override
    public boolean applyChance(TransformerContext context) {
        double pitos = context.getDataModel().fakeReward();
        if (pitos > 0.0d)
            context.setQuantity(pitos);
        return true;
    }
}
