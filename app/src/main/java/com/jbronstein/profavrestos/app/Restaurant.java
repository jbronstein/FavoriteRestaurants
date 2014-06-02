package com.jbronstein.profavrestos.app;

/**
 * Created by Josh on 5/31/14.
 */
public class Restaurant {

        private int mId;
        private String mContent;
        private int mImportant;
        private String mPhone;
        private String mCity;
        private String mAddress;
        private String mUrl;
        private String mNote;


        public Restaurant(int id, String content, int important, String c, String p, String a, String y, String n) {
            mId = id;
            mImportant = important;
            mContent = content;
            mCity = c;
            mPhone = p;
            mAddress = a;
            mUrl = y;
            mNote = n;
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public int getImportant() {
            return mImportant;
        }

        public void setImportant(int important) {
            mImportant = important;
        }

        public String getContent() {
            return mContent;
        }

        public void setContent(String content) {
            mContent = content;
        }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getNote(){
        return mNote;
    }
    public void setNote(String note){
        mNote = note;
    }
    }
