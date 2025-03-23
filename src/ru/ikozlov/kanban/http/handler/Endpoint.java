package ru.ikozlov.kanban.http.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Endpoint {
    private static final Pattern tasksIdPattern = Pattern.compile("^/tasks/(?<taskId>\\d+)$");
    private static final Pattern subtasksIdPattern = Pattern.compile("^/subtasks/(?<taskId>\\d+)$");
    private static final Pattern epicIdPattern = Pattern.compile("^/epics/(?<taskId>\\d+)$");
    private static final Pattern epicIdSubtasksPattern = Pattern.compile("^/epics/(?<taskId>\\d+)/subtasks$");
    protected final Type type;
    protected final Integer taskId;

    private Endpoint(Type type, Integer taskId) {
        this.type = type;
        this.taskId = taskId;
    }

    public static Endpoint create(String method, String path) {
        Matcher tasksIdMatcher = tasksIdPattern.matcher(path);
        Matcher subtasksIdMatcher = subtasksIdPattern.matcher(path);
        Matcher epicsIdMatcher = epicIdPattern.matcher(path);
        Matcher epicIdSubtasksMatcher = epicIdSubtasksPattern.matcher(path);
        if (path.equals("/tasks")) {
            if (method.equals("GET")) {
                return new Endpoint(Type.GET_TASKS, null);
            }
            if (method.equals("POST")) {
                return new Endpoint(Type.POST_TASK, null);
            }
        } else if (tasksIdMatcher.matches()) {
            int taskId = Integer.parseInt(tasksIdMatcher.group("taskId"));
            if (method.equals("GET")) {
                return new Endpoint(Type.GET_TASK_ID, taskId);
            }
            if (method.equals("DELETE")) {
                return new Endpoint(Type.DELETE_TASK_ID, taskId);
            }
        } else if (path.equals("/subtasks")) {
            if (method.equals("GET")) {
                return new Endpoint(Type.GET_SUBTASKS, null);
            }
            if (method.equals("POST")) {
                return new Endpoint(Type.POST_SUBTASK, null);
            }
        } else if (subtasksIdMatcher.matches()) {
            int taskId = Integer.parseInt(subtasksIdMatcher.group("taskId"));
            if (method.equals("GET")) {
                return new Endpoint(Type.GET_SUBTASK_ID, taskId);
            }
            if (method.equals("DELETE")) {
                return new Endpoint(Type.DELETE_SUBTASK_ID, taskId);
            }
        } else if (path.equals("/epics")) {
            if (method.equals("GET")) {
                return new Endpoint(Type.GET_EPICS, null);
            }
            if (method.equals("POST")) {
                return new Endpoint(Type.POST_EPIC, null);
            }
        } else if (epicsIdMatcher.matches()) {
            int taskId = Integer.parseInt(epicsIdMatcher.group("taskId"));
            if (method.equals("GET")) {
                return new Endpoint(Type.GET_EPIC_ID, taskId);
            }
            if (method.equals("DELETE")) {
                return new Endpoint(Type.DELETE_EPIC_ID, taskId);
            }
        } else if (epicIdSubtasksMatcher.matches() && method.equals("GET")) {
            int taskId = Integer.parseInt(epicIdSubtasksMatcher.group("taskId"));
            return new Endpoint(Type.GET_EPIC_ID_SUBTASKS, taskId);
        } else if (path.equals("/history")) {
            return new Endpoint(Type.GET_HISTORY, null);
        } else if (path.equals("/prioritized")) {
            return new Endpoint(Type.GET_PRIORITIZED, null);
        }
        return new Endpoint(Type.UNKNOWN, null);
    }

    public enum Type {
        GET_TASKS,
        GET_TASK_ID,
        POST_TASK,
        DELETE_TASK_ID,
        GET_SUBTASKS,
        GET_SUBTASK_ID,
        POST_SUBTASK,
        DELETE_SUBTASK_ID,
        GET_EPICS,
        GET_EPIC_ID,
        GET_EPIC_ID_SUBTASKS,
        POST_EPIC,
        DELETE_EPIC_ID,
        GET_HISTORY,
        GET_PRIORITIZED,
        UNKNOWN
    }
}
