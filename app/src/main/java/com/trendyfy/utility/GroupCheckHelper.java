package com.trendyfy.utility;

import android.app.Activity;

import com.trendyfy.model.ProductListModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;

import java.util.List;

/**
 * Created by akashdeep on 21-05-2017.
 */

public class GroupCheckHelper {

    public static GroupCheckHelper helper;

    public static GroupCheckHelper getInstance() {
        if (helper == null) {
            helper = new GroupCheckHelper();
        }

        return helper;
    }

    public int AddedProductInCart(Activity mActivity) {

        int addedProduct = 0;
        try {
            List<ProductListModel> productList = AppPreference.getObjectFromPref(mActivity,
                    PreferenceHelp.ADDED_PRODUCT);
            if (productList != null && productList.size() > 0) {
                for (int i = 0; i < productList.size(); i++) {
                    addedProduct += productList.get(i).getAddInCartQty();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedProduct;
    }

    public static String groupGroceryHeading(int groupPosition, String childItem) {
        switch (groupPosition) {
            case 0:
                switch (childItem) {
                    case "Milk":
                        return "Dairy";
                }
        }
        return "Grocery";
    }

    public static String groupHeading(int groupPosition, String childItem) {
        switch (groupPosition) {
            case 3:
                switch (childItem) {
                    case "Bedsheet":
                    case "CushionCover":
                    case "Curtain":
                    case "BeanBag":
                    case "Mat":
                    case "Carpet":
                    case "TableKitchen":
                        return "HomeFurnishing";

                    case "Container":
                    case "Mug":
                    case "Glassware":
                    case "DinnerSet":
                    case "Kitchen":
                    case "Dining":
                    case "Lunch":
                    case "Pots":
                    case "RotiMaker":
                    case "Mixer":
                        return "KitchenDining";

                    case "TableDecor":
                    case "WallDecor":
                    case "GiftsItem":
                    case "Handicraft":
                    case "TableLamp":
                    case "TableClock":
                    case "WallClock":
                        return "HomeDecor";
                    case "Iron":
                        return "HomeAppliances";
                    case "Microwave":
                    case "FoodProcessor":
                    case "InductionChulha":
                    case "ElectricKettle":
                        return "KitchenAppliances";
                }
                break;
            case 4:
                switch (childItem) {
                    case "AllMobile":
                        return "ElectronicsMobiles";
                }
                break;
            case 5:
                switch (childItem) {
                    case "BoysShirt":
                    case "BoysTShirt":
                    case "BoysJacket":
                    case "BoysShorts":
                    case "GirlsCloths":
                    case "BoysCloths":
                    case "GirlDress":
                    case "GirlTShirt":
                        return "KidsClothing";
                    case "BoysFootwear":
                    case "GirlsFootwear":
                        return "KidsFootwear";
                    case "Sunglass":
                    case "Bag":
                    case "Watch":
                        return "KidsAccessories";
                    case "Dolls":
                    case "Construction":
                        return "ToyGames";
                }
                break;
            case 6:
                switch (childItem) {
                    case "Educational":
                    case "Historical":
                    case "Novel":
                    case "Professional":
                        return "Books";
                    case "CorporateGifts":
                        return "OfficeEquipment";
                    case "Diary":
                        return "Stationary";
                    case "Laptop":
                    case "College":
                    case "Trekking":
                        return "Bags";
                }
                break;
        }
        return null;

    }


    public static boolean isHeadingExist(String groupItem, String childItem) {
        switch (groupItem) {
            case "Grocery":
                switch (childItem) {
                    case "Grocery/Staples":
                        return true;
                }
                break;
            case "Men":
                switch (childItem) {
                    case "Men Clothing":
                        return true;
                    case "Men Footwear":
                        return true;
                    case "Men Accessories":
                        return true;
                }
                break;
            case "Women":
                switch (childItem) {
                    case "Women Clothing":
                        return true;
                    case "Women Footwear":
                        return true;
                    case "Women Accessories":
                        return true;
                    case "Beauty & Cosmetics":
                        return true;
                    case "Lingerie, Sleep & Swimwear":
                        return true;
                    case "Jewellery":
                        return true;
                    case "Bags":
                        return true;
                }
                break;
            case "Home & Lifestyle":
                switch (childItem) {
                    case "Home Furnishing":
                        return true;
                    case "Kitchen & Dinning":
                        return true;
                    case "Home Decor":
                        return true;
                    case "Home Appliances":
                        return true;
                    case "Kitchen Appliances":
                        return true;
                }
                break;
            case "Electronics":
                switch (childItem) {
                    case "Mobile Accessories":
                        return true;
                    case "Computer Accessories":
                        return true;
                }
                break;
            case "Kids":
                switch (childItem) {
                    case "Kids Clothing":
                        return true;
                    case "Kids Footwear":
                        return true;
                    case "Kids Accessories":
                        return true;
                    case "Toy and Games":
                        return true;
                }
                break;
            case "Books & Stationary":
                switch (childItem) {
                    case "Books":
                        return true;
                    case "Office Equipment":
                        return true;
                    case "Bags & Luggage":
                        return true;
                    case "Stationary":
                        return true;
                }
                break;
        }
        return false;
    }


    public static boolean isTextHeading(String groupItem, String childItem) {
        switch (groupItem) {
            case "Grocery":
                switch (childItem) {
                    case "Grocery/Staples":
                        return true;
                    case "Milk":
                        return true;
                }
                break;
            case "Men":
                switch (childItem) {
                    case "Men Clothing":
                        return true;
                    case "Men Footwear":
                        return true;
                    case "Men Accessories":
                        return true;
                }
                break;
            case "Women":
                switch (childItem) {
                    case "Women Clothing":
                        return true;
                    case "Women Footwear":
                        return true;
                    case "Women Accessories":
                        return true;
                    case "Beauty & Cosmetics":
                        return true;
                    case "Lingerie, Sleep & Swimwear":
                        return true;
                    case "Jewellery":
                        return true;
                    case "Bags":
                        return true;
                }
                break;
            case "Home & Lifestyle":
                switch (childItem) {
                    case "Home Furnishing":
                        return true;
                    case "Kitchen & Dinning":
                        return true;
                    case "Home Decor":
                        return true;
                    case "Home Appliances":
                        return true;
                    case "Kitchen Appliances":
                        return true;
                }
                break;
            case "Electronics":
                switch (childItem) {
                    case "Mobile Accessories":
                        return true;
                    case "Computer Accessories":
                        return true;
                }
                break;
            case "Kids":
                switch (childItem) {
                    case "Kids Clothing":
                        return true;
                    case "Kids Footwear":
                        return true;
                    case "Kids Accessories":
                        return true;
                    case "Toy & Games":
                        return true;
                }
                break;
            case "Books & Stationary":
                switch (childItem) {
                    case "Books":
                        return true;
                    case "Office Equipment":
                        return true;
                    case "Bags & Luggage":
                        return true;
                    case "Stationary":
                        return true;
                }
                break;
        }
        return false;
    }
}
