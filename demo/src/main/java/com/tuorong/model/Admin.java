package com.tuorong.model;

public class Admin {
    private Integer hrid;

    private String hrname;

    private String hrphone;

    private String loginname;

    private String loginpwd;

    private String secretkey;

    public Integer getHrid() {
        return hrid;
    }

    public void setHrid(Integer hrid) {
        this.hrid = hrid;
    }

    public String getHrname() {
        return hrname;
    }

    public void setHrname(String hrname) {
        this.hrname = hrname == null ? null : hrname.trim();
    }

    public String getHrphone() {
        return hrphone;
    }

    public void setHrphone(String hrphone) {
        this.hrphone = hrphone == null ? null : hrphone.trim();
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname == null ? null : loginname.trim();
    }

    public String getLoginpwd() {
        return loginpwd;
    }

    public void setLoginpwd(String loginpwd) {
        this.loginpwd = loginpwd == null ? null : loginpwd.trim();
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey == null ? null : secretkey.trim();
    }

    @Override
    public String toString() {
        return "Admin{" +
                "hrid=" + hrid +
                ", hrname='" + hrname + '\'' +
                ", hrphone='" + hrphone + '\'' +
                ", loginname='" + loginname + '\'' +
                ", loginpwd='" + loginpwd + '\'' +
                ", secretkey='" + secretkey + '\'' +
                '}';
    }
}