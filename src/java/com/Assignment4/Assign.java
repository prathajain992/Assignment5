/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Assignment4;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONArray;

/**
 * REST Web Service
 *
 * @author Pratha JAin
 */
@Path("products")
public class Assign {
Dataconnection dataconn=new Dataconnection();
    Connection connected=null;
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of ProductResource
     */
    public Assign() {
       connected=dataconn.getConnection();
    }

    /**
     * Retrieves representation of an instance of com.oracle.products.ProductResource
     * @return an instance of java.lang.String
     */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String getAllProducts() throws SQLException
   {
       if(connected==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from product";
           PreparedStatement ps = connected.prepareStatement(query);
           ResultSet rs = ps.executeQuery();
           String res="";
           JSONArray productArr = new JSONArray();
           while (rs.next()) {
                Map proMap = new LinkedHashMap();
                proMap.put("productID", rs.getInt("product_id"));
                proMap.put("name", rs.getString("name"));
                proMap.put("description", rs.getString("description"));
                proMap.put("quantity", rs.getInt("quantity"));
                productArr.add(proMap);
           }
            res = productArr.toString();
          return  res.replace("},", "},\n");
        }
       
   }
   
   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getproduct(@PathParam("id") int id) throws SQLException {
   
      if(connected==null)
       {
           return "not connected";
       }
       else {
           String query="Select * from product where product_id = ?";
           PreparedStatement pstmt = connected.prepareStatement(query);
           pstmt.setInt(1,id);
           ResultSet rs = pstmt.executeQuery();
           String res="";
           JSONArray productArr = new JSONArray();
           while (rs.next()) {
                 Map proMap = new LinkedHashMap();
                proMap.put("productID", rs.getInt("product_id"));
                proMap.put("name", rs.getString("name"));
                proMap.put("description", rs.getString("description"));
                proMap.put("quantity", rs.getInt("quantity"));
                productArr.add(proMap);
           }    
                res = productArr.toString();
                
                 return res;
      }
   
   }
   
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String postProduct(String str) throws SQLException{
       JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> proMap = new LinkedHashMap<String,String>();
       String key="";
       String val="";
       
       while(parser.hasNext()){
        JsonParser.Event event=parser.next();
            switch (event){
            case KEY_NAME :
              key = parser.getString();
              break;
            case VALUE_STRING:
              val=parser.getString();
              proMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              proMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(connected == null){
           return "Not connected";
       }
       else {
            String query="INSERT INTO product (product_id,name,description,quantity) VALUES (?,?,?,?)";
            PreparedStatement pst=connected.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(proMap.get("product_id")));
            pst.setString(2, proMap.get("name"));
            pst.setString(3, proMap.get("description"));
            pst.setInt(4, Integer.parseInt(proMap.get("quantity")));
            pst.executeUpdate();
            return "row has been inserted into the database";
           }
       
       
   }
   
   
   @PUT
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public String  putProduct(@PathParam("id")  int id,String str) throws SQLException{
    JsonParser parser= Json.createParser(new StringReader(str));
       Map<String,String> proMap = new LinkedHashMap<>();
       String key="";
       String val="";
       
       while(parser.hasNext()){
        JsonParser.Event event=parser.next();
            switch (event){
            case KEY_NAME :
              key = parser.getString();
              break;
            case VALUE_STRING:
              val=parser.getString();
              proMap.put(key, val);
              break;
            case VALUE_NUMBER:     
              val=parser.getString();
              proMap.put(key, val);
              break;  
            default :
              break;  
            }
       }    
       if(connected == null){
           return "Not connected";
       }
       else {
            String query="UPDATE product SET  name = ?, description = ?, quantity = ? WHERE product_id =?" ;          PreparedStatement pstmt=connected.prepareStatement(query);
            pstmt.setString(1, proMap.get("name"));
            pstmt.setString(2, proMap.get("description"));
            pstmt.setInt(3, Integer.parseInt(proMap.get("quantity")));
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            return "row has been updated into the database";
           }
   
   }
 
   @DELETE
   @Path("{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String deleteProduct(@PathParam("id") int id) throws SQLException{
       
        if(connected==null)
        {
           return "not connected";
        }
        else {
           String query="DELETE FROM product WHERE product_id = ?";
           PreparedStatement pstmt = connected.prepareStatement(query);
           pstmt.setInt(1,id);
           pstmt.executeUpdate();
           return "The specified row is deleted";
           
        }
   
    }
}
