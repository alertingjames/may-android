package bookclub.technion.maymsgphoto.models;

/**
 * Created by a on 5/13/2017.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by a on 2016.10.24.
 */
public class UserEntity implements Serializable {

    int _idx = 0;
    int _adminId=0;
    String _firstName = "";
    String _lastName = "";
    String _name="";
    String _email = "";
    String _password = "";
    String _photoUrl = "";
    Bitmap _bitmap=null;
    String _city = "";
    String _date_time="";
    String _job="";
    String _education="";
    String _interest="";
    int _age=0;
    String _age_range="";
    Drawable _photo=null;
    int _imageRes=0;
    String _userName="";
    String _relations="";
    String _publicName="";
    String _survey_quest="";
    String _aboutme = "";
    int _friendCount = 0;
    String _term="";
    double _userlat=0.0f;
    double _userlng=0.0;
    boolean _message_flag=false;
    String _num="0";
    String _num_org="0";
    String _num_init="0";
    String secret="";
    String carmode="";
    String date="";
    String regular="";

    String company="";
    String gender="";
    String millennial="";
    String vacayBucksGiven="";
    String vacayBucksUsed="";
    String interactions="";
    String logoUrl="";
    String status="";

    public UserEntity() {
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getCarmode() {
        return carmode;
    }

    public void setCarmode(String carmode) {
        this.carmode = carmode;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public String get_num_init() {
        return _num_init;
    }

    public void set_num_init(String _num_init) {
        this._num_init = _num_init;
    }

    public void set_num(String _num) {
        this._num = _num;
    }

    public String get_num() {
        return _num;
    }

    public String get_num_org() {
        return _num_org;
    }

    public void set_num_org(String _num_org) {
        this._num_org = _num_org;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int get_adminId() {
        return _adminId;
    }

    public void set_adminId(int _adminId) {
        this._adminId = _adminId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setInteractions(String interactions) {
        this.interactions = interactions;
    }

    public void setVacayBucksUsed(String vacayBucksUsed) {
        this.vacayBucksUsed = vacayBucksUsed;
    }

    public void setVacayBucksGiven(String vacayBucksGiven) {
        this.vacayBucksGiven = vacayBucksGiven;
    }

    public void setMillennial(String millennial) {
        this.millennial = millennial;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public String getMillennial() {
        return millennial;
    }

    public String getVacayBucksGiven() {
        return vacayBucksGiven;
    }

    public String getVacayBucksUsed() {
        return vacayBucksUsed;
    }

    public String getInteractions() {
        return interactions;
    }

    ArrayList<UserEntity> _friendList = new ArrayList<>();


    public void set_userName(String _userName) {
        this._userName = _userName;
    }
    public String get_userName(){ return  _userName;}

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_relations() {
        return _relations;
    }

    public void set_relations(String relations) {
        this._relations = relations;
    }

    public String get_publicName() {
        return _publicName;
    }

    public void set_publicName(String publicName) {
        this._publicName = publicName;
    }

    public String get_aboutme() {
        return _aboutme;
    }

    public void set_aboutme(String _aboutme) {
        this._aboutme = _aboutme;
    }

    public Drawable get_photo(){return this._photo;}

    public void set_photo(Drawable photo){this._photo=photo;}

    public Bitmap get_bitmap(){return this._bitmap;}

    public void set_bitmap(Bitmap bitmap){this._bitmap=bitmap;}

    public double get_userlat() {
        return _userlat;
    }

    public void set_userlat(double userlat) {
        this._userlat = userlat;
    }

    public double get_userlng() {
        return _userlng;
    }

    public void set_userlng(double userlng) {
        this._userlng = userlng;
    }


    public String get_firstName() {
        return _firstName;
    }
    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public int get_idx() {
        return _idx;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void set_job(String job){
        this._job=job;
    }
    public String get_job(){
        return _job;
    }

    public void set_age_range(String age_range){
        this._age_range=age_range;
    }
    public String get_age_range(){
        return _age_range;
    }

    public void set_education(String education){
        this._education=education;
    }
    public String get_education(){
        return _education;
    }

    public void set_age(int age){
        this._age=age;
    }
    public int get_age(){
        return _age;
    }

    public void set_message_flag(boolean message_flag){
        this._message_flag=message_flag;
    }
    public boolean is_message_flag(){return _message_flag;}

    public String get_lastName() {
        return _lastName;
    }
    public void set_lastName(String _lastName) {
        this._lastName = _lastName;
    }

    public String get_survey_quest() {
        return _survey_quest;
    }
    public void set_survey_quest(String survey_quest) {
        this._survey_quest = survey_quest;
    }

    public String get_fullName() {

        return _firstName + " " + _lastName;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String name) {
        this._name = name;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public int get_imageRes() {
        return _imageRes;
    }

    public void set_imageRes(int imageRes) {
        this._imageRes = imageRes;
    }

    public String get_photoUrl() {
        return _photoUrl;
    }

    public void set_photoUrl(String _photoUrl) {
        this._photoUrl = _photoUrl;
    }

    public String get_date_time() {
        return _date_time;
    }
    public void set_date_time(String date_time) {
        this._date_time = date_time;
    }

    public String get_city() {
        return _city;
    }
    public void set_city(String city){this._city=city;}

    public void set_interest(String interest) {
        this._interest = interest;
    }
    public String get_interest(){return  _interest;}
    public ArrayList<UserEntity> get_friendList() {
        return _friendList;
    }

    public void set_friendList(ArrayList<UserEntity> friendList) {
        this._friendList.addAll( friendList);
    }

    public boolean isFriend(int idx) {

        for (UserEntity friend : _friendList) {

            if (friend.get_idx() == idx)
                return true;
        }

        return false;
    }

    public void addFriend(UserEntity other) {

        if (!_friendList.contains(other)) {
            _friendList.add(other);
            _friendCount++;
        }
    }

    public void deleteFriend(UserEntity other) {

        if (_friendList.contains(other)) {
            _friendList.remove(other);
            _friendCount--;
        }
    }



    @Override
    public boolean equals(Object o) {

        UserEntity other = (UserEntity) o;

        if (get_idx() == other.get_idx())
            return true;

        return false;
    }
}



