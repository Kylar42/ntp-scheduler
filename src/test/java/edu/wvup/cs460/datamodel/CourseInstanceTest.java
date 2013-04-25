package edu.wvup.cs460.datamodel;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class CourseInstanceTest {

    @Test
    public void testCourseInstance(){
        String crn = "5200";
        String type = "Online";
        boolean crossListed = true;
        String subject="ENG";
        String courseNumber = "403";
        String courseTitle = "Children's Literature";
        short credits = 3;
        String days = "MWF";
        String time = "10:30-12:00";
        String instructor = "T. Chapman";
        String room = "C123";
        Date startDate = new Date();
        Date endDate = new Date();
        short seatsAvail = 19;
        String termLength = "Full Term";
        String campus = "Main Campus";
        String term = "Fall";
        String termYear = "2013";
        CourseInstance instance = new CourseInstance(
            crn, type, crossListed, subject, courseNumber, courseTitle, credits, days, time, instructor,
            room, startDate, endDate, seatsAvail, termLength, campus, term, termYear
        );

        CourseInstance instance2 = new CourseInstance(
            crn, type, crossListed, subject, courseNumber, courseTitle, credits, days, time, instructor,
            room, startDate, endDate, seatsAvail, termLength, campus, term, termYear
        );

        assertEquals(crn, instance.getCrn());
        assertEquals(type, instance.getType());
        assertEquals(crossListed, instance.isCrosslisted());
        assertEquals(subject, instance.getSubject());
        assertEquals(courseNumber, instance.getCourseNumber());
        assertEquals(courseTitle, instance.getCourseTitle());
        assertEquals(credits, instance.getCredits());
        assertEquals(days, instance.getDays());
        assertEquals(time, instance.getTime());
        assertEquals(instructor, instance.getInstructor());
        assertEquals(room, instance.getRoom());
        assertEquals(startDate, instance.getStartDate());
        assertEquals(endDate, instance.getEndDate());
        assertEquals(seatsAvail, instance.getSeatsAvail());
        assertEquals(termLength, instance.getTermLength());
        assertEquals(campus, instance.getCampus());
        assertEquals(term, instance.getTerm());
        assertEquals(termYear, instance.getTermYear());

        assertEquals(0, instance.compareTo(instance2));//should be same, comparatively speaking.

        assertNotNull(instance.toString());//should be good, more for test completeness.

    }

    @Test
    public void testMustHaveCRN(){
        Exception e = null;

        try {
            CourseInstance ci = new CourseInstance(null,null, false, null,null,null,(short)1,null,null,null,
                                                   null,null,null, (short)1,null,null,null,null );
        } catch (IllegalArgumentException e1) {
            e = e1;
        }

        assertNotNull(e);

    }

}
