package com.ambergarden.egoods.orm;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
   final static Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

   private static final String DATASET_NULL = "[NULL]";

   private final DataSource dataSource;
   private final IDataTypeFactory dataTypeFactory;
   private List<Object[]> dataSets;

   @Autowired(required = false)
   private List<DataInitializerBean> dataInitializers;

   @Autowired
   public DatabaseInitializer(DataSource dataSource,
         IDataTypeFactory dataTypeFactory) throws SQLException {
      this.dataSource = dataSource;
      this.dataTypeFactory = dataTypeFactory;
   }

   /**
    * Set XML data locations.
    */
   public void setDataInitializers(List<DataInitializerBean> dataInitializers) {
      this.dataInitializers = dataInitializers;
   }

   /**
    * Initialize test data.
    */
   @PostConstruct
   public void init() throws DatabaseUnitException, SQLException {
      if (dataInitializers != null) {
         logger.info("Processing data configuration.");

         try {
            if (dataSets == null) {
               initializeDataSets();
            }

            processInit(dataSets);
         } catch (Throwable t) {
            logger.error("Error while initializing test data, message: {}", t.getMessage());
            throw new DatabaseUnitRuntimeException("Unable to initialize dataset correctly", t);
         }
      } else {
         logger.info("Not intializing any default data.");
      }
   }

   /**
    * Reset sequences for a database.
    */
   public void resetSequences() {
      Connection connection = null;
      ResultSet resultSet = null;

      try {
         connection = dataSource.getConnection();

         DatabaseMetaData metadata = connection.getMetaData();
         resultSet = metadata.getTables(null, null, "%", new String[] { "TABLE" });

         while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");

            String sql = "SELECT setval('" + tableName + "_ID_SEQ', (select max(id) + 1 from " + tableName
                  + "), false)";

            // Ignore errors (table might not have a sequence
            try {
               connection.createStatement().execute(sql);
            } catch (Exception e) {
            }
         }
      } catch (SQLException e) {
         logger.error(e.getMessage(), e);
      } finally {
         try {
            resultSet.close();
         } catch (Exception e) {
         }
         try {
            connection.close();
         } catch (Exception e) {
         }
      }

      logger.info("DB sequences reset.");
   }

   private void initializeDataSets() throws IOException, DataSetException {
      dataSets = new ArrayList<Object[]>();
      for (DataInitializerBean initializer : dataInitializers) {
         logger.info("Initializing data from '{}' with {}.", initializer.getResource(),
               initializer.getOperation());

         IDataSet dataSet = null;
         if (initializer.isFlatXmlDataSet()) {
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);

            InputStream input = null;
            try {
               input = initializer.getResource().getInputStream();
               dataSet = builder.build(input);
            } finally {
               IOUtils.closeQuietly(input);
            }
         } else {
            InputStream input = null;
            try {
               input = initializer.getResource().getInputStream();
               dataSet = new XmlDataSet(input);
            } finally {
               IOUtils.closeQuietly(input);
            }
         }

         ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
         replacementDataSet.addReplacementObject(DATASET_NULL, null);

         dataSets.add(new Object[] { replacementDataSet, initializer });
      }

      logger.info("Test data initialized.");
   }

   private void processInit(List<Object[]> dataSets) throws DatabaseUnitException, SQLException, IOException {
      DatabaseDataSourceConnection connection = createConnection();

      for (Object[] dataPair : dataSets) {
         IDataSet dataSet = (IDataSet) dataPair[0];
         DataInitializerBean data = (DataInitializerBean) dataPair[1];
         logger.info("Loading data from " + data.getResource().getFilename());
         if (DatabaseOperation.CLEAN_INSERT.equals(data.getOperation())) {
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
         } else if (DatabaseOperation.UPDATE.equals(data.getOperation())) {
            DatabaseOperation.UPDATE.execute(connection, dataSet);
         } else if (DatabaseOperation.INSERT.equals(data.getOperation())) {
            DatabaseOperation.INSERT.execute(connection, dataSet);
         } else if (DatabaseOperation.REFRESH.equals(data.getOperation())) {
            DatabaseOperation.REFRESH.execute(connection, dataSet);
         }
      }

      logger.info("Test data processed.");
   }

   private DatabaseDataSourceConnection createConnection() throws SQLException {
      DatabaseDataSourceConnection connection = new DatabaseDataSourceConnection(dataSource);
      connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
      return connection;
   }
}