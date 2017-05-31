package pim.business;

import java.util.Date;

/**
 * Representation of a user review on a product.
 *
 * @author Niels
 */
public class ProductReview {

	private int productid;
	private int userid;
	private int rating;
	private Date time;

	/**
	 * Constructs a new product review.
	 *
	 * @param productid the id of the product that this review is made on
	 * @param userid    the id of the user who made this review
	 * @param rating    the rating of the product
	 * @param time      the time when the review was made
	 */
	public ProductReview(int productid, int userid, int rating, Date time) {
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

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Date getTime() {
		return time;
	}

	public String toString() {
		return "ProductID: " + productid + " Rating: " + rating;
	}
}
