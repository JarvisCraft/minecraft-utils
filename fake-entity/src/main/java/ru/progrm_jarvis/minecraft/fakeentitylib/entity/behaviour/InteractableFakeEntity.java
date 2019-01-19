package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

public interface InteractableFakeEntity extends FakeEntity {

    int getEntityId();

    void handleInteraction(Player player, FakeEntityInteraction interaction);
}
