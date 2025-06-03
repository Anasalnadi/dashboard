package com.takarub.bean;

import com.takarub.dao.ContentDao;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@ManagedBean
@ViewScoped
public class DataBean {

    private Date startDate;
    private Date endDate;

    private final List<DateRange> customDateRanges = new ArrayList<>();
    private DateRange tempDateRange = new DateRange();

    private List<DataEntity> test;

    @PostConstruct
    void init() {
        test = new ArrayList<>();
    }

    public void generateExcel() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        Workbook workbook = new XSSFWorkbook();

        // Organize data by SENT_DATE manually
        Map<String, List<DataEntity>> groupedData = new LinkedHashMap<>();
        for (DataEntity item : test) {
            Timestamp sentDate = item.getSentDate();
            if (!groupedData.containsKey(sentDate)) {
                groupedData.put(sentDate.toString(), new ArrayList<DataEntity>());
            }
            groupedData.get(sentDate).add(item);
        }

        Iterator<Map.Entry<String, List<DataEntity>>> iterator = groupedData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<DataEntity>> entry = iterator.next();
            String dateKey = entry.getKey();
            List<DataEntity> rows = entry.getValue();

            Sheet sheet = workbook.createSheet(dateKey);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"CODE", "SENT_DATE", "COMPETITION_ID", "MSISDN"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Fill Data
            int rowNum = 1;
            for (int i = 0; i < rows.size(); i++) {
                DataEntity row = rows.get(i);
                Row excelRow = sheet.createRow(rowNum++);
                excelRow.createCell(0).setCellValue(row.getCode());
                excelRow.createCell(1).setCellValue(row.getSentDate());
                excelRow.createCell(2).setCellValue(row.getCompetitionId());
                excelRow.createCell(3).setCellValue(row.getMsisdn());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Configure Response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=organized_data.xlsx");

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            workbook.write(out);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        workbook.close();
        facesContext.responseComplete();
    }

    private Timestamp toTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public void downloadExcel() {
        Timestamp startTimestamp = toTimestamp(startDate);
        Timestamp endTimestamp = toTimestamp(endDate);

        test = ContentDao.getInstance().getAllDataEntity(startTimestamp, endTimestamp);
        System.out.println("test size : " + test.size());
    }

    public void validateEndDate() {
        Timestamp startTimestamp = toTimestamp(startDate);
        Timestamp endTimestamp = toTimestamp(endDate);

        if (startTimestamp != null && endTimestamp != null && endTimestamp.before(startTimestamp)) {
            FacesContext.getCurrentInstance().addMessage("form:messages",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Main End Date must be after Main Start Date."));
            endDate = null;
        }
    }

    public void saveDateRange() {
        if (tempDateRange.getStartDate() == null || tempDateRange.getEndDate() == null) {
            addErrorMessage("Validation Error", "Both start and end dates are required!");
            return;
        }

        if (tempDateRange.getEndDate().before(tempDateRange.getStartDate())) {
            FacesContext.getCurrentInstance().addMessage("form:messages",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Custom End Date must be after Custom Start Date."));
            return;
        }

        Timestamp startTimestamp = toTimestamp(startDate);
        Timestamp endTimestamp = toTimestamp(endDate);
        // Ensure main date range is set
        if (startTimestamp == null || endTimestamp == null) {
            addErrorMessage("Validation Error", "Main start and end dates must be set before adding a custom range.");
            return;
        }

        // Validate the custom date range is within the main range
        if (tempDateRange.getStartDate().before(startDate) || tempDateRange.getEndDate().after(endDate)) {
            addErrorMessage("Invalid Date Range", "Custom date range must be within the main start and end date range.");
            return;
        }

        // Add to the list and reset the temporary range
        customDateRanges.add(tempDateRange);
        tempDateRange = new DateRange();
    }

    public void removeDateRange(int index) {
        if (index >= 0 && index < customDateRanges.size()) {
            customDateRanges.remove(index);
        }
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    // Getters & Setters
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<DateRange> getCustomDateRanges() {
        return customDateRanges;
    }

    public DateRange getTempDateRange() {
        return tempDateRange;
    }

    public void setTempDateRange(DateRange tempDateRange) {
        this.tempDateRange = tempDateRange;
    }

    public List<DataEntity> getTest() {
        return test;
    }

    public void setTest(List<DataEntity> test) {
        this.test = test;
    }

}
