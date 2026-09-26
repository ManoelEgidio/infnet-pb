package br.com.manoelegidio.tp5.taskmanager.domain.model;

import br.com.manoelegidio.tp5.taskmanager.domain.exception.ValidationException;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class TaskTitle {

    private String value;

    protected TaskTitle() {
        // Exigido pelo JPA
    }

    public TaskTitle(String value) {
        validate(value);
        this.value = value.trim();
    }

    private void validate(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("O título da tarefa não pode ser vazio.");
        }
        if (title.trim().length() < 3) {
            throw new ValidationException("O título da tarefa deve ter no mínimo 3 caracteres.");
        }
        if (title.trim().length() > 100) {
            throw new ValidationException("O título da tarefa deve ter no máximo 100 caracteres.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskTitle taskTitle = (TaskTitle) o;
        return Objects.equals(value, taskTitle.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
