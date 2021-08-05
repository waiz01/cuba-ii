package com.example.healthyspotsignin;
public class RowItem {

    private String member_name;
    private int profile_pic_id;
    private String id;
    private String code;
    private String group;

    public RowItem(String member_name, int profile_pic_id, String id, String code, String group) {

        this.member_name = member_name;
        this.profile_pic_id = profile_pic_id;
        this.id = id;
        this.code = code;
        this.group = group;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public int getProfile_pic_id() {
        return profile_pic_id;
    }

    public void setProfile_pic_id(int profile_pic_id) {
        this.profile_pic_id = profile_pic_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}