package com.ambergarden.egoods.orm.entity.category;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.ambergarden.egoods.orm.entity.AbstractVersionedEntity;

@Entity
public class Category extends AbstractVersionedEntity {
   private String name;

   @ManyToOne
   @JoinColumn(name="PARENT_CATEGORY_ID")
   private Category parent;

   // We will retrieve all categories and organized them as a tree,
   // so retrieve them eagerly.
   @OneToMany(mappedBy="parent", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
   private List<Category> children;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Category getParent() {
      return parent;
   }

   public void setParent(Category parent) {
      this.parent = parent;
   }

   public List<Category> getChildren() {
      return children;
   }

   public void setChildren(List<Category> children) {
      this.children = children;

      // TODO: In case that someone missed to set the parent for these children
      // Searched from web, seems there's no better way mentioned. Still need
      // to investigate, like several "Patterns" book for database
      for (Category child : children) {
         child.setParent(this);
      }
   }
}