package edu.wvup.cs460.transform;

import edu.wvup.cs460.datamodel.CourseInstance;
import edu.wvup.cs460.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class CSVCourseImporter implements CourseImporter {
    private static Logger LOG = LoggerFactory.getLogger(CSVCourseImporter.class);

    @Override
    public List<CourseImportContext> getCourses(List<Tuple<String, String>> urlCacheSigs){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            LOG.error("Unable to create Digest for MD5.");
            throw new IllegalArgumentException(e);//need to fail the method.
        }


        List<CourseImportContext> toReturn = new ArrayList<CourseImportContext>();
        try {
            File f = new File("main/resources/classlist.csv");
            InputStream is = new DigestInputStream(new FileInputStream(f), md);
            FileReader reader = new FileReader(f);
            BufferedReader buff = new BufferedReader(reader);
            List<CourseInstance> courseInstanceList = new ArrayList<CourseInstance>();
            String line = buff.readLine();

            while (null != line) {

                String[] splits = line.split(",");
                String courseType = splits[0].trim();
                boolean isCrosslisted = "True".equals(splits[1]);
                String crn = splits[2].trim();
                String subject = splits[3].trim();
                String courseNum = splits[4].trim();
                String courseTitle = splits[5].trim();
                String creditHoursStr = splits[6].trim();
                short creditHours = 0;
                try {
                    creditHours = Short.parseShort(creditHoursStr);
                } catch (NumberFormatException ignored) {
                    //ignored by design
                }
                String courseDays = splits[7];
                String times        = splits[8];
                String instructor = splits[9];
                String classroom = splits[10];
                String startDate = splits[11];
                String seatsAvailableStr = splits[12];
                short seatsAvail = -255;
                try{
                    seatsAvail = Short.parseShort(seatsAvailableStr);

                }catch (NumberFormatException ignored){}

                String term = splits[13];
                String campus = splits[14];

                CourseInstance courseInstance = new CourseInstance(crn, courseType, false, subject, courseNum, courseTitle, creditHours, courseDays, "09:00-12:00 AM",
                                           instructor, classroom, new Date(2012, 8, 12), new Date(2012, 12, 18), seatsAvail,
                                           term,
                                           campus, "Fall", "2012");//fake term

                courseInstanceList.add(courseInstance);

                line = buff.readLine();
            }
            toReturn.add(new CourseImportContext(new String(md.digest()), f.getAbsolutePath(), courseInstanceList));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return toReturn;
    }

    public static void main(String[] args) {

    }
}
