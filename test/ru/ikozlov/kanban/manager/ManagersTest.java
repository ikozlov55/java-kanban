package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    void getDefaultTaskManager() {
        TaskManager manager = Managers.getDefault();

        assertInstanceOf(ImMemoryTaskManager.class, manager);
    }

    @Test
    void getDefaultHistoryManager() {
        HistoryManager manager = Managers.getDefaultHistory();

        assertInstanceOf(InMemoryHistoryManager.class, manager);
    }

}