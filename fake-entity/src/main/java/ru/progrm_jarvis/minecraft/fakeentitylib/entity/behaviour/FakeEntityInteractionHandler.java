package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.FakeEntityManager;

public interface FakeEntityInteractionHandler<P extends Plugin, E extends InteractableFakeEntity>
        extends FakeEntityManager<P, E> {
}
