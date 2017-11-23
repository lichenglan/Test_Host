package com.techjumper.polyhome.polyhomebhost.entity;

/**
 * Created by kevin on 16/6/22.
 */
public class UserInfoEntity {

    private long id;
    private String family_name;
    private long user_id;
    private String ticket;
    private int has_binding; //是否已绑定设备 0-未绑定 1-已绑定

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getHas_binding() {
        return has_binding;
    }

    public void setHas_binding(int has_binding) {
        this.has_binding = has_binding;
    }
}
