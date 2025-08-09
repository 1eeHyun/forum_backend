package com.example.forum.service.tag;

import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostTag;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.tag.Tag;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.repository.post.PostTagRepository;
import com.example.forum.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaggingServiceImpl implements TaggingService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostRepository postRepository;

    private static final int MAX_TAGS_PER_POST = 5;
    private static final int MAX_TAG_LENGTH = 50;

    @Override
    @Transactional
    public void setTagsForPost(Long postId, List<String> rawTags) {
        Post post = postRepository.getReferenceById(postId);
        // PUBLIC
        if (post.getVisibility() != Visibility.PUBLIC) {
            throw new IllegalStateException("Tags can only be edited on PUBLIC posts.");
        }

        List<String> names = normalize(rawTags).stream()
                .distinct()
                .limit(MAX_TAGS_PER_POST)
                .toList();

        Map<String, Tag> byName = loadOrCreate(names);

        List<PostTag> current = postTagRepository.findByPostIdWithTag(postId);
        Set<Long> currentIds = current.stream().map(pt -> pt.getTag().getId()).collect(Collectors.toSet());
        Set<Long> nextIds = names.stream().map(n -> byName.get(n).getId()).collect(Collectors.toSet());

        // Remove
        Set<Long> remove = new HashSet<>(currentIds);
        remove.removeAll(nextIds);
        if (!remove.isEmpty()) {
            postTagRepository.deleteByPostIdAndTagIdIn(postId, remove);
        }

        // Add
        Set<Long> add = new HashSet<>(nextIds);
        add.removeAll(currentIds);
        if (!add.isEmpty()) {
            List<PostTag> toSave = add.stream().map(tagId -> {
                PostTag pt = new PostTag();
                pt.setPost(post);
                Tag t = new Tag(); t.setId(tagId);
                pt.setTag(t);
                return pt;
            }).toList();
            postTagRepository.saveAll(toSave);
        }
    }

    @Override
    @Transactional
    public void addTagsToPost(Long postId, List<String> rawTags) {
        Post post = postRepository.getReferenceById(postId);
        if (post.getVisibility() != Visibility.PUBLIC) {
            throw new IllegalStateException("Tags can only be edited on PUBLIC posts.");
        }

        List<String> names = normalize(rawTags).stream().distinct().toList();

        int currentCount = (int) postTagRepository.countByPostId(postId);
        int canAdd = Math.max(0, MAX_TAGS_PER_POST - currentCount);
        if (canAdd == 0) return;

        names = names.stream().limit(canAdd).toList();
        Map<String, Tag> byName = loadOrCreate(names);

        for (Tag tag : byName.values()) {
            if (!postTagRepository.existsByPostIdAndTagId(postId, tag.getId())) {
                PostTag pt = new PostTag();
                pt.setPost(post);
                pt.setTag(tag);
                postTagRepository.save(pt);
            }
        }
    }

    @Override
    @Transactional
    public void removeTagsFromPost(Long postId, List<String> rawTags) {
        Post post = postRepository.getReferenceById(postId);
        if (post.getVisibility() != Visibility.PUBLIC) {
            throw new IllegalStateException("Tags can only be edited on PUBLIC posts.");
        }

        List<String> names = normalize(rawTags);
        if (names.isEmpty()) return;

        List<Tag> targets = tagRepository.findByNameIn(names);
        if (targets.isEmpty()) return;

        postTagRepository.deleteByPostIdAndTagIdIn(
                postId,
                targets.stream().map(Tag::getId).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getTagNamesForPost(Long postId) {
        return postTagRepository.findByPostIdWithTag(postId).stream()
                .map(pt -> pt.getTag().getName())
                .sorted()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPostsByTag(String tag, Pageable pageable) {
        return postTagRepository.findPostsByTag(tag, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggest(String q, int limit) {
        if (q == null || q.isBlank()) return List.of();
        String norm = normalizeToken(q);
        return tagRepository
                .findByNameStartingWithIgnoreCase(norm, PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(Tag::getName)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> topTags(int limit) {
        return postTagRepository.findTopTagNames(Math.max(1, limit));
    }

    /* ===== helper methods ===== */

    private Map<String, Tag> loadOrCreate(List<String> names) {
        if (names.isEmpty()) return Map.of();

        Map<String, Tag> existing = tagRepository.findByNameIn(names).stream()
                .collect(Collectors.toMap(Tag::getName, t -> t));

        for (String n : names) {
            if (existing.containsKey(n)) continue;
            try {
                Tag t = new Tag(); t.setName(n);
                tagRepository.saveAndFlush(t);
                existing.put(n, t);
            } catch (DataIntegrityViolationException e) {
                tagRepository.findByNameIgnoreCase(n).ifPresent(t -> existing.put(n, t));
            }
        }
        return existing;
    }

    private List<String> normalize(List<String> input) {
        if (input == null) return List.of();
        return input.stream()
                .map(this::normalizeToken)
                .filter(s -> !s.isBlank())
                .map(s -> s.length() > MAX_TAG_LENGTH ? s.substring(0, MAX_TAG_LENGTH) : s)
                .toList();
    }

    private String normalizeToken(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase();
        t = t.replaceAll("\\s+", "-");
        t = t.replaceAll("[^a-z0-9-]", "");
        return t;
    }
}
