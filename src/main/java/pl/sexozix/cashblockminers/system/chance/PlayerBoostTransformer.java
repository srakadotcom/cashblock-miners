package pl.sexozix.cashblockminers.system.chance;

public class PlayerBoostTransformer implements ChanceTransformer {
  @Override
  public boolean applyChance(TransformerContext context) {
    if(context.getDataModel().isBoostActive())
      context.multiplyQuantity(1.5);
    return true;
  }
}
