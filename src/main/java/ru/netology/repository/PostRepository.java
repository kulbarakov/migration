package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepository {
    private final long INITIAL_COUNT = 1;
    private final ConcurrentHashMap<Long, Post> map;
    private final AtomicLong counter;

    public PostRepository() {
        map = new ConcurrentHashMap<>();
        counter = new AtomicLong(INITIAL_COUNT);
    }

    public List<Post> all() {
        return map.values().stream().filter(value -> !value.isRemoved()).collect(Collectors.toList());
    }

    public Optional<Post> getById(long id) {
        var post = map.get(id);
        if (post != null && !post.isRemoved()) return Optional.of(post);
        else return Optional.empty();
    }

    public Post save(Post post) {
        long id = post.getId();
        if (id == 0) {
            long postId = counter.getAndIncrement();
            post.setId(postId);
            map.put(postId, post);
            return post;
        }
        if (map.containsKey(id) && !map.get(id).isRemoved()) {
            map.put(id, post);
            return post;
        }
        throw new NotFoundException();
    }

    public void removeById(long id) {
        var post = map.get(id);
        if (post == null) {
            throw new NotFoundException();
        } else {
            post.setRemoved(true);
        }
    }
}
