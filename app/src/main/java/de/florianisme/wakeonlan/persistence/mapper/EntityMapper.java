package de.florianisme.wakeonlan.persistence.mapper;

public interface EntityMapper<M, E> {

    M entityToModel(E entity);

    E modelToEntity(M model);

}
