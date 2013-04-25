package edu.wvup.cs460.datamodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class CourseMetaTest {

    @Test
    public void testCourseMetaBasic() {
        CourseMetadata meta1 = new CourseMetadata("ENGL", "403");//sets all the stuff implicitly.

        assertEquals("ENGL", meta1.getSubject());
        assertEquals("403", meta1.getCourseNumber());
        assertFalse(meta1.isCommunications());
        assertFalse(meta1.isComputerLit());
        assertFalse(meta1.isMath());
        assertFalse(meta1.isNaturalScience());
        assertFalse(meta1.isHumanities());
        assertFalse(meta1.isSocialScience());
        assertTrue(meta1.isUpperDivision());


    }

    @Test
    public void testCourseMetaMutate() {
        CourseMetadata meta1 = new CourseMetadata("ENGL", "403");//sets all the stuff implicitly.
        CourseMetadata meta2 = new CourseMetadata("ENGL", "403", true, true, true, true, true, true, Boolean.TRUE);

        //shouldn't be equal, but should compare the same, since they have the same course #.
        assertFalse(meta1.equals(meta2));
        assertEquals(0, meta1.compareTo(meta2));

        meta1.setIsCommunications(true);
        meta1.setIsComputerLit(true);
        meta1.setIsHumanities(true);
        meta1.setIsMath(true);
        meta1.setIsSocialScience(true);
        meta1.setIsUpperDivision(false);
        meta1.setIsNaturalScience(true);

        assertTrue(meta1.isCommunications());
        assertTrue(meta1.isComputerLit());
        assertTrue(meta1.isMath());
        assertTrue(meta1.isNaturalScience());
        assertTrue(meta1.isHumanities());
        assertTrue(meta1.isSocialScience());
        assertFalse(meta1.isUpperDivision());

    }

    @Test
    public void testCheckParsingUpperDivision(){
        CourseMetadata meta = new CourseMetadata("MATH", null);
        assertFalse(meta.isUpperDivision());

        meta = new CourseMetadata("ENGL", "403", true, true, true, true, true, true, null);
        assertTrue(meta.isUpperDivision());

    }
}
