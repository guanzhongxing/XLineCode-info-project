package com.vertonur.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;

@Entity(name = "INFO_COR_MDR")
@DiscriminatorValue("MDR")
public class Moderator extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4589023260818026396L;
	private Map<Integer, Integer> categoryDigestingNums = new HashMap<Integer, Integer>();

	@ElementCollection
	@MapKeyColumn(name = "CAT_ID")
	@Column(name = "DST_NUM")
	@CollectionTable(name = "INFO_COR_MDR_CAT_DST_NUM")
	public Map<Integer, Integer> getCategoryDigestingNums() {
		return categoryDigestingNums;
	}

	public void setCategoryDigestingNums(
			Map<Integer, Integer> categoryDigestingNums) {
		this.categoryDigestingNums = categoryDigestingNums;
	}

	public Integer getCategoryDigestingNum(int categoryId) {
		Integer digestingNum = categoryDigestingNums.get(categoryId);
		if (digestingNum == null) {
			digestingNum = new Integer(0);
			categoryDigestingNums.put(categoryId, digestingNum);
		}

		return digestingNum;
	}

	public void setCategoryDigestingNum(int categoryId, int digestingNum) {
		categoryDigestingNums.put(categoryId, digestingNum);
	}

	public void increaseCategoryDigestingNum(int categoryId) {
		Integer digestingNum = categoryDigestingNums.get(categoryId);
		if (digestingNum == null) {
			digestingNum = new Integer(0);
			categoryDigestingNums.put(categoryId, digestingNum);
		}

		categoryDigestingNums.put(categoryId, ++digestingNum);
	}

	public void decreaseCategoryDigestingNum(int categoryId) {
		Integer digestingNum = categoryDigestingNums.get(categoryId);
		if (digestingNum == null) {
			digestingNum = new Integer(0);
			categoryDigestingNums.put(categoryId, digestingNum);
		}
		categoryDigestingNums.put(categoryId, --digestingNum);
	}
}
