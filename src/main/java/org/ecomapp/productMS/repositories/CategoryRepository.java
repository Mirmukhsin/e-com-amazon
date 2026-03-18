package org.ecomapp.productMS.repositories;

import org.ecomapp.productMS.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByParentId(Long categoryId);

    boolean existsByParent_Id(Long parentId);
}
