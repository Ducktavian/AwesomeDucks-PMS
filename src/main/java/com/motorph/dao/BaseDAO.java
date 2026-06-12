package com.motorph.dao;

import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface BaseDAO<T> {
    T findById(String id);
    
    List<T> findAll();
    
    void save(T entity);
    
    void update(T entity);
    
    void delete(String id);
}
