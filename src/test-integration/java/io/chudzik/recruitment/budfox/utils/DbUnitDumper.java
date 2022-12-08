package io.chudzik.recruitment.budfox.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbUnitDumper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUnitDumper.class);

    private DataSource dataSource;

    @Autowired
    public DbUnitDumper(DataSource dataSource) {
        this.dataSource = dataSource; 
    }

    public void saveDbFullDataSetToFile(String filename) {
        try {
            dumpFullDataSet(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            LOGGER.error("Cannot open file '{}'", filename, e);
        }
    }

    public void printDbFullDataSet() {
        dumpFullDataSet(System.out);
    }

    private void dumpFullDataSet(OutputStream stream) {
        try {
            IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
            IDataSet fullDataSet = connection.createDataSet();
            FlatXmlDataSet.write(fullDataSet, stream);
        } catch (DatabaseUnitException | SQLException | IOException ex) {
            LOGGER.error("Error while obtaining data from db", ex);
        }
    }

}
