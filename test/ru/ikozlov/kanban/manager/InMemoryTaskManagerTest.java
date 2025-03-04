package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setup() {
        taskManager = new InMemoryTaskManager();
    }

}