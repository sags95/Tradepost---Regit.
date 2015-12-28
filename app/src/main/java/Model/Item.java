package Model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by HenryChiang on 2015-12-27.
 */
public class Item {

    private int itemId, userId, condition;
    private String title, description, category;
    private ArrayList<String> tags, imageUrls;
    private Calendar addedDate;

    //not sure the type
    private float latitude, longitude;

    public Item(int itemId, int userId, int condition, String title, String description, String category, ArrayList<String> tags, ArrayList<String> imageUrls, Calendar addedDate, float latitude, float longitude) {
        this.itemId = itemId;
        this.userId = userId;
        this.condition = condition;
        this.title = title;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.imageUrls = imageUrls;
        this.addedDate = addedDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Calendar getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Calendar addedDate) {
        this.addedDate = addedDate;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
