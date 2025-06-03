package com.takarub.bean;

import java.time.LocalDate;

/**
 *
 * @author aalnadi
 */
public class DataSearchEntity {
    
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    
}
