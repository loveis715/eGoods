package com.ambergarden.egoods.orm.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ambergarden.egoods.orm.DatabaseInitializer;

public class DatabaseInitializerRunner {
   final static Logger logger = LoggerFactory.getLogger(DatabaseInitializerRunner.class);

   final static String DATA_INIT_CONTEXT_FILE = "/config/data-init-context.xml";

   final static String[] CONFIG_LOCATIONS = {
      "/META-INF/spring/db/dal-datasource-context.xml",
      "/META-INF/spring/db/data-init-context.xml"
      };

   public static void main(String[] args) {
      List<String> configLocations = new ArrayList<String>(Arrays.asList(CONFIG_LOCATIONS));
      configLocations.add(DATA_INIT_CONTEXT_FILE);

      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
      ((AbstractXmlApplicationContext) context).setConfigLocations(configLocations.toArray(new String[] {}));
      ((AbstractXmlApplicationContext) context).refresh();

      DatabaseInitializer initializer = context.getBean(DatabaseInitializer.class);

      try {
         initializer.init();

         initializer.resetSequences();
      } catch (Exception e) {
         logger.error(e.getMessage(), e);
      } finally {
         context.close();
      }
   }
}