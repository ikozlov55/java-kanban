package ru.ikozlov.kanban.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.http.util.EpicsListTypeToken;
import ru.ikozlov.kanban.http.util.SubtasksListTypeToken;
import ru.ikozlov.kanban.http.util.TasksListTypeToken;
import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;
import ru.ikozlov.kanban.testdata.EpicBuilder;
import ru.ikozlov.kanban.testdata.HttpTaskClient;
import ru.ikozlov.kanban.testdata.SubtaskBuilder;
import ru.ikozlov.kanban.testdata.TaskBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {
    Gson gson = HttpTaskServer.makeGson();
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;
    HttpTaskClient taskClient = new HttpTaskClient("http://localhost:8080", gson);

    @BeforeEach
    public void setup() throws IOException {
        taskManager.clearTasks();
        taskManager.clearEpics();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }


    @Test
    void getTasksList() throws IOException, InterruptedException {
        taskManager.createTask(new TaskBuilder(1).build());
        taskManager.createTask(new TaskBuilder(2).build());
        taskManager.createTask(new TaskBuilder(3).build());
        HttpResponse<String> response = taskClient.getTasks();

        Assertions.assertEquals(200, response.statusCode());
        List<Task> obtainedTasks = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        Assertions.assertEquals(3, obtainedTasks.size());
    }

    @Test
    void getEmptyTasksList() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.getTasks();

        Assertions.assertEquals(200, response.statusCode());
        List<Task> obtainedTasks = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        Assertions.assertTrue(obtainedTasks.isEmpty());
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Task task = new TaskBuilder(1).startTime(null).build();
        taskManager.createTask(task);
        HttpResponse<String> response = taskClient.getTaskById(1);

        Assertions.assertEquals(200, response.statusCode());
        Task record = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(task.getId(), 1);
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
        Assertions.assertEquals(task.getDuration(), record.getDuration());
        Assertions.assertEquals(task.getStartTime(), record.getStartTime());
    }

    @Test
    void getTaskByNonexistentId() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.getTaskById(1);

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void taskCreation() throws IOException, InterruptedException {
        Task task = new TaskBuilder(null).build();
        HttpResponse<String> response = taskClient.postTask(task);

        Assertions.assertEquals(201, response.statusCode());
        Task record = taskManager.getTask(1);
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
        Assertions.assertEquals(task.getDuration(), record.getDuration());
        Assertions.assertEquals(task.getStartTime(), record.getStartTime());
    }

    @Test
    void taskCreateTimeIntersectionError() throws IOException, InterruptedException {
        Task task1 = new TaskBuilder(1).duration(Duration.ofHours(1)).build();
        taskManager.createTask(task1);
        Task task2 = new TaskBuilder(null).startTime(task1.getStartTime()).build();
        HttpResponse<String> response = taskClient.postTask(task2);

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void taskUpdate() throws IOException, InterruptedException {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        task.setTitle(task.getTitle() + "Updated");
        task.setTitle(task.getDescription() + "Updated");
        task.setStatus(Task.Status.IN_PROGRESS);
        task.setDuration(task.getDuration().plus(Duration.ofMinutes(30)));
        task.setStartTime(task.getStartTime().plusHours(1));
        HttpResponse<String> response = taskClient.postTask(task);

        Assertions.assertEquals(201, response.statusCode());
        Task record = taskManager.getTask(1);
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
        Assertions.assertEquals(task.getDuration(), record.getDuration());
        Assertions.assertEquals(task.getStartTime(), record.getStartTime());
        Assertions.assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void taskUpdateTimeIntersectionError() throws IOException, InterruptedException {
        Task task1 = new TaskBuilder(1).duration(Duration.ofHours(1)).build();
        Task task2 = new TaskBuilder(2).duration(Duration.ofHours(1))
                .startTime(task1.getEndTime().plusMinutes(1)).build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        task1.setStartTime(task1.getStartTime().plusMinutes(10));
        HttpResponse<String> response = taskClient.postTask(task1);

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(task1.getStartTime().minusMinutes(10), taskManager.getTask(1).getStartTime());
    }

    @Test
    void updateNonexistentTask() throws IOException, InterruptedException {
        Task task = new TaskBuilder(1).build();
        HttpResponse<String> response = taskClient.postTask(task);

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void taskDeletion() throws IOException, InterruptedException {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        HttpResponse<String> response = taskClient.deleteTask(1);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void getEpicsList() throws IOException, InterruptedException {
        taskManager.createEpic(new EpicBuilder(1).build());
        taskManager.createEpic(new EpicBuilder(2).build());
        taskManager.createEpic(new EpicBuilder(3).build());
        HttpResponse<String> response = taskClient.getEpics();

        Assertions.assertEquals(200, response.statusCode());
        List<Epic> obtainedEpics = gson.fromJson(response.body(), new EpicsListTypeToken().getType());
        Assertions.assertEquals(3, obtainedEpics.size());
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        HttpResponse<String> response = taskClient.getEpicById(1);

        Assertions.assertEquals(200, response.statusCode());
        Epic record = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(epic.getId(), 1);
        Assertions.assertEquals(epic.getTitle(), record.getTitle());
        Assertions.assertEquals(epic.getDescription(), record.getDescription());
        Assertions.assertEquals(epic.getStatus(), record.getStatus());
        Assertions.assertEquals(epic.getDuration(), record.getDuration());
        Assertions.assertEquals(epic.getStartTime(), record.getStartTime());
        Assertions.assertEquals(epic.getEndTime(), record.getEndTime());
        Assertions.assertTrue(record.getSubtasks().isEmpty());
    }

    @Test
    void getEpicWithSubtasksById() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        List<Subtask> subtasks = List.of(
                new SubtaskBuilder(2, epic.getId()).duration(Duration.ofMinutes(30)).build(),
                new SubtaskBuilder(3, epic.getId()).duration(Duration.ofMinutes(30)).build(),
                new SubtaskBuilder(4, epic.getId()).duration(Duration.ofMinutes(30)).build()
        );
        epic.setSubtasks(subtasks);
        taskManager.createEpic(epic);
        subtasks.forEach(s -> taskManager.createSubtask(s));
        HttpResponse<String> response = taskClient.getEpicById(1);

        Assertions.assertEquals(200, response.statusCode());
        Epic record = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(3, record.getSubtasks().size());
        for (Subtask subtask : record.getSubtasks()) {
            Assertions.assertEquals(epic.getId(), subtask.getEpicId());
        }
        Assertions.assertEquals(Duration.ofMinutes(90), record.getDuration());
        Assertions.assertEquals(epic.getEndTime(), record.getEndTime());
    }

    @Test
    void getEpicByNonexistentId() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.getEpicById(1);

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void getEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic.getId()).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic.getId()).build());
        taskManager.createSubtask(new SubtaskBuilder(4, epic.getId()).build());
        HttpResponse<String> response = taskClient.getEpicSubtasks(1);

        Assertions.assertEquals(200, response.statusCode());
        List<Subtask> subtasks = gson.fromJson(response.body(), new SubtasksListTypeToken().getType());
        Assertions.assertEquals(3, subtasks.size());
    }

    @Test
    void getEpicSubtasksByNonexistentId() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.getEpicSubtasks(1);

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void epicCreation() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(null).build();
        HttpResponse<String> response = taskClient.postEpic(epic);

        Assertions.assertEquals(201, response.statusCode());
        Epic record = taskManager.getEpic(1);
        Assertions.assertEquals(epic.getTitle(), record.getTitle());
        Assertions.assertEquals(epic.getDescription(), record.getDescription());
        Assertions.assertEquals(epic.getStatus(), record.getStatus());
        Assertions.assertEquals(epic.getEndTime(), record.getEndTime());
        Assertions.assertTrue(record.getSubtasks().isEmpty());
    }

    @Test
    void epicUpdate() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        epic.setTitle(epic.getTitle() + "Updated");
        epic.setTitle(epic.getDescription() + "Updated");
        HttpResponse<String> response = taskClient.postEpic(epic);

        Assertions.assertEquals(201, response.statusCode());
        Epic record = taskManager.getEpic(1);
        Assertions.assertEquals(epic.getTitle(), record.getTitle());
        Assertions.assertEquals(epic.getDescription(), record.getDescription());
        Assertions.assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void updateNonexistentEpic() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        HttpResponse<String> response = taskClient.postEpic(epic);

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void epicDeletion() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        HttpResponse<String> response = taskClient.deleteEpic(1);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void getSubtasksList() throws IOException, InterruptedException {
        Epic epic1 = new EpicBuilder(1).build();
        Epic epic2 = new EpicBuilder(2).build();
        Subtask subtask1 = new SubtaskBuilder(3, epic1.getId()).build();
        Subtask subtask2 = new SubtaskBuilder(4, epic1.getId()).build();
        Subtask subtask3 = new SubtaskBuilder(5, epic2.getId()).build();
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        HttpResponse<String> response = taskClient.getSubtasks();

        Assertions.assertEquals(200, response.statusCode());
        List<Task> obtainedSubtasks = gson.fromJson(response.body(), new SubtasksListTypeToken().getType());
        Assertions.assertEquals(3, obtainedSubtasks.size());
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        HttpResponse<String> response = taskClient.getSubtaskById(2);

        Assertions.assertEquals(200, response.statusCode());
        Subtask record = gson.fromJson(response.body(), Subtask.class);
        Assertions.assertEquals(subtask.getId(), 2);
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(subtask.getDuration(), record.getDuration());
        Assertions.assertEquals(subtask.getStartTime(), record.getStartTime());
        Assertions.assertEquals(subtask.getEpicId(), epic.getId());
    }

    @Test
    void getSubtaskByNonexistentId() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.getSubtaskById(1);

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void subtaskCreation() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(null, epic.getId()).build();
        taskManager.createEpic(epic);
        HttpResponse<String> response = taskClient.postSubtask(subtask);

        Assertions.assertEquals(201, response.statusCode());

        Subtask record = taskManager.getSubtask(2);
        Assertions.assertEquals(2, record.getId());
        Assertions.assertEquals(epic.getId(), record.getEpicId());
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(subtask.getDuration(), record.getDuration());
        Assertions.assertEquals(subtask.getStartTime(), record.getStartTime());
        HttpResponse<String> epicResponse = taskClient.getEpicById(1);

        Assertions.assertEquals(200, epicResponse.statusCode());
        Epic epicRecord = gson.fromJson(epicResponse.body(), Epic.class);
        Assertions.assertEquals(1, epicRecord.getSubtasks().size());
    }

    @Test
    void subtaskUpdate() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        subtask.setTitle(subtask.getTitle() + "Updated");
        subtask.setTitle(subtask.getDescription() + "Updated");
        subtask.setStatus(Task.Status.IN_PROGRESS);
        subtask.setDuration(subtask.getDuration().plus(Duration.ofMinutes(30)));
        subtask.setStartTime(subtask.getStartTime().plusHours(1));
        HttpResponse<String> response = taskClient.postSubtask(subtask);

        Assertions.assertEquals(201, response.statusCode());
        Subtask record = taskManager.getSubtask(2);
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(subtask.getDuration(), record.getDuration());
        Assertions.assertEquals(subtask.getStartTime(), record.getStartTime());
        Assertions.assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    void updateNonexistentSubtask() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createEpic(epic);
        HttpResponse<String> response = taskClient.postSubtask(subtask);

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void subtaskDeletion() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        HttpResponse<String> response = taskClient.deleteSubtask(2);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createTask(new TaskBuilder(3).build());
        taskManager.createTask(new TaskBuilder(4).build());
        taskManager.createTask(new TaskBuilder(5).build());
        taskClient.getTaskById(3);
        taskClient.getTaskById(4);
        taskClient.getSubtaskById(2);
        HttpResponse<String> response = taskClient.getHistory();

        Assertions.assertEquals(200, response.statusCode());
        List<Task> obtainedTasks = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        Assertions.assertEquals(3, obtainedTasks.size());
        Assertions.assertEquals(2, obtainedTasks.getFirst().getId());
        Assertions.assertEquals(4, obtainedTasks.get(1).getId());
        Assertions.assertEquals(3, obtainedTasks.getLast().getId());
    }


    @Test
    void getPrioritized() throws IOException, InterruptedException {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).startTime(LocalDateTime.now().plusHours(3)).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createTask(new TaskBuilder(3).startTime(LocalDateTime.now().plusHours(5)).build());
        taskManager.createTask(new TaskBuilder(4).startTime(LocalDateTime.now()).build());
        taskManager.createTask(new TaskBuilder(5).startTime(LocalDateTime.now().plusHours(1)).build());
        HttpResponse<String> response = taskClient.getPrioritized();

        Assertions.assertEquals(200, response.statusCode());
        List<Task> obtainedTasks = gson.fromJson(response.body(), new TasksListTypeToken().getType());
        Assertions.assertEquals(4, obtainedTasks.size());
        Assertions.assertEquals(4, obtainedTasks.getFirst().getId());
        Assertions.assertEquals(5, obtainedTasks.get(1).getId());
        Assertions.assertEquals(2, obtainedTasks.get(2).getId());
        Assertions.assertEquals(3, obtainedTasks.getLast().getId());
    }
}
