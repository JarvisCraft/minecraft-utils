package ru.progrm_jarvis.minecraft.commons.nms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToStringUtilTest {

    @Test
    void getterNameToStringTest() {
        assertEquals("lel", ToStringUtil.getterNameToString("lel"));
        assertEquals("foo", ToStringUtil.getterNameToString("getFoo"));
        assertEquals("barBaz", ToStringUtil.getterNameToString("getBarBaz"));
        assertEquals("get", ToStringUtil.getterNameToString("get"));
    }
}