package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.manager.inmemory.InMemoryHistoryManager;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    void getDefaultTaskManager() {
        TaskManager manager = Managers.getDefault();

        assertInstanceOf(InMemoryTaskManager.class, manager);
    }

    @Test
    void getDefaultHistoryManager() {
        HistoryManager manager = Managers.getDefaultHistory();

        assertInstanceOf(InMemoryHistoryManager.class, manager);
    }

}