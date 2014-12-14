package com.ambergarden.egoods.orm.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ambergarden.egoods.orm.entity.category.Category;

/**
 * Repository to retrieve categories.
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {
   // TODO: Need to be refined. In current implementation, we have just return
   // the root category and expand it cascadingly. That is not efficient.
   @Query("SELECT n FROM Category n WHERE parent_category_id is NULL")
   Category findRootCategory();
}