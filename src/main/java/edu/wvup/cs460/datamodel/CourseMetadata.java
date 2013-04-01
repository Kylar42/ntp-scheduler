package edu.wvup.cs460.datamodel;

import edu.wvup.cs460.util.StringUtils;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public final class CourseMetadata implements Comparable<CourseMetadata>{

    private final String _subject;
    private final String _courseNum;
    private boolean _isHumanities;
    private boolean _isNaturalScience;
    private boolean _isSocialScience;
    private boolean _isMath;
    private boolean _isCommunications;
    private boolean _isComputerLit;
    private boolean _isUpperDivision;

    public CourseMetadata(String subject, String courseNum, boolean humanities, boolean natScience, boolean socScience,
                          boolean isMath, boolean isComm, boolean isComputerLit, boolean isUpperDivision) {
        _subject = subject;
        _courseNum = courseNum;
        _isHumanities = humanities;
        _isNaturalScience = natScience;
        _isSocialScience = socScience;
        _isMath = isMath;
        _isCommunications = isComm;
        _isComputerLit = isComputerLit;
        _isUpperDivision = isUpperDivision;
    }
    public CourseMetadata(String subject, String courseNum){
        _subject = subject;
        _courseNum = courseNum;
        _isHumanities = false;
        _isNaturalScience = false;
        _isSocialScience = false;
        _isMath = false;
        _isCommunications = false;
        _isComputerLit = false;
        _isUpperDivision = false;

    }

    public String  getSubject()         { return _subject;          }
    public String  getCourseNumber()    { return _courseNum;        }
    public boolean isHumanities()       { return _isHumanities;     }
    public boolean isNaturalScience()   { return _isNaturalScience; }
    public boolean isSocialScience()    { return _isSocialScience;  }
    public boolean isMath()             { return _isMath;           }
    public boolean isCommunications()   { return _isCommunications; }
    public boolean isComputerLit()      { return _isComputerLit;    }
    public boolean isUpperDivision()    { return _isUpperDivision;  }

    public void setIsMath           (boolean newVal){ _isMath = newVal;             }
    public void setIsHumanities     (boolean newVal){ _isHumanities = newVal;       }
    public void setIsNaturalScience (boolean newVal){ _isNaturalScience = newVal;   }
    public void setIsSocialScience  (boolean newVal){ _isSocialScience = newVal;    }
    public void setIsCommunications (boolean newVal){ _isCommunications = newVal;   }
    public void setIsComputerLit    (boolean newVal){ _isComputerLit = newVal;      }
    public void setIsUpperDivision  (boolean newVal){ _isUpperDivision = newVal;    }

    @Override
    public int compareTo(CourseMetadata o) {
        String thisCourse = getSubject()+getCourseNumber();
        String thatCourse = o.getSubject()+o.getCourseNumber();
        return thisCourse.compareTo(thatCourse);
    }
}
