package ru.progrm_jarvis.minecraft.fakeentitylib.misc.structure;

import com.google.gson.Gson;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructureDescriptorTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    void testGson() throws IOException {
        final StructureDescriptor.JsonRepresentation descriptor;
        try (val reader = new BufferedReader(
                new FileReader(new File(getClass().getClass().getResource("/entity_descriptor_1.json").getFile()))
        )) {
            descriptor = gson.fromJson(reader, StructureDescriptor.JsonRepresentation.class);
        }

        assertEquals(2, descriptor.getObjects().length);
        assertEquals(1, descriptor.getKeyframes().length);
    }
}