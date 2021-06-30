import java.util.*;

public class Pair<L,R> {

	private L left;
	private R right;
	
	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}
	
	public Pair(Pair<L,R> pair) {
		this.left = pair.getLeft();
		this.right = pair.getRight();
	}
	
	public L getLeft() {return left;}
	public R getRight() {return right;}
	
	public void setLeft(L param) { left = param; }
	public void setRight(R param) { right = param; }

	public Pair<R,L> inverse() {
		return new Pair<R,L>(getRight(), getLeft());
	}
	
	@Override
	public int hashCode() { return left.hashCode() ^ right.hashCode(); }
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Pair)) return false;
		@SuppressWarnings("rawtypes")
		Pair pairo = (Pair) o;
		return this.left.equals(pairo.getLeft()) &&
				this.right.equals(pairo.getRight());
	}
	
	public String toString() {
		return "[" + getLeft() + ", " + getRight() + "]";
	}
}

class PairRightComp implements Comparator<Pair<Subject, Integer>> {
    @Override
    public int compare(Pair<Subject, Integer> s1, Pair<Subject, Integer> s2) {
        return s1.getRight() - s2.getRight();
    }
}
