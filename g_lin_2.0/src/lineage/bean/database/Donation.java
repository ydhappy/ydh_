package lineage.bean.database;


/**
 * 후원 데이터베이스에 사용되는 객체 항목
 */
public class Donation {

	// 후원 번호
	private int uid;	
	// 후원 케릭터 이름
	private String name;
	// 후원 캐릭터 계정
	private String account;
	// 후원 계정 uid
	private int account_uid;
	// 후원 금액
	private long amount;	
	// 후원 날자
	private long date;
	// 후원 지급 여부
	private boolean provide;


	// 각 필드에 대한 getter와 setter 메서드
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public int getAccount_uid() {
		return account_uid;
	}
	public void setAccount_uid(int account_uid) {
		this.account_uid = account_uid;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
        this.amount = amount;
	}	
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}	
	public boolean isProvide() {
		return provide;
	}
	public boolean getProvide() {
		return provide;
	}
	public void setProvide(boolean provide) {
		this.provide = provide;
	}		
}
