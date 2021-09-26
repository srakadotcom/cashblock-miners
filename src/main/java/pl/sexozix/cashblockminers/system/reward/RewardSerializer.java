package pl.sexozix.cashblockminers.system.reward;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;

public class RewardSerializer implements ObjectSerializer<Reward> {
    @Override
    public boolean supports(Class<? super Reward> aClass) {
        return Reward.class.isAssignableFrom(aClass); //moze teraz?
    }

    @Override
    public void serialize(Reward reward, SerializationData serializationData) {
        serializationData.add("chance", reward.chance());
        serializationData.add("quantity", reward.quantity());
    }

    @Override
    public Reward deserialize(DeserializationData deserializationData, GenericsDeclaration genericsDeclaration) {
        return new Reward(
                deserializationData.get("chance", Double.class),
                deserializationData.get("quantity", Double.class)
        );
    }
}
