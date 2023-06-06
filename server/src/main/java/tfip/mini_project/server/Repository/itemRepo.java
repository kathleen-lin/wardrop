package tfip.mini_project.server.Repository;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

// import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import tfip.mini_project.server.Model.Item;

@Repository
public class itemRepo {
    
    // @Autowired
    // private DataSource dataSource;

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private MongoTemplate mongo;

    private String MONGO_COLLECTION="wardrop";


    private String FIND_LIST_BY_CATEGORY = "select * from item where category = ? and user_name = ?";

    private String FIND_ITEM_BY_ID = "select * from item where item_id= ?";

    private String SQL_INSERT = "insert into item (photo_url, description, price, date_purchased, time_worn, category, user_name) values (?,?,?,?,?,?,?)";

    private String SQL_INCREASE_TIME_WORN = "UPDATE item SET time_worn = time_worn + 1 WHERE item_id = ?";
    
    private String DELETE_BY_ID_SQL = "DELETE from item where item_id = ?";

    // create
    public void upload(String photoUrl, String description, Float price, Date purchaseOn,  int timeWorn, String category, String userName) throws SQLException, IOException{
        // try(Connection con = dataSource.getConnection(); 
        //     PreparedStatement prstmt = con.prepareStatement(SQL_INSERT))
        //     {
        //     InputStream is = photo.getInputStream();
        //     prstmt.setBinaryStream(1, is);
        //     prstmt.setString(2, description);
        //     prstmt.setFloat(3, price);
        //     prstmt.setDate(4, purchaseOn);
        //     prstmt.setInt(5, timeWorn);
        //     prstmt.setString(6, category);
        //     prstmt.executeUpdate();
        // }
        template.update(SQL_INSERT, photoUrl, description, price, purchaseOn, timeWorn, category, userName);
    }

    public Optional<Item> getItemById(int id) {

        return template.query(
            FIND_ITEM_BY_ID,
            (ResultSet rs)->{
                if(!rs.next())
                    return Optional.empty();
                Item it = Item.populate(rs);
                return Optional.of(it);
            }, id);

    }


    // Read (list)
    public Optional<List<Item>> getItemListByCategory(String categoryName, String userName) {
    List<Item> itemsInCategory = new LinkedList<>();

    try {
        final SqlRowSet rs = template.queryForRowSet(FIND_LIST_BY_CATEGORY, categoryName, userName);

            while (rs.next()) {
                Item it = new Item();
                it.setItemId(rs.getInt("item_id"));
                it.setPhotoUrl(rs.getString("photo_url"));
                it.setDescription(rs.getString("description"));
                System.out.println(it.getDescription());
                it.setPrice(rs.getFloat("price"));
                it.setDatePurchased(rs.getDate("date_purchased"));
                it.setTimeWorn(rs.getInt("time_worn"));
                it.setCostPerWear(rs.getFloat("cost_per_wear"));
                it.setCategory(rs.getString("category"));

                itemsInCategory.add(it);
            }

            if (itemsInCategory.isEmpty()) {
                return Optional.empty();
                
            }
            return Optional.of(itemsInCategory);
        
    } catch (Exception e) {
        e.printStackTrace();
        return Optional.empty();
    }
}


    // Increase time worn by 1
    public int increaseTimeWorn(int itemId){

        int updatedBook = 0;
        updatedBook = template.update(SQL_INCREASE_TIME_WORN, itemId);

        return updatedBook;
    } 

    // Delete from item where item_id = 1;
    public int deleteItem(int itemId){
        
        int deletedItem = 0;
        deletedItem = template.update(DELETE_BY_ID_SQL, itemId);

        return deletedItem;
    }



    // Mongo -- insert
    
    public void archiveDeleted(Item i, String reason){
        // convert i to Bson doc
        Document toAdd = i.toDocument(i.toJson());
        toAdd.append("reason", reason);

        mongo.insert(toAdd, MONGO_COLLECTION);

    }

}
