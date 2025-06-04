package com.takarub.bean;

import com.takarub.model.DataSearchEntity;
import com.takarub.model.DataEntity;
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
public class CampaignBean implements Serializable {

    private UIData dataTable;
    private ExcelConfig excelConfig;
    
    private List<DataEntity> data;
    private DataSearchEntity dataSearchEntity;

    @PostConstruct
    void init() {
        dataSearchEntity = new DataSearchEntity();
        data = new ArrayList<>();
        excelConfig = new ExcelConfig();
    }

    public void prepareDownload() throws IOException, InvalidFormatException {
        excelConfig.prepareDownload(dataTable,dataSearchEntity);
    }

    public UIData getDataTable() {
        return dataTable;
    }

    public void setDataTable(UIData dataTable) {
        this.dataTable = dataTable;
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

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }



    public void search() {
        System.out.println("Start date: " + dataSearchEntity.getStartDate());
        System.out.println("End date: " + dataSearchEntity.getEndDate());
        System.out.println("Category : " + dataSearchEntity.getCategory());
//        ------------------------------------------------
        LocalDate startDate = dataSearchEntity.getStartDate();
        LocalDate endDate = dataSearchEntity.getEndDate();
        String category = dataSearchEntity.getCategory();
        data = ContentDao.getInstance().getAllDataEntityTest(category,startDate,endDate);
        System.out.println("total "+ data.size());
    }

}
