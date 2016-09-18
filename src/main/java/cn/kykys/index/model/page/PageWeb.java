package cn.kykys.index.model.page;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 分页使用 配合page.ftl
 * @author wang
 *
 */
public class PageWeb implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7258571016655959890L;
	
	private int pageIndex = 1;//当前页
	private int pageSize = 10;//页大小
	private int pageCount = 0;//总数页数
	private int offset = this.pageIndex;
	private int limit = this.pageCount;
	private int startIndex = 0;//开始
	private int endIndex = 0;//结束
	private int maxPageItems = 5;//最多项
	private int count;//总数
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPageCount() {
		return pageCount;
	}
	private void setPageCount(int pageCount) {
		this.pageCount = pageCount;
		this.setStartIndex(pageCount);
		this.setEndIndex(pageCount);
	}
	public int getOffset() {
		this.offset = (this.pageIndex-1)*this.pageSize;
		return offset;
	}
	public int getLimit() {
		this.limit = this.pageIndex*this.pageSize;
		return limit;
	}
	public int getStartIndex() {
		return startIndex;
	}
	private void setStartIndex(int pageCount) {
		int plus = (new BigDecimal(this.maxPageItems).divide(new BigDecimal(2),0,RoundingMode.DOWN)).intValue();
		int num = this.pageIndex-plus;
		if(num<=0){
			num = 1;
		}
		this.startIndex = num;
	}
	public int getEndIndex() {
		return endIndex;
	}
	private void setEndIndex(int pageCount) {
		int plus = (new BigDecimal(this.maxPageItems-1).divide(new BigDecimal(2),0,RoundingMode.DOWN)).intValue();
		int num = this.pageIndex+plus;
		if(num>pageCount){
			num = pageCount;
			int temp = num-this.startIndex;
			if(temp<this.maxPageItems-1){
				temp = this.maxPageItems-1-temp;
				this.startIndex = this.startIndex-temp;
				if(this.startIndex<=0){
					this.startIndex = 1;
				}
			}
		}else{
			int temp = num-this.startIndex;
			if(temp<this.maxPageItems-1){
				temp = this.maxPageItems-1-temp;
				num = num+temp;
				if(num>pageCount){
					num = pageCount;
					temp = num-this.startIndex;
					this.startIndex = this.startIndex-temp;
					if(this.startIndex<=0){
						this.startIndex = 1;
					}
				}
			}
		}
		this.endIndex = num;
	}
	public int getMaxPageItems() {
		return maxPageItems;
	}
	public void setMaxPageItems(int maxPageItems) {
		if(maxPageItems<=0){
			maxPageItems = 1;
		}
		this.maxPageItems = maxPageItems;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
		int pageCount = (new BigDecimal(count).divide(new BigDecimal(this.pageSize),0,RoundingMode.CEILING)).intValue();
		this.setPageCount(pageCount);
	}
	
}
