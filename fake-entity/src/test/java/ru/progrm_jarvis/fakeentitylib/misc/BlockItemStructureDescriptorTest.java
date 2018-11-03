package ru.progrm_jarvis.fakeentitylib.misc;

import com.google.gson.Gson;
import junit.framework.TestCase;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class BlockItemStructureDescriptorTest extends TestCase {

    Gson gson;

    @Override
    protected void setUp() {
        gson = new Gson();
    }

    @Test
    void testGson() throws IOException {
        final BlockItemStructureDescriptor.JsonRepresentation descriptor;
        try (val reader = new BufferedReader(
                new FileReader(new File(getClass().getClass().getResource("/entity_descriptor_1.json").getFile()))
        )) {
            descriptor = gson.fromJson(reader, BlockItemStructureDescriptor.JsonRepresentation.class);
        }

        assertEquals(2, descriptor.getObjects().length);
        assertEquals(1, descriptor.getKeyframes().length);
    }
}