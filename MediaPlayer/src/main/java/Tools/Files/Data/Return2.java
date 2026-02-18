package Tools.Files.Data;

public class Return2<T1,T2> {
	public T1 one;
	public T2 two;
	public Return2(T1 one, T2 two) {
		this.one = one;
		this.two = two;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Return2 r2) {
			return this.one.equals(r2.one) && this.two.equals(r2.two);
		}
		return super.equals(obj);
	}
}
