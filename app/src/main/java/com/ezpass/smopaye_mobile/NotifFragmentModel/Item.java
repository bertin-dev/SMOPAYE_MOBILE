package com.ezpass.smopaye_mobile.NotifFragmentModel;

/**
 * Structure du model de la classe Item
 *
 * @see Item
 */

public class Item {

    private int thumbnail;

    private String price;

    private String name;

    private String description;

    private String id;


    public Item(String id, String name, String description, int thumbnail, String price) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.price = price;
        this.name = name;
        this.description = description;
    }

    public int getThumbnail ()
    {
        return thumbnail;
    }

    public void setThumbnail (int thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getPrice ()
    {
        return price;
    }

    public void setPrice (String price)
    {
        this.price = price;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

   /* @Override
    public String toString()
    {
        return "ClassPojo [thumbnail = "+thumbnail+", price = "+price+", name = "+name+", description = "+description+", id = "+id+"]";
    }*/
}
