package com.ambergarden.egoods.orm.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@MappedSuperclass
public abstract class AbstractVersionedEntity {

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   private int id = -1;

   @Version
   protected int lockVersion;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getLockVersion() {
      return lockVersion;
   }

   public void setLockVersion(int lockVersion) {
      this.lockVersion = lockVersion;
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder()
         .append(this.getId())
         .append(this.getClass())
         .toHashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      AbstractVersionedEntity entity = (AbstractVersionedEntity) obj;
      return new EqualsBuilder()
         .append(this.getId(), entity.getId())
         .isEquals();
   }
}