package com.takarub.dao;

import com.takarub.bean.DataEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aalnadi
 */
public class ContentDao extends Dao {

    private static ContentDao contentDao;

    private ContentDao() {

    }

    public static ContentDao getInstance() {
        if (contentDao == null) {
            contentDao = new ContentDao();
        }
        return contentDao;
    }

    public List<DataEntity> getAllDataEntity(Timestamp start, Timestamp end) {
        ResultSet rs;
        PreparedStatement ps;
        Connection con = null;

        List<DataEntity> contents = new ArrayList<>();
        DataEntity content;
        try {
            con = getConnection("jdbc/operator1");
            String sql = "SELECT * FROM takarub_competition.CODES where COMPETITION_ID = 100 and flag =1 and SENT_DATE between ? and ? order by 3 asc";

            ps = con.prepareStatement(sql);

            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);

//            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                content = new DataEntity();
//                content.setId(rs.getInt("id"));
                content.setCode(rs.getString("Code"));
                content.setSentDate(rs.getTimestamp("sent_Date"));
                content.setCompetitionId(rs.getString("competition_Id"));
                content.setMsisdn(rs.getString("Msisdn"));

                contents.add(content);
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(ContentDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(con);
        }

        return contents;
    }

    public List<DataEntity> getAllDataEntityTest(String category, LocalDate startDate, LocalDate endDate) {
        ResultSet rs;
        PreparedStatement ps;
        Connection con = null;

        List<DataEntity> contents = new ArrayList<>();
        DataEntity content;
        try {
            con = getConnection("jdbc/operator1");

            String sql = "SELECT * FROM takarub_competition.CODES where COMPETITION_ID = ? and flag =1 and SENT_DATE between ? and ? order by 3 asc";

            ps = con.prepareStatement(sql);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Calendar cal = Calendar.getInstance();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

// --- Start Date ---
            String startDateString = startDate.format(formatter);
            cal.setTime(dateFormat.parse(startDateString));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Timestamp start = new Timestamp(cal.getTimeInMillis());

// --- End Date ---
            String endDateString = endDate.format(formatter);
            cal.setTime(dateFormat.parse(endDateString));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Timestamp end = new Timestamp(cal.getTimeInMillis());

            ps.setString(1, category);
            ps.setTimestamp(2, start);
            ps.setTimestamp(3, end);

            rs = ps.executeQuery();
            while (rs.next()) {
                content = new DataEntity();
                content.setCode(rs.getString("Code"));
                content.setSentDate(rs.getTimestamp("sent_Date"));
                content.setCompetitionId("أرز تايجر");
                content.setMsisdn(rs.getString("Msisdn"));

                contents.add(content);
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(ContentDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(con);
        }

        return contents;
    }

    public List<Date> getAllDistinctSentDates() {
        ResultSet rs;
        PreparedStatement ps;
        Connection con = null;

        List<Date> sentDates = new ArrayList<>();

        try {
            con = getConnection("jdbc/operator1");
            String sql = "SELECT DISTINCT sent_date FROM takarub_competition.CODES ORDER BY sent_date ASC";

            ps = con.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {

                sentDates.add(rs.getDate("sent_date"));
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(ContentDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(con);
        }

        return sentDates;
    }

}
