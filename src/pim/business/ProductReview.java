package pim.business;

import java.util.Date;

/**
 * Created by Bruger on 17-05-2017.
 */
public class ProductReview {
	private int productid;
	private int userid;
	private int rating;
	private Date time;

	public ProductReview(int productid, int userid, int rating, Date time){
		this.productid = productid;
		this.userid = userid;
		this.rating = rating;
		this.time = time;
	}

	public int getProductid() {
		return productid;
	}

	public int getUserid() {
		return userid;
	}

	public int getRating() {
		return rating;
	}

	public Date getTime() {
		return time;
	}

	public void setRating(int rating){
		this.rating = rating;
	}

	public String toString(){
		return "ProductID: " + productid + " Rating: " + rating;
	}
}
