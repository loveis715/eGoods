package com.ambergarden.egoods.orm;

import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.Resource;

/**
 * Record the information for all datasets we'd want to import
 */
public class DataInitializerBean {

   private DatabaseOperation opearation;
   private boolean flatXmlDataSet = true;
   private Resource resource;

   /**
    * Get the database operation to perform.
    */
   public DatabaseOperation getOperation() {
      return opearation;
   }

   /**
    * Set the database operation to perform.
    */
   public void setOperation(DatabaseOperation opearation) {
      this.opearation = opearation;
   }

   /**
    * Get whether this is a <code>FlatXmlDataSet</code> (default) or <code>XmlDataSet</code>
    */
   public boolean isFlatXmlDataSet() {
      return flatXmlDataSet;
   }

   /**
    * Set whether or not this is a <code>FlatXmlDataSet</code> (default) or <code>XmlDataSet</code>
    */
   public void setFlatXmlDataSet(boolean flatXmlDataSet) {
      this.flatXmlDataSet = flatXmlDataSet;
   }

   /**
    * Get the location of the resource file
    */
   public Resource getResource() {
      return resource;
   }

   /**
    * Set the location of the resource file
    */
   public void setResource(Resource resource) {
      this.resource = resource;
   }
}