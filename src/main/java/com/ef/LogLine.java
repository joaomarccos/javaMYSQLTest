package com.ef;

public class LogLine {

    private String date, ip, request, status, userAgent;

    public static final String DATE_PATTERN = "yyyy-MM-dd.HH:mm:ss";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "LogLine{" +
                "date='" + date + '\'' +
                ", ip='" + ip + '\'' +
                ", request='" + request + '\'' +
                ", status='" + status + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
