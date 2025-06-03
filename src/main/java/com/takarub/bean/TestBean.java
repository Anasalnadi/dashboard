package com.takarub.bean;

import com.takarub.config.ExcelConfig;
import com.takarub.dao.ContentDao;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author aalnadi
 */
@ManagedBean
@ViewScoped
public class TestBean implements Serializable {

    private UIData dataTable;
    private List<DataEntity> test;

    private ExcelConfig excelConfig;

    private DataSearchEntity dataSearchEntity;

    @PostConstruct
    void init() {
        dataSearchEntity = new DataSearchEntity();
        test = new ArrayList<>();
        excelConfig = new ExcelConfig();
        
//        test = ContentDao.getInstance().getAllDataEntityTest();
//        System.out.println("total : " + test.size());
            
    }

    public void prepareDownload() throws IOException, InvalidFormatException {
        excelConfig.prepareDownload(dataTable);
    }

    public void downloadDataTableData(String filename, UIData dataTable) throws IOException, InvalidFormatException {
        excelConfig.downloadDataTableData(filename, dataTable);
    }

    public UIData getDataTable() {
        return dataTable;
    }

    public void setDataTable(UIData dataTable) {
        this.dataTable = dataTable;
    }

    public List<DataEntity> getTest() {
        return test;
    }

    public void setTest(List<DataEntity> test) {
        this.test = test;
    }

    public ExcelConfig getExcelConfig() {
        return excelConfig;
    }

    public void setExcelConfig(ExcelConfig excelConfig) {
        this.excelConfig = excelConfig;
    }

    public DataSearchEntity getDataSearchEntity() {
        return dataSearchEntity;
    }

    public void setDataSearchEntity(DataSearchEntity dataSearchEntity) {
        this.dataSearchEntity = dataSearchEntity;
    }



    public void search() {
        System.out.println("Start date: " + dataSearchEntity.getStartDate());
        System.out.println("End date: " + dataSearchEntity.getEndDate());
        System.out.println("Category : " + dataSearchEntity.getCategory());
//        ------------------------------------------------
        LocalDate startDate = dataSearchEntity.getStartDate();
        LocalDate endDate = dataSearchEntity.getEndDate();
        String category = dataSearchEntity.getCategory();
        test = ContentDao.getInstance().getAllDataEntityTest(category,startDate,endDate);
        System.out.println("total "+ test.size());
    }

}
