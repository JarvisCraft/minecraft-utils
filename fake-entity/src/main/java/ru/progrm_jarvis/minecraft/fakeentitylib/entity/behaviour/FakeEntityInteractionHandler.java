package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import lombok.NonNull;

public interface FakeEntityInteractionHandler {

    void register(@NonNull InteractableFakeEntity entity);

    void unregister(@NonNull InteractableFakeEntity entity);
}
