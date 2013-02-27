package edu.wvup.cs460.datamodel;

import edu.wvup.cs460.util.StringUtils;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public final class CourseMetadata {

    private final String _subject;
    private final String _courseNum;
    private final boolean _isHumanities;
    private final boolean _isNaturalScience;
    private final boolean _isSocialScience;
    private final boolean _isMath;
    private final boolean _isCommunications;
    private final boolean _isComputerLit;
    private final boolean _isUpperDivision;

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

    public String getSubject()          { return _subject;          }
    public String getCourseNumber()     { return _courseNum;        }
    public boolean isHumanities()       { return _isHumanities;     }
    public boolean isNaturalScience()   { return _isNaturalScience; }
    public boolean isSocialScience()    { return _isSocialScience;  }
    public boolean isMath()             { return _isMath;           }
    public boolean isCommunications()   { return _isCommunications; }
    public boolean isComputerLit()      { return _isComputerLit;    }
    public boolean isUpperDivision()    { return _isUpperDivision;  }
}
