package io.chudzik.recruitment.budfox.commons.tests

import org.dbunit.DatabaseUnitException
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import javax.sql.DataSource
import java.sql.SQLException

@Component
@TypeChecked
@CompileStatic
@Slf4j
class DbUnitDumper {

    private DataSource dataSource


    @Autowired
    DbUnitDumper(DataSource dataSource) {
        this.dataSource = dataSource
    }


    void saveDbFullDataSetToFile(String filename) {
        try {
            dumpFullDataSet(new FileOutputStream(filename))
        } catch (FileNotFoundException e) {
            log.error("Cannot open file '{}'", filename, e)
        }
    }

    void printDbFullDataSet() {
        dumpFullDataSet(System.out)
    }

    private void dumpFullDataSet(OutputStream stream) {
        try {
            IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection())
            IDataSet fullDataSet = connection.createDataSet()
            FlatXmlDataSet.write(fullDataSet, stream)
        } catch (DatabaseUnitException | SQLException | IOException ex) {
            log.error("Error while obtaining data from db", ex)
        }
    }

}
