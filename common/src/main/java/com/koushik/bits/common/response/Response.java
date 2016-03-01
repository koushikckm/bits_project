package com.koushik.bits.common.response;

import java.io.Serializable;

public class Response implements Serializable
{
    private String ssn;

    private Integer phoneNo;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Integer getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(Integer phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString()
    {
        return "Response{" +
                "SSN ='" + ssn + '\'' +
                ",Phone =" + phoneNo +
                '}';
    }
}
