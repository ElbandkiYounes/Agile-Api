package com.miniprojetspring.Repository;

import com.miniprojetspring.Model.ProductBacklog;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Repository
public class ProductBacklogRepositoryImp implements ProductBacklogRepository {

    @Override
    public void flush() {

    }

    @Override
    public <S extends ProductBacklog> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ProductBacklog> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<ProductBacklog> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public ProductBacklog getOne(UUID uuid) {
        return null;
    }

    @Override
    public ProductBacklog getById(UUID uuid) {
        return null;
    }

    @Override
    public ProductBacklog getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends ProductBacklog> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ProductBacklog> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends ProductBacklog> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends ProductBacklog> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ProductBacklog> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ProductBacklog> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ProductBacklog, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ProductBacklog> S save(S entity) {
        return null;
    }

    @Override
    public <S extends ProductBacklog> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<ProductBacklog> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public List<ProductBacklog> findAll() {
        return List.of();
    }

    @Override
    public List<ProductBacklog> findAllById(Iterable<UUID> uuids) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(ProductBacklog entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends ProductBacklog> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<ProductBacklog> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<ProductBacklog> findAll(Pageable pageable) {
        return null;
    }
}
