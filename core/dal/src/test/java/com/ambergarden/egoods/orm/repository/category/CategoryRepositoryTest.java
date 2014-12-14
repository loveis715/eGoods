package com.ambergarden.egoods.orm.repository.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.egoods.orm.entity.category.Category;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/com/ambergarden/egoods/orm/dal-test-context.xml" })
public class CategoryRepositoryTest {
   List<Category> entitiesToBeDeleted = new ArrayList<Category>();

   private long count;
   private Integer rootId;

   @Autowired
   private CategoryRepository categoryRepo;

   @Before
   public void init() {
      // TODO: This should be some pre-defined data organized as XML, and
      // initialized by some tool. When database becomes more complex,
      // these pre-configure code becomes non-maintanable.
      Category category = new Category();
      category.setName("Root Category");
      category = categoryRepo.saveAndFlush(category);
      rootId = category.getId();

      count = categoryRepo.count();
   }

   @After
   public void restore() {
      Category rootCategory = categoryRepo.findOne(rootId);
      rootCategory = categoryRepo.findOne(rootCategory.getId());
      rootCategory.getChildren().removeAll(entitiesToBeDeleted);
      categoryRepo.save(rootCategory);

      entitiesToBeDeleted.clear();

      assertEquals("Entities created in test cases have not been cleaned up", count, categoryRepo.count());
   }

   @Test
   public void testCreate() {
      Category rootCategory = categoryRepo.findOne(rootId);

      // 1. Create a single category at a time
      Category category = createSingleCategory();
      category.setParent(rootCategory);
      category = categoryRepo.saveAndFlush(category);
      assertNotNull(category);

      entitiesToBeDeleted.add(category);

      category = categoryRepo.findOne(category.getId());
      assertNotNull(category);

      // 2. Cascading creation of category
      Category subRoot = createCategoryWithSubCategories();
      subRoot.setParent(rootCategory);
      subRoot = categoryRepo.saveAndFlush(subRoot);
      assertNotNull(subRoot);

      entitiesToBeDeleted.add(subRoot);

      subRoot = categoryRepo.findOne(subRoot.getId());
      assertNotNull(subRoot);
      assertNotNull(subRoot.getChildren());
      assertTrue(subRoot.getChildren().size() > 0);
   }

   @Test
   public void testUpdate() {
      Category rootCategory = categoryRepo.findOne(rootId);

      // 1. Update category name
      Category category = createSingleCategory();
      category.setParent(rootCategory);
      category = categoryRepo.saveAndFlush(category);
      assertNotNull(category);

      entitiesToBeDeleted.add(category);

      category = categoryRepo.findOne(category.getId());

      String updatedName = "Updated Name";
      category.setName(updatedName);
      category = categoryRepo.saveAndFlush(category);
      assertNotNull(category);
      assertEquals(updatedName, category.getName());

      // 2. Update category's children: add/remove
      Category subRoot = createCategoryWithSubCategories();
      subRoot.setParent(rootCategory);
      subRoot = categoryRepo.saveAndFlush(subRoot);
      assertNotNull(subRoot);

      entitiesToBeDeleted.add(subRoot);

      Category parent = subRoot.getParent();
      parent.getChildren().remove(subRoot);
      parent = categoryRepo.saveAndFlush(parent);
      assertNotNull(parent);
   }

   @Test
   public void testDelete() {
      Category rootCategory = categoryRepo.findOne(rootId);

      // 1. Test delete a single node
      Category category = createSingleCategory();
      category.setParent(rootCategory);
      category = categoryRepo.saveAndFlush(category);

      Integer parentId = category.getParent().getId();
      Category parent = categoryRepo.findOne(parentId);
      parent.getChildren().remove(category);
      parent = categoryRepo.saveAndFlush(parent);

      category = categoryRepo.findOne(category.getId());
      assertNull(category);

      // 2. Test delete a sub-tree
      rootCategory = categoryRepo.findOne(rootId);
      Category subCategory = createCategoryWithSubCategories();
      subCategory.setParent(rootCategory);
      subCategory = categoryRepo.saveAndFlush(subCategory);

      List<Integer> childIds = new ArrayList<Integer>();
      for (Category child : subCategory.getChildren()) {
         childIds.add(child.getId());
      }

      rootCategory = categoryRepo.findOne(rootId);
      rootCategory.getChildren().remove(subCategory);

      rootCategory = categoryRepo.saveAndFlush(rootCategory);
      subCategory = categoryRepo.findOne(subCategory.getId());
      assertNull(subCategory);
   }

   private Category createSingleCategory() {
      Category category = new Category();
      category.setName("Sub Category 1 - Test");

      return category;
   }

   private Category createCategoryWithSubCategories() {
      List<Category> children = new ArrayList<Category>();

      Category category = new Category();
      category.setName("Leaf 1 - Test");
      children.add(category);

      category = new Category();
      category.setName("Leaf 2 - Test");
      children.add(category);

      Category subRoot = new Category();
      subRoot.setName("Sub Category 2 - Test");
      subRoot.setChildren(children);

      return subRoot;
   }
}