package tfip.mini_project.server.Model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Item implements Serializable {
    
    private int itemId;
    private String photoUrl;
    private String description;
    private Float price;
    private Date datePurchased;
    private int timeWorn;
    private Float costPerWear;
    private String category;

    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Float getPrice() {
        return price;
    }
    public void setPrice(Float price) {
        this.price = price;
    }
    public Date getDatePurchased() {
        return datePurchased;
    }
    public void setDatePurchased(Date datePurchased) {
        this.datePurchased = datePurchased;
    }
    public int getTimeWorn() {
        return timeWorn;
    }
    public void setTimeWorn(int timeWorn) {
        this.timeWorn = timeWorn;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public Float getCostPerWear() {
        return costPerWear;
    }
    public void setCostPerWear(Float costPerWear) {
        this.costPerWear = costPerWear;
    }

    

    public static Item populate(ResultSet rs) throws SQLException{
        Item it = new Item();
        it.setItemId(rs.getInt("item_id"));
        it.setPhotoUrl(rs.getString("photo_url"));
        it.setDescription(rs.getString("description"));
        it.setPrice(rs.getFloat("price"));
        it.setDatePurchased(rs.getDate("date_purchased"));
        it.setTimeWorn(rs.getInt("time_worn"));
        it.setCostPerWear(rs.getFloat("cost_per_wear"));
        it.setCategory(rs.getString("category"));
        return it;
    }
    
    public JsonObject toJson() {

        JsonObject jo = Json.createObjectBuilder()
                        .add("itemId", getItemId())
                        .add("photoUrl", getPhotoUrl())
                        .add("description", getDescription())
                        .add("datePurchased", getDatePurchased().toString())
                        .add("timeWorn", getTimeWorn())
                        .add("costPerWear", getCostPerWear().toString())
                        .add("category", getCategory())
                        .build();
        
        return jo;
    }

    public Document toDocument(JsonObject jo){

        return Document.parse(jo.toString());
        
    }

 

}
