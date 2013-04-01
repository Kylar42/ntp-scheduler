package edu.wvup.cs460.action;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public enum ChainStatus {

    PASS_CONTINUE(true, true),
    PASS_DO_NOT_CONTINUE(true, false),
    FAIL_CONTINUE(false, true),
    FAIL_DO_NOT_CONTINUE(false, false);

    private boolean _continue;
    private boolean _success;

    private ChainStatus(boolean shouldContinue, boolean isSuccess){
        _continue = shouldContinue;
        _success = isSuccess;
    }

    public boolean isSuccess(){ return _success;}
    public boolean shouldContinue(){ return _continue;}//naming convention says it should be 'isContinue' but I like this better semantically.

}
