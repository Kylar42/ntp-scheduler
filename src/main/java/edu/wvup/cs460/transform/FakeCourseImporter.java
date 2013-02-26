package edu.wvup.cs460.transform;

import edu.wvup.cs460.datamodel.Course;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class FakeCourseImporter implements CourseImporter {
    @Override
    public List<Course> getCourses() {
        List<Course> toReturn = new ArrayList<Course>();
        try {
            File f = new File("main/resources/temp-course-list.txt");
            FileReader reader = new FileReader(f);
            BufferedReader buff = new BufferedReader(reader);

            String line = buff.readLine();

            while (null != line) {
                System.out.println(line);

                String[] splits = line.split(",");
                String courseTitle = splits[0].trim();
                String crn = splits[1].trim();
                String tmp = splits[2].trim();
                splits = tmp.split(" ");
                String subject = splits[0].trim();
                String courseNum = splits[1].trim();
                Course course = new Course(crn, "Lec", false, subject, courseNum, courseTitle, (short) 3, "M W", "09:00-12:00 AM",
                                           "Ambrozy", "C124 (CAPC)", new Date(2012, 8, 12), new Date(2012, 12, 18),
                                           "FULL TERM (20-AUG-12 - 14-DEC-12)",
                                           "Main");

                toReturn.add(course);

                line = buff.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();//for now. fake, after all.
        }

        return toReturn;
    }
}
