package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Todo;
import com.mycompany.myapp.repository.TodoRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Todo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TodoResource {

    private final Logger log = LoggerFactory.getLogger(TodoResource.class);

    private static final String ENTITY_NAME = "todo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TodoRepository todoRepository;

    public TodoResource(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * {@code POST  /todos} : Create a new todo.
     *
     * @param todo the todo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new todo, or with status {@code 400 (Bad Request)} if the todo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) throws URISyntaxException {
        log.debug("REST request to save Todo : {}", todo);
        if (todo.getId() != null) {
            throw new BadRequestAlertException("A new todo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Todo result = todoRepository.save(todo);
        return ResponseEntity
            .created(new URI("/api/todos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /todos/:id} : Updates an existing todo.
     *
     * @param id the id of the todo to save.
     * @param todo the todo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated todo,
     * or with status {@code 400 (Bad Request)} if the todo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the todo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/todos/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Todo todo)
        throws URISyntaxException {
        log.debug("REST request to update Todo : {}, {}", id, todo);
        if (todo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, todo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!todoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Todo result = todoRepository.save(todo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, todo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /todos/:id} : Partial updates given fields of an existing todo, field will ignore if it is null
     *
     * @param id the id of the todo to save.
     * @param todo the todo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated todo,
     * or with status {@code 400 (Bad Request)} if the todo is not valid,
     * or with status {@code 404 (Not Found)} if the todo is not found,
     * or with status {@code 500 (Internal Server Error)} if the todo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/todos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Todo> partialUpdateTodo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Todo todo
    ) throws URISyntaxException {
        log.debug("REST request to partial update Todo partially : {}, {}", id, todo);
        if (todo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, todo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!todoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Todo> result = todoRepository
            .findById(todo.getId())
            .map(
                existingTodo -> {
                    if (todo.getName() != null) {
                        existingTodo.setName(todo.getName());
                    }
                    if (todo.getDescription() != null) {
                        existingTodo.setDescription(todo.getDescription());
                    }
                    if (todo.getCreatedAt() != null) {
                        existingTodo.setCreatedAt(todo.getCreatedAt());
                    }
                    if (todo.getUpdatedAt() != null) {
                        existingTodo.setUpdatedAt(todo.getUpdatedAt());
                    }

                    return existingTodo;
                }
            )
            .map(todoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, todo.getId().toString())
        );
    }

    /**
     * {@code GET  /todos} : get all the todos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of todos in body.
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> getAllTodos(Pageable pageable) {
        log.debug("REST request to get a page of Todos");
        Page<Todo> page = todoRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /todos/:id} : get the "id" todo.
     *
     * @param id the id of the todo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the todo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> getTodo(@PathVariable Long id) {
        log.debug("REST request to get Todo : {}", id);
        Optional<Todo> todo = todoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(todo);
    }

    /**
     * {@code DELETE  /todos/:id} : delete the "id" todo.
     *
     * @param id the id of the todo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        log.debug("REST request to delete Todo : {}", id);
        todoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
