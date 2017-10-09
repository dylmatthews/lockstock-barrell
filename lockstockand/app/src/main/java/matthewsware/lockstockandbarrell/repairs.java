package matthewsware.lockstockandbarrell;

/**
 * Created by dylanmatthews on 2017/10/09.
 */

public class repairs {

    private String name;
    private String repair;
    private String repairOther;
    private String numberItems;
    private String cost;
    private String cellphone;
    private String imgUrl;

    public repairs ()
    {
        super();
    }



    public repairs(String cost, String cellphone, String image, String name, String numItems, String repair, String repairOther) {
        this.cellphone = cellphone;
        this.cost = cost;
        this.imgUrl = image;
        this.numberItems = numItems;
        this.name = name;
        this.repair = repair;
        this.repairOther = repairOther;


    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberItems() {
        return numberItems;
    }

    public void setNumberItems(String numberItems) {
        this.numberItems = numberItems;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public String getRepairOther() {
        return repairOther;
    }

    public void setRepairOther(String repairOther) {
        this.repairOther = repairOther;
    }
}
