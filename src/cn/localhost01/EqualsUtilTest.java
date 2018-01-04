package cn.localhost01;

public class EqualsUtilTest {

	public static void main(String[] args) {
		B b = new B();
		b.setNum(1);
		b.setChina(true);
		b.setPhone(12545L);
		b.setHeight(15.1f);
		b.setDepart("zs");
		b.setData((byte) 20);
		b.setPower('z');

		B b1 = new B();
		b1.setNum(2);
		b1.setChina(true);
		b1.setPhone(1254L);
		b1.setHeight(15.1f);
		b1.setDepart("zs");
		b1.setData((byte) 20);
		b1.setPower('z');

		A a = new A();
		a.setAge(1);
		a.setBoy(true);
		a.setIdCard(12545L);
		a.setInCome(15.1f);
		a.setName("zs");
		a.setPass((byte) 20);
		a.setTag('a');
		a.setB(b);

		A a2 = new A();
		a2.setAge(1);
		a2.setBoy(true);
		a2.setIdCard(1254L);
		a2.setInCome(15.1f);
		a2.setName("zs");
		a2.setPass((byte) 200);
		a2.setTag('a');
		a2.setB(b1);
		System.out.println(EqualsUtil
				.assertEqualsWithFields(a, a2, new String[] { "tag","isBoy", "name", "b", "isChina" }, true));
	}
}

class A {

	/**
	 * @return the b
	 */
	public B getB() {
		return b;
	}

	/**
	 * @param b
	 *            the b to set
	 */
	public void setB(B b) {
		this.b = b;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the idCard
	 */
	public long getIdCard() {
		return IdCard;
	}

	/**
	 * @param idCard
	 *            the idCard to set
	 */
	public void setIdCard(long idCard) {
		IdCard = idCard;
	}

	/**
	 * @return the isBoy
	 */
	public boolean isBoy() {
		return isBoy;
	}

	/**
	 * @param isBoy
	 *            the isBoy to set
	 */
	public void setBoy(boolean isBoy) {
		this.isBoy = isBoy;
	}

	/**
	 * @return the inCome
	 */
	public float getInCome() {
		return inCome;
	}

	/**
	 * @param inCome
	 *            the inCome to set
	 */
	public void setInCome(float inCome) {
		this.inCome = inCome;
	}

	/**
	 * @return the pass
	 */
	public byte getPass() {
		return pass;
	}

	/**
	 * @param pass
	 *            the pass to set
	 */
	public void setPass(byte pass) {
		this.pass = pass;
	}

	/**
	 * @return the tag
	 */
	public char getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(char tag) {
		this.tag = tag;
	}

	private boolean isBoy;
	private String name;
	private B b;
	private int age;
	private long IdCard;
	private float inCome;
	private byte pass;
	private char tag;
}

class B {

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param num
	 *            the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * @return the depart
	 */
	public String getDepart() {
		return depart;
	}

	/**
	 * @param depart
	 *            the depart to set
	 */
	public void setDepart(String depart) {
		this.depart = depart;
	}

	/**
	 * @return the phone
	 */
	public long getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(long phone) {
		this.phone = phone;
	}

	/**
	 * @return the isChina
	 */
	public boolean isChina() {
		return isChina;
	}

	/**
	 * @param isChina
	 *            the isChina to set
	 */
	public void setChina(boolean isChina) {
		this.isChina = isChina;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @return the data
	 */
	public byte getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(byte data) {
		this.data = data;
	}

	/**
	 * @return the power
	 */
	public char getPower() {
		return power;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(char power) {
		this.power = power;
	}

	private int num;
	private String depart;
	private long phone;
	private boolean isChina;
	private float height;
	private byte data;
	private char power;
}
