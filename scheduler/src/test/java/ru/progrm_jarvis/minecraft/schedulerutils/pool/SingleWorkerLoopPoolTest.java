package ru.progrm_jarvis.minecraft.schedulerutils.pool;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.progrm_jarvis.minecraft.schedulerutils.pool.LoopPool.TaskOptions;

import java.util.Random;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SingleWorkerLoopPoolTest {

    private final Random RANDOM = new Random();

    @Mock private Plugin plugin;
    @InjectMocks private SingleWorkerLoopPool<String> loopPool;

    @BeforeAll
    static void setUp() {
        Bukkit.setServer(mock(Server.class, Answers.RETURNS_MOCKS));
    }

    /* TODO
    @Test
    void addTask() {
    }

    @Test
    void removeTask() {
    }

    @Test
    void removeTasks() {
    }

    @Test
    void removeTask1() {
    }

    @Test
    void removeTasks1() {
    }
    */

    @Test
    void testClearTasks() {
        final int syncTasks = 1 + RANDOM.nextInt(15), asyncTasks = 1 + RANDOM.nextInt(15);
        for (int i = 0; i < syncTasks; i++) loopPool
                .addTask(TaskOptions.of(false, 1 + abs(RANDOM.nextLong())), () -> {});
        for (int i = 0; i < asyncTasks; i++) loopPool
                .addTask(TaskOptions.of(true, 1 + abs(RANDOM.nextLong())), () -> {});
        assertEquals(syncTasks + asyncTasks, loopPool.tasksSize());

        loopPool.clearTasks();
        assertEquals(0, loopPool.tasksSize());
    }
}
