package edu.wvup.cs460.datamodel;

import java.util.Date;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * Copyright (C) 2013 Apple Inc.
 * "Code early, Code often."
 */
public class CourseInstance {
    private final String _crn;
    private final String _type;
    private final boolean _crossListed;
    private final String _subject;
    private final String _courseNumber;
    private final String _courseTitle;
    private final short _credits;
    private final String _days;
    private final String _time;
    private final String _instructor;
    private final String _room;
    private final Date _startDate;
    private final Date _endDate;
    private final short _seatsAvail;
    private final String _termLength;
    private final String _campus;
    private final String _term;
    private final String _termYear;

    public CourseInstance(String _crn, String _type, boolean _crossListed, String _subject, String _courseNumber,
                          String _courseTitle, short _credits, String _days, String _time, String _instructor,
                          String _room, Date _startDate, Date _endDate, short _seatsAvail, String _termLength, String _campus,
                          String _term, String _termYear) {
        if(null == _crn){
            throw new IllegalArgumentException("Unable to create a course without a CRN.");
        }
        this._crn = _crn;

        this._type = _type;
        this._crossListed = _crossListed;
        this._subject = _subject;
        this._courseNumber = _courseNumber;
        this._courseTitle = _courseTitle;
        this._credits = _credits;
        this._days = _days;
        this._time = _time;
        this._instructor = _instructor;
        this._room = _room;
        this._startDate = _startDate;
        this._endDate = _endDate;
        this._seatsAvail = _seatsAvail;
        this._termLength = _termLength;
        this._campus = _campus;
        this._term = _term;
        this._termYear = _termYear;
    }

    public String getCrn() {
        return _crn;
    }

    public String getType() {
        return _type;
    }

    public boolean isCrosslisted() {
        return _crossListed;
    }

    public String getSubject() {
        return _subject;
    }

    public short getCredits() {
        return _credits;
    }

    public String getDays() {
        return _days;
    }

    public String getTime() {
        return _time;
    }

    public String getInstructor() {
        return _instructor;
    }

    public String getRoom() {
        return _room;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public String getTermLength() {
        return _termLength;
    }

    public String getCampus() {
        return _campus;
    }

    public String getCourseNumber() {
        return _courseNumber;
    }

    public String getCourseTitle() {
        return _courseTitle;
    }

    public short getSeatsAvail(){
        return _seatsAvail;
    }

    public String getTerm(){
        return _term;
    }

    public String getTermYear(){
        return _term;
    }

    @Override
    public String toString() {
        return "Course{" +
                "_crn='" + _crn + '\'' +
                ", _type='" + _type + '\'' +
                ", _crossListed=" + _crossListed +
                ", _subject='" + _subject + '\'' +
                ", _courseNumber='" + _courseNumber + '\'' +
                ", _courseTitle='" + _courseTitle + '\'' +
                ", _subject='" + _subject + '\'' +
                ", _credits=" + _credits +
                ", _days='" + _days + '\'' +
                ", _time='" + _time + '\'' +
                ", _instructor='" + _instructor + '\'' +
                ", _room='" + _room + '\'' +
                ", _startDate=" + _startDate +
                ", _endDate=" + _endDate +
                ", _seatsAvail=" + _seatsAvail +
                ", _termLength='" + _termLength + '\'' +
                ", _campus='" + _campus + '\'' +
                ", _term='" + _term + '\'' +
                ", _termyear='" + _termYear + '\'' +
                '}';
    }
}
