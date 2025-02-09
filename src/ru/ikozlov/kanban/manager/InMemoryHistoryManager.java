package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> idToNodeMap = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;
    private int size = 0;

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        remove(taskId);
        Node<Task> node = new Node<>(task.copy());
        if (size == 0) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        idToNodeMap.put(taskId, node);
        size++;
    }

    @Override
    public void remove(int id) {
        Node<Task> node = idToNodeMap.remove(id);
        if (node == null) {
            return;
        }
        if (size == 1) {
            head = tail = null;
        } else if (node.prev == null) {
            node.next.prev = null;
            head = node.next;
            node.next = null;
        } else if (node.next == null) {
            node.prev.next = null;
            tail = node.prev;
            node.prev = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.next = null;
            node.prev = null;
        }
        size--;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> records = new ArrayList<>();
        Node<Task> current = tail;
        while (current != null) {
            records.add(current.data);
            current = current.prev;
        }
        return records;
    }

}

class Node<T> {
    public T data;
    public Node<T> next = null;
    public Node<T> prev = null;

    public Node(T data) {
        this.data = data;
    }
}