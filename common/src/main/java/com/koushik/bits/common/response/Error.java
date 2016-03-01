package com.koushik.bits.common.response;

import java.io.Serializable;

public class Error implements Serializable
{
    private String errorCode;

    private String errorMessage;

    private String errorMessageDetailed;

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessageDetailed()
    {
        return errorMessageDetailed;
    }

    public void setErrorMessageDetailed(String errorMessageDetailed)
    {
        this.errorMessageDetailed = errorMessageDetailed;
    }

    @Override
    public String toString()
    {
        return "Error{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorMessageDetailed='" + errorMessageDetailed + '\'' +
                '}';
    }
}
